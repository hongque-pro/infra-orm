/**
 * THIS FILE IS PART OF HuanJing (huanjing.art) PROJECT
 * Copyright (c) 2023 huanjing.art
 * @author Huanjing Team
 */
package com.labijie.infra.orm.testing

import com.labijie.orm.generator.convertToSpringConfig
import com.labijie.orm.generator.parseSpringConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class SpringConfigParseTester {

    @Test
    fun testParse(){
        val config = """
        org.springframework.aot.hint.RuntimeHintsRegistrar=\
        org.springframework.beans.factory.annotation.JakartaAnnotationsRuntimeHints,\
        org.springframework.beans.BeanUtilsRuntimeHints

        org.springframework.aot.hint.RuntimeHintsRegistrar=\
        org.springframework.beans.factory.annotation.ARuntimeHints,\
        org.springframework.beans.BRuntimeHints

        org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor=\
        org.springframework.beans.factory.aot.BeanRegistrationsAotProcessor
    """.trimIndent()

        val parsed = parseSpringConfig(config)

        assertEquals(2, parsed.keys.size)
        assert(parsed.keys.contains("org.springframework.aot.hint.RuntimeHintsRegistrar"))
        assert(parsed.keys.contains("org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor"))

        assertEquals(4, parsed["org.springframework.aot.hint.RuntimeHintsRegistrar"]!!.size)
        assertEquals(1, parsed["org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor"]!!.size)


        val hintsRegistrar =  parsed["org.springframework.aot.hint.RuntimeHintsRegistrar"]!!
        assert(hintsRegistrar.contains("org.springframework.beans.factory.annotation.JakartaAnnotationsRuntimeHints"))
        assert(hintsRegistrar.contains("org.springframework.beans.BeanUtilsRuntimeHints"))
        assert(hintsRegistrar.contains("org.springframework.beans.factory.annotation.ARuntimeHints"))
        assert(hintsRegistrar.contains("org.springframework.beans.BRuntimeHints"))
    }

    @Test
    fun testToString() {
        val config = """
        org.springframework.aot.hint.RuntimeHintsRegistrar=\
        org.springframework.beans.factory.annotation.JakartaAnnotationsRuntimeHints,\
        org.springframework.beans.BeanUtilsRuntimeHints

        org.springframework.aot.hint.RuntimeHintsRegistrar=\
        org.springframework.beans.factory.annotation.ARuntimeHints,\
        org.springframework.beans.BRuntimeHints

        org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor=\
        org.springframework.beans.factory.aot.BeanRegistrationsAotProcessor
    """.trimIndent()

        val parsed = parseSpringConfig(config)

        println(convertToSpringConfig(parsed))
    }
}