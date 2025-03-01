package com.langportal.app.api

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.fetch.Headers

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual suspend fun fetchJson(url: String): String {
    val headers = Headers().apply {
        append("Content-Type", "application/json")
        append("Accept", "application/json")
    }
    
    val init = RequestInit(
        method = "GET",
        headers = headers
    )

    val response = window.fetch(url).await<Response>()
    val status = response.status.toInt()
    
    if (status != 200) {
        throw RuntimeException("HTTP error! status: $status")
    }
    
    return response.text().await<Map<String, String>>().toString()
}