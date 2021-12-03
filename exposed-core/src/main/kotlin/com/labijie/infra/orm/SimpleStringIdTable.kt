package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

open class SimpleStringIdTable(name: String = "", columnName: String = "id", length: Int = 32) : SimpleIdTable<String>(name) {
    final override val id: Column<String> = varchar(columnName, length)
    final override val primaryKey = PrimaryKey(id)
}