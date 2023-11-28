package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.parameterizedWildcard
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.exposed.sql.Column
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


        val colType = Column::class.asTypeName().parameterizedWildcard()
        val columnArray = Array::class.asClassName()
            .parameterizedBy(WildcardTypeName.producerOf(colType))
            .copy(nullable = true)

        val ignore = ParameterSpec.builder("ignore", columnArray)
            .defaultValue("null")
            .build()

        val selective = ParameterSpec.builder("selective", columnArray)
            .defaultValue("null")
            .build()

        return FunSpec.builder("update")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .addParameter(selective)
            .addParameter(ignore)
            .addParameter(limitParam)
            .addParameter(whereParam)
            .returns(Int::class)
            .beginControlFlow("return %M(%N, limit)", getExposedSqlMember("update"), whereParam)
            .addStatement("val ignoreColumns = ${ignore.name} ?: arrayOf()")
            .addStatement("%N(it, ${context.entityParamName}, selective = %N, *ignoreColumns)", context.assignFunc, selective)
            .endControlFlow()
            .build()
    }

    private fun buildUpdateByIdMethod(context: DSLCodeContext, baseUpdateMethod: FunSpec): FunSpec {

        val selective = ParameterSpec.builder("selective", Column::class.asTypeName().parameterizedWildcard())
            .apply {
                modifiers.add(KModifier.VARARG)
            }
            .build()

        val primaryKeys = context.base.table.primaryKeys.joinToString(", ") { it.name }
        return FunSpec.builder("updateByPrimaryKey")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .addParameter(selective)
            .returns(Int::class)
            .beginControlFlow("return %N(${context.entityParamName}, selective = %N, ignore = arrayOf(${primaryKeys}))",
                baseUpdateMethod,
                selective)
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