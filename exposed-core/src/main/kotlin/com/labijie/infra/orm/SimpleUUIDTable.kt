package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column
import java.util.*

open class SimpleUUIDTable(name: String = "", columnName: String = "id") : SimpleIdTable<UUID>(name) {
    final override val id: Column<UUID> = uuid(columnName)
    final override val primaryKey = PrimaryKey(id)
}