package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.model.Group
import com.langportal.app.viewmodel.GroupsViewModel
import com.langportal.app.viewmodel.GroupDetailViewModel

@Composable
fun GroupsScreen(
    onGroupSelected: (String) -> Unit = {},
    fromActivity: Boolean = false,
    activityName: String = "" // Added parameter for activity name
) {
    val viewModel = remember { GroupsViewModel() }
    val groups by viewModel.groups.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Word Groups", style = MaterialTheme.typography.h5)
        
        when {
            isLoading -> LoadingState()
            error != null -> ErrorState(error = error!!, onRetry = { viewModel.retryLoading() })
            groups.isEmpty() -> EmptyState()
            else -> GroupTable(groups, onGroupSelected, fromActivity, activityName)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No word groups available",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun GroupTable(
    groups: List<Group>,
    onSelectGroup: (String) -> Unit,
    fromActivity: Boolean,
    activityName: String
) {
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 10
    val pageCount = (groups.size + pageSize - 1) / pageSize
    val currentPageGroups = groups.drop(currentPage * pageSize).take(pageSize)

    Column(modifier = Modifier.fillMaxSize()) {
        // Table header
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = "Group Name", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
            Text(text = "Words Count", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
            if (fromActivity) {
                Text(text = "Action", modifier = Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
            }
        }
        Divider()
        LazyColumn {
            items(currentPageGroups) { group ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = group.name, modifier = Modifier.weight(1f))
                    Text(text = "${group.words.size}", modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.weight(1f)) {
                        if (fromActivity) {
                            Button(
                                onClick = { onSelectGroup("flashcards/${group.id}") },
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Activity")
                                    Text("Start ${activityName.replaceFirstChar { it.uppercase() }}")
                                }
                            }
                        } else {
                            Button(
                                onClick = { onSelectGroup("groups/${group.id}") },
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "View Group Details")
                                    Text("View Details")
                                }
                            }
                        }
                    }
                }
                Divider()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { if(currentPage > 0) currentPage-- }, enabled = currentPage > 0) {
                Text("Previous")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Page ${currentPage + 1} of $pageCount")
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { if(currentPage < pageCount - 1) currentPage++ }, enabled = currentPage < pageCount - 1) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: Group,
    onGroupSelected: (String) -> Unit
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
                text = group.name,
                style = MaterialTheme.typography.h6
            )
            group.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2
                )
            }
            Text(
                text = "${group.words.size} words",
                style = MaterialTheme.typography.caption
            )
            
            Button(
                onClick = { onGroupSelected("flashcards/${group.id}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Category, contentDescription = "Select Group")
                    Text("Select Group")
                }
            }
        }
    }
}

@Composable
fun GroupDetailScreen(id: String) {
    val viewModel = remember { GroupDetailViewModel() }
    val group by viewModel.group.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadGroup(id)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            isLoading -> LoadingState()
            error != null -> ErrorState(error = error!!, onRetry = { viewModel.retryLoading(id) })
            group == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Group not found", style = MaterialTheme.typography.h6)
            }
            else -> {
                // Group header
                Text(group!!.name, style = MaterialTheme.typography.h5)
                group!!.description?.let {
                    Text(it, style = MaterialTheme.typography.body1)
                }
                Divider()
                
                // Words list header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Words in Group", style = MaterialTheme.typography.h6)
                    Text("${group!!.words.size} words", style = MaterialTheme.typography.subtitle1)
                }
                
                // Words list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(group!!.words) { word ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        word.sourceWord,
                                        style = MaterialTheme.typography.subtitle1
                                    )
                                    Text(
                                        word.targetWord,
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        "✓ ${word.correctReviews}",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Text(
                                        "✗ ${word.incorrectReviews}",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}