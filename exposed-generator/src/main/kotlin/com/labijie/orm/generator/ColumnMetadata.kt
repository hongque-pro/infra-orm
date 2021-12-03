package com.labijie.orm.generator

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

data class ColumnMetadata(
    val name: String,
    val type: KSType,
    val rawType: KSType,
    val isNull: Boolean,
    val isPrimary: Boolean,
    val isEntityId: Boolean
) {
    fun getTypeQualifiedName(): String {
        return this.type.declaration.qualifiedName!!.asString()
    }

    override fun toString(): String {
        return "ColumnMetadata(name='$name', type=${type.declaration.qualifiedName.toString()}, rawType=${rawType.declaration.qualifiedName.toString()}, isNull=$isNull, isPrimary=$isPrimary, isEntityId=$isEntityId, isEnum=$isEnum)"
    }

    val isEnum by lazy {
        this.type.isEnum()
    }


}