package com.labijie.orm.generator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Origin
import com.labijie.infra.orm.SimpleIdTable
import com.labijie.infra.orm.compile.KspTablePojo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import org.jetbrains.exposed.dao.id.IdTable
import kotlin.reflect.KClass

class TableMetadata(declaration: KSClassDeclaration, val sourceFile: String, private val logger: KSPLogger) {

    class PojoSuper(val type: KSType, val isInterface: Boolean, val isAbstract: Boolean) {
        fun getPublicProperty(propName: String): KSPropertyDeclaration? {
            val declaration = type.declaration as? KSClassDeclaration

            val property = declaration?.getDeclaredProperties()?.find { it ->
                it.simpleName.getShortName() == propName && it.isPublic()
            }

            if(property != null) {
                return property
            }

            if(declaration != null) {
                for(t in declaration.getAllSuperTypes())
                {
                    val superType = t.declaration as? KSClassDeclaration
                    val found = superType?.getDeclaredProperties()?.find { it ->
                        it.simpleName.getShortName() == propName && it.isPublic()
                    }
                    if(found != null) {
                        return found
                    }
                }
            }
            return null
        }
    }

    val columns: MutableList<ColumnMetadata> = mutableListOf()

    val packageName = declaration.packageName.asString()
    val className = declaration.simpleName.getShortName()

    var primaryKeys: MutableList<ColumnMetadata> = mutableListOf()
    var kind: TableKind = TableKind.Normal
        private set

    val isSerializable: Boolean
    val isOpen: Boolean
    val superTypes: MutableList<PojoSuper> = mutableListOf()

    val tableClass by lazy {
        ClassName(packageName, className)
    }


    init {


        loop@ for (superType in declaration.getAllSuperTypes()) {
            val typeName = superType.declaration.qualifiedName?.asString()
            if (typeName == IdTable::class.qualifiedName) {
                kind = TableKind.ExposedIdTable
                break@loop
            }

            if (typeName == SimpleIdTable::class.qualifiedName) {
                kind = TableKind.SimpleIdTable
                break@loop
            }
        }

        val pojoAnnotation = declaration.annotations.firstOrNull {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == KspTablePojo::class.qualifiedName
        }

        val pojoAnnoValues = pojoAnnotation?.getProperties()


        isSerializable = pojoAnnoValues?.getOrDefault(KspTablePojo::kotlinSerializable.name, false) as? Boolean ?: false
        isOpen = pojoAnnoValues?.getOrDefault(KspTablePojo::isOpen.name, true) as? Boolean ?: true

        val types = pojoAnnoValues?.getOrDefault(KspTablePojo::superClasses.name, listOf<KSType>())
        val supers = types as? List<*> ?: listOf<KSType>()

        supers.forEach {
            type->
            if (type is KSType) {
                val classDeclaration = type.declaration as? KSClassDeclaration
                val isKotlin =
                    classDeclaration != null && (classDeclaration.origin == Origin.KOTLIN || classDeclaration.origin == Origin.KOTLIN_LIB)
                val isInterface = classDeclaration?.classKind == com.google.devtools.ksp.symbol.ClassKind.INTERFACE
                val isOpen = classDeclaration?.isOpen() ?: false
                val isAbstract = classDeclaration?.isAbstract() ?: false
                //val byType = if(by != null && by is KSType && by.declaration.simpleName.getShortName() != "Unit") by else null
                if (isKotlin && (isInterface || isOpen || isAbstract)) {
                    val i = PojoSuper(type, isInterface, isAbstract)
                    if (isInterface) {
                        superTypes.add(i)
                    } else {
                        if (!superTypes.any { !it.isInterface }) {
                            superTypes.add(0, i)
                        } else {
                            logger.warn("Table pojo has more than one super class, super class '${type.toTypeName()}' discarded.")
                        }
                    }
                } else {
                    logger.warn("Table pojo super class/interface is invalid, class '${type.toTypeName()}' is not interface or opened class)")
                }
            }
        }
    }

    fun hasPrimaryKey() = primaryKeys.isNotEmpty()

    fun normalizeClassName(): String {
        return if (this.className.endsWith("Table")) this.className.removeSuffix("Table") else "${this.className}POJO"
    }

    override fun toString(): String {
        return "TableMetadata(columns=${columns.size}, packageName='$packageName', className='$className', primaryKeys=${primaryKeys.joinToString { it.name }}, kind=$kind)"
    }


}