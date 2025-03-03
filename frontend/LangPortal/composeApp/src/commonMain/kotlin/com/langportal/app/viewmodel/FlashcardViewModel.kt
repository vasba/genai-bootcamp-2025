package com.langportal.app.viewmodel

import com.langportal.app.api.FlashcardApi 
import com.langportal.app.model.FlashcardState
import com.langportal.app.model.FlashcardAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel : BaseViewModel() {
    private val api = FlashcardApi()
    
    private val _flashcardState = MutableStateFlow<FlashcardState?>(null)
    val flashcardState: StateFlow<FlashcardState?> = _flashcardState.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun startSession(groupId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                api.startSession(groupId)
                    .onSuccess { state -> _flashcardState.value = state }
                    .onFailure { error -> _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showAnswer() {
        _flashcardState.value = _flashcardState.value?.copy(isAnswerVisible = true)
    }

    fun submitAnswer(correct: Boolean) {
        val currentState = _flashcardState.value ?: return
        println("current state in submitAnswer: $currentState")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val answer = FlashcardAnswer(currentState.currentWordId, correct)
                api.submitAnswer(currentState.sessionId, answer)
                    .onFailure { error -> _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNextWord() {
        val currentState = _flashcardState.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                api.getNextWord(currentState.sessionId)
                    .onSuccess { state -> println(state)
                        _flashcardState.value = state }
                    .onFailure { error -> println("getNextWord error ${error.message}")
                        _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }
}