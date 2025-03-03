package com.langportal.app.viewmodel

import com.langportal.app.api.MultipleChoiceApi
import com.langportal.app.model.MultipleChoiceState
import com.langportal.app.model.MultipleChoiceAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MultipleChoiceViewModel : BaseViewModel() {
    private val api = MultipleChoiceApi()
    
    private val _multipleChoiceState = MutableStateFlow<MultipleChoiceState?>(null)
    val multipleChoiceState: StateFlow<MultipleChoiceState?> = _multipleChoiceState.asStateFlow()
    
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
                    .onSuccess { state -> _multipleChoiceState.value = state }
                    .onFailure { error -> _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectAnswer(answer: String) {
        _multipleChoiceState.value = _multipleChoiceState.value?.copy(
            selectedAnswer = answer,
            isAnswerSubmitted = true
        )
    }

    fun submitAnswer() {
        val currentState = _multipleChoiceState.value ?: return
        val selectedAnswer = currentState.selectedAnswer ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val answer = MultipleChoiceAnswer(currentState.currentWordId, selectedAnswer)
                api.submitAnswer(currentState.sessionId, answer)
                    .onFailure { error -> _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNextWord() {
        val currentState = _multipleChoiceState.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                api.getNextWord(currentState.sessionId)
                    .onSuccess { state -> _multipleChoiceState.value = state }
                    .onFailure { error -> _error.value = error.message }
            } finally {
                _isLoading.value = false
            }
        }
    }
}