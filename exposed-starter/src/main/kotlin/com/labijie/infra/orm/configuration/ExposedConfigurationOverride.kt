package com.labijie.infra.orm.configuration

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment

class ExposedConfigurationOverride : ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    companion object {
        private const val DDL_CREATE_CONFIG_KEY = "spring.exposed.generate-ddl"
    }

    override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
        val env = event.environment
        env.merge(hardCodeEnvironment())
    }

    private fun hardCodeEnvironment(): ConfigurableEnvironment {
        val hardCodeEnvironment = StandardEnvironment()
        val propertySources = hardCodeEnvironment.propertySources
        val configMap = mutableMapOf<String, Any>()
        configMap[DDL_CREATE_CONFIG_KEY] = false
        propertySources.addFirst(MapPropertySource("infra-exposed-override", configMap))
        return hardCodeEnvironment
    }
}