package com.labijie.orm.generator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Origin
import com.labijie.infra.orm.SimpleIdTable
import com.labijie.infra.orm.compile.KspTablePojo
import com.labijie.infra.orm.compile.KspTablePojoSuper
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import org.jetbrains.exposed.dao.id.IdTable

class TableMetadata(declaration: KSClassDeclaration, val sourceFile: String, private val logger: KSPLogger) {


    class PojoSuper(val type: KSType, val isInterface: Boolean)

    val columns: MutableList<ColumnMetadata> = mutableListOf()

    val packageName = declaration.packageName.asString()
    val className = declaration.simpleName.getShortName()

    var primaryKeys: MutableList<ColumnMetadata> = mutableListOf()
    var kind: TableKind = TableKind.Normal
        private set

    val isSerializable: Boolean
    val isOpen: Boolean
    val implements: MutableList<PojoSuper> = mutableListOf()

    val tableClass by lazy {
        ClassName(packageName, className)
    }


    init {


        loop@ for (superType in declaration.getAllSuperTypes()) {
            val typeName = superType.declaration.qualifiedName?.asString()
            if (typeName == IdTable::class.qualifiedName) {
                kind = TableKind.ExposedIdTable
                break@loop
            }

            if (typeName == SimpleIdTable::class.qualifiedName) {
                kind = TableKind.SimpleIdTable
                break@loop
            }
        }

        val pojoAnnotation = declaration.annotations.firstOrNull {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == KspTablePojo::class.qualifiedName
        }

        val interfaceAnnotations = declaration.annotations.filter {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == KspTablePojoSuper::class.qualifiedName
        }


        val pojoAnnoValues = pojoAnnotation?.getProperties()


        isSerializable = pojoAnnoValues?.getOrDefault(KspTablePojo::kotlinSerializable.name, false) as? Boolean ?: false
        isOpen = pojoAnnoValues?.getOrDefault(KspTablePojo::isOpen.name, true) as? Boolean ?: true

        interfaceAnnotations.forEach {
            anno->
            val interfaceAnnoValues = anno.getProperties()
            val type = interfaceAnnoValues.getOrDefault(KspTablePojoSuper::type.name, null)
            //val by = interfaceAnnoValues.getOrDefault(KspTablePojoInterface::by.name, null)

            if(type is KSType){
                val classDeclaration = type.declaration as? KSClassDeclaration
                val isKotlin = classDeclaration != null && (classDeclaration.origin == Origin.KOTLIN || classDeclaration.origin == Origin.KOTLIN_LIB)
                val isInterface = classDeclaration?.classKind == com.google.devtools.ksp.symbol.ClassKind.INTERFACE
                val isOpen = classDeclaration?.isOpen() ?: false
                val isAbstract = classDeclaration?.isAbstract() ?: false
                //val byType = if(by != null && by is KSType && by.declaration.simpleName.getShortName() != "Unit") by else null
                if(isKotlin && (isInterface || (isOpen || isAbstract))) {
                    val i = PojoSuper(type, isInterface)
                    if(isInterface) {
                        implements.add(i)
                    }else {
                        if(!implements.any { !it.isInterface }) {
                            implements.add(0, i)
                        }else {
                            logger.warn("Table pojo has more than one super class, super class '${type.toTypeName()}' discarded.")
                        }
                    }
                }else {
                    logger.warn("Table pojo super class/interface is invalid, class '${type.toTypeName()}' is not interface or opened class)")
                }
            }
        }
    }

    fun hasPrimaryKey() = primaryKeys.size > 0

    fun normalizeClassName(): String {
        return if (this.className.endsWith("Table")) this.className.removeSuffix("Table") else "${this.className}POJO"
    }

    override fun toString(): String {
        return "TableMetadata(columns=${columns.size}, packageName='$packageName', className='$className', primaryKeys=${primaryKeys.joinToString { it.name }}, kind=$kind)"
    }


}