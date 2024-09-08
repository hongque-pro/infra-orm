/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm

import com.labijie.infra.orm.configuration.InfraExposedProperties
import com.labijie.infra.orm.interceptor.InfraStatementInterceptor
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.core.env.Environment
import org.springframework.transaction.TransactionExecution
import org.springframework.transaction.TransactionExecutionListener


class ExposedTransactionListener(
    environment: Environment,
    properties: InfraExposedProperties
) : TransactionExecutionListener {


    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger("com.labijie.infra.orm.SQLLogger").apply {
                this.atLevel(Level.INFO)
            }
        }

    }

    private class Slf4jSqlLogger(
        private val isProduction: Boolean,
        private val properties: InfraExposedProperties
    ) : SqlLogger {

        private fun findStack(): StackTraceElement? {
            val stack = Thread.currentThread().stackTrace
            var index = 5
            while (stack.size > index && index < 100) {
                val current = Thread.currentThread().stackTrace[index]
                val c = current.className
                val m = current.methodName
                while (!c.startsWith("org.jetbrains.exposed") &&
                    !c.startsWith("org.springframework.") &&
                    !c.startsWith("kotlin.") &&
                    !c.endsWith("DSL") &&
                    !c.contains(".pojo.dsl.") &&
                    !m.contains("\$lambda")
                ) {
                    return current
                }
                index++
            }
            return null
        }

        /**
         * Logs a message containing the string representation of a complete SQL statement.
         *
         * **Note:** This is only logged if DEBUG level is currently enabled.
         */
        override fun log(context: StatementContext, transaction: Transaction) {
            if ((SqlLoggerSettings.isLogEnabled() ?: properties.showSql) && logger.isInfoEnabled) {
                if (!isProduction) {
                    val prefix = findStack()?.let {
                        "[${it.className}::${it.methodName}] "
                    } ?: ""
                    logger.info("$prefix${context.expandArgs(TransactionManager.current())}")
                } else {
                    logger.info(context.expandArgs(TransactionManager.current()))
                }
            }
        }
    }

    private val sqlLogger = Slf4jSqlLogger(
        environment.activeProfiles.contains("prod") || environment.activeProfiles.contains("production"),
        properties
    )

    override fun afterBegin(transaction: TransactionExecution, beginFailure: Throwable?) {
        super.afterBegin(transaction, beginFailure)
        val tr = TransactionManager.current()
        //tr.registerInterceptor(InfraStatementInterceptor)
        tr.addLogger(sqlLogger)
    }
}