package com.labijie.infra.orm.configuration

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.transaction.annotation.Transactional

class SchemaCreationProcessor(private val properties: InfraExposedProperties) : ApplicationListener<ApplicationStartedEvent>, ApplicationContextAware {
    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(InfraExposedAutoConfiguration::class.java)
        }
    }

    private lateinit var context:ApplicationContext

    private val tablesRegex = properties.generateTables.split(",").filter {
        it.isNotBlank()
    }.map {
       val exp = it.trim()
        Regex("^${exp.replace("*", "\\S*")}$")
    }

    private fun matchTable(table:String): Boolean {
        return tablesRegex.any {
            it.matches(table)
        }
    }

    @Transactional
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val foundTables = context.getBeanProvider(Table::class.java)
        val exposedTables = if(properties.generateTables.trim() == "*"){
            foundTables.toList().toTypedArray()
        }else{
            foundTables.filter { matchTable(it.tableName) }.toTypedArray()
        }

        if(logger.isInfoEnabled){
            val msg = StringBuilder()
                .appendLine("Schema generation for tables: ${exposedTables.count()} tables: ")
                .apply {
                    exposedTables.forEach {
                        this.appendLine(it.ddl.joinToString(System.lineSeparator()))
                    }
                }.toString()

            logger.info(msg)
        }

        SchemaUtils.create(*exposedTables)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}