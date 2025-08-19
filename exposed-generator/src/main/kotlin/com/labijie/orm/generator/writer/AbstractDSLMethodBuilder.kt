package com.labijie.orm.generator.writer

import com.labijie.infra.orm.OffsetList
import com.labijie.orm.generator.parameterizedWildcard
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.exposed.sql.*

abstract class AbstractDSLMethodBuilder : IDSLMethodBuilder {

    companion object {

        val columnSelectiveParameter by lazy {
            val colType = Column::class.asTypeName().parameterizedWildcard()

            ParameterSpec.builder("selective", colType)
                .apply {
                    modifiers.add(KModifier.VARARG)
                }.build()
        }

        val columnSelectiveCollectionParameter by lazy {
            val colType = Column::class.asTypeName().parameterizedWildcard()

            ParameterSpec.builder("selective", Collection::class.asTypeName().parameterizedBy(colType))
                .defaultValue(CodeBlock.of("listOf()"))
                .build()
        }

        fun createQueryExpressionParameter(
            name: String = "where",
            nullable: Boolean = false,
            defaultValue: String? = null
        ): ParameterSpec {
            val rec = Query::class.asTypeName()
            val where = LambdaTypeName.get(rec, returnType = rec.copy(nullable = true)).let {
                if (nullable) {
                    it.copy(nullable = true)
                } else {
                    it
                }
            }

            return ParameterSpec.builder(name, where)
                .apply {
                    if (!defaultValue.isNullOrBlank()) {
                        this.defaultValue(defaultValue)
                    }
                }
                .build()
        }

        fun getExposedSqlExpressionBuilderMember(member: String, isExtension: Boolean = true): MemberName {
            val className = SqlExpressionBuilder::class.asClassName()
            return MemberName(className, member, isExtension)
        }

        fun getExpressionMethod(methodName: String): MemberName {
            val expressionClassName = Expression::class.asClassName()
            return MemberName(expressionClassName, methodName, true)
        }

        fun getSqlExtendMethod(member: String, isExtension: Boolean = true): MemberName {
            return MemberName("org.jetbrains.exposed.sql", member, isExtension)
        }


        val kotlinLetMethod: MemberName by lazy {
            MemberName("kotlin", "let", true)
        }

        val kotlinFirstOrNullMethod: MemberName by lazy {
            kotlinCollectionExtensionMethod("firstOrNull")
        }

        val kotlinArrayOfMethod: MemberName by lazy {
            MemberName("kotlin", "arrayOf", false)
        }

        fun kotlinCollectionExtensionMethod(methodName: String): MemberName {
            return MemberName("kotlin.collections", methodName, isExtension = true)
        }

        fun kotlinTextExtensionMethod(methodName: String): MemberName {
            return MemberName("kotlin.text", methodName, isExtension = true)
        }


        val kotlinIsNotEmpty = kotlinCollectionExtensionMethod("isNotEmpty")
        val kotlinToList = kotlinCollectionExtensionMethod("toList")
        val kotlinToTypedArray = kotlinCollectionExtensionMethod("toTypedArray")
        val kotlinToMutableList = kotlinCollectionExtensionMethod("toMutableList")
        val kotlinRemoveLast = kotlinCollectionExtensionMethod("removeLast")


        val eqMethod = getExposedSqlExpressionBuilderMember("eq")
        val andMethod = getSqlExtendMethod("and")
        val andWhereMethod = getSqlExtendMethod("andWhere")
        val orWhereMethod = getSqlExtendMethod("orWhere")

        val encodeTokenMethod by lazy {
            val offsetListType = OffsetList::class.asTypeName()
            MemberName(offsetListType, "encodeToken")
        }

        val decodeTokenMethod by lazy {
            val offsetListType = OffsetList::class.asTypeName()
            MemberName(offsetListType, "decodeToken")
        }
    }



    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        val m = build(context) ?: return listOf()
        return listOf(m)
    }

    protected open fun build(context: DSLCodeContext): FunSpec? {
        return null
    }

    protected fun DSLCodeContext.isSinglePrimaryKey(): Boolean {
        return this.base.table.primaryKeys.count() == 1
    }

    protected fun buildPrimaryKeyWhere(context: DSLCodeContext, objectVarName: String ? = null): CodeBlock {

        val objectPrefix = if(objectVarName.isNullOrBlank()) "" else "${objectVarName}."
        return CodeBlock.builder().apply {
            context.base.table.primaryKeys.forEachIndexed { i, key ->
                add("%L.%M(${objectPrefix}${key.name})", "${context.base.table.className}.${key.name}", eqMethod)
                if (i < context.base.table.primaryKeys.size - 1) {
                    add(" %M ", andMethod)
                }
            }
        }.build()
    }
}