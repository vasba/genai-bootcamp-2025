package com.langportal.app.model

data class WordListState(
    val words: List<WordDTO> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalWords: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)