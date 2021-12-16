package com.labijie.orm.dummy


import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.SimpleStringIdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

enum class TestEnum {
    OK, Error
}
object UserTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
}

object ShopTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
}

object IntIdTable : IntIdTable("exposed_test_entities") {
    var name: Column<String> = varchar("name", 50)
    var memo = varchar("name", 50).nullable()
    var charCol = char("cc")
    var textCol = text("tt")
    var enumCol = enumeration("dd", TestEnum::class)
    var binaryCol = binary("data")
    var uidCol = uuid("uid")
    val shortCol = short("sht")
    val booleanCol = bool("sht")
    val byteCol = byte("dddd")

}



object TestSimpleTable : SimpleStringIdTable("exposed_test_entities") {
    var name: Column<String> = varchar("name", 50)
    var memo = varchar("name", 50).nullable()
    var charCol = char("cc")
    var textCol = text("tt")
    var enumCol = enumeration("dd", TestEnum::class)
    var binaryCol = binary("data")
    var uidCol = uuid("uid")
    val shortCol = short("sht")
    val booleanCol = bool("sht")
    val byteCol = byte("dddd")
}

fun AA(){
//    val list = mutableListOf<TestEntity>()
//    val r = TestEntityTable.batchInsert(list){raw->
//        TestEntityDSL.applyInsert(this, raw)
//    }
}