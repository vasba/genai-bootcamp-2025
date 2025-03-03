package com.langportal.app.viewmodel

import com.langportal.app.api.KtorHttpClient
import com.langportal.app.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*

class GroupsViewModel : BaseViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = KtorHttpClient.client.get("${KtorHttpClient.BASE_URL}/groups")
                _groups.value = response.body()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoading() {
        loadGroups()
    }
}