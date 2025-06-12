package com.labijie.infra.orm.testing

import com.labijie.orm.generator.findProjectSourceDir
import com.labijie.orm.generator.ksp.ExposedSymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class CompilerTester {
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
import org.jetbrains.exposed.sql.javatime.datetime

enum class Status {
    OK,
    Failed
}

interface NestedInterface {
    enum class NestedEnum {
        Default,
        Failed
    }
}

interface TestInterface {}


@KspTablePojoSuper(type=TestInterface::class)
object TestTable : SimpleLongIdTable("my") {
    var nullableString = varchar("null_str", 32).nullable()
    var array = array<String>("array")
    var name: Column<String> = varchar("name", 50)
    var count = integer("count")
    val status = enumeration("status", Status::class)
    val status2 = enumeration("status2", NestedInterface.NestedEnum::class)
    val dt = datetime("dt")
}
    """
        )

        //https://github.com/ZacSweers/kotlin-compile-testing
        val compilation = KotlinCompilation().apply {

            useKsp2()
            languageVersion = "2.1"

            useKapt4 = true
            //useKapt4 = true

            jvmTarget = "21"
            sources = listOf(kotlinSource)
            compilerPluginRegistrars = listOf()
            symbolProcessorProviders = mutableListOf(ExposedSymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kspProcessorOptions = mutableMapOf(
                "orm.springboot_aot" to "true"
            )
        }
        val result = compilation.compile()
        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK, result.messages)
        val generatedSourcesDir = compilation.kspSourcesDir
        println("ksp dir: $generatedSourcesDir")
    }
}