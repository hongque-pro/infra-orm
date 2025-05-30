package com.labijie.orm.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.labijie.infra.orm.ExposedConverter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName
import java.math.BigDecimal
import java.time.*
import java.util.*
import kotlin.reflect.KFunction

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

    private fun getConvertMethod(func: KFunction<*>) : MemberName {
        val className = ClassName(ExposedConverter::class.java.packageName, ExposedConverter::class.java.simpleName)
        return MemberName(enclosingClassName = className, simpleName = func.name)
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
        ByteArray::class.qualifiedName to  getConvertMethod(ExposedConverter::stringToByteArray),
        UUID::class.qualifiedName to getConvertMethod(ExposedConverter::stringToUUID),
        Boolean::class.qualifiedName to kotlinTextExtensionMethod("toBoolean"),
        Byte::class.qualifiedName to getConvertMethod(ExposedConverter::stringToByteArray),
        Instant::class.qualifiedName to getConvertMethod(ExposedConverter::stringToInstant),
        LocalDate::class.qualifiedName to getConvertMethod(ExposedConverter::stringToLocalDate),
        LocalTime::class.qualifiedName to getConvertMethod(ExposedConverter::stringToLocalTime),
        LocalDateTime::class.qualifiedName to getConvertMethod(ExposedConverter::stringToLocalDateTime),
        Duration::class.qualifiedName to getConvertMethod(ExposedConverter::stringToDuration),
    )

    fun MemberName.isConverterMethod(): Boolean {
        val validPackage = this.enclosingClassName?.packageName?.equals(ExposedConverter::class.java.packageName, true) ?: false
        val validClass = this.enclosingClassName?.simpleName?.equals(ExposedConverter::class.java.simpleName, true) ?: false

        return validPackage && validClass
    }

    private val toStringMethods = mutableMapOf(
        String::class.qualifiedName to null,
        Int::class.qualifiedName to MemberName(ClassName(Int::class.java.packageName, Int::class.java.simpleName),"toString"),
        Short::class.qualifiedName to MemberName(ClassName(Short::class.java.packageName, Int::class.java.simpleName),"toString"),
        Long::class.qualifiedName to MemberName(ClassName(Long::class.java.packageName, Int::class.java.simpleName),"toString"),
        Float::class.qualifiedName to MemberName(ClassName(Float::class.java.packageName, Int::class.java.simpleName),"toString"),
        Double::class.qualifiedName to MemberName(ClassName(Double::class.java.packageName, Int::class.java.simpleName),"toString"),
        BigDecimal::class.qualifiedName to MemberName(ClassName(BigDecimal::class.java.packageName, Int::class.java.simpleName),"toString"),
        Char::class.qualifiedName to MemberName(ClassName(Char::class.java.packageName, Int::class.java.simpleName),"toString"),
        ByteArray::class.qualifiedName to getConvertMethod(ExposedConverter::byteArrayToString),
        UUID::class.qualifiedName to MemberName(ClassName(UUID::class.java.packageName, Int::class.java.simpleName),"toString"),
        Boolean::class.qualifiedName to MemberName(ClassName(Boolean::class.java.packageName, Int::class.java.simpleName),"toString"),
        Byte::class.qualifiedName to MemberName(ClassName(Byte::class.java.packageName, Int::class.java.simpleName),"toString"),
        Instant::class.qualifiedName to getConvertMethod(ExposedConverter::instantToString),
        LocalDate::class.qualifiedName to MemberName(ClassName(LocalDate::class.java.packageName, Int::class.java.simpleName),"toString"),
        LocalTime::class.qualifiedName to MemberName(ClassName(LocalTime::class.java.packageName, Int::class.java.simpleName),"toString"),
        LocalDateTime::class.qualifiedName to MemberName(ClassName(LocalDateTime::class.java.packageName, Int::class.java.simpleName),"toString"),
        Duration::class.qualifiedName to getConvertMethod(ExposedConverter::durationToString),
    )


    fun getToStringMethod(type: KSType): MemberName? {
        val typeName = type.declaration.qualifiedName!!.asString()
        val name = toStringMethods[typeName]
        return name
    }

    fun getParseMethod(type: KSType): MemberName? {
        val typeName = type.declaration.qualifiedName!!.asString()
        val name = parseMethods[typeName]
        return name
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

    fun getValue(propertyName: String, type: KSType): String {
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


        return values[typeName] ?: throw IllegalArgumentException("'${typeName}' (class: ${type.toClassName()}) has not a default value currently at property '${propertyName}'")
    }

    fun getEnumValue(type: KSType): String {
        return values[type.declaration.qualifiedName!!.asString()] ?: throw IllegalArgumentException("'${type.declaration.qualifiedName!!.asString()}' has not a default value currently")
    }
}