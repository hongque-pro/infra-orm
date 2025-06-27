package com.labijie.infra.orm.aot

import com.labijie.infra.orm.serialization.*
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization
import org.graalvm.nativeimage.hosted.RuntimeReflection


/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
@Suppress("unused")
internal class InfraOrmFeature : Feature {

    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {

        RuntimeClassInitialization.initializeAtRunTime("com.labijie.infra.orm.compile")
        RuntimeClassInitialization.initializeAtBuildTime(DeprecationLevel::class.java)

        access?.registerSerializer(BigDecimalSerializer::class.java)
        access?.registerSerializer(DurationSerializer::class.java)
        access?.registerSerializer(InstantSerializer::class.java)
        access?.registerSerializer(LocalDateSerializer::class.java)
        access?.registerSerializer(LocalDateTimeSerializer::class.java)
        access?.registerSerializer(LocalTimeSerializer::class.java)
        access?.registerSerializer(UUIDSerializer::class.java)


        access?.registerClassByName(DeprecationLevel::class.java.name)

        access?.registerClassByName("kotlinx.serialization.protobuf.internal.ProtobufDecoder")
        access?.registerClassByName("kotlinx.serialization.protobuf.internal.ProtobufEncoder")
        access?.registerClassByName("kotlinx.serialization.json.JsonEncoder")
        access?.registerClassByName("kotlinx.serialization.json.JsonDecoder")
    }

    private fun Feature.BeforeAnalysisAccess.registerSerializer(clazz: Class<*>) {
        this.findClassByName(clazz.name)?.let {
            RuntimeReflection.register(it)
            RuntimeReflection.registerFieldLookup(it, "INSTANCE")

            for (constructor in it.getDeclaredConstructors()) {
                RuntimeReflection.register(constructor)
            }
        }
    }

    private fun Feature.BeforeAnalysisAccess.registerClassByName(className: String) {

        val clazz = this.findClassByName(className)
        if (clazz != null) {
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
    }
}