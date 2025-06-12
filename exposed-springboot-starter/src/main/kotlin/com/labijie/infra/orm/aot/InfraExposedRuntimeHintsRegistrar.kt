/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.infra.orm.aot

import com.labijie.infra.orm.configuration.InfraExposedAutoConfiguration
import com.labijie.infra.orm.configuration.TableDefinitionPostProcessor
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class InfraExposedRuntimeHintsRegistrar : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(InfraExposedAutoConfiguration::class.java)
    }
}