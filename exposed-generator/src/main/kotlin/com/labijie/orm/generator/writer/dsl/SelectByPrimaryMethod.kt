package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

@KotlinPoetKspPreview
object SelectByPrimaryMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {
        if(!context.base.table.hasPrimaryKey()){
            return getNoneMethod()
        }

        val block = buildPrimaryKeyWhere(context)

        return FunSpec.builder("selectByPrimaryKey")
            .receiver(context.base.tableClass)
            .apply {
                context.base.table.primaryKeys.forEach {
                    addParameter(it.name, it.type.toClassName())
                }
            }
            .returns(context.base.pojoClass.copy(nullable = true))
            .beginControlFlow("val query = %T.%M", context.base.tableClass, getExposedSqlMember("select"))
            .addCode(block)
            .endControlFlow()
            .addCode("return query.firstOrNull()?.%N()", context.rowMapFunc)
            .build()
    }
}