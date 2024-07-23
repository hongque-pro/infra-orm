/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm


fun <T: Any> withoutSqlLog(action: ()->T?): T? {
    SqlLoggerSettings.apply(false)
    try {
        return action()
    }finally {
        SqlLoggerSettings.reset()
    }
}

fun <T: Any> withSqlLog(action: ()->T?): T? {
    SqlLoggerSettings.apply(true)
    try {
        return action()
    }finally {
        SqlLoggerSettings.reset()
    }
}