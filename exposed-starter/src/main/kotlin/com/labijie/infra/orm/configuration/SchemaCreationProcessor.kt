package com.labijie.infra.orm.configuration

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import java.util.stream.Collectors

class SchemaCreationProcessor(
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

        if(exposedTables.isNotEmpty()) {
            val sql = SchemaUtils.statementsRequiredToActualizeScheme(*exposedTables)
            with(TransactionManager.current()) {
                this.execInBatch(sql)
                commit()
                currentDialect.resetCaches()
            }
        }
        if (!properties.showSql && logger.isInfoEnabled) {
            val msg = StringBuilder()
                .appendLine("Schema generation for tables: ${exposedTables.count()} tables: ")
                .apply {
                    this.appendLine(exposedTables.joinToString(System.lineSeparator()) { it.tableName })
                }.toString()

            logger.info(msg)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}