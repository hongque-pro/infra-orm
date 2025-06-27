package com.labijie.infra.orm.serialization

import com.labijie.infra.orm.serialization.CodecExtensions.isProtobuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
object BigDecimalSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("java.math.BigDecimal") {
            element<Int>("a")
            element<ByteArray>("b")
        }


    override fun deserialize(decoder: Decoder): BigDecimal {

        return if(decoder.isProtobuf()) {
            val composite = decoder.beginStructure(descriptor)
            var scale = 0
            var unscaledBytes = ByteArray(0)
            loop@ while (true) {
                when (val index = composite.decodeElementIndex(descriptor)) {
                    0 -> scale = composite.decodeIntElement(descriptor, 0)
                    1 -> unscaledBytes = composite.decodeSerializableElement(descriptor, 1, ByteArraySerializer())
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw kotlinx.serialization.SerializationException("Unexpected index $index")
                }
            }
            composite.endStructure(descriptor)
            return BigDecimal(BigInteger(unscaledBytes), scale)
        }
        else {
            BigDecimal(decoder.decodeString())
        }
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        val bdString = value.toPlainString()

        return if(encoder.isProtobuf()) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeIntElement(descriptor, 0, value.scale())
            composite.encodeSerializableElement(descriptor, 1, ByteArraySerializer(), value.unscaledValue().toByteArray())
            composite.endStructure(descriptor)
        }
        else {
            encoder.encodeString(bdString)
        }
    }
}