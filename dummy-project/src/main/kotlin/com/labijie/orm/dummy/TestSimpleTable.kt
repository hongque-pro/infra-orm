package com.labijie.orm.dummy

import com.labijie.infra.orm.SimpleStringIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.duration

/**
 * @author Anders Xiao
 * @date 2025/6/13
 */
object TestSimpleTable : SimpleStringIdTable("exposed_test_entities") {
    var name: Column<String> = varchar("name", 50)
    var memo = varchar("memo", 50).nullable()
    var char = char("char")
    var text = text("text")
    var enum = enumeration("enum", TestEnum::class)
    var binary = binary("data")
    var uid = uuid("uid")
    val short = short("short")
    val bool = bool("boolean")
    val byte = byte("byte")
    val dateTime = datetime("date_time")
    val duration = duration("duration")

    var nameNullable = varchar("name_n", 50).nullable()
    var memoNullable = varchar("memo_n", 50).nullable()
    var charNullable = char("char_n").nullable()
    var textNullable = text("text_n").nullable()
    var enumNullable = enumeration("enum_n", TestEnum::class).nullable()
    var binaryNullable = binary("data_n").nullable()
    var uidNullable = uuid("uid_n").nullable()
    val shortNullable = short("short_n").nullable()
    val booleanNullable = bool("boolean_n").nullable()
    val byteNullable = byte("byte_n").nullable()
    val dateTimeNullable = datetime("date_time_n").nullable()
    val durationNullable = duration("duration_n")
}