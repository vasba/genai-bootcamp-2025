package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.model.StudySession
import com.langportal.app.api.DashboardApi

@Composable
fun SessionsScreen() {
    var sessions by remember { mutableStateOf<List<StudySession>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val api = remember { DashboardApi() }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            api.getSessions()
                .onSuccess { 
                    sessions = it
                    error = null
                }
                .onFailure { error = it.message }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Study Sessions", style = MaterialTheme.typography.h5)
        
        when {
            error != null -> Text("Error: $error", color = MaterialTheme.colors.error)
            isLoading -> CircularProgressIndicator()
            sessions.isEmpty() -> Text("No sessions found")
            else -> SessionsList(sessions)
        }
    }
}

@Composable
private fun SessionsList(sessions: List<StudySession>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sessions) { session ->
            SessionCard(session)
        }
    }
}

@Composable
private fun SessionCard(session: StudySession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Activity: ${session.activityName}")
            Text("Group: ${session.groupName}")
            Text("Start Time: ${session.startTime}")
            if (session.endTime != null) {
                Text("End Time: ${session.endTime}")
            }
            Text("Reviews: ${session.reviewItemsCount}")
        }
    }
}