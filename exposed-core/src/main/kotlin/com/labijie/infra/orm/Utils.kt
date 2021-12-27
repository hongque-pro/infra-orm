package com.labijie.infra.orm

import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/28
 * @Description:
 */
fun String.toUUID(): UUID {
    return UUID.fromString(this)
}
