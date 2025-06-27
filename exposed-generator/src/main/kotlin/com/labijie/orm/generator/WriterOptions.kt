package com.labijie.orm.generator

import com.squareup.kotlinpoet.TypeName

class WriterOptions {
    var pojoPackageName: String? = null
    var pojoProjectRootDir: String? = null
    var springbootAot: Boolean = false
    var kotlinSerializable: Boolean = false

    val hintTypesCache = mutableMapOf<String, TypeName>()
}