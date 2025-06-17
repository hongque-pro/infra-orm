/**
 * @author Anders Xiao
 * @date 2025/6/12
 */

package com.labijie.infra.orm.aot

import com.labijie.infra.orm.SimpleTableScanner
import com.labijie.infra.orm.configuration.TableDefinitionPostProcessor
import org.springframework.aot.generate.DefaultMethodReference
import org.springframework.aot.hint.MemberCategory
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.javapoet.ClassName
import org.springframework.javapoet.MethodSpec
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils
import java.beans.Introspector
import javax.lang.model.element.Modifier


class TableScanAotProcessor : BeanFactoryInitializationAotProcessor {

    override fun processAheadOfTime(beanFactory: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution? {

        println("Start Infra-Orm aot process ...")

        // 1. 收集所有 @TableScan 注解
        val tableScans = collectTableScanAnnotations(beanFactory)

        // 2. 合并所有扫描配置
        val allPackages = mutableSetOf<String>()
        val allExcludes = mutableSetOf<String>()

        tableScans.forEach { define ->
            val parsed = parseTableDefinitionPostProcessorBeanDefinition(define.value)
            allPackages += parsed.packages
            allExcludes += parsed.excludeClasses

            (beanFactory as? BeanDefinitionRegistry)?.removeBeanDefinition(define.key)
        }

//        val msg = StringBuilder().apply {
//            appendLine("Table scans:")
//            appendLine("  Packages:")
//            appendLine(allPackages.joinToString("\n") { it.padStart(4, ' ') })
//            appendLine("  Excludes:")
//            appendLine(allExcludes.joinToString("\n") { it.padStart(4, ' ') })
//        }
//        println(msg)


        if (allPackages.isEmpty()) return null

        val scanner = SimpleTableScanner(allExcludes)

        val discoveredTableClasses = mutableSetOf<Class<*>>()

        val candidates = scanner.scan(*allPackages.toTypedArray())

        for (candidate in candidates) {
            println("Detected orm table: ${candidate.beanClassName}")
            if(candidate.beanClassName.isNullOrBlank()) {
                continue
            }

            candidate.beanClassName?.let {
                val clazz = ClassUtils.forName(it, this::class.java.classLoader)
                //val clazz = Class.forName(candidate.beanClassName)
                if (clazz.name !in allExcludes) {
                    discoveredTableClasses += clazz
                }
            }
        }

        if (discoveredTableClasses.isEmpty()) return null

        return BeanFactoryInitializationAotContribution { generationContext, code ->

            val hints = generationContext.runtimeHints
            val classes = discoveredTableClasses.distinct()
            if(classes.isNotEmpty()) {
                for (clazz in classes) {
                    // 注册 Table 类的 AOT Hint
                    hints.reflection().registerType(clazz) {
                        it.withMembers(
                            MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.PUBLIC_FIELDS,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
                        )
                    }
                }

                val registerTablesMethod = MethodSpec.methodBuilder("registerTables")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(
                        ConfigurableListableBeanFactory::class.java,
                        "beanFactory"
                    )
                    .apply {
                        discoveredTableClasses.forEach { tableClass ->
                            val beanName = Introspector.decapitalize(tableClass.simpleName)
                            /**
                             * Generate this code:
                             *
                             * if (!beanFactory.containsSingleton(beanName)) {
                             *     beanFactory.registerSingleton(beanName, instance)
                             * }
                             */

                            beginControlFlow(" if (!beanFactory.containsSingleton(\"${beanName}\"))")
                                .addStatement(" beanFactory.registerSingleton(\"${beanName}\", \$T.INSTANCE)", tableClass)
                                .endControlFlow()
                        }
                    }.build()

                val className = ClassName.get("com.labijie.infra.orm.aot", "InfraOrmAot")
                val generatedClass = generationContext.generatedClasses.getOrAddForFeatureComponent("TableRegistrations", className) {
                    it.addModifiers(Modifier.PUBLIC)
                    it.addMethod(registerTablesMethod)
                }


                val reference = DefaultMethodReference(registerTablesMethod, generatedClass.name)
                code.addInitializer(reference)
            }

        }
    }

    private fun parseTableDefinitionPostProcessorBeanDefinition(beanDefinition: BeanDefinition): TableScanAnnotationInfo {
        val packages = beanDefinition.propertyValues.get(TableDefinitionPostProcessor::packages.name)?.toString()
        val excludes = beanDefinition.propertyValues.get(TableDefinitionPostProcessor::excludeClasses.name)?.toString()

        val packagesArray =
            if(!packages.isNullOrBlank()) StringUtils.tokenizeToStringArray(packages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS).toSet() else emptySet<String>()

        val excludeArray =
            if(!excludes.isNullOrBlank()) StringUtils.tokenizeToStringArray(excludes, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS).toSet() else emptySet<String>()

        return TableScanAnnotationInfo(packagesArray, excludeArray)
    }


    private fun collectTableScanAnnotations(
        beanFactory: ConfigurableListableBeanFactory
    ): Map<String, BeanDefinition> {
        return beanFactory.beanDefinitionNames
            .mapNotNull { beanName ->
                beanFactory.getBeanDefinition(beanName).let { bd ->
                    if(bd.beanClassName == TableDefinitionPostProcessor::class.java.name) Pair(beanName, bd) else null
                }
            }.toMap()
    }

    data class TableScanAnnotationInfo(
        val packages: Set<String> = emptySet(),
        val excludeClasses : Set<String> = emptySet()
    )

}