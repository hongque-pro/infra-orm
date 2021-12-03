package com.labijie.orm.dummy.pojo

import com.labijie.orm.dummy.TestEnum
import kotlin.Int
import kotlin.Long
import kotlin.String

public open class User {
  public var name: String = ""

  public var status: TestEnum = TestEnum.OK

  public var count: Int = 0

  public var id: Long = 0L
}
