package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.TestEnum
import java.util.UUID
import kotlin.Boolean
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Char
import kotlin.Long
import kotlin.Short
import kotlin.String

public open class TestEntityNoId {
  public var entityId: Long = 0L

  public var name: String = ""

  public var memo: String? = null

  public var charCol: Char = '\u0000'

  public var textCol: String = ""

  public var enumCol: TestEnum = TestEnum.OK

  public var binaryCol: ByteArray = ByteArray(0)

  public var uidCol: UUID = UUID.randomUUID()

  public var shortCol: Short = 0

  public var booleanCol: Boolean = false

  public var byteCol: Byte = 0
}
