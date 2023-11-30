package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.ResultRow

object BatchInsertMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {
        val ignoreErrors = ParameterSpec.builder("ignoreErrors", Boolean::class)
            .defaultValue("false")
            .build()

        val shouldReturnGeneratedValues = ParameterSpec.builder("shouldReturnGeneratedValues", Boolean::class)
            .defaultValue("false")
            .build()

        return FunSpec.builder("batchInsert")
            .receiver(context.base.tableClass)
            .addParameter("list", Iterable::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .addParameter(ignoreErrors)
            .addParameter(shouldReturnGeneratedValues)
            .returns(List::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName()))
            .beginControlFlow("val rows = %M(list, ignoreErrors, shouldReturnGeneratedValues)", getSqlExtendMethod("batchInsert"))
            .addStatement("entry -> %N(this, entry)", context.assignFunc)
            .endControlFlow()
            .addStatement("return rows")
            .build()
    }
}