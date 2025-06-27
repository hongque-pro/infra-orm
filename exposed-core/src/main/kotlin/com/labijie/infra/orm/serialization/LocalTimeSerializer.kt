package com.labijie.infra.orm.serialization

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */

import com.labijie.infra.orm.serialization.CodecExtensions.isProtobuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.LocalTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        if (encoder.isProtobuf()) {
            encoder.encodeLong(value.toNanoOfDay())
        } else {
            encoder.encodeString(value.toString()) // e.g., "12:34:56.789"
        }
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return if (decoder.isProtobuf()) {
            val v = decoder.decodeLong()
            LocalTime.ofNanoOfDay(v)
        } else {
            LocalTime.parse(decoder.decodeString())
        }
    }
}