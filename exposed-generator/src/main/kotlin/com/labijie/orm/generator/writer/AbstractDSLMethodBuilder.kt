package com.labijie.orm.generator.writer

import com.labijie.orm.generator.parameterizedWildcard
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query

abstract class AbstractDSLMethodBuilder : IDSLMethodBuilder {

    companion object {
        @JvmStatic
        private val EMPTY_METHOD = FunSpec.builder("EMPTY").build()

        val columnSelectiveParameter by lazy {
            val colType = Column::class.asTypeName().parameterizedWildcard()

            ParameterSpec.builder("selective", colType)
                .apply {
                    modifiers.add(KModifier.VARARG)
                }.build()
        }

        val columnSelectiveListParameter by lazy {
            val colType = Column::class.asTypeName().parameterizedWildcard()

            ParameterSpec.builder("selective", List::class.asTypeName().parameterizedBy(colType))
                .defaultValue(CodeBlock.of("listOf()"))
                .build()
        }

        fun createQueryExpressionParameter(
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

            return ParameterSpec.builder(name, where)
                .apply {
                    if (!defaultValue.isNullOrBlank()) {
                        this.defaultValue(defaultValue)
                    }
                }
                .build()
        }


        fun getExposedSqlMember(member: String, isExtension: Boolean = true): MemberName {
            return MemberName("org.jetbrains.exposed.sql", member, isExtension)
        }

        val kotlinLetMethod: MemberName by lazy {
            MemberName("kotlin", "let", true)
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

        val exposedAndWhere = getExposedSqlMember("andWhere")
    }

    protected fun getNoneMethod() = EMPTY_METHOD



    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        val m = build(context)
        if (m === EMPTY_METHOD) {
            return listOf()
        }
        return listOf(m)
    }

    protected open fun build(context: DSLCodeContext): FunSpec {
        return getNoneMethod()
    }

    protected fun DSLCodeContext.isSinglePrimaryKey(): Boolean {
        return this.base.table.primaryKeys.count() == 1
    }

    protected fun buildPrimaryKeyWhere(context: DSLCodeContext): CodeBlock {
        val primaryKeyCount = context.base.table.primaryKeys.size

        return CodeBlock.builder().apply {
            if (primaryKeyCount == 1) {
                val key = context.base.table.primaryKeys.first()
                addStatement("%L eq ${key.name}", "${context.base.table.className}.${key.name}")
            } else {
                context.base.table.primaryKeys.mapIndexed { i, it ->
                    addStatement("(%L eq ${it.name})", "${context.base.table.className}.${it.name}")
                    if (i < context.base.table.primaryKeys.size - 1) {
                        addStatement(" %M ", getExposedSqlMember("and"))
                    }
                }
            }
        }.build()
    }
}