package com.labijie.orm.generator.writer

import com.labijie.orm.generator.GenerationContext
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName

abstract class AbstractDSLMethodBuilder: IDSLMethodBuilder {

    companion object {
        @JvmStatic
        private val EMPTY_METHOD = FunSpec.builder("EMPTY").build()
    }

    protected fun getNoneMethod() = EMPTY_METHOD

    override fun buildMethods(context: DSLCodeContext): List<FunSpec> {
        val m = build(context)
        if(m === EMPTY_METHOD){
            return listOf()
        }
        return listOf(m)
    }

    protected open fun build(context: DSLCodeContext): FunSpec {
        return getNoneMethod()
    }

    protected fun getExposedSqlMember(member: String, isExtension: Boolean = true): MemberName {
        return MemberName("org.jetbrains.exposed.sql", member, isExtension)
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