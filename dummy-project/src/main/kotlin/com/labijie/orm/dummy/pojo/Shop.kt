package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.TestEnum
import kotlin.Int
import kotlin.Long
import kotlin.String

/**
 * POJO for ShopTable
 *
 * This class made by a code generator (https://github.com/hongque-pro/infra-orm).
 *
 * Origin Exposed Table:
 * @see com.labijie.orm.dummy.ShopTable
 */
public open class Shop {
  public var name: String = ""

  public var status: TestEnum = TestEnum.OK

  public var count: Int = 0

  public var id: Long = 0L
}
