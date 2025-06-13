package com.labijie.infra.orm.testing

import com.labijie.orm.generator.findProjectSourceDir
import com.labijie.orm.generator.ksp.ExposedSymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import java.net.URLDecoder
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText
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

    private fun getTestFile(): Path {
        // 获取当前 class 文件的位置
        val classLocation = this::class.java.protectionDomain.codeSource.location
        val decodedPath = URLDecoder.decode(classLocation.path, Charsets.UTF_8.name()).removePrefix("/")

        val buildDirIndex = decodedPath.indexOf("/build/")

        val moduleDir = decodedPath.substring(0, buildDirIndex)

        return Path(moduleDir, "src/test/kotlin", "TestSource.kt")
    }


    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun testCompile(){

        val testFile = getTestFile()
        val fileContent = testFile.readText(Charsets.UTF_8)

        val kotlinSource = SourceFile.kotlin("TestSourceCode.kt", fileContent)

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