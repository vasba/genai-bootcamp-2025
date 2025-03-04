package com.langportal.app.api

import io.ktor.client.call.*
import io.ktor.client.request.*

actual class WordApi {
    private val baseUrl = KtorHttpClient.BASE_URL

    actual suspend fun getWords(page: Int, sortBy: String, order: String): WordsResponse {
        return KtorHttpClient.client.get("$baseUrl/words") {
            url {
                parameters.append("page", page.toString())
                parameters.append("sortBy", sortBy)
                parameters.append("order", order)
            }
        }.body()
    }
}