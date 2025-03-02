package com.langportal.app.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun fetchJson(url: String): String {
    return KtorHttpClient.client.get(url) {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }.bodyAsText()
}

suspend fun postJson(url: String, body: String): String {
    return KtorHttpClient.client.post(url) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.bodyAsText()
}