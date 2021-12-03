package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

object ApplyExtensionMethod : AbstractDSLMethodBuilder() {

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        return listOf(
            buildApplyExtension(context.applyInsertFunc, context),
            buildApplyExtension(context.applyUpdateFunc, context)
        )
    }

    private fun buildApplyExtension(
        applyMethod: FunSpec,
        context: DSLCodeContext
    ): FunSpec {
        return FunSpec.builder("apply")
            .receiver(applyMethod.parameters[0].type)
            .addParameter(context.entityParamName, applyMethod.parameters[1].type)
            .addStatement("return %N(this, ${context.entityParamName})", applyMethod)
            .build()
    }


}