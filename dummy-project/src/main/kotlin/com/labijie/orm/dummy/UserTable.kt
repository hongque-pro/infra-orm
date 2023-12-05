package com.labijie.orm.dummy


import com.labijie.infra.orm.SimpleIntIdTable
import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.SimpleStringIdTable
import com.labijie.infra.orm.compile.KspTableIgnore
import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.*


enum class TestEnum {
    OK, Error
}
object UserTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
    var description = varchar("desc", 255)

    override val tableName: String
        get() = super.tableName
}

object ShopTable : SimpleLongIdTable("my", "id") {
    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
}

object IntIdTable : SimpleIntIdTable("exposed_test_entities") {
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

@KspTableIgnore
object IgnoreTable : SimpleStringIdTable("ignore") {
    var name: Column<String> = varchar("name", 50)
}

object MultiKeyTable : Table("ignore") {

    @KspPrimaryKey
    val key1 = varchar("key1", 50)

    @KspPrimaryKey
    val key2 = integer("key2")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(key1, key2)
}


enum class Status(val code: Byte, val description: String) {
    OK(0, "OK"),
    Failed(1, "Failed")
}

object DescribeEnumTable : Table("ignore") {

    @KspPrimaryKey
    val key1 = varchar("key1", 50)

    @KspPrimaryKey
    val key2 = integer("key2")

    val status = enumeration("status", Status::class)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(key1, key2)
}