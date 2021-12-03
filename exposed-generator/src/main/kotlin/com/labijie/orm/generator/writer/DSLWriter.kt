package com.labijie.orm.generator.writer

import com.labijie.orm.generator.*
import com.labijie.orm.generator.writer.dsl.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement

@KotlinPoetKspPreview
object DSLWriter {

    private const val OBJECT_PARAMETER_NAME = "raw"
    private val methodBuilders: MutableList<IDSLMethodBuilder> = mutableListOf()

    init {
        methodBuilders.add(InsertMethod)
        methodBuilders.add(InsertAndGetIdMethod)
        methodBuilders.add(BatchInsertMethod)
        methodBuilders.add(UpdateMethod)
        methodBuilders.add(DeleteByPrimaryKeyMethod)
        methodBuilders.add(SelectByPrimaryMethod)
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
        val applyInsert = generateApplyInsertMethod(context)
        val applyUpdate = generateApplyUpdateMethod(context)

        val rowMap = generateRowMapMethod(context)
        val slice = generateSliceExtensionMethod(context)

        val fileBuilder = FileSpec.builder(context.dslPackageName, fileName = context.dslClass.simpleName)
        val file = fileBuilder
            .addImport(context.tableClass, context.table.columns.map { it.name })
            .addType(
                TypeSpec.objectBuilder(context.dslClass)
                    .addFunction(parseRow)
                    .addFunction(applyInsert)
                    .addFunction(applyUpdate)
                    //exposed methods
                    .addFunction(rowMap)
                    .addFunction(slice)
                    .apply {
                        val context = DSLCodeContext(
                            context,
                            fileBuilder,
                            parseRow,
                            applyInsert,
                            applyUpdate,
                            rowMap,
                            slice,
                            OBJECT_PARAMETER_NAME
                        )
                        this.buildMethods(context)
                    }
                    .build()
            )
            .build()

        file.writeTo(context.options.getSourceFolder())
    }


    private fun generateRowMapMethod(
        context: GenerationContext
    ): FunSpec {
        return FunSpec.builder("to${context.pojoClass.simpleName}")
            .receiver(ResultRow::class)
            .returns(context.pojoClass)
            .addStatement("return parseRow(this)")
            .build()
    }


    private fun generateSliceExtensionMethod(
        context: GenerationContext
    ): FunSpec {
        val returnType = List::class.asTypeName().parameterizedBy(context.pojoClass)

        return FunSpec.builder("to${context.pojoClass.simpleName}List")
            .receiver(Iterable::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName()))
            .returns(returnType)
            .addStatement("return this.map(::parseRow)", context.tableClass)
            .build()
    }



    private fun generateApplyUpdateMethod(
        context: GenerationContext
    ): FunSpec {
        val updateStatementType = UpdateStatement::class.asTypeName()

        val applyUpdate = FunSpec.builder("applyUpdate")
            .addParameter("statement", updateStatementType)
            .addParameter(OBJECT_PARAMETER_NAME, context.pojoClass)
            .returns(Unit::class.asTypeName())
            .apply {
                context.table.columns.forEach {
                    if (!it.isEntityId) {
                        this.addStatement("statement[${it.name}] = ${OBJECT_PARAMETER_NAME}.${it.name}")
                    }
                }
            }
            .build()
        return applyUpdate
    }

    private fun generateApplyInsertMethod(context: GenerationContext): FunSpec {
        val insertStatementType = UpdateBuilder::class.asTypeName().parameterizedWildcard()

        insertStatementType.copy()

        val applyInsert = FunSpec.builder("applyInsert")
            .addParameter("statement", insertStatementType)
            .addParameter(OBJECT_PARAMETER_NAME, context.pojoClass)
            .returns(Unit::class.asTypeName())
            .apply {
                context.table.columns.forEach {
                    if (!it.isEntityId) {
                        this.addStatement("statement[${it.name}] = ${OBJECT_PARAMETER_NAME}.${it.name}")
                    }
                }
            }
            .build()
        return applyInsert
    }

    private fun generateParseRowMethod(
        context: GenerationContext
    ): FunSpec {
        val parseRow = FunSpec.builder("parseRow")
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