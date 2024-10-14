/**
 * @author Anders Xiao
 * @date 2024-10-01
 */
package com.labijie.infra.orm.configuration


import org.springframework.dao.DataAccessException
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import org.springframework.transaction.*
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager
import org.springframework.transaction.support.TransactionCallback
import org.springframework.util.Assert
import java.lang.reflect.UndeclaredThrowableException
import java.sql.SQLException

open class JdbcExposedTransactionManager(
    private val properties: InfraExposedProperties,
    private val transactionManager: PlatformTransactionManager
) : CallbackPreferringPlatformTransactionManager {

    private var exceptionTranslator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator()
    protected fun translateException(ex: Throwable): RuntimeException {
        val translator = this.exceptionTranslator
        if (ex is SQLException) {
            val dae: DataAccessException? = translator.translate("Exposed Exec", null, ex)
            if (dae != null) {
                return dae
            }
        }
        if(ex is RuntimeException) {
            return ex
        }
        return TransactionSystemException(ex.message.orEmpty(), ex)
    }

    fun setExceptionTranslator(translator: SQLExceptionTranslator) {
        this.exceptionTranslator = translator
    }

    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        return this.transactionManager.getTransaction(definition)
    }

    override fun commit(status: TransactionStatus) {
        this.transactionManager.commit(status)
    }

    override fun rollback(status: TransactionStatus) {
        this.transactionManager.rollback(status)
    }

    override fun <T : Any?> execute(definition: TransactionDefinition?, callback: TransactionCallback<T>): T? {

        val status = transactionManager.getTransaction(definition)
        val result: T?
        try {
            result = callback.doInTransaction(status)
        } catch (ex: Throwable) {
            val exception: Throwable = if (properties.translateSqlException) {
                translateException(ex)
            } else {
                ex
            }
            rollbackOnException(status, exception)
            throw exception
        }
        transactionManager.commit(status)
        return result
    }

    @Throws(TransactionException::class)
    private fun rollbackOnException(status: TransactionStatus, ex: Throwable) {
        try {
            transactionManager.rollback(status)
        } catch (ex2: TransactionSystemException) {
            ex2.initApplicationException(ex)
            throw ex2
        } catch (ex2: Throwable) {
            throw ex2
        }
    }

}