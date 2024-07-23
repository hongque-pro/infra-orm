package com.labijie.infra.orm.testing

import com.labijie.orm.generator.findProjectSourceDir
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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Table
import com.labijie.infra.orm.compile.*

enum class Status {
    OK,
    Failed
}

@KspPojoGeneration(true)
object TestTable : Table("my") {
    var name: Column<String> = varchar("name", 50)
    var count = integer("count")
    val status = enumeration("status", Status::class)
}
    """
        )
        val compilation = KotlinCompilation().apply {
            //TODO: upgrade kotlin 2.0
            languageVersion = "1.9"
            sources = listOf(kotlinSource)
            symbolProcessorProviders = mutableListOf(ExposedSymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
        }
        val result = compilation.compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK, result.messages)
    }
}