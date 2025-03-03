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
    private var cachedWords: List<WordDTO> = emptyList()
    private var currentWordIndex: Int = 0

    actual suspend fun startSession(groupId: Long): Result<FlashcardState> = runCatching {
        // Create a new session first
        val sessionResponse = KtorHttpClient.client.post("$baseUrl/study-sessions") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("groupId" to groupId, "studyActivityId" to 1))
        }.body<ApiResponse<StudySession>>()
        
        val session = sessionResponse.data
        val sessionId = session.id

        // Get words from group and cache them
        val groupResponse = KtorHttpClient.client.get("$baseUrl/groups/$groupId").body<GroupResponse>()
        cachedWords = groupResponse.words
        currentWordIndex = 0
        val firstWord = cachedWords.first()
        
        FlashcardState(
            currentWordId = firstWord.id,
            sourceWord = firstWord.sourceWord,
            targetWord = firstWord.targetWord,
            totalWords = cachedWords.size,
            completedWords = 0,
            correctAnswers = 0,
            sessionId = sessionId
        )
    }
    
    actual suspend fun submitAnswer(sessionId: Long, answer: FlashcardAnswer): Result<Boolean> = runCatching {
        val response = KtorHttpClient.client.post("$baseUrl/study-sessions/$sessionId/review") {
            contentType(ContentType.Application.Json)
            setBody(answer)
        }.body<ApiResponse<Boolean>>()
        
        if (!response.success) {
            throw Exception(response.error ?: "Unknown error occurred while submitting answer")
        }
        
        response.data
    }

    actual suspend fun getNextWord(sessionId: Long): Result<FlashcardState> = runCatching {
        // Instead of making an API call, use the cached words
        currentWordIndex++
        if (currentWordIndex >= cachedWords.size) {
            throw IllegalStateException("No more words available in the session")
        }
        
        val nextWord = cachedWords[currentWordIndex]
        FlashcardState(
            currentWordId = nextWord.id,
            sourceWord = nextWord.sourceWord,
            targetWord = nextWord.targetWord,
            totalWords = cachedWords.size,
            completedWords = currentWordIndex,
            correctAnswers = 0, // This should be updated based on the session state
            sessionId = sessionId
        )
    }
}