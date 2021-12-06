package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

abstract class SimpleIntIdTable(name: String = "", columnName: String = "id", autoIncrement: Boolean = false) :
    SimpleIdTable<Int>(name) {
    final override val id: Column<Int> = integer(columnName).run {
        if (autoIncrement) this.autoIncrement() else this
    }
    final override val primaryKey = PrimaryKey(id)
}