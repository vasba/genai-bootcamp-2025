package com.langportal.app.model

import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceState(
    val currentWordId: Long,
    val sourceWord: String,
    val options: List<String>,
    val correctAnswer: String,
    val isAnswerSubmitted: Boolean = false,
    val selectedAnswer: String? = null,
    val totalWords: Int,
    val completedWords: Int,
    val correctAnswers: Int,
    val sessionId: Long
)

@Serializable
data class MultipleChoiceAnswer(
    val wordId: Long,
    val selectedAnswer: String
)