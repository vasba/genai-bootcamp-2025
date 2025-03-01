package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class StudySession(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val activityId: Long,
    val activityName: String,
    val startTime: String,
    val endTime: String? = null,
    val reviewItemsCount: Int,
    val studyActivityId : Long,
    val createdAt: String
)

@Serializable
data class StudyActivity(
    val id: Long,
    val name: String,
    val url: String
)

@Serializable
data class WordReviewItem(
    val id: Long,
    val correct: Boolean,
    val timestamp: String
)