package com.labijie.orm.generator.writer

import com.labijie.infra.orm.ExposedConverter
import com.labijie.orm.generator.*
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder.Companion.columnSelectiveParameter
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder.Companion.getSqlExtendMethod
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder.Companion.kotlinIsNotEmpty
import com.labijie.orm.generator.writer.AbstractDSLMethodBuilder.Companion.kotlinToList
import com.labijie.orm.generator.writer.dsl.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMembers

object DSLWriter {

    private const val OBJECT_PARAMETER_NAME = "raw"
    private val methodBuilders: MutableList<IDSLMethodBuilder> = mutableListOf()

    init {
        methodBuilders.add(SetValueExtensionMethod)
        methodBuilders.add(InsertMethod)
        methodBuilders.add(InsertAndGetIdMethod)
        methodBuilders.add(InsertIgnoreMethod)
        methodBuilders.add(UpsertMethod)
        methodBuilders.add(BatchInsertMethod)
        methodBuilders.add(BatchUpsertMethod)
        methodBuilders.add(UpdateMethod)
        methodBuilders.add(DeleteByPrimaryKeyMethod)
        methodBuilders.add(SelectByPrimaryMethod)
        methodBuilders.add(SelectMethod)
        methodBuilders.add(ReplaceMethod)
    }

    private fun TypeSpec.Builder.buildMethods(context: DSLCodeContext) {
        methodBuilders.forEach {
            it.buildMethods(context).forEach { method ->
                this.addFunction(method)
            }
        }
    }

    private val selectiveParameter by lazy {
        val colType = Column::class.asTypeName().parameterizedWildcard()

        ParameterSpec.builder("selective", colType)
            .apply {
                modifiers.add(KModifier.VARARG)
            }.build()
    }


    fun write(context: GenerationContext) {

        val parseRow = generateParseRowMethod(context)
        val parseRowSelective = generateParseRowSelectiveMethod(context)
        val rowMap = generateRowMapMethod(context, parseRow, parseRowSelective)
        val listMap = generateSliceExtensionMethod(context, rowMap)
        val allColumns = generateAllColumnsField(context)

        val applyInsert = generateAssignMethod(context)
        val getColumnValue = generateGetColumnValueMethod(context)
        val getColumnValueString = generateGetColumnValueStringMethod(context)
        val parseColumnValue = generateParseColumnValueMethod(context)
        val getColumnType = generateGetColumnTypeMethod(context)
        val selectSlice = generateSelectSliceMethod(context)


        val fileBuilder = FileSpec
            .builder(context.dslPackageName, fileName = context.dslClass.simpleName)
            .suppressRedundantVisibilityModifierWarning()

        val convertMethods = ExposedConverter::class.declaredMembers.filter {
            it.visibility == KVisibility.PUBLIC
        }.map { it.name }

        val file = fileBuilder
            .addImport(context.tableClass, context.table.columns.map { it.name })
            .addType(
                TypeSpec.objectBuilder(context.dslClass)
                    .addAnnotation(suppressAnnotation("unused", "DuplicatedCode", "MemberVisibilityCanBePrivate", "RemoveRedundantQualifierName"))
                    .addComments("DSL support for ${context.tableClass.simpleName}", context)
                    .addProperty(allColumns)
                    .addFunction(parseRow)
                    .addFunction(parseRowSelective)
                    .addFunction(getColumnType)
                    .addFunction(getColumnValueString)
                    .addFunction(parseColumnValue)
                    .addFunction(getColumnValue)
                    .addFunction(applyInsert)
                    //exposed methods
                    .addFunction(rowMap)
                    .addFunction(listMap)
                    .addFunction(selectSlice)
                    .apply {
                        val dslCtx = DSLCodeContext(
                            context,
                            allColumns,
                            fileBuilder,
                            parseRow,
                            parseRowSelective,
                            getColumnValue,
                            getColumnValueString,
                            parseColumnValue,
                            getColumnType,
                            applyInsert,
                            rowMap,
                            listMap,
                            selectSlice,
                            OBJECT_PARAMETER_NAME
                        )
                        this.buildMethods(dslCtx)
                    }
                    .build()
            )
            .build()
        val folder = context.options.getSourceFolder(context.table)
        file.writeTo(folder)
    }


    private fun generateRowMapMethod(
        context: GenerationContext,
        parseMethod: FunSpec,
        parseSelectiveMethod: FunSpec,
    ): FunSpec {
        return FunSpec.builder("to${context.pojoClass.simpleName}")
            .receiver(ResultRow::class)
            .addParameter(selectiveParameter)
            .returns(context.pojoClass)
            .beginControlFlow(
                "if(%N.%M())",
                selectiveParameter,
                MemberName("kotlin.collections", "isNotEmpty", isExtension = true)
            )
            .addStatement("return %N(this)", parseSelectiveMethod)
            .endControlFlow()
            .addStatement("return %N(this)", parseMethod)
            .build()
    }


