package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.langportal.app.model.FlashcardState
import com.langportal.app.viewmodel.FlashcardViewModel

@Composable
fun FlashcardScreen(
    groupId: Long,
    onFinish: () -> Unit = {}
) {
    val viewModel = remember { FlashcardViewModel() }
    val flashcardState by viewModel.flashcardState.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.startSession(groupId)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            error != null -> ErrorState(
                error = error!!,
                onRetry = { viewModel.startSession(groupId) },
                onBack = onFinish
            )
            isLoading -> LoadingState()
            flashcardState != null -> FlashcardContent(
                state = flashcardState!!,
                onShowAnswer = { viewModel.showAnswer() },
                onCorrect = { 
                    viewModel.submitAnswer(true)
                    viewModel.getNextWord()
                },
                onIncorrect = {
                    viewModel.submitAnswer(false)
                    viewModel.getNextWord()
                },
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun LoadingState() {
    CircularProgressIndicator()
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (error.contains("No more words") || error.contains("Empty group") || error.contains("no words")) {
                "This group has no words available for practice"
            } else {
                error
            },
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Groups")
                    Text("Back to Groups")
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    state: FlashcardState,
    onShowAnswer: () -> Unit,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress
        LinearProgressIndicator(
            progress = state.completedWords.toFloat() / state.totalWords,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "${state.completedWords}/${state.totalWords}",
            style = MaterialTheme.typography.subtitle1
        )

        // Score
        Text(
            text = "Score: ${state.correctAnswers}/${state.completedWords}",
            style = MaterialTheme.typography.h6
        )

        // Flashcard
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.sourceWord,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center
                )
                
                if (state.isAnswerVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.targetWord,
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Action buttons
        if (!state.isAnswerVisible) {
            Button(
                onClick = onShowAnswer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Answer")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onIncorrect,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Incorrect")
                }
                Button(
                    onClick = onCorrect,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Correct")
                }
            }
        }

        if (state.completedWords == state.totalWords) {
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finish")
            }
        }
    }
}