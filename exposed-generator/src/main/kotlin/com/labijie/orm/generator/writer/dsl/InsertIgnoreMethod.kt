package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.statements.InsertStatement

object InsertIgnoreMethod: AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {

        val resultType = InsertStatement::class.asTypeName().parameterizedBy(Long::class.asTypeName())

        return FunSpec.builder("insertIgnore")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .returns(resultType)
            .beginControlFlow("return %M", getSqlExtendMethod("insertIgnore"))
            .addStatement("%N(it, ${context.entityParamName})", context.assignFunc)
            .endControlFlow()
            .build()
    }
}