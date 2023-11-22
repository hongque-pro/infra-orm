package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.TableKind
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import org.jetbrains.exposed.dao.id.EntityID

object InsertAndGetIdMethod : AbstractDSLMethodBuilder() {


    override fun build(context: DSLCodeContext): FunSpec? {
        if (context.base.table.kind != TableKind.ExposedIdTable) {
            return null
        }
        val entityIdType = context.base.table.primaryKeys.first().type.toClassName()

        val resultType = EntityID::class.asTypeName().parameterizedBy(entityIdType)
        return FunSpec.builder("insertAndGetId")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .returns(resultType)
            .beginControlFlow("return %T.%M", context.base.tableClass, getExposedSqlMember("insertAndGetId"))
            .addStatement("%N(it, ${context.entityParamName})", context.assignFunc)
            .endControlFlow()
            .build()
    }
}