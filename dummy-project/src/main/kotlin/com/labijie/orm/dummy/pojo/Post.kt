@file:Suppress("RedundantVisibilityModifier")

package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.TestEnum
import kotlin.Long
import kotlin.String

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
  public var title: String = ""

  public var status: TestEnum = TestEnum.OK

  public var description: String = ""

  public var id: Long = 0L
}
