package com.labijie.infra.orm.aot


/**
 * @author Anders Xiao
 * @date 2025/6/27
 */

import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.serialization.*
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization
import org.graalvm.nativeimage.hosted.RuntimeReflection
import org.jetbrains.exposed.sql.Table


@Suppress("unused")
class InfraOrmFeature : Feature {

    companion object {
        const val SERIALIZATION_NAME_SPACE = "com.labijie.infra.orm.serialization"

        fun serializerClassName(simpleName: String): String {
            return "$SERIALIZATION_NAME_SPACE.$simpleName"
        }
    }

    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {

        RuntimeClassInitialization.initializeAtBuildTime("com.labijie.infra.orm.compile")

        access?.initAtBuildTime("kotlin.DeprecationLevel")

        access?.findClass("kotlinx.serialization.KSerializer")?.let {

            RuntimeReflection.register(it)

            registerSerializer(BigDecimalSerializer::class.java)
            registerSerializer(DurationSerializer::class.java)
            registerSerializer(InstantSerializer::class.java)
            registerSerializer(LocalDateSerializer::class.java)
            registerSerializer(LocalDateTimeSerializer::class.java)
            registerSerializer(LocalTimeSerializer::class.java)
            registerSerializer(UUIDSerializer::class.java)

            println("Infra-ORM kotlin serializers registered.")
        }

        access?.registerClass(OffsetList::class.java)


        access?.registerClassByName("kotlinx.serialization.protobuf.internal.ProtobufDecoder")
        access?.registerClassByName("kotlinx.serialization.protobuf.internal.ProtobufEncoder")
        access?.registerClassByName("kotlinx.serialization.json.JsonEncoder")
        access?.registerClassByName("kotlinx.serialization.json.JsonDecoder")
    }

    private fun Feature.BeforeAnalysisAccess.initAtBuildTime(className: String): Class<*>? {
        return try {
            val clazz: Class<*>? = findClass(className)
            if (clazz != null) {
                RuntimeClassInitialization.initializeAtBuildTime(clazz)
                println("Registered class for build-time init: " + clazz.getName())
            } else {
                println("Class not found: $className")
            }
            clazz
        } catch (e: Exception) {
            System.err.println("Error during class check: " + e.printStackTrace())
            null
        }
    }

    private fun Feature.BeforeAnalysisAccess.findClass(clazzName: String): Class<*>? {
        return try {
            this.findClassByName(clazzName)
        }catch (e: ClassNotFoundException) {
            null
        }
    }

    private fun registerSerializer(clazz: Class<*>) {
        RuntimeReflection.register(clazz)

        try {
            val instanceField = clazz.getDeclaredField("INSTANCE")
            RuntimeReflection.register(instanceField)
        } catch (ex: NoSuchFieldException) {
            // 不是 object 就忽略
        }

        for (constructor in clazz.declaredConstructors) {
            RuntimeReflection.register(constructor)
        }
    }

    private fun Feature.BeforeAnalysisAccess.registerSerializer(className: String) {
        findClass(className)?.let {
            registerSerializer(it)
        }
    }

    private fun Feature.BeforeAnalysisAccess.registerClass(clazz: Class<*>) {
        RuntimeReflection.register(clazz)

        for (constructor in clazz.getDeclaredConstructors()) {
            RuntimeReflection.register(constructor)
        }

        for (method in clazz.getDeclaredMethods()) {
            RuntimeReflection.register(method)
        }

        for (field in clazz.getDeclaredFields()) {
            RuntimeReflection.register(field)
        }
    }

    private fun Feature.BeforeAnalysisAccess.registerClassByName(className: String) {

        findClass(className)?.let {
            clazz->
            registerClass(clazz)
        }
    }

}