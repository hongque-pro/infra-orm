package com.labijie.orm.generator

class VisitContext {
    private val tables: MutableList<TableMetadata> = mutableListOf()
    private val visited: MutableSet<Any> = mutableSetOf()

    var currentTable: TableMetadata? = null
        private set

    fun addTable(table: TableMetadata) {
        tables.add(table)
        currentTable = table
    }

    fun getTables(): List<TableMetadata> = tables.toList()

    fun checkVisited(symbol: Any): Boolean {
        return !visited.add(symbol)
    }
}