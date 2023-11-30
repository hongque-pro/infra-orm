package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.parameterizedWildcard
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement

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
            .beginControlFlow("return %M(%N, limit)", getSqlExtendMethod("update"), whereParam)
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
            .addCode(buildPrimaryKeyWhere(context, context.entityParamName))
            .addStatement("")
            .endControlFlow()
            .build()
    }

    private fun buildUpdateByIdMethod2(context: DSLCodeContext): FunSpec {

        val params = context.base.table.primaryKeys.map {
            ParameterSpec.builder(it.name, it.type.toTypeName())
                .build()
        }

        /**
         * public fun Table.updateByPrimaryKey(id: Int, body: Table.(UpdateStatement) -> Unit): Int =
         *     IntIdTable.update({ Table.id.eq(id) }, body = body)
         *
         */

        val rec = context.base.tableClass
        val bodyLambda = LambdaTypeName.get(rec, UpdateStatement::class.asClassName(), returnType = Unit::class.asTypeName())

        val body = ParameterSpec.builder("builder", bodyLambda)
            .build()

        return FunSpec.builder("updateByPrimaryKey")
            .receiver(context.base.tableClass)
            .apply {
                params.forEach {
                    addParameter(it)
                }
            }
            .addParameter(body)
            .returns(Int::class)
            .addCode("return update({ ")
            .apply {
               addCode(buildPrimaryKeyWhere(context))
            }
            .addCode(" }")
            .addCode(", body = builder")
            .addCode(")")
            .build()
    }

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        val baseUpdate = buildBaseUpdateMethod(context)
        if(!context.base.table.hasPrimaryKey()){
            return listOf(baseUpdate)
        }
        val updateById = buildUpdateByIdMethod(context, baseUpdate)
        val updateById2 = buildUpdateByIdMethod2(context)

        return listOf(baseUpdate, updateById, updateById2)
    }

}