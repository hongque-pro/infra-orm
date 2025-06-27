package com.labijie.infra.orm.serialization

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
typealias OrmBigDecimal = @Serializable(with = BigDecimalSerializer::class) BigDecimal

typealias OrmUUID = @Serializable(with = UUIDSerializer::class) UUID

typealias OrmDuration = @Serializable(with = DurationSerializer::class) Duration

typealias OrmLocalDateTime = @Serializable(with = LocalDateTimeSerializer::class) LocalDateTime

typealias OrmLocalDate = @Serializable(with = LocalDateSerializer::class) LocalDate

typealias OrmLocalTime = @Serializable(with = LocalTimeSerializer::class) LocalTime

typealias OrmInstant = @Serializable(with = InstantSerializer::class) Instant