package com.labijie.orm.generator.writer

import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.Serializable

object PojoWriter {

    fun write(context: GenerationContext) {
        context.table
        val file = FileSpec.builder(context.pojoPackageName, fileName = context.pojoClass.simpleName)
            .suppressRedundantVisibilityModifierWarning()
            .addType(
                TypeSpec.classBuilder(context.pojoClass)
                    .addComments("POJO for ${context.tableClass.simpleName}", context)
                    .addProperties(context.table.columns)
                    .executeIf(context.table.isOpen) {
                        addModifiers(KModifier.OPEN)
                    }
                    .executeIf(context.table.isSerializable) {
                        addAnnotation(ClassName("kotlinx.serialization", "Serializable"))
                    }
                    .build()
            )
            .build()

        file.writeTo(context.options.getSourceFolder(context.table))
    }

    private fun TypeSpec.Builder.executeIf(condition: Boolean, execution: (TypeSpec.Builder.()->Unit)): TypeSpec.Builder {
        if(condition) {
            execution.invoke(this)
        }
        return this
    }

    private fun TypeSpec.Builder.addProperties(columns: Collection<ColumnMetadata>): TypeSpec.Builder {
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