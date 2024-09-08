/**
 * @author Anders Xiao
 * @date 2024-09-03
 */
package com.labijie.infra.orm.interceptor

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLErrorCodesFactory
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator
import java.lang.reflect.Method
import java.sql.SQLException
import javax.sql.DataSource


class PreparedStatementApiProxy(private val target: PreparedStatementApi, private val dataSource: DataSource) :
    MethodInterceptor {

    private var exceptionTranslator: SQLExceptionTranslator? = null

    private val userProvidedErrorCodesFilePresent by lazy {
        ClassPathResource(
            SQLErrorCodesFactory.SQL_ERROR_CODE_OVERRIDE_PATH,
            SQLErrorCodesFactory::class.java.classLoader
        ).exists()
    }

    private fun getExceptionTranslator(): SQLExceptionTranslator {
        var exceptionTranslator = this.exceptionTranslator
        if (exceptionTranslator != null) {
            return exceptionTranslator
        }
        synchronized(this) {
            exceptionTranslator = this.exceptionTranslator
            if (exceptionTranslator == null) {
                exceptionTranslator = if (userProvidedErrorCodesFilePresent) {
                    SQLErrorCodeSQLExceptionTranslator(dataSource)
                } else {
                    SQLExceptionSubclassTranslator()
                }
                this.exceptionTranslator = exceptionTranslator
            }
            return exceptionTranslator!!
        }
    }

    override fun intercept(thisRef: Any, method: Method, args: Array<out Any>, proxy: MethodProxy): Any? {
        try {
            val result = proxy.invoke(target, args)
            return result
        } catch (e: ExposedSQLException) {
            if (e.cause is SQLException) {
                getExceptionTranslator().translate("Sql exec", null, e)?.let {
                    throw it
                }
            }
            getExceptionTranslator().translate("Sql exec", null, e)?.let {
                throw it
            }
            throw e
        } catch (e: Throwable) {
            throw e
        }
    }


    fun getProxy(): PreparedStatementApi {

        val enhancer = Enhancer()
        enhancer.setSuperclass(PreparedStatementApi::class.java)
        enhancer.setCallback(this)
        val proxy = enhancer.create()
        if (proxy is PreparedStatementApi) {
            return proxy
        }
        throw IllegalStateException("Unable to got proxy for ${PreparedStatementApi::class.java.simpleName}")
    }
}