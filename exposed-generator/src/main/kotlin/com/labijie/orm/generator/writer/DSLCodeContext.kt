package com.labijie.orm.generator.writer

import com.labijie.orm.generator.GenerationContext
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

data class DSLCodeContext(
    val base: GenerationContext,
    val file: FileSpec.Builder,
    val parseRowFunc: FunSpec,
    val applyInsertFunc: FunSpec,
    val applyUpdateFunc: FunSpec,
    val rowMapFunc: FunSpec,
    val rowListMapFunc: FunSpec,
    val entityParamName: String
){

}