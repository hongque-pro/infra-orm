package com.labijie.infra.orm.test.configuration

import com.labijie.infra.orm.configuration.InfraExposedAutoConfiguration
import com.labijie.infra.orm.configuration.InfraExposedProperties
import com.labijie.infra.orm.configuration.SchemaChangesProcessor
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(InfraExposedAutoConfiguration::class)
class ExposedTestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SchemaChangesProcessor::class)
    @ConditionalOnBean(value = [InfraExposedProperties::class])
    fun schemaCreationProcessor(
        transactionTemplate: TransactionTemplate,
        properties: InfraExposedProperties
    ): SchemaChangesProcessor {
        return SchemaChangesProcessor(transactionTemplate, properties)
    }

}