/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm


internal class SqlLoggerSettings private constructor() {
    companion object {
        private val localSettings = ThreadLocal<SqlLoggerSettings>()

        @JvmStatic
        fun apply(action: ((SqlLoggerSettings).()->Unit)? = null): SqlLoggerSettings {
            val value = localSettings.get() ?: SqlLoggerSettings().apply {
                localSettings.set(this)
            }
            action?.invoke(value)
            return value
        }

        @JvmStatic
        fun isLogEnabled(): Boolean {
            val v = localSettings.get()
            return v == null || v.allowSqlLogger
        }

        @JvmStatic
        fun reset() {
            localSettings.remove()
        }
    }

    var allowSqlLogger: Boolean = true
}