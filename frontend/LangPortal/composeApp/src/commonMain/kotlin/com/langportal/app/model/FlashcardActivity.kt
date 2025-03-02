package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class FlashcardState(
    val currentWordId: Long,
    val sourceWord: String,
    val targetWord: String,
    val isAnswerVisible: Boolean = false,
    val totalWords: Int,
    val completedWords: Int,
    val correctAnswers: Int,
    val sessionId: Long
)

@Serializable
data class FlashcardAnswer(
    val wordId: Long,
    val correct: Boolean
)