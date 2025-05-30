package com.labijie.infra.orm

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.collections.map

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

        fun encodeToken(key: String): String {
            if(key.isEmpty()) return ""
            return encodeToken(arrayOf(key))
        }

        fun encodeToken(values: Array<out String>): String {
            val str = values.joinToString("&") { URLEncoder.encode(it, Charsets.UTF_8.name()) }
            return Base64.getUrlEncoder().encode(str.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
        }

        fun encodeToken(values: Iterable<String>): String {
            val str = values.joinToString("&") { URLEncoder.encode(it, Charsets.UTF_8.name()) }
            return Base64.getUrlEncoder().encode(str.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
        }

        @JvmStatic
        fun decodeToken(token: String): List<String> {
            if(token.isBlank()) return listOf()

            val data =token.toByteArray(Charsets.UTF_8)
            val decodedBase64 = Base64.getUrlDecoder().decode(data).toString(Charsets.UTF_8)

            val list =decodedBase64.split("&")
            return list.map { URLDecoder.decode(it, Charsets.UTF_8.name()) }
        }
    }


    fun <R> map(transform: (T) -> R): OffsetList<R> {
        val ll = this.list
        val rr = ll.map(transform)
        return OffsetList(rr, this.forwardToken)
    }

}