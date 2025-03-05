package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.langportal.app.model.StudySession
import com.langportal.app.model.Page
import com.langportal.app.api.DashboardApi

@Composable
fun SessionsScreen() {
    var page by remember { mutableStateOf<Page<StudySession>?>(null) }
    var currentPage by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val api = remember { DashboardApi() }

    LaunchedEffect(currentPage) {
        try {
            isLoading = true
            api.getSessions(currentPage)
                .onSuccess { 
                    page = it
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
            page == null || page?.content?.isEmpty() == true -> Text("No sessions found")
            else -> {
                // Table header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("ID", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.subtitle2)
                        Text("Activity", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                        Text("Group", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                        Text("Start Time", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                        Text("End Time", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                        Text("Reviews", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.subtitle2)
                    }
                }

                SessionsList(page!!.content)
                
                // Pagination controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if(!page!!.first) currentPage-- },
                        enabled = !page!!.first
                    ) {
                        Text("Previous")
                    }
                    Text(
                        "Page ${currentPage + 1} of ${page!!.totalPages}",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Button(
                        onClick = { if(!page!!.last) currentPage++ },
                        enabled = !page!!.last
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionsList(sessions: List<StudySession>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(sessions) { session ->
            SessionRow(session)
        }
    }
}

@Composable
private fun SessionRow(session: StudySession) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${session.id}",
                modifier = Modifier.weight(0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = session.activityName,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = session.groupName,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = session.startTime,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = session.endTime ?: "-",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = session.reviewItemsCount.toString(),
                modifier = Modifier.weight(0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}