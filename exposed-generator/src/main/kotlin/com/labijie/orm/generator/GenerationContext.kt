package com.labijie.orm.generator

import com.squareup.kotlinpoet.ClassName

class GenerationContext(val table: TableMetadata, val options: WriterOptions) {
    val pojoPackageName = (options.pojoPackageName ?: "").ifBlank { "${table.packageName}.pojo" }
    val dslPackageName = "${pojoPackageName}.dsl"

    val pojoClass = ClassName(pojoPackageName, table.normalizeClassName())
    val dslClass = ClassName(dslPackageName, "${table.normalizeClassName()}DSL")

    val tableClass = ClassName(table.packageName, table.className)
}