package com.langportal.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    Surface {
        val isDark = isSystemInDarkTheme()
        var darkMode by remember { mutableStateOf(isDark) }
        var currentRoute by remember { mutableStateOf("/dashboard") }
        
        MaterialTheme(
            colors = if (darkMode) darkColors() else lightColors()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column {
                    // Top navigation bar
                    TopAppBar(
                        title = { Text("LangPortal") },
                        actions = {
                            // Dark mode toggle
                            IconButton(onClick = { darkMode = !darkMode }) {
                                Icon(
                                    imageVector = if (darkMode) Icons.Default.Add else Icons.Default.Close,
                                    contentDescription = if (darkMode) "Switch to light mode" else "Switch to dark mode"
                                )
                            }
                        }
                    )

                    // Navigation bar
                    AppNavigationBar(
                        currentRoute = currentRoute,
                        onRouteSelected = { currentRoute = it }
                    )

                    // Content area
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentRoute) {
                            "/dashboard" -> DashboardScreen()
                            "/study-activities" -> StudyActivitiesScreen()
                            "/words" -> WordsScreen()
                            "/groups" -> GroupsScreen()
                            "/sessions" -> SessionsScreen()
                            "/settings" -> SettingsScreen()
                            else -> {
                                if (currentRoute.startsWith("/study-activities/")) {
                                    val id = currentRoute.substringAfterLast("/")
                                    StudyActivityDetailScreen(id)
                                } else if (currentRoute.startsWith("/words/")) {
                                    val id = currentRoute.substringAfterLast("/")
                                    WordDetailScreen(id)
                                } else if (currentRoute.startsWith("/groups/")) {
                                    val id = currentRoute.substringAfterLast("/")
                                    GroupDetailScreen(id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavigationBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavButton("Dashboard", "/dashboard", currentRoute, onRouteSelected)
        NavButton("Study Activities", "/study-activities", currentRoute, onRouteSelected)
        NavButton("Words", "/words", currentRoute, onRouteSelected)
        NavButton("Word Groups", "/groups", currentRoute, onRouteSelected)
        NavButton("Sessions", "/sessions", currentRoute, onRouteSelected)
        NavButton("Settings", "/settings", currentRoute, onRouteSelected)
    }
}

@Composable
private fun NavButton(
    text: String,
    route: String,
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    TextButton(
        onClick = { onRouteSelected(route) },
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (currentRoute == route)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        )
    ) {
        Text(text)
    }
}