

package com.labijie.orm.dummy

import com.labijie.orm.dummy.otherpackage.NestedInterface
import com.labijie.orm.dummy.pojo.Test
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.apache.commons.logging.LogFactory
import org.springframework.boot.CommandLineRunner
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
class CommandLineTestRunner : CommandLineRunner {

    private val logger by lazy {
        LogFactory.getLog(CommandLineTestRunner::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun run(vararg args: String?) {
        repeat(10) {
            logger.info("Testing $it...")

            val createTest = createRandomTest()
            val bytes = ProtoBuf.encodeToByteArray(Test.serializer(), createTest)

            val decoded = ProtoBuf.decodeFromByteArray(Test.serializer(), bytes)

            assertTestEquals(createTest, decoded)
        }
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    fun assertTestEquals(expected: Test, actual: Test) {
        assert(expected.title == actual.title) { "title mismatch: ${expected.title} != ${actual.title}" }
        assert(expected.status == actual.status) { "status mismatch: ${expected.status} != ${actual.status}" }
        assert(expected.description == actual.description) { "description mismatch: ${expected.description} != ${actual.description}" }
        assert(expected.status2 == actual.status2) { "status2 mismatch: ${expected.status2} != ${actual.status2}" }
        assert(expected.array.toTypedArray().contentEquals(actual.array.toTypedArray())) { "array mismatch: ${expected.array} != ${actual.array}" }

        assert(expected.dateTime.equals(actual.dateTime)) { "dateTime mismatch: ${expected.dateTime} != ${actual.dateTime}" }
        assert(expected.duration.equals(actual.duration)) { "duration mismatch: ${expected.duration} != ${actual.duration}" }
        assert(expected.time.equals(actual.time)) { "time mismatch: ${expected.time} != ${actual.time}" }
        assert(expected.date.equals(actual.date)) { "date mismatch: ${expected.date} != ${actual.date}" }
        assert(expected.timestamp.equals(actual.timestamp)) { "timestamp mismatch: ${expected.timestamp} != ${actual.timestamp}" }

        assert(expected.decimal.equals(actual.decimal)) { "decimal mismatch: ${expected.decimal} != ${actual.decimal}" }
        assert(expected.decimalNullable == actual.decimalNullable) { "decimalNullable mismatch: ${expected.decimalNullable} != ${actual.decimalNullable}" }

        assert(expected.uuid.equals(actual.uuid)) { "uuid mismatch: ${expected.uuid} != ${actual.uuid}" }
        assert(expected.uuidNullable == actual.uuidNullable) { "uuidNullable mismatch: ${expected.uuidNullable} != ${actual.uuidNullable}" }

        assert(expected.datetime.equals(actual.datetime)) { "datetime mismatch: ${expected.datetime} != ${actual.datetime}" }
        assert(expected.datetimeNullable?.equals(actual.datetimeNullable) ?: (actual.datetimeNullable == null)) { "datetimeNullable mismatch: ${expected.datetimeNullable} != ${actual.datetimeNullable}" }

        assert(expected.id == actual.id) { "id mismatch: ${expected.id} != ${actual.id}" }
    }

    fun createRandomTest(): Test {
        val random = Random(System.nanoTime())

        fun <T : Enum<T>> randomEnum(enumClass: Class<T>): T {
            val constants = enumClass.enumConstants
            return constants[random.nextInt(constants.size)]
        }


        return Test().apply {
            title = if (random.nextBoolean()) "Title ${random.nextInt(1000)}" else null
            status = randomEnum(TestEnum::class.java)
            description = "Desc ${UUID.randomUUID()}"
            status2 = randomEnum(NestedInterface.StatusEnum::class.java)
            array = List(random.nextInt(1, 5)) { "Item-${random.nextInt(1000)}" }

            dateTime = LocalDateTime.ofEpochSecond(random.nextLong(0, 1_000_000_000), random.nextInt(0, 1_000_000_000), ZoneOffset.UTC)
            duration = Duration.ofMillis(random.nextLong(0, 10_000))
            time = LocalTime.ofSecondOfDay(random.nextLong(0, 86400))
            date = LocalDate.ofEpochDay(random.nextLong(0, 365 * 50)) // 50年范围
            timestamp = Instant.ofEpochSecond(random.nextLong(0, 1_000_000_000))

            decimal = BigDecimal.valueOf(random.nextDouble(0.0, 10000.0)).setScale(2, RoundingMode.HALF_UP)
            decimalNullable = if (random.nextBoolean()) BigDecimal.valueOf(random.nextDouble(0.0, 10000.0)) else null

            uuid = UUID.randomUUID()
            uuidNullable = if (random.nextBoolean()) UUID.randomUUID() else null

            datetime = LocalDateTime.ofEpochSecond(random.nextLong(0, 1_000_000_000), random.nextInt(0, 1_000_000_000), ZoneOffset.UTC)
            datetimeNullable = if (random.nextBoolean())
                LocalDateTime.ofEpochSecond(random.nextLong(0, 1_000_000_000), 0, ZoneOffset.UTC)
            else null

            id = random.nextLong(1, Long.MAX_VALUE)
        }
    }
}