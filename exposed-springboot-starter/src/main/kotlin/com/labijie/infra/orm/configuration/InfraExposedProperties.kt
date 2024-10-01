package com.labijie.infra.orm.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("infra.exposed")
data class InfraExposedProperties(
    var showSql: Boolean = false,

    val translateSqlException: Boolean = true,

    @NestedConfigurationProperty
    val generateSchema: SchemaGenerationSettings = SchemaGenerationSettings(),
)