package com.labijie.infra.orm.configuration

import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.TypeExcludeFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory

/**
 * @author Anders Xiao
 * @date 2025/6/30
 */
@EnableConfigurationProperties(InfraExposedProperties::class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
class InfraExposedExcludeJpaAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = ["infra.exposed.disable-jpa"], havingValue = "true", matchIfMissing = true)
    fun jpaExcludeFilter(): JpaExcludeFilter {
        return JpaExcludeFilter()
    }

    class JpaExcludeFilter: TypeExcludeFilter() {
        override fun match(metadataReader: MetadataReader, metadataReaderFactory: MetadataReaderFactory): Boolean {
            val jpaConfigs = listOf(
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration",
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
            )
            val className: String = metadataReader.classMetadata.className
            return jpaConfigs.contains(className)
        }
    }
}