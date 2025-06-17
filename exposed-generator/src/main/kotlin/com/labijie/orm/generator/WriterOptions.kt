package com.labijie.orm.generator

import com.squareup.kotlinpoet.TypeName

class WriterOptions {
    var pojoPackageName: String? = null
    var pojoProjectRootDir: String? = null
    var tableGroupId: String = ""
    var tableArtifactId: String = ""
    var springbootAot: Boolean = false

    val hintTypesCache = mutableMapOf<String, TypeName>()
}