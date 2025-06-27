package com.labijie.infra.orm.serialization

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */

import com.labijie.infra.orm.serialization.CodecExtensions.isProtobuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.time.Instant") {
        element<Long>("epochSeconds")
        element<Int>("nano")
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        if (encoder.isProtobuf()) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeLongElement(descriptor, 0, value.epochSecond)
            composite.encodeIntElement(descriptor, 1, value.nano)
            composite.endStructure(descriptor)
        } else {
            encoder.encodeString(value.toString()) // e.g., "2025-06-27T12:34:56.789Z"
        }
    }

    override fun deserialize(decoder: Decoder): Instant {
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
            Instant.ofEpochSecond(seconds, nanos.toLong())
        } else {
            Instant.parse(decoder.decodeString())
        }
    }
}