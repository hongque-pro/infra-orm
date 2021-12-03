package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.labijie.orm.generator.writer.DSLWriter
import com.labijie.orm.generator.writer.IDSLMethodBuilder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

object UpdateMethod : AbstractDSLMethodBuilder() {

    private fun buildBaseUpdateMethod(context: DSLCodeContext): FunSpec {
        val rec = SqlExpressionBuilder::class.asTypeName()
        val where = LambdaTypeName.get(rec, returnType = Op::class.parameterizedBy(Boolean::class))

        val whereParam = ParameterSpec.builder("where", where)
            .build()

        val limitParam = ParameterSpec.builder("limit", Int::class.asTypeName().copy(nullable = true))
            .defaultValue("null")
            .build()


        return FunSpec.builder("update")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .addParameter(limitParam)
            .addParameter(whereParam)
            .returns(Int::class)
            .beginControlFlow("return %T.%M(where, limit)", context.base.tableClass, getExposedSqlMember("update"))
            .addStatement("%N(it, ${context.entityParamName})", context.applyUpdateFunc)
            .endControlFlow()
            .build()
    }

    private fun buildUpdateByIdMethod(context: DSLCodeContext, baseUpdateMethod: FunSpec): FunSpec {
        return FunSpec.builder("update")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .returns(Int::class)
            .beginControlFlow("return %T.%N(${context.entityParamName})", context.base.tableClass, baseUpdateMethod)
            .addCode(buildPrimaryKeyWhere(context))
            .endControlFlow()
            .build()
    }

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        val baseUpdate = buildBaseUpdateMethod(context)
        if(!context.base.table.hasPrimaryKey()){
            return listOf(baseUpdate)
        }
        val updateById = buildUpdateByIdMethod(context, baseUpdate)

        return listOf(baseUpdate, updateById)
    }

}