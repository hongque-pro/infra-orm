package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column

abstract class SimpleLongIdTable(name: String, idColumnName: String = "id", autoIncrement: Boolean = false) :
    SimpleIdTable<Long>(name) {
    final override val id: Column<Long> = long(idColumnName).run {
        if (autoIncrement) this.autoIncrement() else this
    }
    final override val primaryKey = PrimaryKey(id)
}