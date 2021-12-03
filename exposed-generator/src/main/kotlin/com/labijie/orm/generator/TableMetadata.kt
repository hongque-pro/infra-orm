package com.labijie.orm.generator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.labijie.infra.orm.SimpleIdTable
import org.jetbrains.exposed.dao.id.IdTable
import java.text.Normalizer

class TableMetadata(declaration: KSClassDeclaration) {
    val columns: MutableList<ColumnMetadata> = mutableListOf()

    val packageName = declaration.packageName.asString()
    val className =declaration.simpleName.getShortName()

    var primaryKeys: MutableList<ColumnMetadata> = mutableListOf()
    var kind: TableKind = TableKind.Normal
        private set

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
    }

    fun hasPrimaryKey() = primaryKeys.size > 0

    fun normalizeClassName(): String {
        return if (this.className.endsWith("Table")) this.className.removeSuffix("Table") else "${this.className}POJO"
    }

    override fun toString(): String {
        return "TableMetadata(columns=${columns.size}, packageName='$packageName', className='$className', primaryKeys=${primaryKeys.joinToString { it.name }}, kind=$kind)"
    }


}