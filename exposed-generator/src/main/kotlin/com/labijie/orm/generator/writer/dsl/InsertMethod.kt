package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.statements.InsertStatement

object InsertMethod: AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {
        val resultType = InsertStatement::class.asTypeName().parameterizedBy(Number::class.asTypeName())
        return FunSpec.builder("insert")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .returns(resultType)
            .beginControlFlow("return %M", getSqlExtendMethod("insert"))
            .addStatement("%N(it, ${context.entityParamName})", context.assignFunc)
            .endControlFlow()
            .build()
    }
}