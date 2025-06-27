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
import java.util.*

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
object UUIDSerializer: KSerializer<UUID> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.util.UUID") {
        element<Long>("msb")
        element<Long>("lsb")
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        if (encoder.isProtobuf()) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeLongElement(descriptor, 0, value.mostSignificantBits)
            composite.encodeLongElement(descriptor, 1, value.leastSignificantBits)
            composite.endStructure(descriptor)
        } else {
            // JSON / String-based format
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): UUID {
        return if (decoder.isProtobuf()) {
            var msb = 0L
            var lsb = 0L
            val composite = decoder.beginStructure(descriptor)
            loop@ while (true) {
                when (val index = composite.decodeElementIndex(descriptor)) {
                    0 -> msb = composite.decodeLongElement(descriptor, 0)
                    1 -> lsb = composite.decodeLongElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
            composite.endStructure(descriptor)
            UUID(msb, lsb)
        } else {
            UUID.fromString(decoder.decodeString())
        }
    }


}