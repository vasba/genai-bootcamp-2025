package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.consoleLog
import com.langportal.app.model.StudyActivity
import com.langportal.app.viewmodel.StudyActivitiesViewModel

@Composable
fun StudyActivitiesScreen(
    onActivitySelected: (String) -> Unit = {},
) {
    val viewModel = remember { StudyActivitiesViewModel() }
    val activities by viewModel.activities.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Study Activities", style = MaterialTheme.typography.h5)
        
        when {
            isLoading -> LoadingState()
            error != null -> ErrorState(
                error = error ?: "Unknown error",
                onRetry = { viewModel.retryLoading() }
            )
            activities.isEmpty() -> EmptyState()
            else -> ActivityGrid(activities, onActivitySelected)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.body1
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry")
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No study activities are available yet",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ActivityGrid(
    activities: List<StudyActivity>,
    onActivitySelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(activities) { activity ->
            ActivityCard(activity, onActivitySelected)
        }
    }
}

@Composable
private fun ActivityCard(
    activity: StudyActivity,
    onActivitySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = activity.name,
                style = MaterialTheme.typography.h6
            )
            
            Button(
                onClick = { println("onClick ${activity}")
                    onActivitySelected(activity.url) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Open Activity")
                    Text("Start Activity")
                }
            }
        }
    }
}

@Composable
fun StudyActivityDetailScreen(activityId: String) {
    val parts = activityId.split("/")
    consoleLog("Unknown activity type with id: $activityId") // Log!

    if (parts.size == 1 && parts[0] == "flashcards") {
        FlashcardScreen(
            groupId = 1,
            onFinish = { /* Handle finish */ }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Unknown activity type")
        }
    }
}