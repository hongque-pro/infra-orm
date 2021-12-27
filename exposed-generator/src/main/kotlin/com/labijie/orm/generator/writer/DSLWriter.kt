package com.labijie.orm.generator.writer

import com.labijie.orm.generator.*
import com.labijie.orm.generator.writer.dsl.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@KotlinPoetKspPreview
object DSLWriter {

    private const val OBJECT_PARAMETER_NAME = "raw"
    private val methodBuilders: MutableList<IDSLMethodBuilder> = mutableListOf()

    init {
        methodBuilders.add(ApplyExtensionMethod)
        methodBuilders.add(InsertMethod)
        methodBuilders.add(InsertAndGetIdMethod)
        methodBuilders.add(BatchInsertMethod)
        methodBuilders.add(UpdateMethod)
        methodBuilders.add(DeleteByPrimaryKeyMethod)
        methodBuilders.add(SelectByPrimaryMethod)
        methodBuilders.add(SelectMethod)
    }

    private fun TypeSpec.Builder.buildMethods(context: DSLCodeContext){
        methodBuilders.forEach {
            it.buildMethods(context).forEach {
                method->
                this.addFunction(method)
            }
        }
    }


    fun write(context: GenerationContext) {

        val parseRow = generateParseRowMethod(context)
        val rowMap = generateRowMapMethod(context, parseRow)
        val slice = generateSliceExtensionMethod(context, parseRow)

        val applyInsert = generateApplyMethod(context)
        val getColumnValue = generateGetColumnValueMethod(context)


        val fileBuilder = FileSpec.builder(context.dslPackageName, fileName = context.dslClass.simpleName)
        val file = fileBuilder
            .addImport(context.tableClass, context.table.columns.map { it.name })
            .addType(
                TypeSpec.objectBuilder(context.dslClass)
                    .addComments("DSL support for ${context.tableClass.simpleName}", context)
                    .addFunction(parseRow)
                    .addFunction(getColumnValue)
                    .addFunction(applyInsert)
                    //exposed methods
                    .addFunction(rowMap)
                    .addFunction(slice)
                    .apply {
                        val dslCtx = DSLCodeContext(
                            context,
                            fileBuilder,
                            parseRow,
                            getColumnValue,
                            applyInsert,
                            rowMap,
                            slice,
                            OBJECT_PARAMETER_NAME
                        )
                        this.buildMethods(dslCtx)
                    }
                    .build()
            )
            .build()
        val folder = context.options.getSourceFolder(context.table)
        context.logger.println("File generate in '${folder}'")
        file.writeTo(folder)
    }


    private fun generateRowMapMethod(
        context: GenerationContext,
        parseMethod: FunSpec
    ): FunSpec {
        return FunSpec.builder("to${context.pojoClass.simpleName}")
            .receiver(ResultRow::class)
            .returns(context.pojoClass)
            .addStatement("return %N(this)", parseMethod)
            .build()
    }


    private fun generateSliceExtensionMethod(
        context: GenerationContext,
        parseMethod: FunSpec
    ): FunSpec {
        val returnType = List::class.asTypeName().parameterizedBy(context.pojoClass)

        return FunSpec.builder("to${context.pojoClass.simpleName}List")
            .receiver(Iterable::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName()))
            .returns(returnType)
            .addStatement("return this.map(%L)", context.dslClass.member(parseMethod.name).reference())
            .build()
    }

    private fun generateGetColumnValueMethod(context: GenerationContext): FunSpec {

        val typeVar = TypeVariableName("T")

        val suppress = AnnotationSpec.builder(Suppress::class)
            .addMember("%S", "UNCHECKED_CAST")
            .build()

        //@Suppress("UNCHECKED_CAST")
        return FunSpec.builder("getColumnValue")
            .addTypeVariable(typeVar)
            .addAnnotation(suppress)
            .receiver(context.pojoClass)
            .addParameter("column", Column::class.asTypeName().parameterizedBy(typeVar))
            .returns(typeVar)
            .beginControlFlow("return when(column)")
            .apply {
                context.table.columns.forEach {
                    this.addStatement("${context.table.className}.${it.name}->this.${it.name} as T")
                }
                val errorMessage = "Unknown column <\${column.name}> for '${context.pojoClass.simpleName}'"
                this.addStatement("else->throw %T(%P)", IllegalArgumentException::class, errorMessage)
            }
            .endControlFlow()
            .build()
    }



    private fun generateApplyMethod(context: GenerationContext): FunSpec {
        val updateBuilder = UpdateBuilder::class.asTypeName().parameterizedWildcard()

        updateBuilder.copy()

        val ignore = ParameterSpec.builder("ignore", Column::class.asTypeName().parameterizedWildcard())
            .apply {
                modifiers.add(KModifier.VARARG)
            }
            .build()

        val columnArray = Array::class.asClassName().parameterizedBy(Column::class.asTypeName().parameterizedWildcard()).copy(nullable = true)

        val selective = ParameterSpec.builder("selective", columnArray)
            .defaultValue("null")
            .build()

        val applyInsert = FunSpec.builder("apply${context.pojoClass.simpleName}")
            .addParameter("builder", updateBuilder)
            .addParameter(OBJECT_PARAMETER_NAME, context.pojoClass)
            .addParameter(selective)
            .addParameter(ignore)
            .returns(Unit::class.asTypeName())
            .apply {
                context.table.columns.forEach {
                    if (!it.isEntityId) {
                        this.addStatement("if((selective == null || selective.contains(${it.name})) && !%N.contains(${it.name}))", ignore)
                        this.addStatement("  builder[${it.name}] = ${OBJECT_PARAMETER_NAME}.${it.name}")
                    }
                }
            }
            .build()
        return applyInsert
    }

    private fun generateParseRowMethod(
        context: GenerationContext
    ): FunSpec {
        val parseRow = FunSpec.builder("parse${context.pojoClass.simpleName}Row")
            .addParameter(OBJECT_PARAMETER_NAME, ResultRow::class)
            .returns(context.pojoClass)
            .addStatement("val plain = %T()", context.pojoClass)
            .apply {
                context.table.columns.forEach {
                    if (it.isEntityId) {
                        this.addStatement("plain.${it.name} = ${OBJECT_PARAMETER_NAME}[${it.name}].value")
                    } else {
                        this.addStatement("plain.${it.name} = ${OBJECT_PARAMETER_NAME}[${it.name}]")
                    }
                }
            }
            .addStatement("return plain")
            .build()
        return parseRow
    }

}