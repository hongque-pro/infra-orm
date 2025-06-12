/**
 * @author Anders Xiao
 * @date 2025-06-11
 */
package com.labijie.orm.generator.native

import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReflectConfigEntry(
    val name: String = "",
    val allDeclaredConstructors: Boolean? = null,
    val allPublicConstructors: Boolean? = null,
    val allDeclaredMethods: Boolean? = null,
    val allPublicMethods: Boolean? = null,
    val allDeclaredFields: Boolean? = null,
    val allPublicFields: Boolean? = null,
    val fields: List<Field>? = null,
    val methods: List<Method>? = null
) {
    data class Field(
        val name: String = "",
    )

    data class Method(
        val name: String = "",
        val parameterTypes: List<String>? = null
    )
}