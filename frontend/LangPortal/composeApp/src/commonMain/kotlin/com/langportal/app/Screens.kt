package com.langportal.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.langportal.app.model.DashboardData
import com.langportal.app.model.SessionSummary
import com.langportal.app.viewmodel.DashboardState
import com.langportal.app.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen() {
    val viewModel = remember { DashboardViewModel() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with refresh button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dashboard",
                style = MaterialTheme.typography.h4
            )
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, "Refresh dashboard")
            }
        }

        when (val state = viewModel.dashboardState.value) {
            is DashboardState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is DashboardState.Success -> {
                DashboardContent(state.data)
            }
        }
    }
}

@Composable
private fun DashboardContent(data: DashboardData) {
    // Progress Summary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Your Progress",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProgressionStats(data)
        }
    }
}

@Composable
private fun ProgressionStats(data: DashboardData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatRow("Total Reviews", data.totalReviews, data.totalReviews)
        StatRow("Correct Reviews", data.correctReviews, data.totalReviews)
        StatRow("Accuracy", (data.accuracy * 100).toInt(), 100)
    }
}

@Composable
private fun StatRow(label: String, value: Int, total: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text("$value/$total")
        }
        LinearProgressIndicator(
            progress = if (total > 0) value.toFloat() / total else 0f,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun StudyActivitiesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Study Activities")
    }
}

@Composable
fun StudyActivityDetailScreen(id: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Study Activity Detail: $id")
    }
}

@Composable
fun WordsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Words")
    }
}

@Composable
fun WordDetailScreen(id: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Word Detail: $id")
    }
}

@Composable
fun GroupsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Groups")
    }
}

@Composable
fun GroupDetailScreen(id: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Group Detail: $id")
    }
}

@Composable
fun SessionsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sessions")
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings")
    }
}