/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
package com.labijie.infra.orm.test.configuration

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource


class ExposedTestEnvProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val config:Map<String, Any> = mapOf(
            "infra.exposed.show-sql" to true
        )
        val propertySource = MapPropertySource(
            "exposed-test-config", config
        )
        environment.propertySources.addLast(propertySource)
    }
}