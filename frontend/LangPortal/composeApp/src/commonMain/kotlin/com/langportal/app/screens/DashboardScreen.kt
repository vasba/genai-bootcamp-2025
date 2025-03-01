package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.viewmodel.DashboardViewModel
import com.langportal.app.model.StudySession
import com.langportal.app.model.ReviewStatistics
import kotlin.math.round

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val lastSession by viewModel.lastSession.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LastSessionSection(lastSession, error)
        StatisticsSection(statistics, error)
    }
}

@Composable
private fun LastSessionSection(data: StudySession?, error: String?) {
    Section("Last Session") {
        when {
            error != null -> Text("Error: $error")
            data == null -> CircularProgressIndicator()
            else -> {
                Column {
                    Text("Activity: ${data.activityName}")
                    Text("Group: ${data.groupName}")
                    Text("Start Time: ${data.startTime}")
                    if (data.endTime != null) {
                        Text("End Time: ${data.endTime}")
                    }
                    Text("Reviews: ${data.reviewItemsCount}")
                }
            }
        }
    }
}

@Composable
private fun StatisticsSection(data: ReviewStatistics?, error: String?) {
    Section("Statistics") {
        when {
            error != null -> Text("Error: $error")
            data == null -> CircularProgressIndicator()
            else -> {
                Column {
                    Text("Total Reviews: ${data.totalReviews}")
                    Text("Correct Reviews: ${data.correctReviews}")
                    Text("Accuracy: ${formatPercent(data.accuracy)}%")
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title)
        content()
    }
}

private fun formatPercent(value: Double): String {
    return round(value * 100).toInt().toString()
}