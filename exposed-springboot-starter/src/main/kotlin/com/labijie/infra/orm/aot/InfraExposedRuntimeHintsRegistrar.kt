/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.infra.orm.aot

import com.labijie.infra.orm.SimpleTableScanner
import com.labijie.infra.orm.configuration.InfraExposedAutoConfiguration
import com.labijie.infra.orm.configuration.TableScanner
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

class InfraExposedRuntimeHintsRegistrar : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(SimpleTableScanner::class.java)
        hints.reflection().registerType(InfraExposedAutoConfiguration::class.java)
        hints.resources().registerPattern("git-info/git.properties")
    }
}