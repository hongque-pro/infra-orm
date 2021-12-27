package com.labijie.orm.generator.writer.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.exposed.sql.*
import java.util.*
import kotlin.reflect.full.companionObject

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/27
 * @Description:
 */
object SelectMethod : AbstractDSLMethodBuilder() {
    private fun buildSelectMany(context: DSLCodeContext): FunSpec {
        val whereParam = createQueryExpressionParameter()

        return FunSpec.builder("selectMany")
            .receiver(context.base.tableClass)
            .addParameter(whereParam)
            .returns(List::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .addStatement("val query = %T.%M()", context.base.tableClass, getExposedSqlMember("selectAll"))
            .addStatement("%N.invoke(query)", whereParam)
            .addStatement("return query.%N()", context.rowListMapFunc)
            .build()
    }

    private fun buildSelectOne(context: DSLCodeContext): FunSpec {
        val whereParam = createQueryExpressionParameter()

        return FunSpec.builder("selectOne")
            .receiver(context.base.tableClass)
            .addParameter(whereParam)
            .returns(context.base.pojoClass.copy(nullable = true))
            .addStatement("val query = %T.%M()", context.base.tableClass, getExposedSqlMember("selectAll"))
            .addStatement("%N.invoke(query)", whereParam)
            .addStatement("return query.firstOrNull()?.%N()", context.rowMapFunc)
            .build()
    }

    private fun createQueryExpressionParameter(
        name: String = "where",
        nullable: Boolean = false,
        defaultValue: String? = null
    ): ParameterSpec {
        val rec = Query::class.asTypeName()
        val where = LambdaTypeName.get(rec, returnType = Unit::class.asTypeName()).let {
            if (nullable) {
                it.copy(nullable = true)
            } else {
                it
            }
        }

        return ParameterSpec.builder("where", where)
            .apply {
                if (!defaultValue.isNullOrBlank()) {
                    this.defaultValue(defaultValue)
                }
            }
            .build()
    }

    private fun stringToNumberMethod(context: DSLCodeContext): Pair<Boolean, MemberName?> {
        if (context.base.table.primaryKeys.size != 1) {
            return Pair(false, null)
        }

        val primary = context.base.table.primaryKeys.first()

        val member = when (primary.rawType.declaration.qualifiedName?.asString()) {
            String::class.qualifiedName -> null
            Int::class.qualifiedName -> MemberName("kotlin.text", "toInt", isExtension = true)
            Long::class.qualifiedName -> MemberName("kotlin.text", "toLong", isExtension = true)
            Float::class.qualifiedName -> MemberName("kotlin.text", "toFloat", isExtension = true)
            Double::class.qualifiedName -> MemberName("kotlin.text", "toDouble", isExtension = true)
            UUID::class.qualifiedName -> MemberName("com.labijie.infra.orm", "ToUUID", isExtension = true)
            else -> return Pair(false, null)
        }
        return Pair(true, member)
    }

    private fun buildSelectForward(context: DSLCodeContext): FunSpec? {
        val (valid, convertStringMethod) = stringToNumberMethod(context)
        if (!valid) {
            return null
        }

        val offsetListType = OffsetList::class.companionObject!!.asTypeName()
        val encodeTokenMethod = MemberName(offsetListType, "encodeToken")
        val decodeTokenMethod = MemberName(offsetListType, "decodeToken")

        val t = TypeVariableName("T")
        val typeVar = TypeVariableName("T", Comparable::class.asClassName().parameterizedBy(t))

        val whereParam = createQueryExpressionParameter(nullable = true, defaultValue = "null")

        val pageSize = ParameterSpec.builder("pageSize", Int::class)
            .defaultValue("50")
            .build()

        val forwardToken = ParameterSpec.builder("forwardToken", String::class.asTypeName().copy(nullable = true))
            .defaultValue("null")
            .build()

        val sortColumn = ParameterSpec.builder("sortColumn", Column::class.asTypeName().parameterizedBy(typeVar))
            .build()


        val order = ParameterSpec.builder("order", SortOrder::class)
            .defaultValue("%T.${SortOrder.DESC.name}", SortOrder::class.asTypeName()).build()

        val primaryKeyPropertyName = context.base.table.primaryKeys.first().name
        val primaryKey = MemberName(context.base.tableClass, primaryKeyPropertyName)

        return FunSpec.builder("selectForward")
            .receiver(context.base.tableClass)
            .addTypeVariable(typeVar)
            .addParameter(sortColumn)
            .addParameter(forwardToken)
            .addParameter(order)
            .addParameter(pageSize)
            .addParameter(whereParam)
            .returns(OffsetList::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .beginControlFlow("if(pageSize < 1)")
            .addStatement("return %T()", OffsetList::class)
            .endControlFlow()
            .addStatement(
                "val kp = %N?.let { %M(it) }",
                forwardToken,
                decodeTokenMethod
            )
            .apply {
                if (convertStringMethod != null) {
                    addStatement("val offsetKey = kp?.first?.%M()", convertStringMethod)
                    addStatement("val excludeKeys = kp?.second?.map { it.%M() }", convertStringMethod)
                } else {
                    addStatement("val offsetKey = kp?.first")
                    addStatement("val excludeKeys = kp?.second")
                }
            }
            .addStatement("val query = %T.%M()", context.base.tableClass, getExposedSqlMember("selectAll"))
            .beginControlFlow("offsetKey?.%N", kotlinLetMethod)
            //when
            .beginControlFlow("when(order)")
            .addStatement(
                "%T.DESC, %T.DESC_NULLS_FIRST, %T.DESC_NULLS_LAST->",
                SortOrder::class,
                SortOrder::class,
                SortOrder::class
            )
            .addStatement(
                "query.%M { %N lessEq it }",
                getExposedSqlMember("andWhere"),
                sortColumn
            )
            .addStatement(
                "else-> query.%M { %N greaterEq it }",
                getExposedSqlMember("andWhere"),
                sortColumn
            )
            .endControlFlow()
            //end when
            .endControlFlow()

            .beginControlFlow("excludeKeys?.let")
            //if
            .beginControlFlow(
                "if(it.%M())",
                MemberName("kotlin.collections", "isNotEmpty", isExtension = true)
            )
            .addStatement(
                "query.%M { %M notInList it }",
                getExposedSqlMember("andWhere"),
                primaryKey
            )
            .endControlFlow()
            //end if
            .endControlFlow()

            .addStatement("%N?.invoke(query)", whereParam)

            .beginControlFlow("val sorted = if(%N != %M)", sortColumn, primaryKey)
            .addStatement("query.orderBy(Pair(%N, order), Pair(%M,order))", sortColumn, primaryKey)
            .endControlFlow()
            .beginControlFlow("else")
            .addStatement("query.orderBy(%M, order)", primaryKey)
            .endControlFlow()

            .addStatement("val list = sorted.limit(pageSize).%N()", context.rowListMapFunc)

            .addStatement(
                "val token = if(list.size < pageSize) null else %M(list, { %N(%N) }, %T::%N)",
                encodeTokenMethod,
                context.getColumnValueFunc,
                sortColumn,
                context.base.pojoClass,
                primaryKey
            )
            .addStatement("return %T(list, token)", OffsetList::class.asTypeName())
            .build()
    }

    private fun buildSelectForwardByPrimaryKey(context: DSLCodeContext, forwardFun: FunSpec): FunSpec {
        val forwardToken = ParameterSpec.builder("forwardToken", String::class.asTypeName().copy(nullable = true))
            .defaultValue("null")
            .build()

        val order = ParameterSpec.builder("order", SortOrder::class)
            .defaultValue("%T.${SortOrder.DESC.name}", SortOrder::class.asTypeName())
            .build()

        val pageSize = ParameterSpec.builder("pageSize", Int::class)
            .defaultValue("50")
            .build()

        val whereParam = createQueryExpressionParameter(nullable = true, defaultValue = "null")

        val primaryKey = context.base.table.primaryKeys.first()

        return FunSpec.builder("selectForwardByPrimaryKey")
            .receiver(context.base.tableClass)
            .addParameter(forwardToken)
            .addParameter(order)
            .addParameter(pageSize)
            .addParameter(whereParam)
            .addStatement(
                "return this.%N(${context.base.table.className}.${primaryKey.name}, %N, %N, %N, %N)",
                forwardFun,
                forwardToken,
                order,
                pageSize,
                whereParam
            )
            .build()
    }

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        return buildSelectForward(context)?.let {
            listOf(
                buildSelectMany(context),
                buildSelectOne(context),
                it,
                buildSelectForwardByPrimaryKey(context, it)
            )
        } ?: listOf(
            buildSelectMany(context),
            buildSelectOne(context),
        )
    }
}