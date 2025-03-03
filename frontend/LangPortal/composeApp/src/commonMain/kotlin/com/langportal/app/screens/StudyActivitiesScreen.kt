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
                onClick = { onActivitySelected(activity.url.removePrefix("/study/")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Open Activity")
                    Text("Open Activity")
                }
            }
        }
    }
}

@Composable
fun StudyActivityDetailScreen(activityId: String) {
    var selectedGroupId by remember { mutableStateOf<Long?>(null) }

    when (activityId) {
        "flashcards" -> {
            if (selectedGroupId != null) {
                FlashcardScreen(
                    groupId = selectedGroupId!!,
                    onFinish = { selectedGroupId = null }
                )
            } else {
                GroupsScreen(
                    onGroupSelected = { groupRoute -> 
                        val groupId = groupRoute.substringAfter("flashcards/").toLongOrNull()
                        if (groupId != null) {
                            selectedGroupId = groupId
                        }
                    }
                )
            }
        }
        "multiple-choice" -> {
            if (selectedGroupId != null) {
                MultipleChoiceScreen(
                    groupId = selectedGroupId!!,
                    onFinish = { selectedGroupId = null }
                )
            } else {
                GroupsScreen(
                    onGroupSelected = { groupRoute -> 
                        val groupId = groupRoute.substringAfter("flashcards/").toLongOrNull()
                        if (groupId != null) {
                            selectedGroupId = groupId
                        }
                    }
                )
            }
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Unknown activity type")
            }
        }
    }
}