/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm

import com.labijie.infra.orm.configuration.InfraExposedProperties
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.transaction.TransactionExecution
import org.springframework.transaction.TransactionExecutionListener


class ExposedTransactionListener(private val properties: InfraExposedProperties) : TransactionExecutionListener {

   companion object {
       private val logger by lazy {
           LoggerFactory.getLogger("SQLLogger").apply { 
               this.atLevel(Level.INFO)
           }
       }

   }

    private object Slf4jSqlLogger : SqlLogger {
        /**
         * Logs a message containing the string representation of a complete SQL statement.
         *
         * **Note:** This is only logged if DEBUG level is currently enabled.
         */
        override fun log(context: StatementContext, transaction: Transaction) {
            if (logger.isInfoEnabled) {
                logger.info(context.expandArgs(TransactionManager.current()))
            }
        }
    }

    override fun afterBegin(transaction: TransactionExecution, beginFailure: Throwable?) {
        super.afterBegin(transaction, beginFailure)
        val tr = TransactionManager.current()
        if(properties.showSql && SqlLoggerSettings.isLogEnabled()) {
            tr.addLogger(Slf4jSqlLogger)
        }
    }
}