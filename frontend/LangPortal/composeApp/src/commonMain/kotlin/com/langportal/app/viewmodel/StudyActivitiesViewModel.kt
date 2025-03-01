package com.langportal.app.viewmodel

import com.langportal.app.api.StudyActivitiesApi
import com.langportal.app.api.NoActivitiesException
import com.langportal.app.api.ActivityNotFoundException
import com.langportal.app.model.StudyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyActivitiesViewModel : BaseViewModel() {
    private val api = StudyActivitiesApi()
    
    private val _activities = MutableStateFlow<List<StudyActivity>>(emptyList())
    val activities: StateFlow<List<StudyActivity>> = _activities.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                api.getAllActivities()
                    .onSuccess { activities -> 
                        _activities.value = activities
                        if (activities.isEmpty()) {
                            _error.value = "No study activities are available yet"
                        } else {
                            _error.value = null
                        }
                    }
                    .onFailure { error -> 
                        when (error) {
                            is NoActivitiesException -> {
                                _activities.value = emptyList()
                                _error.value = error.message
                            }
                            else -> _error.value = "Failed to load activities: ${error.message}"
                        }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoading() {
        loadActivities()
    }
}