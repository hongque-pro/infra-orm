/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.orm.generator.writer

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import kotlin.io.path.*

object SpringRuntimeHintWriter {

    fun write(tables: List<TableMetadata>, writerOptions: WriterOptions, logger: KSPLogger) {

        logger.info("native build enabled: ${writerOptions.springbootAot}")
        if (writerOptions.springbootAot && tables.isNotEmpty()) {

            val springResourceFolder = writerOptions.getFolder(tables.first()).springResourceDir

            createFolderIfNotExisted(springResourceFolder)

            val springAotFile = Path(springResourceFolder.absolutePathString(), "aot.factories")

            val configKey = "org.springframework.aot.hint.RuntimeHintsRegistrar"

            val config = if (springAotFile.exists()) {
                parseSpringConfig(springAotFile.readText(Charsets.UTF_8))
            } else {
                mutableMapOf()
            }

            val tablePackages = tables.groupBy { it.packageName }

            val registrarClassNames = mutableSetOf<String>()

            for (pkg in tablePackages) {

                val folder = writerOptions.getFolder(pkg.value.first()).tableSourceDir
                val context = GenerationContext(pkg.value.first(), writerOptions)

                registrarClassNames.add(context.runtimeHintsRegistrarClass.reflectionName())

                val file =
                    FileSpec.builder(context.aotPackageName, fileName = context.runtimeHintsRegistrarClass.simpleName)
                        .suppressRedundantVisibilityModifierWarning()
                        .addType(
                            TypeSpec.classBuilder(context.runtimeHintsRegistrarClass)
                                .addComments("SpringBoot runtime hint for orm tables", context)
                                .addModifiers(KModifier.INTERNAL)
                                .addSuperinterface(RuntimeHintsRegistrar::class)
                                .addFunction(overrideRegisterHintsFunc(tables, options = writerOptions))
                                .build()
                        )
                        .build()

                file.writeTo(folder)
            }

            if (registrarClassNames.isNotEmpty()) {
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

    @Suppress("SameParameterValue")
    private fun FunSpec.Builder.addMemoryCategory(vararg member: MemberCategory): FunSpec.Builder {

        val blocks = member.map {
            CodeBlock.builder().addStatement("%T.${it},", MemberCategory::class.asTypeName())
                .build()
        }

        blocks.forEachIndexed {
                i, b->
            addCode(b)
        }
        return this
    }

    private fun addColumnHintTypes(table: TableMetadata, writerOptions: WriterOptions): Array<TypeName> {

        table.columns.forEach {
            col->
            val type = col.type.makeNotNullable()
            if(col.isEnum) {
                val key = type.toTypeName().toString()
                writerOptions.hintTypesCache.putIfAbsent(key, type.toTypeName())
            }
            if(col.isGeneric && !col.isCollection) {
                col.type.declaration.typeParameters.forEach {
                    p->
                    val typeName = p.toTypeVariableName()
                    val key = typeName.toString();
                    writerOptions.hintTypesCache.putIfAbsent(key, typeName)
                }
            }
        }
        return writerOptions.hintTypesCache.values.toTypedArray()
    }

    private fun overrideRegisterHintsFunc(tables: List<TableMetadata>, options: WriterOptions): FunSpec {

        val hintsParam = ParameterSpec.builder("hints", RuntimeHints::class).build()
        val nullableClassLoader = ClassLoader::class.asClassName().copy(nullable = true)
        val classLoaderParam = ParameterSpec.builder("classLoader", nullableClassLoader).build()


        return FunSpec.builder("registerHints")
            .apply {
                modifiers.add(KModifier.OVERRIDE)
            }
            .addParameter(hintsParam)
            .addParameter(classLoaderParam)
            .apply {

                addComment("""
                    Begin Table Pojo types hint register
                """.trimIndent())

                tables.forEach {

                    val ctx = GenerationContext(it, options)

                    beginControlFlow(
                        "hints.reflection().registerType(%T.of(%S))",
                        TypeReference::class,
                        ctx.pojoClass.reflectionName()
                    )
                    addStatement("it.withMembers(")

                    addMemoryCategory(
                        MemberCategory.PUBLIC_FIELDS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS
                    )

                    addStatement(")")
                    .endControlFlow()

                    addColumnHintTypes(it, writerOptions = options)
                }

                addComment("""
                    Begin Column types hint register
                """.trimIndent())


                options.hintTypesCache.values.forEach {
                        col->
                    beginControlFlow("hints.reflection().registerType(%T::class.java)", col)
                    addStatement("it.withMembers(")
                    addMemoryCategory(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, // 调用构造器（某些情况下有用）
                        MemberCategory.INVOKE_PUBLIC_METHODS,        // 调用 .name(), .ordinal(), .values() 等方法
                        MemberCategory.DECLARED_FIELDS
                    )
                    addStatement(")")
                    endControlFlow()
                }
            }
            .build()
    }
}