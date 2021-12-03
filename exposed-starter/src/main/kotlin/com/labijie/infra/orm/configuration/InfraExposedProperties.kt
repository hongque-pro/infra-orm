package com.labijie.infra.orm.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("infra.exposed")
class InfraExposedProperties {
    var generateTables: String = ""
    var showSql: Boolean = false
}