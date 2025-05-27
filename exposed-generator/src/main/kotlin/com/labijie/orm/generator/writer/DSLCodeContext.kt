package com.labijie.orm.generator.writer

import com.labijie.orm.generator.GenerationContext
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec

data class DSLCodeContext(
    val base: GenerationContext,
    val allColumns: PropertySpec,
    val file: FileSpec.Builder,
    val parseRowFunc: FunSpec,
    val parseRowSelectiveFunc: FunSpec,
    val getColumnValueFunc: FunSpec,
    val getColumnValueStringFunc: FunSpec,
    val parseColumnValueFunc: FunSpec,
    val getColumnTypeFunc: FunSpec,
    val assignFunc: FunSpec,
    val rowMapFunc: FunSpec,
    val rowListMapFunc: FunSpec,
    val selectSliceFunc: FunSpec,
    val entityParamName: String
){

}