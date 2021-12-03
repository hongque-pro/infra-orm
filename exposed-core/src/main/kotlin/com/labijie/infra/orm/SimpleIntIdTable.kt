package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

open class SimpleIntIdTable(name: String = "", columnName: String = "id") : SimpleIdTable<Int>(name) {
    final override val id: Column<Int> = integer(columnName)
    final override val primaryKey = PrimaryKey(id)
}