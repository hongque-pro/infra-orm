package com.labijie.infra.orm.serialization

import com.labijie.infra.orm.serialization.CodecExtensions.isProtobuf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.LocalDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        if (encoder.isProtobuf()) {
            encoder.encodeLong(value.toEpochDay())
        } else {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return if (decoder.isProtobuf()) {
            val value = decoder.decodeLong()
            LocalDate.ofEpochDay(value)
        } else {
            LocalDate.parse(decoder.decodeString())
        }
    }
}