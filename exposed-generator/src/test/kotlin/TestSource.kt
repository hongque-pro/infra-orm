import com.labijie.infra.orm.SimpleLongIdTable
import com.labijie.infra.orm.compile.KspTablePojo
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime

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
object TestTable : SimpleLongIdTable("my") {
    var nullableString = varchar("null_str", 32).nullable()
    var array = array<String>("array")
    var name: Column<String> = varchar("name", 50)
    var count = integer("count")
    val status = enumeration("status", Status::class)
    val status2 = enumeration("status2", NestedInterface.NestedEnum::class)
    val dt = datetime("dt")
}