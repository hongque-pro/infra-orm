/**
 * @author Anders Xiao
 * @date 2024-09-03
 */
package com.labijie.infra.orm.interceptor

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.springframework.context.ApplicationContext
import javax.sql.DataSource


object InfraStatementInterceptor : StatementInterceptor {

    var springContext: ApplicationContext? = null

    override fun afterStatementPrepared(transaction: Transaction, preparedStatement: PreparedStatementApi) {
        super.afterStatementPrepared(transaction, preparedStatement)
        val dataSource = springContext?.getBeanProvider(DataSource::class.java)?.ifAvailable
        if(dataSource != null) {
            transaction.currentStatement = PreparedStatementApiProxy(preparedStatement, dataSource).getProxy()
        }
    }

}