package com.labijie.orm.generator.writer

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Nullability
import com.labijie.infra.orm.serialization.LocalDateTimeSerializer
import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

object PojoWriter {

    private data class SpecialTypeMapping(
        val javaClass: Class<*>,
        val wrapperName: String
    )

    private val specialTypeMappings = listOf(
        SpecialTypeMapping(BigDecimal::class.java, "OrmBigDecimal"),
        SpecialTypeMapping(UUID::class.java, "OrmUUID"),
        SpecialTypeMapping(Duration::class.java, "OrmDuration"),
        SpecialTypeMapping(LocalDateTime::class.java, "OrmLocalDateTime"),
        SpecialTypeMapping(LocalDate::class.java, "OrmLocalDate"),
        SpecialTypeMapping(LocalTime::class.java, "OrmLocalTime"),
        SpecialTypeMapping(Instant::class.java, "OrmInstant"),
    )


    fun write(context: GenerationContext) {

        val fileBuilder = FileSpec.builder(context.pojoPackageName, fileName = context.pojoClass.simpleName)
        val file = fileBuilder
            .suppressRedundantVisibilityModifierWarning()
            .addType(
                TypeSpec.classBuilder(context.pojoClass)
                    .addComments("POJO for ${context.tableClass.simpleName}", context)
                    .addProperties(fileBuilder, context)
                    .executeIf(context.table.isOpen) {
                        addModifiers(KModifier.OPEN)
                    }
                    .executeIf(context.tableSerializable) {
                        addAnnotation(ClassName("kotlinx.serialization", "Serializable"))
                    }
                    .executeIf(context.table.superTypes.isNotEmpty()) {
                        context.table.superTypes.forEach {
                            if(it.isInterface) {
                                addSuperinterface(it.type.toTypeName())
                            }else {
                                superclass(it.type.toTypeName())
                            }
                        }
                    }
                    .build()
            )
            .build()

        file.writeTo(context.options.getFolder(context.table).pojoSourceDir)
    }

    private fun TypeSpec.Builder.executeIf(
        condition: Boolean,
        execution: (TypeSpec.Builder.() -> Unit)
    ): TypeSpec.Builder {
        if (condition) {
            execution.invoke(this)
        }
        return this
    }

    private fun getColumnType(file: FileSpec.Builder, column: ColumnMetadata, serializable: Boolean): TypeName {
        if (serializable) {
            val matched = specialTypeMappings.find {
                column.type.declaration.qualifiedName?.asString() == it.javaClass.name
            }

            if (matched != null) {
                if (!column.isNullableColumn) {
                    file.addImport(matched.javaClass.packageName, matched.javaClass.simpleName)
                }

                return ClassName("com.labijie.infra.orm.serialization", matched.wrapperName)
                    .copy(nullable = column.isNullableColumn)
            }
        }

        return column.type.toTypeName().copy(nullable = column.isNullableColumn)
    }

    private fun TypeSpec.Builder.addProperties(file: FileSpec.Builder, context: GenerationContext): TypeSpec.Builder {

        context.table.columns.forEach { column ->

            var property: KSPropertyDeclaration? = null
            for(superType in context.table.superTypes) {
                property = superType.getPublicProperty(column.name)
                if(property != null) {
                    break
                }
            }


            val isAbstract = property?.isAbstract() == true

            val isNullable = property?.type?.resolve()?.nullability == Nullability.NULLABLE || column.isNullableColumn

            if(property == null || isAbstract) {
                addProperty(
                    PropertySpec.builder(
                        column.name,
                        getColumnType(file, column, context.tableSerializable)
                    ).mutable().let { self ->
                        if (isAbstract) self.addModifiers(
                            KModifier.PUBLIC,
                            KModifier.OVERRIDE
                        ) else self.addModifiers(KModifier.PUBLIC)
                    }
                        .initializer(if (isNullable) "null" else DefaultValues.getValue(propertyName ="${context.table.className}.${column.name}"  ,column.type))
                        .build()
                )
            }
        }

        return this
    }

}