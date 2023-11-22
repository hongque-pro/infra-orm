package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName

object DeleteByPrimaryKeyMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec? {
        if(!context.base.table.hasPrimaryKey()){
            return null
        }
        val block = buildPrimaryKeyWhere(context)

        return FunSpec.builder("deleteByPrimaryKey")
            .receiver(context.base.tableClass)
            .apply {
                context.base.table.primaryKeys.forEach {
                    addParameter(it.name, it.type.toClassName())
                }
            }
            .returns(Int::class)
            .beginControlFlow("return %T.%M", context.base.tableClass, getExposedSqlMember("deleteWhere"))
            .addCode(block)
            .endControlFlow()
            .build()
    }
}