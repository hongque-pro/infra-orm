package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/28
 * @Description:
 */
fun String.toUUID(): UUID {
    return UUID.fromString(this)
}


fun Table.dropColumn(column: String): String {
    val tr = TransactionManager.current()
    val columnName = tr.db.identifierManager.quoteIdentifierWhenWrongCaseOrNecessary(column)
    return "ALTER TABLE ${tr.identity(this)} DROP COLUMN $columnName"
}
