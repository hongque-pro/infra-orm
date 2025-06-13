package com.labijie.infra.orm

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/28
 * @Description:
 */
object ExposedConverter {
    @JvmStatic
    fun stringToUUID(value: String): UUID {
        return UUID.fromString(value)
    }

    @JvmStatic
    fun stringToUUIDOrNull(value: String): UUID? {
        return if(value.isNotBlank()) UUID.fromString(value) else null
    }

    @JvmStatic
    fun instantToString(value: Instant): String {
        return value.toEpochMilli().toString()
    }

    @JvmStatic
    fun stringToInstant(value: String): Instant {
        return Instant.ofEpochMilli(value.toLong())
    }

    @JvmStatic
    fun stringToInstantOrNull(value: String): Instant? {
        return value.toLongOrNull()?.let {
            Instant.ofEpochMilli(it)
        }
    }


    @JvmStatic
    fun byteArrayToString(array: ByteArray?): String {
        return array?.toString(Charsets.UTF_8) ?: ""
    }

    @JvmStatic
    fun stringToByteArray(value: String): ByteArray {
        return value.toByteArray(Charsets.UTF_8)
    }

    @JvmStatic
    fun stringToByteArrayOrNull(value: String): ByteArray? {
        return if(value.isNotBlank()) value.toByteArray(Charsets.UTF_8) else null
    }

    @JvmStatic
    fun durationToString(duration: Duration): String {
        return duration.toMillis().toString()
    }

    @JvmStatic
    fun stringToDuration(value: String): Duration {
        return Duration.ofMillis(value.toLong())
    }

    @JvmStatic
    fun stringToDurationOrNull(value: String): Duration? {
        return value.toLongOrNull()?.let {
            Duration.ofMillis(value.toLong())
        }
    }

    @JvmStatic
    fun stringToLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    @JvmStatic
    fun stringToLocalDateOrNull(date: String): LocalDate? {
        return if(date.isNotBlank()) LocalDate.parse(date) else null
    }

    @JvmStatic
    fun stringToLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    @JvmStatic
    fun stringToLocalTimeOrNull(time: String): LocalTime? {
        return if(time.isNotBlank()) LocalTime.parse(time) else null
    }

    @JvmStatic
    fun stringToLocalDateTime(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }
    @JvmStatic
    fun stringToLocalDateTimeOrNull(dateTime: String): LocalDateTime? {
        return if(dateTime.isNotBlank()) LocalDateTime.parse(dateTime) else null
    }

}

fun Table.dropColumn(column: String): String {
    val tr = TransactionManager.current()
    val columnName = tr.db.identifierManager.quoteIdentifierWhenWrongCaseOrNecessary(column)
    return "ALTER TABLE ${tr.identity(this)} DROP COLUMN $columnName"
}


/**
 * Mutate Query instance and add `andPart` to where condition with `and` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.andWhereIf(condition:Boolean, andPart: SqlExpressionBuilder.() -> Op<Boolean>): Query {
    return if(condition) {
        this.andWhere(andPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `orPart` to where condition with `or` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.orWhereIf(condition:Boolean, orPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.orWhere(orPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `andPart` to having condition with `and` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.andHavingIf(condition:Boolean, andPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.andHaving(andPart)
    }else {
        this
    }
}

/**
 * Mutate Query instance and add `orPart` to having condition with `or` operator.
 * @return same Query instance which was provided as a receiver.
 */
fun Query.orHavingIf(condition:Boolean, orPart: SqlExpressionBuilder.() -> Op<Boolean>) : Query {
    return if(condition) {
        this.orHaving(orPart)
    }else {
        this
    }
}

fun Op<Boolean>.andIf(condition: Boolean, andPart: SqlExpressionBuilder.() -> Op<Boolean>): Op<Boolean> {
    return if(condition) {
        this.and(andPart)
    }else {
        this
    }
}

fun Op<Boolean>.orIf(condition: Boolean, orPart: SqlExpressionBuilder.() -> Op<Boolean>): Op<Boolean> {
    return if(condition) {
        this.or(orPart)
    }else {
        this
    }
}
