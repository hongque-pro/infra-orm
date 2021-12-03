package com.labijie.infra.orm

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

abstract class SimpleIdTable<T : Comparable<T>>(name: String) : Table(name) {
    abstract val id: Column<T>
}