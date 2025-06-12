package com.labbijie.infra.orm.testing

import com.labijie.infra.orm.OffsetList
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class OffsetTokenEncodingTester {

    @ParameterizedTest
    @CsvSource(value = ["&&&,12323", "===fsdfsd,123232", "0000,&&&232"])
    fun testEncoderDecoder(key1: String, key2: String) {
        val values = listOf(key1, key2)

        val token = OffsetList.encodeToken(values)

        assertTrue { token.isNotEmpty() }

        val values2 = OffsetList.decodeToken(token)

        assertEquals(values.size, values2.size)

        for (i in values.indices) {
            assertEquals(values[i], values2[i])
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1234567890", "===fsdfsd,123232", "0000,&&&232"])
    fun testEncoderDecoderSingleKey(key: String) {
        val values = listOf(key)

        val token = OffsetList.encodeToken(values)

        assertTrue { token.isNotEmpty() }

        val values2 = OffsetList.decodeToken(token)

        assertEquals(values.size, values2.size)

        for (i in values.indices) {
            assertEquals(values[i], values2[i])
        }
    }
}