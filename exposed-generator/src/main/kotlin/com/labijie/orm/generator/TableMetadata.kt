package com.labijie.orm.generator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.labijie.infra.orm.SimpleIdTable
import com.labijie.infra.orm.compile.KspTablePojo
import org.jetbrains.exposed.dao.id.IdTable

class TableMetadata(declaration: KSClassDeclaration, val sourceFile: String) {
    val columns: MutableList<ColumnMetadata> = mutableListOf()

    val packageName = declaration.packageName.asString()
    val className = declaration.simpleName.getShortName()

    var primaryKeys: MutableList<ColumnMetadata> = mutableListOf()
    var kind: TableKind = TableKind.Normal
        private set

    val isSerializable: Boolean
    val isOpen: Boolean

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

        val defaultArgs = pojoAnnotation?.defaultArguments
        val defaultParams = mutableMapOf<Int, String>()
        defaultArgs?.forEachIndexed { index, item ->
            val name = item.name?.getShortName()
            if (!name.isNullOrBlank()) {
                defaultParams[index] = name
            }
        }

        val args = pojoAnnotation?.arguments
        val parameterValues = mutableMapOf<String, Any?>()
        args?.forEachIndexed { index, item ->
            val name = item.name?.getShortName() ?: defaultParams[index]
            if (!name.isNullOrBlank()) {
                parameterValues[name] = item.value
            }
        }


        isSerializable = parameterValues.getOrDefault(KspTablePojo::kotlinSerializable.name, false) as? Boolean ?: false
        isOpen = parameterValues.getOrDefault(KspTablePojo::isOpen.name, true) as? Boolean ?: true
    }

    fun hasPrimaryKey() = primaryKeys.size > 0

    fun normalizeClassName(): String {
        return if (this.className.endsWith("Table")) this.className.removeSuffix("Table") else "${this.className}POJO"
    }

    override fun toString(): String {
        return "TableMetadata(columns=${columns.size}, packageName='$packageName', className='$className', primaryKeys=${primaryKeys.joinToString { it.name }}, kind=$kind)"
    }


}