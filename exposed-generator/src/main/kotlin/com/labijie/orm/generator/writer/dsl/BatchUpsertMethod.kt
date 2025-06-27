/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.orm.generator.writer.dsl

import com.labijie.orm.generator.parameterizedWildcard
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.statements.UpsertBuilder
import org.jetbrains.exposed.sql.statements.UpsertStatement


object BatchUpsertMethod : AbstractDSLMethodBuilder() {
    override fun build(context: DSLCodeContext): FunSpec {

//        public fun ShopTable.batchUpsert(
//            raw: Iterable<Shop>,
//            onUpdateExclude: List<Column<*>>? = null,
//            onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)?? = null,
//            `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
//            shouldReturnGeneratedValues: Boolean = false,
//        ): List<Shop>  {
//
//            val rows =  this.batchUpsert(
//                data = raw,
//                keys = arrayOf(id, name),
//                onUpdate = onUpdate,
//                onUpdateExclude =  onUpdateExclude,
//                where =  where,
//                shouldReturnGeneratedValues = shouldReturnGeneratedValues) {
//                {
//                    data: Shop->
//                    assign(this, data)
//                }
//            }
//
//            return rows.map { it.toShop() }
//        }

        val colType = Column::class.asTypeName().parameterizedWildcard()
        val expressionType = Expression::class.asTypeName().parameterizedWildcard()

        //(UpsertBuilder.(UpdateStatement) -> Unit)? = null,
        val onUpdateRec = UpsertBuilder::class.asTypeName()
        val onUpdateParam = UpdateStatement::class.asTypeName()
        val onUpdateLambda =
            LambdaTypeName.get(onUpdateRec, parameters = arrayOf(onUpdateParam), returnType = Unit::class.asTypeName())
                .copy(nullable = true)
        val onUpdate = ParameterSpec.builder("onUpdate", onUpdateLambda)
            .defaultValue("null")
            .build()

        val onUpdateExclude = ParameterSpec.builder(
            "onUpdateExclude",
            List::class.asTypeName().parameterizedBy(colType).copy(nullable = true)
        )
            .defaultValue("null")
            .build()

        val rec = SqlExpressionBuilder::class.asTypeName()
        val whereType =
            LambdaTypeName.get(rec, returnType = Op::class.parameterizedBy(Boolean::class)).copy(nullable = true)

        val where = ParameterSpec.builder("where", whereType)
            .defaultValue("null")
            .build()

        val shouldReturnGeneratedValues = ParameterSpec.builder("shouldReturnGeneratedValues", Boolean::class)
            .defaultValue("false")
            .build()

        val resultType = List::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName())


        val keysBlock = CodeBlock.builder()
            .add("%N(%L)", kotlinArrayOfMethod, context.base.table.primaryKeys.joinToString(", ") { it.name })
            .build()

        return FunSpec.builder("batchUpsert")
            .receiver(context.base.tableClass)
            .addParameter("list", Iterable::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .addParameter(onUpdateExclude)
            .addParameter(onUpdate)
            .addParameter(shouldReturnGeneratedValues)
            .addParameter(where)
            .returns(resultType)
            .beginControlFlow(
                "val rows =  %M(data = list, keys = %L, onUpdate = onUpdate, onUpdateExclude = onUpdateExclude, where = where, shouldReturnGeneratedValues = shouldReturnGeneratedValues)",
                getSqlExtendMethod("batchUpsert"),
                keysBlock.toString()
            )
            .addStatement("data: %T-> %N(this, data)", context.base.pojoClass, context.assignFunc)
            .endControlFlow()
            .addStatement("return rows")
            .build()
    }
}