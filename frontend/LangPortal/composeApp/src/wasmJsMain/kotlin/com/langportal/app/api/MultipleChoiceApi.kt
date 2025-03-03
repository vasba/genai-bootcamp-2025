package com.langportal.app.api

import com.langportal.app.model.MultipleChoiceState
import com.langportal.app.model.MultipleChoiceAnswer
import com.langportal.app.model.StudySession
import com.langportal.app.model.WordDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class WordsResponse(
    val words: List<WordDTO>,
    val totalPages: Int,
    val currentPage: Int,
    val totalWords: Long
)

actual class MultipleChoiceApi {
    private val baseUrl = KtorHttpClient.BASE_URL
    private var cachedGroupWords: List<WordDTO> = emptyList()
    private var cachedAllWords: List<WordDTO>? = null
    private var currentWordIndex: Int = 0

    private suspend fun getAllWords(): List<WordDTO> {
        if (cachedAllWords == null) {
            val response = KtorHttpClient.client.get("$baseUrl/words").body<WordsResponse>()
            cachedAllWords = response.words
            println("getAllWords: $cachedAllWords")
        }
        return cachedAllWords!!
    }

    actual suspend fun startSession(groupId: Long): Result<MultipleChoiceState> = runCatching {
        val sessionResponse = KtorHttpClient.client.post("$baseUrl/study-sessions") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("groupId" to groupId, "studyActivityId" to 2))
        }.body<ApiResponse<StudySession>>()
        
        val session = sessionResponse.data
        val sessionId = session.id

        val groupResponse = KtorHttpClient.client.get("$baseUrl/groups/$groupId").body<GroupResponse>()
        cachedGroupWords = groupResponse.words
        currentWordIndex = 0
        val firstWord = cachedGroupWords.first()
        
        // Get all available words to use as options
        val allWords = getAllWords()
        
        // Generate 2 random incorrect options from all available words
        val incorrectOptions = allWords
            .filter { it.id != firstWord.id }
            .shuffled()
            .take(2)
            .map { it.targetWord }
        println("Incorrect options: $incorrectOptions")
        val options = (incorrectOptions + firstWord.targetWord).shuffled()
        println("Options: $options")
        MultipleChoiceState(
            currentWordId = firstWord.id,
            sourceWord = firstWord.sourceWord,
            options = options,
            correctAnswer = firstWord.targetWord,
            totalWords = cachedGroupWords.size,
            completedWords = 0,
            correctAnswers = 0,
            sessionId = sessionId
        )
    }

    actual suspend fun submitAnswer(sessionId: Long, answer: MultipleChoiceAnswer): Result<Boolean> = runCatching {
        val response = KtorHttpClient.client.post("$baseUrl/study-sessions/$sessionId/review") {
            contentType(ContentType.Application.Json)
            setBody(answer)
        }.body<ApiResponse<Boolean>>()
        
        if (!response.success) {
            throw Exception(response.error ?: "Unknown error occurred while submitting answer")
        }
        
        response.data
    }

    actual suspend fun getNextWord(sessionId: Long): Result<MultipleChoiceState> = runCatching {
        currentWordIndex++
        if (currentWordIndex >= cachedGroupWords.size) {
            throw IllegalStateException("No more words available in the session")
        }
        
        val nextWord = cachedGroupWords[currentWordIndex]
        
        // Get all available words to use as options
        val allWords = getAllWords()
        println("getNextWord All words: $allWords")
        
        // Generate 2 random incorrect options from all available words
        val incorrectOptions = allWords
            .filter { it.id != nextWord.id }
            .shuffled()
            .take(2)
            .map { it.targetWord }
        println("getNextWord Incorrect options: $incorrectOptions")
        val options = (incorrectOptions + nextWord.targetWord).shuffled()
        println("getNextWord Options: $options")
        MultipleChoiceState(
            currentWordId = nextWord.id,
            sourceWord = nextWord.sourceWord,
            options = options,
            correctAnswer = nextWord.targetWord,
            totalWords = cachedGroupWords.size,
            completedWords = currentWordIndex,
            correctAnswers = 0, // This should be updated based on the session state
            sessionId = sessionId
        )
    }
}