package com.labijie.infra.orm.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("infra.exposed")
class InfraExposedProperties {
    var showSql: Boolean = false
    var generateSchema: Boolean = false
}