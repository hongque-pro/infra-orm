/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.infra.orm.compile

import kotlin.reflect.KClass


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KspTablePojo(
    val kotlinSerializable: KspToggles = KspToggles.AUTO,
    val isOpen: Boolean = true,
    val superClasses: Array<KClass<*>> = [],
)