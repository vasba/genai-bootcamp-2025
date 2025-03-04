package com.langportal.app.viewmodel

import com.langportal.app.api.WordApi
import com.langportal.app.model.WordListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordListViewModel : BaseViewModel() {
    private val api = WordApi()
    
    private val _state = MutableStateFlow(WordListState())
    val state: StateFlow<WordListState> = _state.asStateFlow()
    
    init {
        loadWords()
    }
    
    fun loadWords(page: Int = 1) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = api.getWords(page)
                _state.value = WordListState(
                    words = response.words,
                    currentPage = response.currentPage,
                    totalPages = response.totalPages,
                    totalWords = response.totalWords,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load words"
                )
            }
        }
    }

    fun nextPage() {
        if (_state.value.currentPage < _state.value.totalPages) {
            loadWords(_state.value.currentPage + 1)
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 1) {
            loadWords(_state.value.currentPage - 1)
        }
    }
}