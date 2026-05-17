package com.pacetrace.app.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pacetrace.app.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

object ApiClient {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val APPKEY = "389885588s0648fa"
    private const val APPSECRET = "56E39A1658455588885690425C0FD16055A21676"
    private const val BASE_URL = "https://run-lb.tanmasports.com/"
    private const val UA = "okhttp/3.10.0"

    private var token: String = ""

    fun setToken(t: String) {
        token = t
    }

    fun getToken(): String = token

    private fun md5(s: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(s.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun md5Upper(s: String): String = md5(s).uppercase()

    private fun buildSign(method: String, url: String, bodyStr: String? = null): String {
        val uri = URI(url)
        val query = uri.query ?: ""
        val params = query.split("&").filter { it.isNotEmpty() }.map {
            val parts = it.split("=", limit = 2)
            parts[0] to (parts.getOrElse(1) { "" })
        }.sortedBy { it.first }

        val parts = mutableListOf<String>()
        for ((k, v) in params) {
            parts.add("$k$v")
        }
        parts.add(APPKEY)
        parts.add(APPSECRET)
        if (bodyStr != null && method.uppercase() in listOf("POST", "PUT", "PATCH")) {
            parts.add(bodyStr)
        }
        return md5Upper(parts.joinToString(""))
    }

    suspend fun get(
        path: String,
        params: Map<String, Any?>? = null
    ): Map<String, Any?> {
        return request("GET", path, params = params)
    }

    suspend fun post(
        path: String,
        body: Map<String, Any?>? = null
    ): Map<String, Any?> {
        return request("POST", path, body = body)
    }

    private suspend fun request(
        method: String,
        path: String,
        params: Map<String, Any?>? = null,
        body: Map<String, Any?>? = null
    ): Map<String, Any?> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val urlBuilder = StringBuilder(BASE_URL.trimEnd('/'))
            urlBuilder.append("/")
            urlBuilder.append(path.trimStart('/'))

            if (!params.isNullOrEmpty()) {
                val sorted = params.filter { it.value != null }.entries.sortedBy { it.key }
                val queryParts = sorted.joinToString("&") {
                    "${java.net.URLEncoder.encode(it.key, "UTF-8")}=${java.net.URLEncoder.encode(it.value.toString(), "UTF-8")}"
                }
                if (queryParts.isNotEmpty()) {
                    urlBuilder.append("?")
                    urlBuilder.append(queryParts)
                }
            }

            val fullUrl = urlBuilder.toString()
            val bodyStr = if (body != null) gson.toJson(body) else null
            val sign = buildSign(method, fullUrl, bodyStr)

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .header("appKey", APPKEY)
                .header("sign", sign)
                .header("User-Agent", UA)

            if (token.isNotEmpty()) {
                requestBuilder.header("token", token)
            }

            if (bodyStr != null) {
                requestBuilder.method(method, bodyStr.toRequestBody("application/json; charset=UTF-8".toMediaType()))
            } else {
                requestBuilder.method(method, null)
            }

            try {
                val okResponse = client.newCall(requestBuilder.build()).execute()
                val responseBody = okResponse.body?.string() ?: "{}"
                val type = object : TypeToken<Map<String, Any?>>() {}.type
                return@withContext gson.fromJson(responseBody, type)
            } catch (e: Exception) {
                android.util.Log.e("ApiClient", "request failed: ${method} ${path}", e)
                return@withContext mapOf("code" to -1, "msg" to (e.message ?: "unknown error"))
            }
        }
    }

}
