package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

open class SimpleLongIdTable(name: String = "", columnName: String = "id", autoIncrement: Boolean = false) :
    SimpleIdTable<Long>(name) {
    final override val id: Column<Long> = long(columnName).run {
        if (autoIncrement) this.autoIncrement() else this
    }
    final override val primaryKey = PrimaryKey(id)
}