/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.statements.ReplaceStatement
import org.jetbrains.exposed.sql.statements.UpsertStatement


object ReplaceMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {
        val resultType = ReplaceStatement::class.asTypeName().parameterizedBy(Long::class.asTypeName())
        return FunSpec.builder("replace")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .returns(resultType)
            .beginControlFlow("return %M", getSqlExtendMethod("replace"))
            .addStatement("%N(it, ${context.entityParamName})", context.assignFunc)
            .endControlFlow()
            .build()
    }
}