package com.labijie.orm.generator.writer.dsl

import com.google.devtools.ksp.processing.KSBuiltIns
import com.labijie.infra.orm.OffsetList
import com.labijie.orm.generator.ColumnMetadata
import com.labijie.orm.generator.DefaultValues
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder
import com.labijie.orm.generator.writer.DSLCodeContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
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
            .addParameter(columnSelectiveParameter)
            .addParameter(whereParam)
            .returns(List::class.asTypeName().parameterizedBy(context.base.pojoClass))
            .addStatement(
                "val query = %N(*%N)",
                context.selectSliceFunc,
                columnSelectiveParameter
            )
            .addStatement("%N.invoke(query)", whereParam)
            .addStatement("return query.%N(*%N)", context.rowListMapFunc, columnSelectiveParameter)
            .build()
    }

    private fun buildSelectOne(context: DSLCodeContext): FunSpec {
        val whereParam = createQueryExpressionParameter()

        return FunSpec.builder("selectOne")
            .receiver(context.base.tableClass)
            .addParameter(columnSelectiveParameter)
            .addParameter(whereParam)
            .returns(context.base.pojoClass.copy(nullable = true))
            .addStatement(
                "val query = %N(*%N)",
                context.selectSliceFunc,
                columnSelectiveParameter
            )
            .addStatement("%N.invoke(query)", whereParam)
            .addStatement("return query.firstOrNull()?.%N(*%N)", context.rowMapFunc, columnSelectiveParameter)
            .build()
    }


    private fun buildSelectForward(context: DSLCodeContext, selectForwardByPrimary: FunSpec): FunSpec {

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

        val primary = context.base.table.primaryKeys.first()
        val primaryKey = MemberName(context.base.tableClass, primary.name)

        val parseMethod = DefaultValues.getParseMethod(primary.type)

        return FunSpec.builder("selectForward")
            .receiver(context.base.tableClass)
            .addTypeVariable(typeVar)
            .addParameter(sortColumn)
            .addParameter(forwardToken)
            .addParameter(order)
            .addParameter(pageSize)
            .addParameter(columnSelectiveCollectionParameter)
            .addParameter(whereParam)
            .returns(OffsetList::class.asTypeName().parameterizedBy(context.base.pojoClass))

            .beginControlFlow("if(pageSize < 1)")
            .addStatement("return %T.empty()", OffsetList::class)
            .endControlFlow()

            .beginControlFlow("if(%N == %M)", sortColumn, primaryKey)
            .addStatement(
                "return this.%N(%N, %N, %N, %N, %N)",
                selectForwardByPrimary,
                forwardToken,
                order,
                pageSize,
                columnSelectiveCollectionParameter,
                whereParam,
            )
            .endControlFlow()

            .addStatement(
                "val kp = %N?.let { %M(it) }",
                forwardToken,
                decodeTokenMethod
            )
            .addStatement("val offsetKey = kp?.first")
            .apply {
                if (parseMethod != null) {
                    addStatement(
                        "val excludeKeys = kp?.second?.%M { it.%M() }",
                        kotlinCollectionExtensionMethod("map"),
                        parseMethod
                    )
                } else {
                    addStatement("val excludeKeys = kp?.second")
                }
            }
            .addStatement(
                "val query = %N(*%N.%N())",
                context.selectSliceFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray
            )
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
                kotlinCollectionExtensionMethod("isNotEmpty")
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

            .addStatement("val sorted = query.orderBy(Pair(%N, order), Pair(%M, order))", sortColumn, primaryKey)

            .addStatement(
                "val list = sorted.limit(pageSize).%N(*%N.%N())",
                context.rowListMapFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray
            )

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


    private fun buildSelectForwardByPrimaryKey(context: DSLCodeContext): FunSpec {
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
        val primaryColumn = context.base.table.primaryKeys.first()

        val primaryKeyPropertyName = primaryColumn.name
        val primaryKey = MemberName(context.base.tableClass, primaryKeyPropertyName)

        val parseMethod = DefaultValues.getParseMethod(primaryColumn.type)

//        if (parseMethod == null) {
//            context.base.logger.error(
//                "Primary type is not an supported type: " +
//                        "${context.base.table.className}:${primaryColumn.name}" +
//                        "type: ${primaryColumn.type.toClassName()}"
//            )
//        }

        return FunSpec.builder("selectForwardByPrimaryKey")
            .receiver(context.base.tableClass)
            .addParameter(forwardToken)
            .addParameter(order)
            .addParameter(pageSize)
            .addParameter(columnSelectiveCollectionParameter)
            .addParameter(whereParam)
            .returns(OffsetList::class.asTypeName().parameterizedBy(context.base.pojoClass))

            .beginControlFlow("if(pageSize < 1)")
            .addStatement("return %T.empty()", OffsetList::class)
            .endControlFlow()

            .addStatement(
                "val offsetKey = forwardToken?.%N { %T.getUrlDecoder().decode(it).toString(%T.UTF_8) }",
                kotlinLetMethod,
                Base64::class.java.asTypeName(),
                Charsets::class.asTypeName()
            )
            .addStatement(
                "val query = %N(*%N.%N())",
                context.selectSliceFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray
            )

            .beginControlFlow("offsetKey?.%N", kotlinLetMethod)
            //when
            .beginControlFlow("when(order)")
            .addStatement(
                "%T.DESC, %T.DESC_NULLS_FIRST, %T.DESC_NULLS_LAST->",
                SortOrder::class,
                SortOrder::class,
                SortOrder::class
            )
            .apply {
                if (parseMethod != null) {
                    addStatement(
                        "query.%N { %M less it.%M() }",
                        getExposedSqlMember("andWhere"),
                        primaryKey,
                        parseMethod
                    )
                    .addStatement(
                        "else-> query.%M { %N greater it.%M() }",
                        getExposedSqlMember("andWhere"),
                        primaryKey,
                        parseMethod
                    )
                } else {
                    addStatement(
                        "query.%N { %M less it }",
                        getExposedSqlMember("andWhere"),
                        primaryKey
                    )
                    .addStatement(
                        "else-> query.%M { %N greater it }",
                        getExposedSqlMember("andWhere"),
                        primaryKey
                    )
                }
            }

            .endControlFlow()
            //end when
            .endControlFlow()

            .addStatement("%N?.invoke(query)", whereParam)

            .addStatement("val sorted = query.orderBy(%M, order)", primaryKey)
            .addStatement(
                "val list = sorted.limit(pageSize).%N(*%N.%N())",
                context.rowListMapFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray
            )


            .beginControlFlow("val token = if(list.size >= pageSize)")
            .addStatement(
                "val lastId = list.%M().${primaryKeyPropertyName}.toString().%M(%T.UTF_8)",
                kotlinCollectionExtensionMethod("last"),
                kotlinTextExtensionMethod("toByteArray"),
                Charsets::class.asTypeName()
            )
            .addStatement("%T.getUrlEncoder().encodeToString(lastId)", Base64::class.java.asTypeName())
            .endControlFlow()
            .beginControlFlow("else")
            .addStatement("null")
            .endControlFlow()

            .addStatement("return %T(list, token)", OffsetList::class.asTypeName())

            .build()
    }

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        return if (context.base.table.primaryKeys.size == 1) {
            val selectForwardByPrimary = buildSelectForwardByPrimaryKey(context)

            listOf(
                buildSelectMany(context),
                buildSelectOne(context),
                selectForwardByPrimary,
                buildSelectForward(context, selectForwardByPrimary)
            )
        } else listOf(
            buildSelectMany(context),
            buildSelectOne(context),
        )
    }
}