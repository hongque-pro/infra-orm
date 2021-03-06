package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

abstract class SimpleStringIdTable(name: String, idColumnName: String = "id", length: Int = 32) : SimpleIdTable<String>(name) {
    final override val id: Column<String> = varchar(idColumnName, length)
    final override val primaryKey = PrimaryKey(id)
}