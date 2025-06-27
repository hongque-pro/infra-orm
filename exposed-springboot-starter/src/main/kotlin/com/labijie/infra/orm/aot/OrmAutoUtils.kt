package com.labijie.infra.orm.aot

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.TypeReference

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
@Suppress("unused")
fun RuntimeHints.registerOrmPojoType(className: String) {
    reflection().registerType(TypeReference.of(className)) {
        it.withMembers(
            MemberCategory.PUBLIC_FIELDS,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.INTROSPECT_PUBLIC_METHODS
        )
    }
}