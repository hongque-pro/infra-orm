package com.labijie.orm.generator.ksp

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import org.jetbrains.exposed.sql.Table

object TablePropertiesResolver {

    private fun resolveProperties(
        isTop: Boolean,
        dc: KSClassDeclaration,
        aggregation: MutableMap<String, KSPropertyDeclaration>,
        continueResolve: (type: KSClassDeclaration) -> Boolean
    ) {
        if (isTop || continueResolve.invoke(dc)) {
            val properties = dc.getDeclaredProperties()
            properties.forEach {
                aggregation.putIfAbsent(it.simpleName.getShortName(), it)
            }
            loop@ for (r in dc.superTypes) {
                val type = r.resolve()
                val classDeclaration = type.declaration as? KSClassDeclaration
                if (classDeclaration != null) {
                    resolveProperties(false, classDeclaration, aggregation, continueResolve)
                    break@loop //解析很昂贵，找到类型跳出，避免继续解析到接口
                }
            }
        }
    }

    fun getAllProperties(dc: KSClassDeclaration, logger: KSPLogger? = null): Collection<KSPropertyDeclaration> {
        val agg = mutableMapOf<String, KSPropertyDeclaration>()
        resolveProperties(true, dc, agg) {

            val matched = (it.qualifiedName!!.asString() != Table::class.qualifiedName)
            matched
        }
        return agg.values
    }
}