    private fun generateSliceExtensionMethod(
        context: GenerationContext,
        toEntityMethod: FunSpec
    ): FunSpec {
        val returnType = List::class.asTypeName().parameterizedBy(context.pojoClass)

        return FunSpec.builder("to${context.pojoClass.simpleName}List")
            .receiver(Iterable::class.asTypeName().parameterizedBy(ResultRow::class.asTypeName()))
            .addParameter(selectiveParameter)
            .returns(returnType)
            .beginControlFlow("return this.map")
            .addStatement("it.%N(*%N)", toEntityMethod, selectiveParameter)
            .endControlFlow()
            .build()
    }

    private fun generateGetColumnValueMethod(context: GenerationContext): FunSpec {

        val typeVar = TypeVariableName("T")


        //@Suppress("UNCHECKED_CAST")
        return FunSpec.builder("getColumnValue")
            .addTypeVariable(typeVar)
            .addAnnotation(suppressUncheckedCastAnnotation)
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

    private fun generateParseColumnValueMethod(context: GenerationContext): FunSpec {

        val typeVar = TypeVariableName("T")

        //@Suppress("UNCHECKED_CAST")
        return FunSpec.builder("parseColumnValue")
            .addModifiers(KModifier.PRIVATE)
            .addTypeVariable(typeVar)
            .addAnnotation(suppressUncheckedCastAnnotation)
            .addParameter("valueString", String::class.asTypeName())
            .addParameter("column", Column::class.asTypeName().parameterizedBy(typeVar))
            .returns(typeVar)
            .beginControlFlow("val value = when(column)")
            .apply {
                context.table.columns.forEach {
                    if(it.isString) {
                        this.addStatement("${context.table.className}.${it.name} -> valueString")
                    }else {
                        val parseMethod = DefaultValues.getParseMethod(it.type)
                        parseMethod?.let { _ ->
                            addCode("${context.table.className}.${it.name} ->")
                            addCode(generateParsedValueCodeBlock("valueString", parseMethod).toString())
                        }
                    }
                }
                val errorMessage = "Can't converter value of ${context.pojoClass.simpleName}::\${column.name} to string."
                this.addStatement("else->throw %T(%P)", IllegalArgumentException::class, errorMessage)
            }
            .endControlFlow()
            .addStatement("return value as T")
            .build()
    }

    private fun generateGetColumnValueStringMethod(context: GenerationContext): FunSpec {

        val typeVar = TypeVariableName("T")
        val returnType = String::class.asTypeName()

        //@Suppress("UNCHECKED_CAST")
        return FunSpec.builder("getColumnValueString")
            .addModifiers(KModifier.PRIVATE)
            .addTypeVariable(typeVar)
            .receiver(context.pojoClass)
            .addParameter("column", Column::class.asTypeName().parameterizedBy(typeVar))
            .returns(returnType)
            .beginControlFlow("return when(column)")
            .apply {
                context.table.columns.forEach {
                    if(it.isString) {
                        if(it.isNullableColumn) {
                            this.addStatement(
                                "${context.table.className}.${it.name}->this.${it.name}.orEmpty()",
                            )
                        }else {
                            this.addStatement(
                                "${context.table.className}.${it.name}->this.${it.name}",
                            )
                        }
                    }
                    else {
                        val toStringMethod = DefaultValues.getToStringMethod(it.type)
                        toStringMethod?.let { _ ->
                            this.addStatement(
                                "${context.table.className}.${it.name}->%L",
                                generateToStringCodeBlock("this.${it.name}", toStringMethod).toString()
                            )
                        }
                    }
                }
                val errorMessage = "Can't converter value of ${context.pojoClass.simpleName}::\${column.name} to string."
                this.addStatement("else->throw %T(%P)", IllegalArgumentException::class, errorMessage)
            }
            .endControlFlow()
            .build()
    }

    private fun generateGetColumnTypeMethod(context: GenerationContext): FunSpec {

        val typeVar = TypeVariableName("T")

        //@Suppress("UNCHECKED_CAST")
        return FunSpec.builder("getColumnType")
            .addTypeVariable(typeVar)
            .receiver(context.tableClass)
            .addParameter("column", Column::class.asTypeName().parameterizedBy(typeVar))
            .returns(KClass::class.asTypeName().parameterizedWildcard())
            .beginControlFlow("return when(column)")
            .apply {
                context.table.columns.forEach {
//                    if(it.isEntityId) {
//                        this.addStatement("${it.name}->%T::class", it.type.toTypeName())
//                    }
//                    else {
//                        val t =
//                            if (!it.rawType.isMarkedNullable) it.rawType.toTypeName() else it.rawType.makeNotNullable()
//                                .toTypeName()
//                        this.addStatement("${it.name}->%T::class", t)
//                    }
                    val t = if (!it.type.isMarkedNullable) it.type else it.type.makeNotNullable()
                    this.addStatement("${it.name}->%T::class", t.toClassName())
                }
                val errorMessage = "Unknown column <\${column.name}> for '${context.pojoClass.simpleName}'"
                this.addStatement("else->throw %T(%P)", IllegalArgumentException::class, errorMessage)
            }
            .endControlFlow()
            .build()
    }


    private fun generateAssignMethod(context: GenerationContext): FunSpec {
        val updateBuilder = UpdateBuilder::class.asTypeName().parameterizedWildcard()

        updateBuilder.copy()

        val ignore = ParameterSpec.builder("ignore", Column::class.asTypeName().parameterizedWildcard())
            .apply {
                modifiers.add(KModifier.VARARG)
            }
            .build()

        val colType = Column::class.asTypeName().parameterizedWildcard()
        val columnArray = Array::class.asClassName()
            .parameterizedBy(WildcardTypeName.producerOf(colType)).copy(nullable = true)

        val selective = ParameterSpec.builder("selective", columnArray)
            .defaultValue("null")
            .build()

        val applyInsert = FunSpec.builder("assign")
            .addParameter("builder", updateBuilder)
            .addParameter(OBJECT_PARAMETER_NAME, context.pojoClass)
            .addParameter(selective)
            .addParameter(ignore)
            .returns(Unit::class.asTypeName())
            .addStatement("val list = if(selective.isNullOrEmpty()) null else selective")
            .apply {
                context.table.columns.forEach {
                    if (!it.isEntityId) {
                        this.addStatement(
                            "if((list == null || list.contains(${it.name})) && !%N.contains(${it.name}))",
                            ignore
                        )
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

    private fun generateAllColumnsField(
        context: GenerationContext
    ): PropertySpec {

        val colType = Column::class.asTypeName().parameterizedWildcard()
        //for out WildcardTypeName.producerOf(colType)
        val columnArray = Array::class.asClassName()
            .parameterizedBy(colType)

        val allColumns = PropertySpec.builder("allColumns", columnArray)
            .receiver(context.tableClass)
            .delegate(
                CodeBlock.builder()
                    .beginControlFlow("lazy")
                    .addStatement("arrayOf(")
                    .apply {
                        context.table.columns.mapIndexed { index, col ->
                            addStatement(if (index == context.table.columns.size) col.name else "${col.name},")
                        }
                    }
                    .addStatement(")")
                    .endControlFlow()
                    .build()
            )
            .build()
        return allColumns
    }

    private fun generateParseRowSelectiveMethod(
        context: GenerationContext
    ): FunSpec {
        val parseRow = FunSpec.builder("parseRowSelective")
            .addParameter("row", ResultRow::class)
            .returns(context.pojoClass)
            .addStatement("val plain = %T()", context.pojoClass)
            .apply {
                context.table.columns.forEach {
                    this.beginControlFlow("if(row.hasValue(${it.name}))", selectiveParameter)
                    if (it.isEntityId) {
                        this.addStatement("plain.${it.name} = row[${it.name}].value")
                    } else {
                        this.addStatement("plain.${it.name} = row[${it.name}]")
                    }
                    this.endControlFlow()
                }
            }
            .addStatement("return plain")
            .build()
        return parseRow
    }

    private fun FunSpec.Builder.addSelectSelective(
        varName: String,
        context: GenerationContext,
        selectiveParameter: ParameterSpec
    ): FunSpec.Builder {
        this.beginControlFlow("val $varName = if(%N.%M())", columnSelectiveParameter, kotlinIsNotEmpty)
            .addStatement(
                "select(%N.%M())",
                selectiveParameter,
                kotlinToList
            )
            .endControlFlow()
            .beginControlFlow("else")
            .addStatement("%M()", getSqlExtendMethod("selectAll"))
            .endControlFlow()
        return this
    }

    private fun generateSelectSliceMethod(context: GenerationContext): FunSpec {
        val varName = "query"
        return FunSpec.builder("selectSlice")
            .receiver(context.tableClass)
            .addParameter(columnSelectiveParameter)
            .returns(Query::class.asTypeName())
            .addSelectSelective(varName, context, columnSelectiveParameter)
            .addStatement("return $varName")
            .build()
    }

}