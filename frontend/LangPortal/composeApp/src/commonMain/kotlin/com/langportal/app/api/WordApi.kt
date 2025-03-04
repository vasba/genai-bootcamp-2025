package com.langportal.app.api

import com.langportal.app.model.WordDTO
import kotlinx.serialization.Serializable

@Serializable
data class WordsResponse(
    val words: List<WordDTO>,
    val totalPages: Int,
    val currentPage: Int,
    val totalWords: Long
)

expect class WordApi() {
    suspend fun getWords(page: Int = 1, sortBy: String = "romanian", order: String = "asc"): WordsResponse
}