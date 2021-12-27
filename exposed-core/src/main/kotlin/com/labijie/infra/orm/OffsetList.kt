package com.labijie.infra.orm

import java.util.*

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/27
 * @Description:
 */
class OffsetList<T>(var list: List<T> = emptyList(), var forwardToken: String? = null) {

    companion object {

        @JvmStatic
        fun <T> empty(): OffsetList<T> {
            return OffsetList(emptyList(), null)
        }

        @JvmStatic
        fun <TElement> encodeToken(
            queryResult: List<TElement>,
            offsetField: TElement.() -> Any,
            keyField: TElement.() -> Any
        ): String? {

            if (queryResult.isEmpty()) {
                return null
            }
            val keys = mutableSetOf<String>()
            val last = queryResult.last()
            var lastIndex = queryResult.size - 1
            val lastOffset = offsetField(last)
            val lastKey = keyField.invoke(last)
            keys.add(lastKey.toString())
            while (lastIndex > 0) {
                lastIndex--
                val nextEntry = queryResult[lastIndex]
                val nextOffset = offsetField.invoke(nextEntry)
                if (lastOffset == nextOffset) {
                    val nextKey = keyField.invoke(nextEntry)
                    keys.add(nextKey.toString())
                } else {
                    break
                }
            }
            val token = "${lastOffset}:${keys.joinToString(":")}"
            return Base64.getUrlEncoder().encodeToString(token.toByteArray(Charsets.UTF_8))
        }

        @JvmStatic
        fun decodeToken(forwardToken: String): Pair<String, List<String>> {
            val tokenString = Base64.getUrlDecoder().decode(forwardToken).toString(Charsets.UTF_8)
            val elements = tokenString.split(":")
            val offset = elements.first()
            val keys = elements.subList(1, elements.size)
            return Pair(offset, keys)
        }
    }
}