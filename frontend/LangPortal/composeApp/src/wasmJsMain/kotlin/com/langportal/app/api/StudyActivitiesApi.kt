package com.langportal.app.api

import com.langportal.app.model.StudyActivity
import io.ktor.client.call.*
import io.ktor.client.request.*

actual class StudyActivitiesApi {
    private val baseUrl = KtorHttpClient.BASE_URL

    actual suspend fun getAllActivities(): Result<List<StudyActivity>> = runCatching {
        KtorHttpClient.client.get("$baseUrl/activities").body()
    }

    actual suspend fun getActivityById(id: Long): Result<StudyActivity> = runCatching {
        KtorHttpClient.client.get("$baseUrl/activities/$id").body()
    }
}