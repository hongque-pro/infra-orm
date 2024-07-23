package com.labijie.infra.orm.configuration

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.StringUtils
import java.util.function.Consumer
import javax.sql.DataSource


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
@EnableConfigurationProperties(InfraExposedProperties::class)
@EnableTransactionManagement
class InfraExposedAutoConfiguration : ApplicationContextAware  {

    private lateinit var applicationContext: ApplicationContext

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(InfraExposedAutoConfiguration::class.java)
        }
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseConfig::class)
    open fun databaseConfig(): DatabaseConfig {
        return DatabaseConfig.invoke {  }
    }

    @Bean
    @ConditionalOnMissingBean(SpringTransactionManager::class)
    fun exposedSpringTransactionManager(properties: InfraExposedProperties, databaseConfig: DatabaseConfig, dataSource: DataSource): SpringTransactionManager {
        val txm = SpringTransactionManager(dataSource, databaseConfig, properties.showSql)
        return txm
    }

    class ExposedTableRegistrar : BeanFactoryAware, ImportBeanDefinitionRegistrar  {

        private var beanFactory: BeanFactory? = null

        override fun setBeanFactory(beanFactory: BeanFactory) {
            this.beanFactory = beanFactory

            SchemaUtils.createStatements()
        }

        override fun registerBeanDefinitions(
            importingClassMetadata: AnnotationMetadata,
            registry: BeanDefinitionRegistry
        ) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic table scanning disabled.")
                return
            }

            logger.debug("Searching for mappers annotated with @Table")

            val packages = AutoConfigurationPackages.get(beanFactory)
            if (logger.isDebugEnabled) {
                packages.forEach(Consumer { pkg: String? ->
                    logger.debug("Using auto-configuration base package '${pkg}'.")
                })
            }

            val builder: BeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(TableDefinitionPostProcessor::class.java)

            builder.addPropertyValue(TableDefinitionPostProcessor::packages.name, StringUtils.collectionToCommaDelimitedString(packages))

            registry.registerBeanDefinition(TableDefinitionPostProcessor::class.java.name, builder.beanDefinition)
        }
    }

    /**
     * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
     * mappers based on the same component-scanning path as Spring Boot itself.
     */
    @Configuration(proxyBeanMethods = false)
    @Import(ExposedTableRegistrar::class)
    @ConditionalOnMissingBean(TableDefinitionPostProcessor::class)
    class TableScannerRegistrarNotFoundConfiguration : InitializingBean {
        override fun afterPropertiesSet() {
            logger.info(
                "Not found configuration for registering kotlin expose table bean using @TableScan or TableDefinitionPostProcessor."
            )
        }
    }

    @Bean
    fun exposedConfigurationOverride() = ExposedConfigurationOverride()

    @Bean
    @ConditionalOnProperty(prefix = "infra.exposed", name = ["generate-schema.enabled"], havingValue = "true", matchIfMissing = false)
    fun schemaCreationProcessor(transactionTemplate: TransactionTemplate, properties: InfraExposedProperties): SchemaChangesProcessor {
        return SchemaChangesProcessor(transactionTemplate, properties)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}