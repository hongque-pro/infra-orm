package com.labijie.infra.orm.annotation

import com.labijie.infra.orm.configuration.TabScannerRegistrar
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Import(TabScannerRegistrar::class)
annotation class TableScan(
    val basePackages:Array<String> = [],
    val basePackageClasses: Array<KClass<*>> = [],
    val excludeClasses: Array<KClass<*>> = [])
