package com.langportal.app.api

import com.langportal.app.model.DashboardData
import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import com.langportal.app.model.Page
import kotlinx.serialization.json.Json

expect class DashboardApi() {
    suspend fun getLastSession(): Result<StudySession>
    suspend fun getWordStatistics(): Result<ReviewStatistics>
    suspend fun getSessions(page: Int = 0, size: Int = 20): Result<Page<StudySession>>
}