/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
package com.labijie.infra.orm

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionTemplate


object DatabaseMigration {
    private val logger by lazy { LoggerFactory.getLogger(DatabaseMigration::class.java) }

    fun migrate(transactionTemplate: TransactionTemplate, tables: Array<out Table>, allowDropColumns:Boolean = false, logSql: Boolean) {
        withoutSqlLog {
            var hasSql = false
            val sb = StringBuilder()
            sb.appendLine()
            sb.appendLine("-------------Start Migrate Schema--------------")
            sb.appendLine()

            val executedSql = transactionTemplate.execute {
                val sql = SchemaUtils.statementsRequiredToActualizeScheme(*tables)

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

            if (allowDropColumns) {

                try {
                    val commands = transactionTemplate.execute {
                        val sql2 = ExposedUtils.checkExcessiveColumns(*tables)
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


            if (hasSql && logSql && logger.isInfoEnabled) {
                logger.info(sb.toString())
            }
        }
    }
}