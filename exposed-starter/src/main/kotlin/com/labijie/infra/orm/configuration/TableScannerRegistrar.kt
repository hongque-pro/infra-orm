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
    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val tableScanAttrs = AnnotationAttributes
            .fromMap(importingClassMetadata.getAnnotationAttributes(TableScan::class.java.name))
        if (tableScanAttrs != null) {
            registerBeanDefinitions(
                importingClassMetadata, tableScanAttrs, registry,
                generateBaseBeanName(importingClassMetadata, 0)
            )
        }
    }

    private fun registerBeanDefinitions(
        annoMeta: AnnotationMetadata, annoAttrs: AnnotationAttributes,
        registry: BeanDefinitionRegistry, beanName: String?
    ) {
        val builder: BeanDefinitionBuilder =
            BeanDefinitionBuilder.genericBeanDefinition(TableDefinitionPostProcessor::class.java)

        builder.addPropertyValue(TableDefinitionPostProcessor::processPropertyPlaceHolders.name, true);

        val basePackages: MutableSet<String> = mutableSetOf()

        val excludeClasses = annoAttrs.getClassArray(TableScan::excludeClasses.name).map { it.name }

        basePackages.addAll(annoAttrs.getStringArray(TableScan::basePackages.name).filter { !it.isNullOrBlank() })
        basePackages.addAll(
            annoAttrs.getClassArray(TableScan::basePackageClasses.name).map { ClassUtils.getPackageName(it) })

        if(basePackages.isEmpty()) {
            basePackages.add(getDefaultBasePackage(annoMeta))
        }


        builder.addPropertyValue(
            TableDefinitionPostProcessor::packages.name,
            StringUtils.collectionToCommaDelimitedString(basePackages)
        )
        builder.addPropertyValue(TableDefinitionPostProcessor::excludeClasses.name, StringUtils.collectionToCommaDelimitedString(excludeClasses))
        registry.registerBeanDefinition(beanName!!, builder.beanDefinition)
    }

    private fun generateBaseBeanName(importingClassMetadata: AnnotationMetadata, index: Int): String? {
        return importingClassMetadata.className + "#" + TableScannerRegistrar::class.java.simpleName + "#" + index
    }

    private fun getDefaultBasePackage(importingClassMetadata: AnnotationMetadata): String {
        return ClassUtils.getPackageName(importingClassMetadata.className)
    }
}