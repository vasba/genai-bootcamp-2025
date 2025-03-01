package com.langportal.app.viewmodel

import com.langportal.app.api.DashboardApi
import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : BaseViewModel() {
    private val api = DashboardApi()
    
    private val _lastSession = MutableStateFlow<StudySession?>(null)
    val lastSession: StateFlow<StudySession?> = _lastSession.asStateFlow()
    
    private val _statistics = MutableStateFlow<ReviewStatistics?>(null)
    val statistics: StateFlow<ReviewStatistics?> = _statistics.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                api.getLastSession()
                    .onSuccess { _lastSession.value = it }
                    .onFailure { _error.value = it.message }
                
                api.getWordStatistics()
                    .onSuccess { _statistics.value = it }
                    .onFailure { _error.value = it.message }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}