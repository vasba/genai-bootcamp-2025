package com.langportal.app.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WordsScreen() {
    WordListScreen()
}

@Composable
fun WordDetailScreen(id: String) {
    // Word detail implementation will be added later
    WordsScreen()
}