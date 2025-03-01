package com.langportal.app.api

import com.langportal.app.model.StudyActivity
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

actual class StudyActivitiesApi {
    private val baseUrl = "http://localhost:8080/api"
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        coerceInputValues = true // Handle null values more gracefully
    }

    actual suspend fun getAllActivities(): Result<List<StudyActivity>> = runCatching {
        val response = fetchJson("$baseUrl/activities")
        if (response.isNullOrBlank() || response == "null") {
            emptyList()
        } else {
            json.decodeFromString(ListSerializer(StudyActivity.serializer()), response)
                .takeIf { it.isNotEmpty() }
                ?: throw NoActivitiesException("No study activities are available")
        }
    }

    actual suspend fun getActivityById(id: Long): Result<StudyActivity> = runCatching {
        val response = fetchJson("$baseUrl/activities/$id")
        if (response.isNullOrBlank() || response == "null") {
            throw ActivityNotFoundException("Activity with id $id not found")
        }
        json.decodeFromString<StudyActivity>(response)
    }
}