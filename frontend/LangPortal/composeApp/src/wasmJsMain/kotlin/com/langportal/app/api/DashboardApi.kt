package com.langportal.app.api

import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

actual class DashboardApi {
    private val baseUrl = "http://localhost:8080/api"
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    actual suspend fun getLastSession(): Result<StudySession> = runCatching {
        val response = fetchJson("$baseUrl/study-sessions/last")
        json.decodeFromString<StudySession>(response)
    }

    actual suspend fun getWordStatistics(): Result<ReviewStatistics> = runCatching {
        val response = fetchJson("$baseUrl/statistics/words")
        json.decodeFromString<ReviewStatistics>(response)
    }

    actual suspend fun getSessions(): Result<List<StudySession>> = runCatching {
        val response = fetchJson("$baseUrl/study-sessions")
        json.decodeFromString(ListSerializer(StudySession.serializer()), response)
    }
}