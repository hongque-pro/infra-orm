package com.labijie.orm.generator

import com.google.devtools.ksp.symbol.KSType

data class ColumnMetadata(
    val name: String,
    val type: KSType,
    val rawType: KSType,
    val isNullableColumn: Boolean,
    val isPrimary: Boolean,
    val isEntityId: Boolean,
) {
    fun getTypeQualifiedName(): String {
        return this.type.declaration.qualifiedName!!.asString()
    }

    val isGeneric: Boolean
        get() = this.type.declaration.typeParameters.isNotEmpty()

    val isCollection: Boolean
        get() = this.type.declaration.qualifiedName!!.asString() == "kotlin.Array" || this.type.declaration.qualifiedName!!.asString().startsWith("kotlin.collections")

    override fun toString(): String {
        return "ColumnMetadata(name='$name', type=${type.declaration.qualifiedName.toString()}, rawType=${rawType.declaration.qualifiedName.toString()}, isNull=$isNullableColumn, isPrimary=$isPrimary, isEntityId=$isEntityId, isEnum=$isEnum)"
    }

    val isEnum by lazy {
        this.type.isEnum()
    }

    val isString by lazy {
        this.type.isKotlinType(String::class)
    }



}