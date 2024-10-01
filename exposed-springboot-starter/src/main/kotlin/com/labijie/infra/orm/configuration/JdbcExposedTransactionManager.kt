/**
 * @author Anders Xiao
 * @date 2024-10-01
 */
package com.labijie.infra.orm.configuration


import org.springframework.dao.DataAccessException
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.TransactionSystemException
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager
import org.springframework.transaction.support.TransactionCallback
import java.sql.SQLException

class JdbcExposedTransactionManager(
    private val properties: InfraExposedProperties,
    private val transactionManager: PlatformTransactionManager) : CallbackPreferringPlatformTransactionManager {

    private var exceptionTranslator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator()
    protected fun translateException(ex: Throwable): RuntimeException {
        val translator = this.exceptionTranslator
        if(ex is SQLException) {
            val dae: DataAccessException? = translator.translate("Exposed Exec", null, ex)
            if (dae != null) {
                return dae
            }
        }
        throw TransactionSystemException(ex.message.orEmpty(), ex)
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
        val status = this.transactionManager.getTransaction(definition)
        return try {
            callback.doInTransaction(status)
        }catch (e: Throwable) {
            if(properties.translateSqlException) {
                throw translateException(e)
            }else {
                throw e
            }
        }
    }

}