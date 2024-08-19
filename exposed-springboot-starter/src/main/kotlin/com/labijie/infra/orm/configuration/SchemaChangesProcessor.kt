package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.DatabaseMigration
import org.jetbrains.exposed.sql.Table
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.transaction.support.TransactionTemplate
import java.util.stream.Collectors

class SchemaChangesProcessor(
    private val transactionTemplate: TransactionTemplate,
    private val properties: InfraExposedProperties
) : ApplicationListener<ApplicationStartedEvent>, ApplicationContextAware {

    private lateinit var context: ApplicationContext


    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val exposedTables =
            context.getBeanProvider(Table::class.java).orderedStream().collect(Collectors.toList()).toTypedArray()

        if (exposedTables.isNotEmpty()) {
            DatabaseMigration.migrate(transactionTemplate, exposedTables, properties.generateSchema.allowDropColumns, properties.showSql)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

}