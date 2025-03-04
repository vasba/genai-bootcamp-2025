package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.viewmodel.WordListViewModel
import com.langportal.app.model.WordListState

@Composable
fun WordListScreen() {
    val viewModel = remember { WordListViewModel() }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Words", style = MaterialTheme.typography.h5)
        
        when {
            state.isLoading -> LoadingState()
            state.error != null -> ErrorState(
                error = state.error!!,
                onRetry = { viewModel.loadWords() }
            )
            state.words.isEmpty() -> EmptyState()
            else -> WordList(state, viewModel)
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
            "No words available",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun WordList(state: WordListState, viewModel: WordListViewModel) {
    Column {
        // Table Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Source Word", style = MaterialTheme.typography.subtitle1, modifier = Modifier.weight(1f))
                Text("Target Word", style = MaterialTheme.typography.subtitle1, modifier = Modifier.weight(1f))
                Text("Word Groups", style = MaterialTheme.typography.subtitle1, modifier = Modifier.weight(1f))
            }
        }
        
        // Word List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.words) { word ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(word.sourceWord, modifier = Modifier.weight(1f))
                        Text(word.targetWord, modifier = Modifier.weight(1f))
                        Text(
                            word.groups?.joinToString(", ") { it.name } ?: "",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        // Pagination Controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.previousPage() },
                enabled = state.currentPage > 1,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Previous")
            }
            
            Text(
                "Page ${state.currentPage} of ${state.totalPages}",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.body1
            )
            
            Button(
                onClick = { viewModel.nextPage() },
                enabled = state.currentPage < state.totalPages,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Next")
            }
        }
    }
}