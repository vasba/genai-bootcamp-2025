package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val totalReviews: Int,
    val correctReviews: Int,
    val accuracy: Double
)

@Serializable
data class SessionSummary(
    val date: String,
    val activityType: String,
    val score: Int,
    val totalWords: Int
)

@Serializable
data class StudentProgression(
    val totalWords: Int,
    val masteredWords: Int,
    val inProgressWords: Int
)