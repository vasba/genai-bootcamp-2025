package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class ReviewStatistics(
    val totalReviews: Int,
    val correctReviews: Int,
    val accuracy: Double,
)