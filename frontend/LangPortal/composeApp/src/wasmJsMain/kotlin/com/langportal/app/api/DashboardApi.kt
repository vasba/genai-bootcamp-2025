package com.langportal.app.api

import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import io.ktor.client.call.*
import io.ktor.client.request.*

actual class DashboardApi {
    private val baseUrl = KtorHttpClient.BASE_URL

    actual suspend fun getLastSession(): Result<StudySession> = runCatching {
        KtorHttpClient.client.get("$baseUrl/study-sessions/last").body()
    }

    actual suspend fun getWordStatistics(): Result<ReviewStatistics> = runCatching {
        KtorHttpClient.client.get("$baseUrl/statistics/words").body()
    }

    actual suspend fun getSessions(): Result<List<StudySession>> = runCatching {
        KtorHttpClient.client.get("$baseUrl/study-sessions").body()
    }
}