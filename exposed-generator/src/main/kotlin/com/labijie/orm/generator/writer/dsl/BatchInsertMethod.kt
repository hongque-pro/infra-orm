package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.ResultRow

object BatchInsertMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {
        return FunSpec.builder("batchInsert")
            .receiver(context.base.tableClass)
            .addParameter("list", Iterable::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .returns(List::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName()))
            .beginControlFlow("val rows = %T.%M(list)", context.base.tableClass, getExposedSqlMember("batchInsert"))
            .addStatement("entry -> %N(this, entry)", context.applyInsertFunc)
            .endControlFlow()
            .addStatement("return rows")
            .build()
    }
}