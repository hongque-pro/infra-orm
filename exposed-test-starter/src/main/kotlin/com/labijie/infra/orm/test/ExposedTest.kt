package com.labijie.infra.orm.test

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.test.context.BootstrapWith
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.lang.annotation.Inherited


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@BootstrapWith(ExposedTestContextBootstrapper::class)
@OverrideAutoConfiguration(enabled = false)
@TypeExcludeFilters(ExposedTypeExcludeFilter::class)
@EnableTransactionManagement
@AutoConfigureTestDatabase
@ImportAutoConfiguration
@EnableAutoConfiguration
@AutoConfigureExposed
annotation class ExposedTest(
    /**
     * Properties in form key=value that should be added to the Spring [Environment] before the test
     * runs.
     *
     * @return the properties to add
     * @since 2.1.0
     */
    val properties: Array<String> = [],
    /**
     * Determines if default filtering should be used with [@SpringBootApplication][SpringBootApplication]. By
     * default no beans are included.
     *
     * @return if default filters should be used
     * @see .includeFilters
     * @see .excludeFilters
     */
    val useDefaultFilters: Boolean = true,
    /**
     * A set of include filters which can be used to add otherwise filtered beans to the application context.
     *
     * @return include filters to apply
     */
    val includeFilters: Array<Filter> = [],
    /**
     * A set of exclude filters which can be used to filter beans that would otherwise be added to the application
     * context.
     *
     * @return exclude filters to apply
     */
    val excludeFilters: Array<Filter> = [],
)
