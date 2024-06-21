package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.AdditionalSchemaUtils
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect
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
        val exposedTables =
            context.getBeanProvider(Table::class.java).orderedStream().collect(Collectors.toList()).toTypedArray()

        if (exposedTables.isNotEmpty()) {
            transactionTemplate.execute {
                val sql = SchemaUtils.statementsRequiredToActualizeScheme(*exposedTables)

                if (sql.isNotEmpty()) {
                    with(TransactionManager.current()) {
                        this.queryTimeout = 30
                        this.execInBatch(sql)
                        commit()
                        currentDialect.resetCaches()
                    }

                }

                val sql2 = AdditionalSchemaUtils.checkExcessiveColumns(*exposedTables)
                if(sql2.isNotEmpty()) {
                    try {
                        with(TransactionManager.current()) {
                            this.queryTimeout = 30
                            this.execInBatch(sql)
                            commit()
                            currentDialect.resetCaches()
                        }
                    }
                    catch (e: Throwable) {
                        logger.warn("Drop excessive columns failed, columns patch has been skipped.", e)
                    }
                }
            }
        }



        if (!properties.showSql && logger.isInfoEnabled) {
            val msg = StringBuilder()
                .appendLine("Schema of tables modified: ${exposedTables.count()} tables: ")
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