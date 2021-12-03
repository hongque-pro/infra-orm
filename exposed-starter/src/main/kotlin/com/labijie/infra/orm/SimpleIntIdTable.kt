package com.labijie.infra.orm

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

open class SimpleIntIdTable(name: String = "", columnName: String = "id") : SimpleIdTable<Int>(name) {
    final override val id: Column<Int> = integer(columnName)
    override val primaryKey = PrimaryKey(id)
}