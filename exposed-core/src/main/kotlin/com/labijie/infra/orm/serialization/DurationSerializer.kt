package com.labijie.infra.orm.serialization

import com.labijie.infra.orm.serialization.CodecExtensions.isProtobuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.time.Duration") {
        element<Long>("seconds")
        element<Int>("nanos")
    }

    override fun serialize(encoder: Encoder, value: Duration) {
        if (encoder.isProtobuf()) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeLongElement(descriptor, 0, value.seconds)
            composite.encodeIntElement(descriptor, 1, value.nano)
            composite.endStructure(descriptor)
        } else {
            encoder.encodeString(value.toString()) // ISO-8601 like: "PT1H2M"
        }
    }

    override fun deserialize(decoder: Decoder): Duration {
        return if (decoder.isProtobuf()) {
            var seconds = 0L
            var nanos = 0
            val composite = decoder.beginStructure(descriptor)
            loop@ while (true) {
                when (val index = composite.decodeElementIndex(descriptor)) {
                    0 -> seconds = composite.decodeLongElement(descriptor, 0)
                    1 -> nanos = composite.decodeIntElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
            composite.endStructure(descriptor)
            Duration.ofSeconds(seconds, nanos.toLong())
        } else {
            Duration.parse(decoder.decodeString()) // from ISO-8601
        }
    }
}