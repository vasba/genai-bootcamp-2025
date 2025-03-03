package com.langportal.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.langportal.app.model.MultipleChoiceState
import com.langportal.app.viewmodel.MultipleChoiceViewModel

@Composable
fun MultipleChoiceScreen(
    groupId: Long,
    onFinish: () -> Unit = {}
) {
    val viewModel = remember { MultipleChoiceViewModel() }
    val multipleChoiceState by viewModel.multipleChoiceState.collectAsState()
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
            multipleChoiceState != null -> MultipleChoiceContent(
                state = multipleChoiceState!!,
                onSelectAnswer = { answer -> 
                    viewModel.selectAnswer(answer)
                },
                onNextWord = {
                    viewModel.submitAnswer()
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
            text = if (error.contains("No more words") || error.contains("Empty group")) {
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Groups")
                    Text("Back to Groups")
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceContent(
    state: MultipleChoiceState,
    onSelectAnswer: (String) -> Unit,
    onNextWord: () -> Unit,
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

        // Question
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Translate:",
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = state.sourceWord,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            println("Options: ${state.options}")
            state.options.forEach { option ->
                val isSelected = option == state.selectedAnswer
                val isCorrect = state.isAnswerSubmitted && option == state.correctAnswer
                val isWrong = state.isAnswerSubmitted && isSelected && !isCorrect

                Button(
                    onClick = { if (!state.isAnswerSubmitted) onSelectAnswer(option) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = when {
                            isCorrect -> MaterialTheme.colors.primary
                            isWrong -> MaterialTheme.colors.error
                            isSelected -> MaterialTheme.colors.secondary
                            else -> MaterialTheme.colors.surface
                        }
                    )
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Next or Finish button
        if (state.isAnswerSubmitted) {
            if (state.completedWords == state.totalWords) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finish")
                }
            } else {
                Button(
                    onClick = onNextWord,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Word")
                }
            }
        }
    }
}