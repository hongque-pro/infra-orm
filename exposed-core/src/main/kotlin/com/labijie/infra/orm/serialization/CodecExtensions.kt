package com.labijie.infra.orm.serialization

import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * @author Anders Xiao
 * @date 2025/6/27
 */
object CodecExtensions {
    fun Decoder.isJson(): Boolean {
        return this::class.qualifiedName?.startsWith("kotlinx.serialization.json.") == true
    }

    fun Decoder.isProtobuf(): Boolean {
        return this::class.qualifiedName?.startsWith("kotlinx.serialization.protobuf.") == true
    }

    fun Encoder.isJson(): Boolean {
        return this::class.qualifiedName?.startsWith("kotlinx.serialization.json.") == true
    }

    fun Encoder.isProtobuf(): Boolean {
        return this::class.qualifiedName?.startsWith("kotlinx.serialization.protobuf.") == true
    }


//    fun Decoder.getJsonPrimitiveContent(): String {
//        return ((this as JsonDecoder).decodeJsonElement().jsonPrimitive.content)
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    fun Encoder.toJsonPrimitiveContent(value: String?) {
//        (this as JsonEncoder).encodeJsonElement(JsonUnquotedLiteral(value))
//    }
}