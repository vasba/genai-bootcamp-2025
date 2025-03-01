package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.Card
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
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val screenWidth = maxWidth
        val isWideScreen = screenWidth > 600.dp
        
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    LastSessionSection(
                        lastSession, 
                        error,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    StatisticsSection(
                        statistics, 
                        error,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                }
            } else {
                // Stack cards vertically on mobile
                LastSessionSection(
                    lastSession, 
                    error,
                    modifier = Modifier.fillMaxWidth()
                )
                StatisticsSection(
                    statistics, 
                    error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LastSessionSection(
    data: StudySession?, 
    error: String?,
    modifier: Modifier = Modifier
) {
    Section("Last Session", modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            when {
                error != null -> Text(
                    "Error: $error",
                    modifier = Modifier.padding(16.dp)
                )
                data == null -> CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
                else -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
}

@Composable
private fun StatisticsSection(
    data: ReviewStatistics?, 
    error: String?,
    modifier: Modifier = Modifier
) {
    Section("Statistics", modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            when {
                error != null -> Text(
                    "Error: $error",
                    modifier = Modifier.padding(16.dp)
                )
                data == null -> CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
                else -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Total Reviews: ${data.totalReviews}")
                        Text("Correct Reviews: ${data.correctReviews}")
                        Text("Accuracy: ${formatPercent(data.accuracy)}%")
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title)
        content()
    }
}

private fun formatPercent(value: Double): String {
    return round(value * 100).toInt().toString()
}