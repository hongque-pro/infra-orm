package com.labbijie.infra.orm.testing

import com.labijie.infra.orm.serialization.OrmBigDecimal
import com.labijie.infra.orm.serialization.OrmDuration
import com.labijie.infra.orm.serialization.OrmInstant
import com.labijie.infra.orm.serialization.OrmLocalDate
import com.labijie.infra.orm.serialization.OrmLocalDateTime
import com.labijie.infra.orm.serialization.OrmLocalTime
import com.labijie.infra.orm.serialization.OrmUUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
@OptIn(ExperimentalSerializationApi::class)
class SerializationTester {

    companion object {

        private const val bdExpected1 = "725345854747326287606413621318.311864440287151714280387858224"
        private const val bdExpected2 = "336052472523017262165484244513.836582112201211216526831524328"
        private const val bdExpected3 = "211054843014778386028147282517.011200287614476453868782405400"
        private const val bdExpected4 = "364751025728628060231208776573.207325218263752602211531367642"
        private const val bdExpected5 = "508257556021513833656664177125.824502734715222686411316853148"
        private const val bdExpected6 = "127134584027580606401102614002.366672301517071543257300444000"
    }

    @Serializable
    private data class BigDecimalHolder(
        val bd: OrmBigDecimal,
        val bdNullable: OrmBigDecimal?
    )

    @Serializable
    private data class UUIDHolder(
        val bd: OrmUUID,
        val bdNullable: OrmUUID?
    )

    @Serializable
    private data class DurationHolder(
        val d: OrmDuration,
        val dNullable: OrmDuration?
    )

    @Serializable
    private data class LocalDateTimeHolder(
        val dt: OrmLocalDateTime,
        val dtNullable: OrmLocalDateTime?
    )

    @Serializable
    private data class LocalDateHolder(
        val d: OrmLocalDate,
        val dNullable: OrmLocalDate?
    )

    @Serializable
    private data class LocalTimeHolder(
        val t: OrmLocalTime,
        val tNullable: OrmLocalTime?
    )

    @Serializable
    private data class InstantHolder(
        val i: OrmInstant,
        val iNullable: OrmInstant?
    )

    private fun randomInstant(): Instant {
        val seconds = Random.nextLong(0, Instant.now().epochSecond) // up to now
        val nanos = Random.nextInt(0, 1_000_000_000)
        return Instant.ofEpochSecond(seconds, nanos.toLong())
    }

    private fun randomLocalTime(): LocalTime {
        val second = Random.nextInt(0, 24 * 60 * 60)
        val nanos = Random.nextInt(0, 1_000_000_000)
        return LocalTime.ofSecondOfDay(second.toLong()).withNano(nanos)
    }

    private fun randomLocalDate(): LocalDate {
        val startEpoch = LocalDate.of(2000, 1, 1).toEpochDay()
        val randomEpoch = startEpoch + Random.nextLong(0, 10_000)
        return LocalDate.ofEpochDay(randomEpoch)
    }

    private fun randomLocalDateTime(): LocalDateTime {
        val base = LocalDateTime.of(2000, 1, 1, 0, 0)
        val daysToAdd = Random.nextLong(0, 10_000)
        val seconds = Random.nextLong(0, 86_400) // up to 1 day
        val nanos = Random.nextInt(0, 1_000_000_000)
        return base.plusDays(daysToAdd).plusSeconds(seconds).plusNanos(nanos.toLong())
    }

    @ParameterizedTest
    @ValueSource(strings = [bdExpected1, bdExpected2,bdExpected3, bdExpected4, bdExpected5, bdExpected6])
    fun testBigDecimalJson(value: String) {
        val  v = BigDecimal(value)
        val holderOrigin = BigDecimalHolder(v, if(Random.nextBoolean()) null else v)

        val json = Json.encodeToString(BigDecimalHolder.serializer(), holderOrigin)
        val holder = Json.decodeFromString<BigDecimalHolder>(json)

        assertEquals(holderOrigin, holder)
    }

    @ParameterizedTest
    @ValueSource(strings = [bdExpected1, bdExpected2,bdExpected3, bdExpected4, bdExpected5, bdExpected6])
    fun testBigDecimalProtobuf(value: String) {
        val  v = BigDecimal(value)
        val holderOrigin = BigDecimalHolder(v, if(Random.nextBoolean()) null else v)

        val json = ProtoBuf.encodeToByteArray(BigDecimalHolder.serializer(), holderOrigin)
        val holder = ProtoBuf.decodeFromByteArray(BigDecimalHolder.serializer(), json)

        assertEquals(holderOrigin, holder)
    }

