@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.SimpleBaseClass
import com.labijie.orm.dummy.SimpleInterface
import com.labijie.orm.dummy.TestEnum
import java.util.UUID
import kotlin.Boolean
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Char
import kotlin.Int
import kotlin.Short
import kotlin.String

/**
 * POJO for IntIdTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.IntIdTable
 */
public open class IntId : SimpleBaseClass(), SimpleInterface {
  public override var name: String = ""

  public var charCol: Char = '\u0000'

  public var textCol: String = ""

  public var enumCol: TestEnum = TestEnum.OK

  public var binaryCol: ByteArray = ByteArray(0)

  public var uidCol: UUID = UUID.randomUUID()

  public var shortCol: Short = 0

  public var booleanCol: Boolean = false

  public var byteCol: Byte = 0

  public var id: Int = 0
}
