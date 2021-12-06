package com.labijie.infra.orm.configuration

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.transaction.support.TransactionTemplate
import java.util.stream.Collectors

class SchemaCreationProcessor(
    private val transactionTemplate: TransactionTemplate,
    private val properties: InfraExposedProperties
) : ApplicationListener<ApplicationStartedEvent>, ApplicationContextAware {
    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(InfraExposedAutoConfiguration::class.java)
        }
    }

    private lateinit var context: ApplicationContext


    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val exposedTables = context.getBeanProvider(Table::class.java).orderedStream().collect(Collectors.toList()).toTypedArray()

        transactionTemplate.execute {
            if (!properties.showSql && logger.isInfoEnabled) {
                val msg = StringBuilder()
                    .appendLine("Schema generation for tables: ${exposedTables.count()} tables: ")
                    .apply {
                        exposedTables.forEach {
                            this.appendLine(it.ddl.joinToString(System.lineSeparator()))
                        }
                    }.toString()

                logger.info(msg)
            }
            SchemaUtils.create(*exposedTables)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}