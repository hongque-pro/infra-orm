/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
package com.labijie.orm.generator.writer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.devtools.ksp.processing.KSPLogger
import com.labijie.orm.generator.GenerationContext
import com.labijie.orm.generator.TableMetadata
import com.labijie.orm.generator.WriterOptions
import com.labijie.orm.generator.createFolderIfNotExisted
import com.labijie.orm.generator.getFolder
import com.labijie.orm.generator.native.ReflectConfigEntry
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText


object NativeReflectConfigWriter {

    val mapper by lazy {
        ObjectMapper()
    }

    private fun getTableResourceFileFolder(rootDir: String, packageName: String, writerOptions: WriterOptions): String {
        val groups = packageName.split(".")
        val groupId =
            if (groups.size > 2) groups.subList(0, groups.size - 1).joinToString(".") else groups.joinToString(".")
                .ifBlank { "orm" }
        val artifactId =
            if (groups.size > 2) groups.subList(groups.size - 1, groups.size).joinToString(".") else "tables"

        val fileDir = Path(rootDir, groupId, artifactId)
        createFolderIfNotExisted(fileDir)
        return fileDir.absolutePathString()
    }

    fun write(tables: List<TableMetadata>, writerOptions: WriterOptions, logger: KSPLogger) {

        if (writerOptions.springbootAot && tables.isNotEmpty()) {

            if (writerOptions.tableArtifactId.isBlank()) {
                logger.warn("Project artifact id of orm table is blank, unable to generate graalvm metadata.")
                return
            }

            if (writerOptions.tableGroupId.isBlank()) {
                logger.warn("project group id of orm table is blank, unable to generate graalvm metadata.")
                return
            }

            val projects = tables.groupBy { writerOptions.getFolder(it).nativeImageResourceDir.absolutePathString() }

            for (project in projects) {

                val rootDir = project.key

                val filePath = Path(rootDir, writerOptions.tableGroupId, writerOptions.tableArtifactId, "reflect-config.json")
                createFolderIfNotExisted(filePath.parent)
                val list = if (filePath.exists()) {
                    val json = filePath.readText(Charsets.UTF_8)
                    try {
                        mapper.readValue(
                            json,
                            object : TypeReference<MutableList<ReflectConfigEntry>>() {})
                    } catch (e: Throwable) {
                        logger.error("Read reflect json file failed\n$e")
                        throw e
                    }
                } else {
                    null
                }

                val config = project.value.map { t ->
                    val ctx = GenerationContext(t, writerOptions)
                    ReflectConfigEntry(ctx.tableClass.reflectionName(), allPublicFields = true)
                }

                if (list != null) {
                    for (c in config) {
                        list.removeIf { it.name == c.name }
                        list.add(c)
                    }
                }

                val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config)

                filePath.writeText(json, Charsets.UTF_8)
            }
        }
    }
}