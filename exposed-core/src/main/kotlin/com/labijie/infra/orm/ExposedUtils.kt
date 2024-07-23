/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect


object ExposedUtils {

    data class DropColumnCommand(
        val table: String,
        val sql: String,
        val column: String
    )

    fun checkExcessiveColumns(vararg tables: Table, withLogs: Boolean = true): List<DropColumnCommand> {
        val statements = ArrayList<DropColumnCommand>()

        val dbSupportsAlterTableWithAddColumn = TransactionManager.current().db.supportsAlterTableWithAddColumn
        if (dbSupportsAlterTableWithAddColumn) {
            val existingTablesColumns = currentDialect.tableColumns(*tables)
            val existingIndices = currentDialect.existingIndices(*tables)

            for (table in tables) {
                // create columns
                val thisTableExistingColumns = existingTablesColumns[table].orEmpty()
                val codeColumnNames = table.columns.map { it.nameUnquoted().lowercase() }

                val excessiveColumns = thisTableExistingColumns.filter { it.name.lowercase() !in codeColumnNames }.map { it }
//                val excessiveColumnNames = excessiveColumns.map { it.name.lowercase() }
//
//                val thisTableExistingIndices = existingIndices[table].orEmpty()
//                val indexesToDrop = thisTableExistingIndices.filter { it.columns.any { c-> c.name.lowercase() in excessiveColumnNames } }
//
//                if (indexesToDrop.isNotEmpty()) {
//                    indexesToDrop.flatMapTo(statements) { index -> index.dropStatement() }
//                }

                if (excessiveColumns.isNotEmpty()) {
                    excessiveColumns.mapTo(statements) {
                        col->
                        DropColumnCommand(
                            table = table.tableName,
                            column = col.name,
                            sql = table.dropColumn(col.name)
                        )
                    }
                }
            }
        }
        return statements
    }
}