package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.ExposedTransactionListener
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.DatabaseConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.support.SQLExceptionTranslator
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource


@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
@EnableConfigurationProperties(InfraExposedProperties::class)
@EnableTransactionManagement
class InfraExposedAutoConfiguration : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(InfraExposedAutoConfiguration::class.java)
        }
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseConfig::class)
    fun databaseConfig(): DatabaseConfig {
        return DatabaseConfig.invoke { }
    }

    @Bean
    @ConditionalOnBean(DataSource::class)
    @ConditionalOnMissingBean(JdbcExposedTransactionManager::class)
    fun exposedSpringTransactionManager(
        environment: Environment,
        properties: InfraExposedProperties,
        databaseConfig: DatabaseConfig,
        dataSource: DataSource,
        @Autowired(required = false) sqlExceptionTranslator: SQLExceptionTranslator?
    ): JdbcExposedTransactionManager {

        val txm = SpringTransactionManager(dataSource, databaseConfig, false)
        txm.addListener(ExposedTransactionListener(environment, properties))
        return JdbcExposedTransactionManager(properties, txm).apply {
            sqlExceptionTranslator?.let {
                this.setExceptionTranslator(sqlExceptionTranslator)
            }
        }
    }

    /**
     * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
     * mappers based on the same component-scanning path as Spring Boot itself.
     */
    @Configuration(proxyBeanMethods = false)
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
    @ConditionalOnProperty(
        prefix = "infra.exposed",
        name = ["generate-schema.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun schemaCreationProcessor(
        transactionTemplate: TransactionTemplate,
        properties: InfraExposedProperties
    ): SchemaChangesProcessor {
        return SchemaChangesProcessor(transactionTemplate, properties)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

}