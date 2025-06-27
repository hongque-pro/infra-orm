/**
 * @author Anders Xiao
 * @date 2024-10-11
 */
package com.labijie.infra.orm.compile

import kotlin.reflect.KClass

/**
 * Use [KspTablePojo.superClasses] property instead
 */
@Deprecated(
    "Use KspTablePojo.superClasses instead.",
    replaceWith = ReplaceWith("KspTablePojo.superClass")
)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KspTablePojoSuper(
    val type: KClass<*>
)