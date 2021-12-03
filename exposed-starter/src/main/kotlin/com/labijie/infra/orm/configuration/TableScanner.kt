package com.labijie.infra.orm.configuration

import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.AssignableTypeFilter
import java.util.function.Supplier


class TableScanner(registry: BeanDefinitionRegistry) :
    ClassPathBeanDefinitionScanner(registry) {


    init {
        this.addIncludeFilter(AssignableTypeFilter(Table::class.java))

        this.addExcludeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            val className = metadataReader.classMetadata.className
            className.endsWith("package-info")
        }
    }

    override fun postProcessBeanDefinition(beanDefinition: AbstractBeanDefinition, beanName: String) {
        beanDefinition.instanceSupplier = Supplier<Any> {
           Class.forName(beanDefinition.beanClassName).kotlin.objectInstance
       }
    }

}