package com.langportal.app.api

import com.langportal.app.model.MultipleChoiceState
import com.langportal.app.model.MultipleChoiceAnswer

expect class MultipleChoiceApi() {
    suspend fun startSession(groupId: Long): Result<MultipleChoiceState>
    suspend fun submitAnswer(sessionId: Long, answer: MultipleChoiceAnswer): Result<Boolean>
    suspend fun getNextWord(sessionId: Long): Result<MultipleChoiceState>
}