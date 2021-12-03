package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column
import java.util.*

open class SimpleUUIDTable(name: String = "", columnName: String = "id", autoGen: Boolean = false) :
    SimpleIdTable<UUID>(name) {
    final override val id: Column<UUID> = uuid(columnName).run {
        if (autoGen) this.autoGenerate() else this
    }
    final override val primaryKey = PrimaryKey(id)
}