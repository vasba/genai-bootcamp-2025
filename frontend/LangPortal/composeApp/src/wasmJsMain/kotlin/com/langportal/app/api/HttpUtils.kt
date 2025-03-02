package com.langportal.app.api

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.fetch.Headers
import org.w3c.fetch.Request


actual suspend fun postJson(url: String, body: String): String {
    val headers = Headers().apply {
        append("Content-Type", "application/json")
    }

    val init = RequestInit(
        method = "POST",
        headers = headers,
        body = body.toJsString()
    )
   //val request = Request(url)
    val response = window.fetch(url).await<Response>()
    if (!response.ok) {
        throw Error("HTTP error! status: ${response.status}")
    }
    return response.text().await()
}