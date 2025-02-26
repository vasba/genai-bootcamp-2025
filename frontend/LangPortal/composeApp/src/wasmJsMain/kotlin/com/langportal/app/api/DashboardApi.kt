package com.langportal.app.api

import com.langportal.app.model.DashboardData
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.*

actual class DashboardApi {
    private val backendUrl = "http://localhost:8080/api/statistics/words"
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        coerceInputValues = true // Add this to handle numeric type coercion
    }

    actual suspend fun getDashboardData(): Result<DashboardData> = runCatching {
        val headers = Headers()
        headers.append("Content-Type", "application/json")
        headers.append("Accept", "application/json")
        headers.append("Access-Control-Allow-Origin", "*")

        val init = RequestInit(
            method = "GET",
            headers = headers,
            mode = RequestMode.CORS,
            cache = RequestCache.DEFAULT,
            credentials = RequestCredentials.SAME_ORIGIN
        )
        
        val response = window.fetch(backendUrl).await<Response>()

        if (response.status.toInt() != 200) {
            throw Exception("HTTP error! status: ${response.status}")
        }
        val responseText = response.text().await<Map<String, String>>()

        json.decodeFromString(DashboardData.serializer(), responseText.toString())
    }
}