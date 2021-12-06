package com.labijie.infra.orm.test

import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import java.lang.annotation.Documented
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@ImportAutoConfiguration
annotation class AutoConfigureExposed {
}