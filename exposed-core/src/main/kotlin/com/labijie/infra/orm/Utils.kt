package com.labijie.infra.orm

import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/27
 * @Description:
 */
fun String.ToUUID(): UUID {
    return UUID.fromString(this)
}