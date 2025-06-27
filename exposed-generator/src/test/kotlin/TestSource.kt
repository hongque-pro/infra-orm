import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.SimpleStringIdTable
import com.labijie.infra.orm.compile.KspTablePojo
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.duration

enum class Status {
    OK,
    Failed
}

interface NestedInterface {
    enum class NestedEnum {
        Default,
        Failed
    }
}

interface TestInterface {}


@KspTablePojo(superClasses = [TestInterface::class])
object TestSimpleTable : SimpleStringIdTable("exposed_test_entities") {
    var name: Column<String> = varchar("name", 50)
    var memo = varchar("memo", 50).nullable()
    var char = char("char")
    var text = text("text")
    var enum = enumeration("enum", Status::class)
    var binary = binary("data")
    var uid = uuid("uid")
    val short = short("short")
    val bool = bool("boolean")
    val byte = byte("byte")
    val dateTime = datetime("date_time")
    val duration = duration("duration")
    val bigDecimal = decimal("bigDecimal", 10, 10)

    var nameNullable = varchar("name_n", 50).nullable()
    var memoNullable = varchar("memo_n", 50).nullable()
    var charNullable = char("char_n").nullable()
    var textNullable = text("text_n").nullable()
    var enumNullable = enumeration("enum_n", Status::class).nullable()
    var binaryNullable = binary("data_n").nullable()
    var uidNullable = uuid("uid_n").nullable()
    val shortNullable = short("short_n").nullable()
    val booleanNullable = bool("boolean_n").nullable()
    val byteNullable = byte("byte_n").nullable()
    val dateTimeNullable = datetime("date_time_n").nullable()
    val durationNullable = duration("duration_n")
    val bigDecimalNullable = decimal("bigDecimal_n", 10, 10)
}