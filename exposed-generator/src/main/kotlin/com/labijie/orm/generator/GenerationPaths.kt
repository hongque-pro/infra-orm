/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.orm.generator

import java.nio.file.Path


data class GenerationPaths(
    val tableSourceDir: Path,
    val pojoSourceDir: Path,
    val tableResourceDir: Path,
    val springResourceDir: Path,
    val nativeImageResourceDir: Path
)