    @Test
    fun testUUIDJson() {
        repeat(10) {
            val  v = UUID.randomUUID()
            val holderOrigin = UUIDHolder(v, if(Random.nextBoolean()) null else v)

            val json = Json.encodeToString(UUIDHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString<UUIDHolder>(json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testUUIDProtobuf() {

        repeat(10) {

            val  v = UUID.randomUUID()
            val holderOrigin = UUIDHolder(v, if(Random.nextBoolean()) null else v)

            val json = ProtoBuf.encodeToByteArray(UUIDHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(UUIDHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testDurationJson() {
        repeat(10) {
            val d = Duration.ofSeconds(Random.nextLong(0, 3600), Random.nextLong(0, 1_000_000_000))
            val holderOrigin = DurationHolder(d, if (Random.nextBoolean()) null else d)

            val json = Json.encodeToString(DurationHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString(DurationHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testDurationProtobuf() {
        repeat(10) {
            val d = Duration.ofSeconds(Random.nextLong(0, 3600), Random.nextLong(0, 1_000_000_000))
            val holderOrigin = DurationHolder(d, if (Random.nextBoolean()) null else d)

            val bytes = ProtoBuf.encodeToByteArray(DurationHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(DurationHolder.serializer(), bytes)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalDateTimeJson() {
        repeat(10) {
            val dt = randomLocalDateTime().truncatedTo(ChronoUnit.MICROS) // 精度统一
            val holderOrigin = LocalDateTimeHolder(dt, if (Random.nextBoolean()) null else dt)

            val json = Json.encodeToString(LocalDateTimeHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString(LocalDateTimeHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalDateTimeProtobuf() {
        repeat(10) {
            val dt = randomLocalDateTime().truncatedTo(ChronoUnit.MICROS)
            val holderOrigin = LocalDateTimeHolder(dt, if (Random.nextBoolean()) null else dt)

            val bytes = ProtoBuf.encodeToByteArray(LocalDateTimeHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(LocalDateTimeHolder.serializer(), bytes)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalDateJson() {
        repeat(10) {
            val d = randomLocalDate()
            val holderOrigin = LocalDateHolder(d, if (Random.nextBoolean()) null else d)

            val json = Json.encodeToString(LocalDateHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString(LocalDateHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalDateProtobuf() {
        repeat(10) {
            val d = randomLocalDate()
            val holderOrigin = LocalDateHolder(d, if (Random.nextBoolean()) null else d)

            val bytes = ProtoBuf.encodeToByteArray(LocalDateHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(LocalDateHolder.serializer(), bytes)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalTimeJson() {
        repeat(10) {
            val t = randomLocalTime().truncatedTo(ChronoUnit.MICROS)
            val holderOrigin = LocalTimeHolder(t, if (Random.nextBoolean()) null else t)

            val json = Json.encodeToString(LocalTimeHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString(LocalTimeHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testLocalTimeProtobuf() {
        repeat(10) {
            val t = randomLocalTime().truncatedTo(ChronoUnit.MICROS)
            val holderOrigin = LocalTimeHolder(t, if (Random.nextBoolean()) null else t)

            val bytes = ProtoBuf.encodeToByteArray(LocalTimeHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(LocalTimeHolder.serializer(), bytes)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testInstantJson() {
        repeat(10) {
            val instant = randomInstant()
            val holderOrigin = InstantHolder(instant, if (Random.nextBoolean()) null else instant)

            val json = Json.encodeToString(InstantHolder.serializer(), holderOrigin)
            val holder = Json.decodeFromString(InstantHolder.serializer(), json)

            assertEquals(holderOrigin, holder)
        }
    }

    @Test
    fun testInstantProtobuf() {
        repeat(10) {
            val instant = randomInstant()
            val holderOrigin = InstantHolder(instant, if (Random.nextBoolean()) null else instant)

            val bytes = ProtoBuf.encodeToByteArray(InstantHolder.serializer(), holderOrigin)
            val holder = ProtoBuf.decodeFromByteArray(InstantHolder.serializer(), bytes)

            assertEquals(holderOrigin, holder)
        }
    }
}