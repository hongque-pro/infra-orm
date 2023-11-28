package com.labijie.orm.generator.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.labijie.infra.orm.TableKspIgnore
import com.labijie.orm.generator.*
import com.labijie.orm.generator.writer.DSLWriter
import com.labijie.orm.generator.writer.PojoWriter
import org.jetbrains.exposed.sql.Table


class ExposedSymbolProcessor(
    private val logger: KSPLogger,
    options: Map<String, String>
) : SymbolProcessor {

    private var invoked = false

    private val writerOptions = buildWriterOptions(options)


    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val visitContext = VisitContext()

        resolver.getAllFiles().forEach {
            it.accept(ExposedTableVisitor(logger), visitContext)
        }

        val tables = visitContext.getTables()

        logger.println("${tables.size} exposed table processed")

        tables.forEach {
            val context = GenerationContext(it, writerOptions, logger)
            PojoWriter.write(context)
            DSLWriter.write(context)
        }
        invoked = true
        return emptyList()
    }

    class ExposedTableVisitor(
        private val logger: KSPLogger
    ) : KSDefaultVisitor<VisitContext, Unit>() {

        private var currentFile: String = ""

        override fun visitFile(file: KSFile, data: VisitContext) {
            if(data.checkVisited(file)) return
            currentFile = file.filePath
            logger.println("process file: ${file.filePath}")
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
                    val isPrimary = ((table.kind == TableKind.ExposedIdTable || table.kind == TableKind.SimpleIdTable) && columnName == "id")


                    logger.println("property: ${columnName}, table: ${table.kind}")

                    val col = ColumnMetadata(
                        name = property.simpleName.getShortName(),
                        type = columnType.type,
                        rawType = columnType.rawType,
                        isNull = columnType.isNullable,
                        isPrimary = isPrimary,
                        isEntityId = isPrimary && table.kind == TableKind.ExposedIdTable
                    )

                    if (isPrimary) {
                        table.primaryKeys.add(col)
                    }

                    table.columns.add(col)
                }
            }
        }


        override fun visitDeclaration(declaration: KSDeclaration, data: VisitContext) {
            if(data.checkVisited(declaration)) return
            if(declaration is KSClassDeclaration){
                declaration.accept(this, data)
            }
        }


        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: VisitContext) {
            val isTable = classDeclaration.getAllSuperTypes()
                .any { it.declaration.qualifiedName?.asString() == Table::class.qualifiedName }

            val isIgnore = classDeclaration.annotations.any {
                it.shortName.asString() == TableKspIgnore::class.simpleName
            }

            if (classDeclaration.classKind == ClassKind.OBJECT && isTable && !isIgnore) {
                acceptTable(classDeclaration, data)
            } else {
                logger.println("skip symbol: ${classDeclaration.qualifiedName?.asString()}")
            }
        }


        private fun acceptTable(tableDeclaration: KSClassDeclaration, context: VisitContext) {
            val table = TableMetadata(tableDeclaration, this.currentFile)
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