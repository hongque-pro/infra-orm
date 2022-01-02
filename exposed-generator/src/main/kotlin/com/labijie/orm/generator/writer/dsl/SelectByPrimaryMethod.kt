package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

@KotlinPoetKspPreview
object SelectByPrimaryMethod : AbstractDSLMethodBuilder() {
    private fun buildSelectByPrimaryKey(context: DSLCodeContext): FunSpec {

        val block = buildPrimaryKeyWhere(context)

        return FunSpec.builder("selectByPrimaryKey")
            .receiver(context.base.tableClass)
            .apply {
                context.base.table.primaryKeys.forEach {
                    addParameter(it.name, it.type.toClassName())
                }
            }
            .addParameter(columnSelectiveParameter)
            .returns(context.base.pojoClass.copy(nullable = true))
            .beginControlFlow(
                "val query = %T.%N(*%N).%N",
                context.base.tableClass,
                context.selectSliceFunc,
                columnSelectiveParameter,
                exposedAndWhere
            )
            .addCode(block)
            .endControlFlow()
            .addCode("return query.firstOrNull()?.%N(*%N)", context.rowMapFunc, columnSelectiveParameter)
            .build()
    }

    private fun buildSelectByPrimaryKeys(context: DSLCodeContext): FunSpec {

        val primaryKey = context.base.table.primaryKeys.first()

        return FunSpec.builder("selectByPrimaryKeys")
            .receiver(context.base.tableClass)
            .addParameter(
                "ids",
                Iterable::class.asTypeName().parameterizedBy(primaryKey.type.toClassName())
            )
            .addParameter(columnSelectiveParameter)
            .returns(List::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .beginControlFlow(
                "val query = %T.%N(*%N).%N",
                context.base.tableClass,
                context.selectSliceFunc,
                columnSelectiveParameter,
                exposedAndWhere
            )
            .addStatement("%L inList ids", "${context.base.table.className}.${primaryKey.name}")
            .endControlFlow()
            .addCode("return query.%N(*%N)", context.rowListMapFunc, columnSelectiveParameter)
            .build()
    }

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        if(!context.base.table.hasPrimaryKey()){
            return listOf()
        }
        if(context.isSinglePrimaryKey()){
            return listOf(
                buildSelectByPrimaryKey(context),
                buildSelectByPrimaryKeys(context)
            )
        }
        return listOf(
            buildSelectByPrimaryKey(context),
        )
    }
}