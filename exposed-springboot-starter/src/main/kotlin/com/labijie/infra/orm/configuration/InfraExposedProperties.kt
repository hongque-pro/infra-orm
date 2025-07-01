package com.labijie.infra.orm.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("infra.exposed")
class InfraExposedProperties {
    var showSql: Boolean = false

    var disableJpa: Boolean = false

    var translateSqlException: Boolean = true

    @NestedConfigurationProperty
    var generateSchema: SchemaGenerationSettings = SchemaGenerationSettings()
}