package com.labijie.infra.orm.test

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper
import org.springframework.core.annotation.AnnotatedElementUtils

class ExposedTestContextBootstrapper : SpringBootTestContextBootstrapper() {
    override fun getProperties(testClass: Class<*>?): Array<String> {
        val annotation = AnnotatedElementUtils.getMergedAnnotation(testClass!!, ExposedTest::class.java)
        return annotation?.properties ?: arrayOf()
    }
}