package com.langportal.app.api

import com.langportal.app.model.FlashcardState
import com.langportal.app.model.FlashcardAnswer
import com.langportal.app.model.StudySession
import com.langportal.app.model.WordDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val error: String? = null
)

@Serializable
data class GroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val words: List<WordDTO>
)

actual class FlashcardApi {
    private val baseUrl = KtorHttpClient.BASE_URL

    actual suspend fun startSession(groupId: Long): Result<FlashcardState> = runCatching {
        // Create a new session first
        val sessionResponse = KtorHttpClient.client.post("$baseUrl/study-sessions") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("groupId" to groupId, "studyActivityId" to 1))
        }.body<ApiResponse<StudySession>>()
        
        val session = sessionResponse.data
        val sessionId = session.id

        // Get first word from group
        val groupResponse = KtorHttpClient.client.get("$baseUrl/groups/$groupId").body<GroupResponse>()
        val firstWord = groupResponse.words.first()
        
        FlashcardState(
            currentWordId = firstWord.id,
            sourceWord = firstWord.sourceWord,
            targetWord = firstWord.targetWord,
            totalWords = groupResponse.words.size,
            completedWords = 0,
            correctAnswers = 0,
            sessionId = sessionId
        )
    }
    
    actual suspend fun submitAnswer(sessionId: Long, answer: FlashcardAnswer): Result<FlashcardState> = runCatching {
        KtorHttpClient.client.post("$baseUrl/study-sessions/$sessionId/review") {
            contentType(ContentType.Application.Json)
            setBody(answer)
        }.body()
    }

    actual suspend fun getNextWord(sessionId: Long): Result<FlashcardState> = runCatching {
        KtorHttpClient.client.get("$baseUrl/study-sessions/$sessionId/next").body()
    }
}