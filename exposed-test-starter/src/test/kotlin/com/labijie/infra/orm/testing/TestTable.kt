package com.labijie.infra.orm.testing

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object TestEntityTable : IntIdTable("exposed_test_entities") {
    val name: Column<String> = varchar("name", 50)
}