package com.labijie.infra.orm.test

import org.springframework.boot.test.autoconfigure.filter.AnnotationCustomizableTypeExcludeFilter
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.annotation.AnnotatedElementUtils

class ExposedTypeExcludeFilter(testClass: Class<*>) : AnnotationCustomizableTypeExcludeFilter() {

    private var annotation: ExposedTest? = AnnotatedElementUtils.getMergedAnnotation(testClass, ExposedTest::class.java)


    override fun hasAnnotation(): Boolean = (this.annotation != null)

    override fun getFilters(type: FilterType?): Array<ComponentScan.Filter> {
        return when (type) {
            FilterType.INCLUDE -> annotation?.includeFilters ?: arrayOf()
            FilterType.EXCLUDE -> annotation?.excludeFilters ?: arrayOf()
            else -> throw IllegalStateException("Unsupported type $type")
        }
    }

    override fun isUseDefaultFilters(): Boolean {
        return this.annotation?.useDefaultFilters ?: true
    }

    override fun getDefaultIncludes(): MutableSet<Class<*>> {
         return mutableSetOf()
    }

    override fun getComponentIncludes(): MutableSet<Class<*>> {
        return mutableSetOf()
    }
}