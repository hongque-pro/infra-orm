/**
 * @author Anders Xiao
 * @date 2024-10-01
 */
package com.labijie.infra.orm.testing.tables

import com.labijie.infra.orm.SimpleLongIdTable

object TestTable : SimpleLongIdTable("test") {
    val name1 = varchar("name1", 32).index()
    val name2 = varchar("name2", 32).index()
    val delete = bool("deleted").index()
}