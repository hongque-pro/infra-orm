/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
package com.labijie.orm.generator

import java.nio.file.Path


data class GenerationPaths(
    val pojoSourceDir: Path,
    val tableResourceDir: Path,
    val nativeImageResourceDir: Path
)