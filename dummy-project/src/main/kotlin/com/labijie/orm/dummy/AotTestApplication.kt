package com.labijie.orm.dummy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Anders Xiao
 * @date 2025/6/12
 */
@SpringBootApplication
class AotTestApplication {

    fun main(args: Array<String>) {
        SpringApplication.run(AotTestApplication::class.java, *args)
    }
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