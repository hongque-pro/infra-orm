package com.labijie.orm.dummy


import com.labijie.infra.orm.SimpleIntIdTable
import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.SimpleStringIdTable
import com.labijie.infra.orm.compile.KspPrimaryKey
import com.labijie.infra.orm.compile.KspTableIgnore
import com.labijie.infra.orm.compile.KspTablePojo
import com.labijie.orm.dummy.otherpackage.NestedInterface
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*


enum class TestEnum {
    OK, Error
}

object PostTable : SimpleLongIdTable("posts", "id") {
    var title = varchar("name", 50).nullable()
    var status = enumeration("status", TestEnum::class)
    var description = varchar("desc", 255)
    var status2 = enumeration("nest_status", NestedInterface.StatusEnum::class)
    var array = array<String>("array")
    var dateTime = datetime("dateTime")
    var duration = duration("duration")
    var time = time("time")
    var date = date("date")
    var timestamp = timestamp("timestamp")
}

object ShopTable : SimpleLongIdTable("shops", "id") {

    var name: Column<String> = varchar("name", 50)
    var status = enumeration("status", TestEnum::class)
    var count = integer("count")
}


@KspTablePojo(superClasses = [SimpleBaseClass::class, SimpleInterface::class])
object IntIdTable : SimpleIntIdTable("exposed_test_entities") {
    var name: Column<String> = varchar("name", 50)
    var memo = varchar("memo", 50).nullable()
    var charCol = char("char")
    var textCol = text("text")
    var enumCol = enumeration("enum", TestEnum::class)
    var binaryCol = binary("data")
    var uidCol = uuid("uid")
    val shortCol = short("short")
    val booleanCol = bool("boolean")
    val byteCol = byte("dddd")
}

object DurationIdEntityTable : Table("duration_id_entities") {
    var name: Column<String> = varchar("name", 50)

    @KspPrimaryKey
    val id = duration("id")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
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



