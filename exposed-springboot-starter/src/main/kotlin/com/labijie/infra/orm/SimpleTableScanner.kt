package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.AssignableTypeFilter

/**
 * @author Anders Xiao
 * @date 2025/6/12
 */
open class SimpleTableScanner(excludeClassNames: Set<String>? = null) : ClassPathScanningCandidateComponentProvider(false) {

    init {
        addIncludeFilter(AssignableTypeFilter(Table::class.java))
        addExcludeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            val className = metadataReader.classMetadata.className
            className.endsWith("package-info") || (excludeClassNames?.contains(className) ?: false) || metadataReader.classMetadata.isAbstract
        }
    }

    override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
        return true
    }


    fun scan(vararg basePackages: String): List<BeanDefinition> {
        return if (basePackages.isNotEmpty()) {
            basePackages.flatMap { pkg -> findCandidateComponents(pkg) }
        } else emptyList()
    }
}