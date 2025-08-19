package com.labijie.orm.dummy

import org.jetbrains.exposed.sql.orWhere
import org.jetbrains.exposed.sql.selectAll
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

/**
 * @author Anders Xiao
 * @date 2025/6/12
 */
@SpringBootApplication
@Import(value = [CommandLineTestRunner::class])
class AotTestApplication

fun main(args: Array<String>) {
    SpringApplication.run(AotTestApplication::class.java, *args)
}

//fun main(args: Array<String>) {
//
//    val args = arrayOf(
//        "com.labijie.orm.dummy.AotTestApplicationKt",
//        "/main/sources",
//        "/main/resources",
//        "/main/classes",
//        "com.labijie.orm.dummy",
//        "application")
//
//
//    SpringApplicationAotProcessor.main(args)
//}