package com.langportal.dto

data class WordReviewRequestDTO(
    val wordId: Long,
    val correct: Boolean,
)
