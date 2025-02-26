package com.langportal.app.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.langportal.app.api.DashboardApi
import com.langportal.app.model.DashboardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel {
    private val api = DashboardApi()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _dashboardState: MutableState<DashboardState> = mutableStateOf(DashboardState.Loading)
    val dashboardState: State<DashboardState> = _dashboardState

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        _dashboardState.value = DashboardState.Loading
        scope.launch {
            api.getDashboardData()
                .onSuccess { data -> 
                    _dashboardState.value = DashboardState.Success(data)
                }
                .onFailure { error ->
                    _dashboardState.value = DashboardState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}