package com.langportal.app.api

import com.langportal.app.model.DashboardData
import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import kotlinx.serialization.json.Json

expect suspend fun fetchJson(url: String): String

expect class DashboardApi() {
    suspend fun getLastSession(): Result<StudySession>
    suspend fun getWordStatistics(): Result<ReviewStatistics>
    suspend fun getSessions(): Result<List<StudySession>>
}