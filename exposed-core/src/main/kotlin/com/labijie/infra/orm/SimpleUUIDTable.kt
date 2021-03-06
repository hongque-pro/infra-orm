package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column
import java.util.*

abstract class SimpleUUIDTable(name: String, idColumnName: String = "id", autoGen: Boolean = false) :
    SimpleIdTable<UUID>(name) {
    final override val id: Column<UUID> = uuid(idColumnName).run {
        if (autoGen) this.autoGenerate() else this
    }
    final override val primaryKey = PrimaryKey(id)
}