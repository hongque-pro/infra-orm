package com.labijie.orm.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ksp.toClassName
import org.jetbrains.exposed.dao.id.EntityID
import java.math.BigDecimal
import java.util.*

object DefaultValues {
    //exposed types: https://github.com/JetBrains/Exposed/wiki/DataTypes

    private val values = mutableMapOf(
        String::class.qualifiedName to "\"\"",
        Int::class.qualifiedName to "0",
        Short::class.qualifiedName to "0",
        Long::class.qualifiedName to "0L",
        Float::class.qualifiedName to "0f",
        Double::class.qualifiedName to "0.0",
        BigDecimal::class.qualifiedName to "BigDecimal.ZERO",
        Char::class.qualifiedName to "'\\u0000'",
        ByteArray::class.qualifiedName to "ByteArray(0)",
        UUID::class.qualifiedName to "UUID.randomUUID()",
        Boolean::class.qualifiedName to "false",
        Byte::class.qualifiedName to "0",
    )

    fun getValue(type: KSType): String {
        if(type.isEnum()){
            val enumDeclaration = type.declaration as KSClassDeclaration
            val firstEnumEntry = enumDeclaration.declarations.firstOrNull() ?: throw java.lang.IllegalArgumentException("enum type '${type.declaration.simpleName.asString()}' value missed")
            val filedName = firstEnumEntry.simpleName.asString()
            return "${type.declaration.simpleName.getShortName()}.${filedName}"
        }

        val typeName = type.declaration.qualifiedName!!.asString()


        return values[typeName] ?: throw IllegalArgumentException("'${typeName}' has not a default value currently")
    }

    fun getEnumValue(type: KSType): String {
        return values[type.declaration.qualifiedName!!.asString()] ?: throw IllegalArgumentException("'${type.declaration.qualifiedName!!.asString()}' has not a default value currently")
    }
}