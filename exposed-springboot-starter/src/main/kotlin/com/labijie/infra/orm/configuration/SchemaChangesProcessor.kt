package com.labijie.infra.orm.configuration

import com.labijie.infra.orm.ExposedUtils
import com.labijie.infra.orm.withoutSqlLog
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

class SchemaChangesProcessor(
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

            withoutSqlLog {
                var hasSql = false
                val sb = StringBuilder()
                sb.appendLine()
                sb.appendLine("-------------Start Migrate Schema--------------")
                sb.appendLine()

                val executedSql = transactionTemplate.execute {
                    val sql = SchemaUtils.statementsRequiredToActualizeScheme(*exposedTables)

                    if (sql.isNotEmpty()) {
                        with(TransactionManager.current()) {
                            this.queryTimeout = 30
                            this.execInBatch(sql)
                            commit()
                            currentDialect.resetCaches()
                        }
                    }
                    sql
                }!!
                hasSql = hasSql || executedSql.isNotEmpty()
                sb.appendLine(executedSql.joinToString(System.lineSeparator()))

                if (properties.generateSchema.allowDropColumns) {

                    try {
                        val commands = transactionTemplate.execute {
                            val sql2 = ExposedUtils.checkExcessiveColumns(*exposedTables)
                            if (sql2.isNotEmpty()) {
                                with(TransactionManager.current()) {
                                    this.queryTimeout = 30
                                    this.execInBatch(sql2.map { it.sql })
                                    commit()
                                    currentDialect.resetCaches()
                                }
                            }
                            sql2
                        }!!
                        hasSql = hasSql || commands.isNotEmpty()
                        sb.appendLine(commands.joinToString(System.lineSeparator()) { it.sql })

                    } catch (e: Throwable) {
                        logger.warn("Drop excessive columns failed, columns patch has been skipped.", e)
                    }

                }
                sb.appendLine("--------------End Migrate Schema---------------")


                if (hasSql && properties.showSql && logger.isInfoEnabled) {
                    logger.info(sb.toString())
                }
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }


}