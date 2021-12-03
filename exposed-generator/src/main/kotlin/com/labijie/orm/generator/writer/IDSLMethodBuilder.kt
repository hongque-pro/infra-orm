package com.labijie.orm.generator.writer

import com.squareup.kotlinpoet.FunSpec

interface IDSLMethodBuilder {
    fun buildMethods(context: DSLCodeContext): List<FunSpec>
}