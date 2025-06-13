package com.labijie.orm.generator.writer

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

object PojoWriter {

    fun write(context: GenerationContext) {
        val file = FileSpec.builder(context.pojoPackageName, fileName = context.pojoClass.simpleName)
            .suppressRedundantVisibilityModifierWarning()
            .addType(
                TypeSpec.classBuilder(context.pojoClass)
                    .addComments("POJO for ${context.tableClass.simpleName}", context)
                    .addProperties(context)
                    .executeIf(context.table.isOpen) {
                        addModifiers(KModifier.OPEN)
                    }
                    .executeIf(context.table.isSerializable) {
                        addAnnotation(ClassName("kotlinx.serialization", "Serializable"))
                    }
                    .executeIf(context.table.superTypes.isNotEmpty()) {
                        context.table.superTypes.forEach {
                            if(it.isInterface) {
                                addSuperinterface(it.type.toTypeName())
                            }else {
                                superclass(it.type.toTypeName())
                            }
                        }
                    }
                    .build()
            )
            .build()

        file.writeTo(context.options.getFolder(context.table).pojoSourceDir)
    }

    private fun TypeSpec.Builder.executeIf(
        condition: Boolean,
        execution: (TypeSpec.Builder.() -> Unit)
    ): TypeSpec.Builder {
        if (condition) {
            execution.invoke(this)
        }
        return this
    }


    private fun TypeSpec.Builder.addProperties(content: GenerationContext): TypeSpec.Builder {

        content.table.columns.forEach { column ->

            var property: KSPropertyDeclaration? = null
            for(superType in content.table.superTypes) {
                property = superType.getPublicProperty(column.name)
                if(property != null) {
                    break
                }
            }


            val isAbstract = property?.isAbstract() == true

            val isNullable = property?.type?.resolve()?.nullability == Nullability.NULLABLE || column.isNullableColumn

            if(property == null || isAbstract) {
                addProperty(
                    PropertySpec.builder(
                        column.name,
                        column.type.toTypeName().copy(nullable = column.isNullableColumn)
                    ).mutable().let { self ->
                        if (isAbstract) self.addModifiers(
                            KModifier.PUBLIC,
                            KModifier.OVERRIDE
                        ) else self.addModifiers(KModifier.PUBLIC)
                    }
                        .initializer(if (isNullable) "null" else DefaultValues.getValue(propertyName ="${content.table.className}.${column.name}"  ,column.type))
                        .build()
                )
            }
        }
        return this
    }

}