package com.labijie.orm.generator.writer.dsl

import com.labijie.infra.orm.OffsetList
import com.labijie.orm.generator.DefaultValues
import com.labijie.orm.generator.DefaultValues.isConverterMethod
import com.labijie.orm.generator.generateParsedValueCodeBlock
import com.labijie.orm.generator.generateToStringCodeBlock
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

        val splitChars = ":::"

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
                "val sortColAndId = %N?.%N { if(it.isNotBlank()) %T.getUrlDecoder().decode(it).toString(Charsets.UTF_8) else null }",
                forwardToken,
                kotlinLetMethod,
                Base64::class.java.asTypeName()
            )
            .addStatement("val kp = sortColAndId?.%N(%S)", kotlinTextExtensionMethod("split"), splitChars)
            .addStatement("val offsetKey = if(!kp.%N()) %N(kp.%N(), %N) else null",
                kotlinCollectionExtensionMethod("isNullOrEmpty"),
                context.parseColumnValueFunc,
                kotlinCollectionExtensionMethod("first"),
                sortColumn)

            .addStatement("val lastId = if(kp != null && kp.size > 1 && kp[1].%N()) %N(kp[1], %N) else null",
                kotlinTextExtensionMethod("isNotBlank"),
                context.parseColumnValueFunc,
                primaryKey
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
            .addStatement(
                "query.%M { %N lessEq it }",
                andWhereMethod,
                sortColumn
            )
            .addStatement(
                "else-> query.%M { %N greaterEq it }",
                andWhereMethod,
                sortColumn
            )
            .endControlFlow()
            //end when
            .endControlFlow()

            .beginControlFlow("lastId?.let")

            //when
            .beginControlFlow("when(order)")
            .addStatement(
                "%T.DESC, %T.DESC_NULLS_FIRST, %T.DESC_NULLS_LAST->",
                SortOrder::class,
                SortOrder::class,
                SortOrder::class
            )
            .addStatement(
                "query.%M { %M less it }",
                andWhereMethod,
                primaryKey
            )
            .addStatement(
                "else-> query.%M { %M greater it }",
                andWhereMethod,
                primaryKey
            )
            .endControlFlow()
            //end if
            .endControlFlow()

            .addStatement("%N?.invoke(query)", whereParam)

            .addStatement("val sorted = query.orderBy(Pair(%N, order), Pair(%M, order))", sortColumn, primaryKey)

            .addStatement(
                "val list = sorted.limit(pageSize + 1).%N(*%N.%N()).%N()",
                context.rowListMapFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray,
                kotlinToMutableList
            )
            .addStatement("val dataCount = list.size")

            .beginControlFlow("val token = if(dataCount > pageSize)")
            .addStatement("list.%N()", kotlinRemoveLast)
            .addStatement("val idToEncode = list.last().%N(%M)", context.getColumnValueStringFunc, primaryKey)
            .addStatement("val sortKey = list.last().%N(%N)", context.getColumnValueStringFunc, sortColumn)
            .addStatement("val tokenValue = %P.toByteArray(Charsets.UTF_8)", "\${idToEncode}$splitChars\${sortKey}")
            .addStatement("%T.getUrlEncoder().encodeToString(tokenValue)", Base64::class.java.asTypeName(),)
            .endControlFlow()
            .addStatement("else null")

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

            .addStatement("val keyValue = %N(it, %N)", context.parseColumnValueFunc, primaryKey)

            //when
            .beginControlFlow("when(order)")
            .addStatement(
                "%T.DESC, %T.DESC_NULLS_FIRST, %T.DESC_NULLS_LAST->",
                SortOrder::class,
                SortOrder::class,
                SortOrder::class
            )
            .apply {
                addStatement(
                    "query.%M { %M less keyValue }",
                    andWhereMethod,
                    primaryKey
                )
                .addStatement(
                    "else-> query.%M { %N greater keyValue }",
                    andWhereMethod,
                    primaryKey
                )
            }

            .endControlFlow()
            //end when
            .endControlFlow()

            .addStatement("%N?.invoke(query)", whereParam)

            .addStatement("val sorted = query.orderBy(%M, order)", primaryKey)
            .addStatement(
                "val list = sorted.limit(pageSize + 1).%N(*%N.%N()).%N()",
                context.rowListMapFunc,
                columnSelectiveCollectionParameter,
                kotlinToTypedArray,
                kotlinToMutableList
            )
            .addStatement("val dataCount = list.size")

            .beginControlFlow("val token = if(dataCount > pageSize)")
            .addStatement("list.%N()", kotlinRemoveLast)
            .addStatement("val idString = list.%M().%N(%N)", kotlinCollectionExtensionMethod("last"), context.getColumnValueStringFunc, primaryKey)
            .addStatement(
                "val idArray = idString.%M(%T.UTF_8)",
                kotlinTextExtensionMethod("toByteArray"),
                Charsets::class.asTypeName()
            )
            .addStatement("%T.getUrlEncoder().encodeToString(idArray)", Base64::class.java.asTypeName())
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