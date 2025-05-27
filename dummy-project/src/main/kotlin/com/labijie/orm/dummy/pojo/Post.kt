@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.TestEnum
import com.labijie.orm.dummy.otherpackage.NestedInterface
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.Long
import kotlin.String
import kotlin.collections.List

/**
 * POJO for PostTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.PostTable
 */
public open class Post {
  public var title: String? = null

  public var status: TestEnum = TestEnum.OK

  public var description: String = ""

  public var status2: NestedInterface.StatusEnum = NestedInterface.StatusEnum.Default

  public var array: List<String> = listOf()

  public var dateTime: LocalDateTime = LocalDateTime.of(0, 1, 1, 0, 0,0, 0)

  public var duration: Duration = Duration.ZERO

  public var time: LocalTime = LocalTime.MIN

  public var date: LocalDate = LocalDate.of(0, 1, 1)

  public var timestamp: Instant = Instant.EPOCH

  public var id: Long = 0L
}
