package com.labijie.orm.generator.writer

import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName

@KotlinPoetKspPreview
object PojoWriter {

    fun write(context: GenerationContext) {

        val file = FileSpec.builder(context.pojoPackageName, fileName = context.pojoClass.simpleName)
            .addType(
                TypeSpec.classBuilder(context.pojoClass)
                    .addModifiers(KModifier.OPEN)
                    .addProperty(context.table.columns)
                    .build()
            )
            .build()

        file.writeTo(context.options.getSourceFolder(context.table))
    }

    private fun TypeSpec.Builder.addProperty(columns: Collection<ColumnMetadata>): TypeSpec.Builder {
        columns.forEach {
            addProperty(
                PropertySpec.builder(
                    it.name,
                    it.type.toTypeName().copy(nullable = it.isNull)
                ).mutable()
                    .initializer(if(it.isNull) "null" else DefaultValues.getValue(it.type))
                .build()
            )
        }
        return this
    }

}