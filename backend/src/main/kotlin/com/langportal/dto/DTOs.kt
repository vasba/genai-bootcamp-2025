package com.langportal.dto

import java.time.LocalDateTime

data class GroupDTO(
    val id: Long?,
    val name: String,
    val description: String?,
    val words: List<WordDTO> = emptyList()
)

data class WordDTO(
    val id: Long,
    val sourceWord: String,
    val targetWord: String,
    val groups: List<GroupDTO> = emptyList(),
    val correctReviews: Int = 0,
    val incorrectReviews: Int = 0
)

data class WordReviewItemDTO(
    val id: Long,
    val word: WordDTO,
    val correct: Boolean,
    val timestamp: LocalDateTime
)

data class ReviewStatsDTO(
    val totalReviews: Int,
    val correctReviews: Int,
    val accuracy: Double
)