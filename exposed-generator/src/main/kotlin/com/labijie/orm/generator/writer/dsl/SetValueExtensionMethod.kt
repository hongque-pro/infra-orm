package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec

object SetValueExtensionMethod : AbstractDSLMethodBuilder() {

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        return listOf(
            buildSetValueExtension(context.assignFunc, context),
            buildSetValueSelectiveExtension(context.assignFunc, context)
        )
    }

    private fun buildSetValueExtension(
        applyMethod: FunSpec,
        context: DSLCodeContext
    ): FunSpec {
        val ignore = columnSelectiveParameter.toBuilder("ignore").build()

        return FunSpec.builder("setValue")
            .receiver(applyMethod.parameters[0].type)
            .addParameter(context.entityParamName, applyMethod.parameters[1].type)
            .addParameter(ignore)
            .addStatement("return %N(this, ${context.entityParamName}, ignore = %N)", applyMethod, ignore)
            .build()
    }

    private fun buildSetValueSelectiveExtension(
        applyMethod: FunSpec,
        context: DSLCodeContext
    ): FunSpec {

        val selective = columnSelectiveParameter

        return FunSpec.builder("setValueSelective")
            .receiver(applyMethod.parameters[0].type)
            .addParameter(context.entityParamName, applyMethod.parameters[1].type)
            .addParameter(selective)
            .addStatement("return %N(this, ${context.entityParamName}, selective = %N)", applyMethod, selective)
            .build()
    }


}