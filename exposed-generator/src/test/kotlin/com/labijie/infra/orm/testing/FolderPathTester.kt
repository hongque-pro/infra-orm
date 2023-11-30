package com.labijie.infra.orm.testing

import com.labijie.orm.generator.findProjectSourceDir
import com.labijie.orm.generator.ksp.ExposedSymbolProcessor
import com.labijie.orm.generator.ksp.ExposedSymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FolderPathTester {
    @Test
    fun testPath(){
        val path = "E:\\Work\\infra-orm\\dummy-project\\src\\main\\kotlin\\com\\labijie\\orm\\dummy".replace("\\", File.separator)
        val result = "E:\\Work\\infra-orm\\dummy-project\\src\\main\\kotlin".replace("\\", File.separator)
        val find = findProjectSourceDir(path)
        assertEquals(result, find.toString())
    }

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun testCompile(){
        val kotlinSource = SourceFile.kotlin(
            "KClass.kt", """
package com.labijie.orm.testing

import com.labijie.infra.orm.SimpleLongIdTable
import org.jetbrains.exposed.sql.Column

object TestTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var count = integer("count")
}
    """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            symbolProcessorProviders = listOf(ExposedSymbolProcessorProvider())
        }
        val result = compilation.compile()
    }
}