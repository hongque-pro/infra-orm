/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


object ExposedUtils {
    fun getInfraOrmGitProperties(): Properties {
        val systemResources: Enumeration<URL> =
            (ExposedUtils::class.java.classLoader ?: ClassLoader.getSystemClassLoader()).getResources("git-info/git.properties")
        while (systemResources.hasMoreElements()) {
            systemResources.nextElement().openStream().use { stream ->
                val properties = Properties().apply {
                    this.load(stream)
                }.let {
                    if (it.getProperty("project.group") == "com.labijie.orm") {
                        it
                    } else null
                }
                if(properties != null) {
                    return properties
                }
            }
        }
        return Properties()
    }

    data class DropColumnCommand(
        val table: String,
        val sql: String,
        val column: String
    )

    fun checkExcessiveColumns(vararg tables: Table): List<DropColumnCommand> {
        val statements = ArrayList<DropColumnCommand>()

        val dbSupportsAlterTableWithAddColumn = TransactionManager.current().db.supportsAlterTableWithAddColumn
        if (dbSupportsAlterTableWithAddColumn) {
            val existingTablesColumns = currentDialect.tableColumns(*tables)
            //           val existingIndices = currentDialect.existingIndices(*tables)

            for (table in tables) {
                // create columns
                val thisTableExistingColumns = existingTablesColumns[table].orEmpty()
                val codeColumnNames = table.columns.map { it.nameUnquoted().lowercase() }

                val excessiveColumns =
                    thisTableExistingColumns.filter { it.name.lowercase() !in codeColumnNames }.map { it }
//                val excessiveColumnNames = excessiveColumns.map { it.name.lowercase() }
//
//                val thisTableExistingIndices = existingIndices[table].orEmpty()
//                val indexesToDrop = thisTableExistingIndices.filter { it.columns.any { c-> c.name.lowercase() in excessiveColumnNames } }
//
//                if (indexesToDrop.isNotEmpty()) {
//                    indexesToDrop.flatMapTo(statements) { index -> index.dropStatement() }
//                }

                if (excessiveColumns.isNotEmpty()) {
                    excessiveColumns.mapTo(statements) { col ->
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