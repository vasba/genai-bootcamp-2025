package com.langportal.app.api

import com.langportal.app.model.FlashcardState
import com.langportal.app.model.FlashcardAnswer

expect class FlashcardApi() {
    suspend fun startSession(groupId: Long): Result<FlashcardState>
    suspend fun submitAnswer(sessionId: Long, answer: FlashcardAnswer): Result<FlashcardState>
    suspend fun getNextWord(sessionId: Long): Result<FlashcardState>
}