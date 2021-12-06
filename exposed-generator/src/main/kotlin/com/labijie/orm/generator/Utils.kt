package com.labijie.orm.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory

fun KSPLogger.println(message: String, symbol: KSNode? = null) {
    this.info("[expose gen] $message", symbol)
}


private fun KSType.isExposedColumn(): Boolean {
    val isColumnType = this.declaration.qualifiedName?.asString() == Column::class.qualifiedName
    return isColumnType && this.declaration.typeParameters.size == 1
}


private fun KSType.getIDTypeFromEntityID(): KSType? {
    if (this.arguments.size != 1) {
        return null
    }
    if (this.declaration.qualifiedName?.asString() != EntityID::class.qualifiedName) {
        return null
    }
    return this.arguments.first().type?.resolve()
}

fun KSPropertyDeclaration.getColumnType(): ColumnType? {
    val argCount = this.type.element?.typeArguments?.count()

    if ((argCount == 1 || argCount == null)) {
        val propertyType = this.type.resolve()

        val columnType = propertyType.arguments.firstOrNull()?.type?.resolve()
        if (columnType != null) {
            val idType = columnType.getIDTypeFromEntityID()
            if (idType != null) {
                return ColumnType(columnType, idType, columnType.isMarkedNullable)
            }
            return ColumnType(columnType, columnType, columnType.isMarkedNullable)
        }
    }
    //尽量避免解析
    return null
}

fun KSType.isEnum(): Boolean {
    val classDeclaration = this.declaration as? KSClassDeclaration
    return classDeclaration != null && classDeclaration.classKind == ClassKind.ENUM_CLASS
}


private fun createFolderIfNotExisted(path: Path) {
    if (!Files.exists(path) || !path.isDirectory()) {
        path.createDirectories()
    }
}

fun buildWriterOptions(env: Map<String, String>): WriterOptions {
    val dir = env.getOrDefault(EnvVariables.PROJECT_DIR, null)
    if (dir != null && !Path(dir).isAbsolute) {
        throw ExposedGenerationException("`${EnvVariables.PROJECT_DIR}` must be an absolute path")
    }

    return WriterOptions().apply {
        this.packageName = env.getOrDefault(EnvVariables.PACKAGE_NAME, null)
        this.projectDir = env.getOrDefault(EnvVariables.PROJECT_DIR, null)
    }
}

fun WriterOptions.getSourceFolder(table: TableMetadata): Path {
    val root = this.projectDir
    if(root != null) {
        val path = Path(root, "src", "main", "kotlin")
        createFolderIfNotExisted(path)
        return path
    }
    return findProjectSourceDir(File(table.sourceFile).parentFile.absolutePath)
}


private val ANY_WILDCARD = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))

fun ClassName.parameterizedWildcard(): ParameterizedTypeName {
    return this.parameterizedBy(ANY_WILDCARD)
}

private val srcPath = "/src/main/kotlin/".replace("/", File.separator)
private val folders = mutableMapOf<String, Path>()

fun findProjectSourceDir(sourceFile: String): Path {
    val existed = folders[sourceFile]
    if(existed != null){
        return existed
    }
    val index = sourceFile.indexOf(srcPath)
    if(index > 0) {
        val folder = sourceFile.substring(0, index)
        folders[sourceFile] = Path("${folder}${srcPath}".trimEnd(File.separatorChar))
    }
    return folders[sourceFile] ?: throw ExposedGenerationException("Unable to get project folder from file '${sourceFile}'")
}
