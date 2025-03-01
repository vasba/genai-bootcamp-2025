package com.langportal.app.api

import com.langportal.app.model.StudyActivity

expect class StudyActivitiesApi() {
    suspend fun getAllActivities(): Result<List<StudyActivity>>
    suspend fun getActivityById(id: Long): Result<StudyActivity>
}