/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.parameterizedWildcard
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpsertStatement


object UpsertMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {

//        onUpdate: List<Pair<Column<*>, Expression<*>>>? = null,
//        onUpdateExclude: List<Column<*>>? = null,
//        where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,

        val colType = Column::class.asTypeName().parameterizedWildcard()
        val expressionType = Expression::class.asTypeName().parameterizedWildcard()

        val pairType = Pair::class.asTypeName().parameterizedBy(colType, expressionType)
        val onUpdate = ParameterSpec.builder("onUpdate", List::class.asTypeName().parameterizedBy(pairType).copy(nullable = true))
            .defaultValue("null")
            .build()

        val onUpdateExclude = ParameterSpec.builder("onUpdateExclude", List::class.asTypeName().parameterizedBy(colType).copy(nullable = true))
            .defaultValue("null")
            .build()

        val rec = SqlExpressionBuilder::class.asTypeName()
        val whereType = LambdaTypeName.get(rec, returnType = Op::class.parameterizedBy(Boolean::class)).copy(nullable = true)

        val where = ParameterSpec.builder("where", whereType)
            .defaultValue("null")
            .build()

        val resultType = UpsertStatement::class.asTypeName().parameterizedBy(Long::class.asTypeName())
        return FunSpec.builder("upsert")
            .receiver(context.base.tableClass)
            .addParameter(context.entityParamName, context.base.pojoClass)
            .addParameter(onUpdate)
            .addParameter(onUpdateExclude)
            .addParameter(where)
            .returns(resultType)
            .beginControlFlow("return %M(where = where, onUpdate = onUpdate, onUpdateExclude = onUpdateExclude)", getSqlExtendMethod("upsert"))
            .addStatement("%N(it, ${context.entityParamName})", context.assignFunc)
            .endControlFlow()
            .build()
    }
}