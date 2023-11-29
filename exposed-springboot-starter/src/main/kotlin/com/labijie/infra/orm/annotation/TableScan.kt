package com.labijie.infra.orm.annotation

import com.labijie.infra.orm.configuration.TableScannerRegistrar
import org.springframework.context.annotation.Import
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Import(TableScannerRegistrar::class)
annotation class TableScan(
    val basePackages:Array<String> = [],
    val basePackageClasses: Array<KClass<*>> = [],
    val excludeClasses: Array<KClass<*>> = [])
