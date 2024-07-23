/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.infra.orm


fun <T: Any> withoutSqlLog(action: ()->T?): T? {
    SqlLoggerSettings.apply {
        allowSqlLogger = false
    }
    return action()
}