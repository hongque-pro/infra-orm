package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

open class SimpleStringIdTable(name: String = "", columnName: String = "id", lenght: Int = 32) : SimpleIdTable<String>(name) {
    final override val id: Column<String> = varchar(columnName, lenght)
    override val primaryKey = PrimaryKey(id)
}