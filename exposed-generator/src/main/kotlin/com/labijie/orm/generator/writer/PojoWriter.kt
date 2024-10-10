package com.labijie.orm.generator.writer

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.labijie.orm.generator.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

object PojoWriter {

    fun write(context: GenerationContext) {
        context.table
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
                    .executeIf(context.table.implements.isNotEmpty()) {
                        context.table.implements.forEach {
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

        file.writeTo(context.options.getSourceFolder(context.table))
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

        content.table.columns.forEach {

            val superTypes = content.table.implements.filter { superType ->
                val declaration = superType.type.declaration as? KSClassDeclaration
                declaration?.getDeclaredProperties()?.any { property ->
                    property.simpleName.getShortName() == it.name
                } ?: false
            }


//            val hasPropertyOnDelegate = content.table.interfaces.any { interfaceType ->
//                val declaration = interfaceType.by?.declaration as? KSClassDeclaration
//                declaration?.getDeclaredProperties()?.any { property ->
//                    property.simpleName.getShortName() == it.name && property.isPublic()
//                } ?: false
//            }
            val propertyOnBaseClass = superTypes.any { t-> !t.isInterface }
            val propertyOnInterface = superTypes.any { t-> t.isInterface }
            if(!propertyOnBaseClass) {
                addProperty(
                    PropertySpec.builder(
                        it.name,
                        it.type.toTypeName().copy(nullable = it.isNull)
                    ).mutable().let { self ->
                        if (propertyOnInterface) self.addModifiers(
                            KModifier.PUBLIC,
                            KModifier.OVERRIDE
                        ) else self.addModifiers(KModifier.PUBLIC)
                    }
                        .initializer(if (it.isNull) "null" else DefaultValues.getValue(it.type))
                        .build()
                )
            }
        }
        return this
    }

}