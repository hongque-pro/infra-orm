package com.labijie.orm.generator.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.labijie.infra.orm.compile.KspPrimaryKey
import com.labijie.infra.orm.compile.KspTableIgnore
import com.labijie.orm.generator.*
import com.labijie.orm.generator.writer.DSLWriter
import com.labijie.orm.generator.writer.PojoWriter
import com.labijie.orm.generator.writer.SpringRuntimeHintWriter
import org.jetbrains.exposed.sql.Table


class ExposedSymbolProcessor(
    private val logger: KSPLogger,
    options: Map<String, String> = mapOf()
) : SymbolProcessor {

    private val writerOptions = buildWriterOptions(options)


    override fun process(resolver: Resolver): List<KSAnnotated> {


        val visitContext = VisitContext()

        resolver.getAllFiles().forEach {
            it.accept(ExposedTableVisitor(logger), visitContext)
        }

        val tables = visitContext.getTables()

        logger.println("${tables.size} exposed table processed")

        tables.forEach {
            val context = GenerationContext(it, writerOptions)
            PojoWriter.write(context)
            DSLWriter.write(context)
            context
        }

        //SpringRuntimeHintWriter.write(tables, writerOptions, logger)
        //NativeReflectConfigWriter.write(tables, writerOptions, logger)

        return emptyList()
    }

    class ExposedTableVisitor(
        private val logger: KSPLogger
    ) : KSDefaultVisitor<VisitContext, Unit>() {

        private var currentFile: String = ""

        override fun visitFile(file: KSFile, data: VisitContext) {
            if(data.checkVisited(file)) return
            currentFile = file.filePath
            logger.info("process file: ${file.filePath}")
            file.declarations.forEach {
                it.accept(this, data)
            }
        }


        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: VisitContext) {
            val table = data.currentTable
            if (table != null) {
                //it.origin == Origin.KOTLIN 用来判断是否是来自直接声明
                //logger.println("property receiver: ${property.parentDeclaration?.qualifiedName?.getShortName()}")

                val columnType = property.getColumnType()
                if (columnType != null) {
                    val columnName = property.simpleName.getShortName()
                    val isSimplePrimary = ((table.kind == TableKind.ExposedIdTable || table.kind == TableKind.SimpleIdTable) && columnName == "id")
                    val isAnnotatedPrimary = property.annotations.any {
                        it.annotationType.resolve().declaration.qualifiedName?.asString() == KspPrimaryKey::class.qualifiedName
                    }

                    val isPrimaryKey = isSimplePrimary || isAnnotatedPrimary

                    val col = collectColumn(property, columnType, isPrimaryKey, table, isNullableProperty = false)

                    if (isPrimaryKey) {
                        table.primaryKeys.add(col)
                    }

                    table.columns.add(col)
                }
            }
        }

        private fun collectColumn(
            property: KSPropertyDeclaration,
            columnType: ColumnType,
            isPrimary: Boolean,
            table: TableMetadata,
            isNullableProperty: Boolean,
        ): ColumnMetadata {
            logger.info("column: ${property.simpleName.getShortName()}")
            val col = ColumnMetadata(
                name = property.simpleName.getShortName(),
                type = columnType.type,
                rawType = columnType.rawType,
                isNullableColumn = columnType.isNullable,
                isNullableProperty = isNullableProperty,
                isPrimary = isPrimary,
                isEntityId = isPrimary && table.kind == TableKind.ExposedIdTable
            )
            return col
        }

        override fun visitDeclaration(declaration: KSDeclaration, data: VisitContext) {
            if(data.checkVisited(declaration)) return
            if(declaration is KSClassDeclaration){
                declaration.accept(this, data)
            }
        }


        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: VisitContext) {
            val isTable = classDeclaration.getAllSuperTypes()
                .any {
                    it.declaration.qualifiedName?.asString() == Table::class.qualifiedName

                }

            val isIgnore = classDeclaration.annotations.any {
                it.shortName.asString() == KspTableIgnore::class.simpleName
            }

            if (classDeclaration.classKind == ClassKind.OBJECT && isTable && !isIgnore) {
                acceptTable(classDeclaration, data)
            } else {
                logger.println("skip symbol: ${classDeclaration.qualifiedName?.asString()}")
            }
        }


        private fun acceptTable(tableDeclaration: KSClassDeclaration, context: VisitContext) {
            val table = TableMetadata(tableDeclaration, this.currentFile, logger)
            context.addTable(table)
            TablePropertiesResolver.getAllProperties(tableDeclaration).forEach {
                it.accept(this, context)
            }
            logger.println("prepare $table")
        }

        override fun defaultHandler(node: KSNode, data: VisitContext) {

        }
    }
}