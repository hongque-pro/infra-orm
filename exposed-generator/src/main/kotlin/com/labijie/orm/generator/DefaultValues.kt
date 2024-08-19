package com.labijie.orm.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.MemberName
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

object DefaultValues {
    //exposed types: https://jetbrains.github.io/Exposed/data-types.html

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
        Array::class.qualifiedName to "arrayOf<{type}>()"
    )

    private fun kotlinTextExtensionMethod(methodName: String): MemberName {
        return MemberName("kotlin.text", methodName, isExtension = true)
    }

    private val parseMethods = mutableMapOf(
        String::class.qualifiedName to null,
        Int::class.qualifiedName to kotlinTextExtensionMethod("toInt"),
        Short::class.qualifiedName to kotlinTextExtensionMethod("toShort"),
        Long::class.qualifiedName to kotlinTextExtensionMethod("toLong"),
        Float::class.qualifiedName to kotlinTextExtensionMethod("toFloat"),
        Double::class.qualifiedName to kotlinTextExtensionMethod("toDouble"),
        BigDecimal::class.qualifiedName to kotlinTextExtensionMethod("toDouble"),
        Char::class.qualifiedName to kotlinTextExtensionMethod("first"),
        ByteArray::class.qualifiedName to kotlinTextExtensionMethod("toByteArray"),
        UUID::class.qualifiedName to MemberName("com.labijie.infra.orm", "toUUID", isExtension = true),
        Boolean::class.qualifiedName to kotlinTextExtensionMethod("toBoolean"),
        Byte::class.qualifiedName to kotlinTextExtensionMethod("toByte"),
    )


    fun getParseMethod(type: KSType): MemberName? {
        val typeName = type.declaration.qualifiedName!!.asString()
        val name = parseMethods[typeName]
        return name;
    }

    private fun getFullClassNameForNested(declaration: KSDeclaration): String {
        val list = mutableListOf<String>()
        var d: KSDeclaration? = declaration
        list.add(declaration.simpleName.getShortName())
        while (d?.parentDeclaration != null) {
           val name = (d.parentDeclaration?.simpleName?.getShortName()).orEmpty()
            list.add(0, name)
            d = d.parentDeclaration
        }

        return StringBuilder().apply {
            list.forEach {
                this.append("${it}.")
            }
        }.toString()
    }

    fun getValue(type: KSType): String {
        if(type.isEnum()){
            val enumDeclaration = type.declaration as KSClassDeclaration
            val firstEnumEntry = enumDeclaration.declarations.filter { it is KSClassDeclaration }.firstOrNull() ?: throw java.lang.IllegalArgumentException("enum type '${type.declaration.simpleName.asString()}' value missed")
            val filedName = firstEnumEntry.simpleName.asString()
            val className = getFullClassNameForNested(type.declaration)
            return "${className}${filedName}"
        }

        if(type.isJavaType<List<*>>()) {
            return "listOf()"
        }

        if(type.isJavaType<LocalDateTime>()) {
            return "LocalDateTime.of(0, 1, 1, 0, 0,0, 0)"
        }

        if(type.isJavaType<LocalDate>()) {
            return "LocalDate.of(0, 1, 1)"
        }

        if(type.isJavaType<Duration>()) {
            return "Duration.ZERO"
        }

        if(type.isJavaType<LocalTime>()) {
            return "LocalTime.MIN"
        }

        if(type.isJavaType<Instant>()) {
            return "Instant.EPOCH"
        }

        val typeName = type.declaration.qualifiedName!!.asString()


        return values[typeName] ?: throw IllegalArgumentException("'${typeName}' has not a default value currently")
    }

    fun getEnumValue(type: KSType): String {
        return values[type.declaration.qualifiedName!!.asString()] ?: throw IllegalArgumentException("'${type.declaration.qualifiedName!!.asString()}' has not a default value currently")
    }
}