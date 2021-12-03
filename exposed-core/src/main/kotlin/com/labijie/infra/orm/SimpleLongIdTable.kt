package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

open class SimpleLongIdTable(name: String = "", columnName: String = "id") : SimpleIdTable<Long>(name) {
    final override val id: Column<Long> = long(columnName)
    final override val primaryKey = PrimaryKey(id)
}