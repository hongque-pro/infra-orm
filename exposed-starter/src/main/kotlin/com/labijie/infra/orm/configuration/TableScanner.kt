package com.labijie.infra.orm.configuration

import ch.qos.logback.core.util.StringCollectionUtil
import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.util.StringUtils
import java.util.function.Supplier


class TableScanner(registry: BeanDefinitionRegistry, private val excludeClasses: String? = null) :
    ClassPathBeanDefinitionScanner(registry) {

    val excludeClassNames = if(excludeClasses.isNullOrBlank()) setOf<String>() else StringUtils.commaDelimitedListToSet(excludeClasses)

    init {
        this.addIncludeFilter(AssignableTypeFilter(Table::class.java))

        this.addExcludeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            val className = metadataReader.classMetadata.className
            className.endsWith("package-info") || excludeClassNames.contains(className)
        }
    }

    override fun postProcessBeanDefinition(beanDefinition: AbstractBeanDefinition, beanName: String) {
        beanDefinition.instanceSupplier = Supplier<Any> {
           Class.forName(beanDefinition.beanClassName).kotlin.objectInstance
       }
    }

}