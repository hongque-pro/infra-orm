package com.labijie.infra.orm.aot

import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.FactoryBean
import kotlin.reflect.KClass

/**
 * @author Anders Xiao
 * @date 2025/6/12
 */
class TableFactoryBean<T : Table>(private val clazz: KClass<T>) : FactoryBean<T> {

    override fun getObject(): T {
        return clazz.objectInstance!!
    }

    override fun getObjectType(): Class<*> = clazz.java
    override fun isSingleton(): Boolean = true
}