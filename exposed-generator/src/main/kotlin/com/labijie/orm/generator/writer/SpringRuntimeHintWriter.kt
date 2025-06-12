/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.orm.generator.writer

import com.google.devtools.ksp.processing.KSPLogger
import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import kotlin.io.path.*


object SpringRuntimeHintWriter {

    fun write(tables: List<TableMetadata>, writerOptions: WriterOptions, logger: KSPLogger) {

        logger.info("native build enabled: ${writerOptions.springbootAot}")
        if (writerOptions.springbootAot && tables.isNotEmpty()) {

            val springResourceFolder = writerOptions.getFolder(tables.first()).springResourceDir

            createFolderIfNotExisted(springResourceFolder)

            val springAotFile = Path(springResourceFolder.absolutePathString(), "aot.factories")

            val configKey = "org.springframework.aot.hint.RuntimeHintsRegistrar"

            val config = if(springAotFile.exists()) {
                parseSpringConfig(springAotFile.readText(Charsets.UTF_8))
            }else {
                mutableMapOf()
            }

            val tablePackages = tables.groupBy { it.packageName }

            val registrarClassNames = mutableSetOf<String>()

            for (pkg in tablePackages) {

                val folder = writerOptions.getFolder(pkg.value.first()).tableSourceDir
                val context = GenerationContext(pkg.value.first(), writerOptions)

                registrarClassNames.add(context.runtimeHintsRegistrarClass.reflectionName())

                val file = FileSpec.builder(context.aotPackageName, fileName = context.runtimeHintsRegistrarClass.simpleName)
                    .suppressRedundantVisibilityModifierWarning()
                    .addType(
                        TypeSpec.classBuilder(context.runtimeHintsRegistrarClass)
                            .addComments("SpringBoot runtime hint for ${context.tableClass.simpleName}", context)
                            .addSuperinterface(RuntimeHintsRegistrar::class)
                            .addFunction(overrideRegisterHintsFunc(tables))
                            .build()
                    )
                    .build()

                file.writeTo(folder)
            }

            if(registrarClassNames.isNotEmpty()) {
                config.getOrPut(configKey) { mutableSetOf() }.addAll(registrarClassNames)
            }

            val content = convertToSpringConfig(config)
            springAotFile.writeText(content, Charsets.UTF_8)

        }
    }


//    fun registerHints(hints: RuntimeHints, classLoader: ClassLoader) {
//        hints.reflection().registerType(PojoWriter::class.java) {
//            it.withMembers()
//        }
//    }
    private fun overrideRegisterHintsFunc(tables: List<TableMetadata>) : FunSpec {

        val hintsParam  = ParameterSpec.builder("hints", RuntimeHints::class).build()
        val nullableClassLoader = ClassLoader::class.asClassName().copy(nullable = true)
        val classLoaderParam  = ParameterSpec.builder("classLoader", nullableClassLoader).build()

        return FunSpec.builder("registerHints")
            .apply {
                modifiers.add(KModifier.OVERRIDE)
            }
            .addParameter(hintsParam)
            .addParameter(classLoaderParam)
            .apply {
                tables.forEach {
                    beginControlFlow("hints.reflection().registerType(%T::class.java)", it.tableClass)
                    .addStatement("it.withMembers(%T.${MemberCategory.PUBLIC_FIELDS}, %T.${MemberCategory.INVOKE_DECLARED_METHODS},%T.${MemberCategory.INVOKE_DECLARED_CONSTRUCTORS})",
                        MemberCategory::class,
                        MemberCategory::class,
                        MemberCategory::class)
                    .endControlFlow()
                }
            }
            .build()
    }
}