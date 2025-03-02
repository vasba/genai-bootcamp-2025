package com.langportal.app.api

import com.langportal.app.model.FlashcardState
import com.langportal.app.model.FlashcardAnswer
import kotlinx.serialization.json.Json

actual class FlashcardApi {
    private val baseUrl = "http://localhost:8080/api"
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    actual suspend fun startSession(groupId: Long): Result<FlashcardState> = runCatching {
        // Create a new session first
        val sessionRequest = """{"groupId":$groupId,"studyActivityId":1}"""
        val sessionResponse = postJson("$baseUrl/study-sessions", sessionRequest)
        val sessionData = json.decodeFromString<SessionResponse>(sessionResponse)
        val sessionId = sessionData.data.id

        // Get first word from group
        val wordResponse = fetchJson("$baseUrl/groups/$groupId")
        val group = json.decodeFromString<GroupResponse>(wordResponse)
        val firstWord = group.words.first()

        FlashcardState(
            currentWordId = firstWord.id,
            sourceWord = firstWord.sourceWord,
            targetWord = firstWord.targetWord,
            totalWords = group.words.size,
            completedWords = 0,
            correctAnswers = 0,
            isAnswerVisible = false,
            sessionId = sessionId
        )
    }

    actual suspend fun submitAnswer(sessionId: Long, answer: FlashcardAnswer): Result<FlashcardState> = runCatching {
        val reviewRequest = """{"word":{"id":${answer.wordId}},"correct":${answer.correct}}"""
        postJson("$baseUrl/study-sessions/$sessionId/review", reviewRequest)

        // Get updated state
        val wordResponse = fetchJson("$baseUrl/study-sessions/$sessionId/reviews")
        val reviews = json.decodeFromString<ReviewsResponse>(wordResponse)
        
        val groupId = reviews.data.firstOrNull()?.studySessionId ?: throw IllegalStateException("No session found")
        val groupResponse = fetchJson("$baseUrl/groups/$groupId")
        val group = json.decodeFromString<GroupResponse>(groupResponse)
        
        val nextUnreviewedWord = group.words.find { word -> 
            reviews.data.none { it.word.id == word.id }
        } ?: group.words.last()

        FlashcardState(
            currentWordId = nextUnreviewedWord.id,
            sourceWord = nextUnreviewedWord.sourceWord,
            targetWord = nextUnreviewedWord.targetWord,
            totalWords = group.words.size,
            completedWords = reviews.data.size,
            correctAnswers = reviews.data.count { it.correct },
            isAnswerVisible = false,
            sessionId = sessionId
        )
    }

    actual suspend fun getNextWord(sessionId: Long): Result<FlashcardState> = runCatching {
        // Reuse the same logic as submitAnswer without submitting a new review
        val wordResponse = fetchJson("$baseUrl/study-sessions/$sessionId/reviews")
        val reviews = json.decodeFromString<ReviewsResponse>(wordResponse)
        
        val groupId = reviews.data.firstOrNull()?.studySessionId ?: throw IllegalStateException("No session found")
        val groupResponse = fetchJson("$baseUrl/groups/$groupId")
        val group = json.decodeFromString<GroupResponse>(groupResponse)
        
        val nextUnreviewedWord = group.words.find { word -> 
            reviews.data.none { it.word.id == word.id }
        } ?: group.words.last()

        FlashcardState(
            currentWordId = nextUnreviewedWord.id,
            sourceWord = nextUnreviewedWord.sourceWord,
            targetWord = nextUnreviewedWord.targetWord,
            totalWords = group.words.size,
            completedWords = reviews.data.size,
            correctAnswers = reviews.data.count { it.correct },
            isAnswerVisible = false,
            sessionId = sessionId
        )
    }

    // Response data classes
    @kotlinx.serialization.Serializable
    private data class SessionResponse(val data: SessionDTO)

    @kotlinx.serialization.Serializable
    private data class SessionDTO(val id: Long)

    @kotlinx.serialization.Serializable
    private data class GroupResponse(val words: List<WordDTO>)

    @kotlinx.serialization.Serializable
    private data class WordDTO(
        val id: Long,
        val sourceWord: String,
        val targetWord: String
    )

    @kotlinx.serialization.Serializable
    private data class ReviewsResponse(val data: List<ReviewDTO>)

    @kotlinx.serialization.Serializable
    private data class ReviewDTO(
        val word: WordDTO,
        val correct: Boolean,
        val studySessionId: Long
    )
}