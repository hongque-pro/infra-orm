/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm

import java.util.Stack


internal class SqlLoggerSettings private constructor() {
    companion object {
        private val localSettings = ThreadLocal<SqlLoggerSettings>()

        @JvmStatic
        fun apply(enabled: Boolean): SqlLoggerSettings {
            val value = localSettings.get() ?: SqlLoggerSettings().apply {
                localSettings.set(this)
            }
            value.enableStack.push(enabled)
            return value
        }

        @JvmStatic
        fun isLogEnabled(): Boolean? {
            return localSettings.get()?.enableStack?.peek()
        }

        @JvmStatic
        fun reset() {
            val v = localSettings.get()
            if(v != null) {
                v.enableStack.pop()
                if(v.enableStack.isEmpty()) {
                    localSettings.remove()
                }
            }
        }
    }

    var enableStack: Stack<Boolean> = Stack()
}