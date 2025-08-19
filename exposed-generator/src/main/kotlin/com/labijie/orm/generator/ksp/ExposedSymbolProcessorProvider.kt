package com.labijie.orm.generator.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.labijie.orm.generator.WriterOptions
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

class ExposedSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor{

        val gitProperties = getGitProperties(WriterOptions::class.java)
        val version = gitProperties?.get("git.build.version")?.toString() ?: "unknown"

        environment.logger.info("Init Orm KSP Processor (version: $version)")

        return ExposedSymbolProcessor(version, environment.logger, environment.options)
    }

    private fun getGitProperties(
        packageClass: Class<*>,
        gitPropertiesFile: String = "git-info/git.properties",
    ): Properties? {
        if (gitPropertiesFile.isBlank()) {
            throw IllegalArgumentException("Git properties file can not be blank.")
        }
        return try {
            loadResources(gitPropertiesFile, packageClass.classLoader) {
                Properties().apply {
                    this.load(it)
                }
            }

        } catch (e: IOException) {
            println("Load git properties failed. ${e.printStackTrace()}")
            null
        }
    }

    private fun <T : Any> loadResources(
        name: String, classLoader: ClassLoader?,
        filter: ((InputStream) -> T?)? = null
    ): T? {
        val systemResources: Enumeration<URL> =
            (classLoader ?: ClassLoader.getSystemClassLoader()).getResources(name)
        while (systemResources.hasMoreElements()) {
            val url = systemResources.nextElement()
            try {
                url.openStream().use { stream ->
                    val content: T? = filter?.invoke(stream)
                    if (content != null) return content
                }
            } catch (e: Exception) {
                println("Skip invalid resource: $url -> ${e.message}")
            }
        }
        return null
    }

}