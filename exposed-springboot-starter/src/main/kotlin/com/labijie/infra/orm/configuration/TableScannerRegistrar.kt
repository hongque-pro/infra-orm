package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.annotation.TableScan
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils


class TableScannerRegistrar : ImportBeanDefinitionRegistrar {

    data class ScanInfo(
        val basePackages: Set<String> = mutableSetOf(),
        val excludeClasses: Set<String> = mutableSetOf()
    )

    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {

        val tableScanAttrs = AnnotationAttributes
            .fromMap(importingClassMetadata.getAnnotationAttributes(TableScan::class.java.name))
        if (tableScanAttrs != null) {

            registerBeanDefinitions(
                importingClassMetadata, tableScanAttrs, registry,
                generateBaseBeanName(importingClassMetadata)
            )
        }
    }

    private fun parseTableScanInfo(
        annoMeta: AnnotationMetadata, annoAttrs: AnnotationAttributes,
    ): ScanInfo {
        val basePackages: MutableSet<String> = mutableSetOf()

        val excludeClasses = annoAttrs.getClassArray(TableScan::excludeClasses.name).map { it.name }.toSet()

        basePackages.addAll(annoAttrs.getStringArray(TableScan::basePackages.name).filter { !it.isNullOrBlank() })
        basePackages.addAll(
            annoAttrs.getClassArray(TableScan::basePackageClasses.name).map { ClassUtils.getPackageName(it) })

        if(basePackages.isEmpty()) {
            basePackages.add(getDefaultBasePackage(annoMeta))
        }

        return ScanInfo(basePackages, excludeClasses)
    }

    private fun registerBeanDefinitions(
        annoMeta: AnnotationMetadata, annoAttrs: AnnotationAttributes,
        registry: BeanDefinitionRegistry, beanName: String?
    ) {
        val builder: BeanDefinitionBuilder =
            BeanDefinitionBuilder.genericBeanDefinition(TableDefinitionPostProcessor::class.java)

        builder.addPropertyValue(TableDefinitionPostProcessor::processPropertyPlaceHolders.name, true)

        val info = parseTableScanInfo(annoMeta, annoAttrs)

        if(info.basePackages.isNotEmpty()) {

            builder.addPropertyValue(
                TableDefinitionPostProcessor::packages.name,
                StringUtils.collectionToCommaDelimitedString(info.basePackages)
            )
            builder.addPropertyValue(
                TableDefinitionPostProcessor::excludeClasses.name,
                StringUtils.collectionToCommaDelimitedString(info.excludeClasses)
            )


            registry.registerBeanDefinition(beanName!!, builder.beanDefinition)
        }
    }

    private fun generateBaseBeanName(importingClassMetadata: AnnotationMetadata): String? {
        return importingClassMetadata.className + "#" + TableScannerRegistrar::class.java.simpleName
    }

    private fun getDefaultBasePackage(importingClassMetadata: AnnotationMetadata): String {
        return ClassUtils.getPackageName(importingClassMetadata.className)
    }
}