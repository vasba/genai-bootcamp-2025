package com.langportal.app

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.langportal.app.screens.*
import com.langportal.app.viewmodel.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun App() {
    Surface {
        val isDark = isSystemInDarkTheme()
        var darkMode by remember { mutableStateOf(isDark) }
        var currentRoute by remember { mutableStateOf("/dashboard") }
        val viewModel = remember { DashboardViewModel() }
        
        // Use window width to determine layout
        var isCompactScreen by remember { mutableStateOf(true) }
        
        MaterialTheme(
            colors = if (darkMode) darkColors() else lightColors()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                if (isCompactScreen) {
                    // Vertical layout for small screens
                    Column {
                        TopAppBar(
                            title = { Text("LangPortal") },
                            actions = {
                                IconButton(onClick = { darkMode = !darkMode }) {
                                    Icon(
                                        imageVector = if (darkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                        contentDescription = if (darkMode) "Switch to light mode" else "Switch to dark mode",
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        )

                        // Content area with lazy loading
                        Box(modifier = Modifier.weight(1f)) {
                            ContentArea(
                                currentRoute = currentRoute,
                                viewModel = viewModel,
                                onRouteChange = { currentRoute = it }
                            )
                        }

                        // Bottom navigation for small screens
                        BottomNavigation(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colors.surface
                        ) {
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                                label = { Text("Dashboard") },
                                selected = currentRoute == "/dashboard",
                                onClick = { currentRoute = "/dashboard" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.School, "Study") },
                                label = { Text("Study") },
                                selected = currentRoute == "/study-activities",
                                onClick = { currentRoute = "/study-activities" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Translate, "Words") },
                                label = { Text("Words") },
                                selected = currentRoute == "/words",
                                onClick = { currentRoute = "/words" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Category, "Groups") },
                                label = { Text("Groups") },
                                selected = currentRoute == "/groups",
                                onClick = { currentRoute = "/groups" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Timeline, "Sessions") },
                                label = { Text("Sessions") },
                                selected = currentRoute == "/sessions",
                                onClick = { currentRoute = "/sessions" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Settings, "Settings") },
                                label = { Text("Settings") },
                                selected = currentRoute == "/settings",
                                onClick = { currentRoute = "/settings" },
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                            )
                        }
                    }
                } else {
                    // Horizontal layout for larger screens
                    Column {
                        TopAppBar(
                            title = { Text("LangPortal") },
                            actions = {
                                IconButton(onClick = { darkMode = !darkMode }) {
                                    Icon(
                                        imageVector = if (darkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                        contentDescription = if (darkMode) "Switch to light mode" else "Switch to dark mode",
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        )

                        AppNavigationBar(
                            currentRoute = currentRoute,
                            onRouteSelected = { currentRoute = it }
                        )

                        ContentArea(
                            currentRoute = currentRoute,
                            viewModel = viewModel,
                            onRouteChange = { currentRoute = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentArea(
    currentRoute: String,
    viewModel: DashboardViewModel,
    onRouteChange: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            currentRoute == "/dashboard" -> DashboardScreen(viewModel = viewModel)
            currentRoute == "/study-activities" -> StudyActivitiesScreen(
                onActivitySelected = { id -> onRouteChange("/study-activities/$id") }
            )
            currentRoute.startsWith("/study-activities/") -> {
                val id = currentRoute.substringAfterLast("/")
                StudyActivityDetailScreen(id)
            }
            currentRoute == "/words" -> WordsScreen()
            currentRoute == "/groups" -> GroupsScreen(
                onGroupSelected = { id -> onRouteChange("/groups/$id") }
            )
            currentRoute == "/sessions" -> SessionsScreen()
            currentRoute == "/settings" -> SettingsScreen()
            else -> {
                if (currentRoute.startsWith("/flashcards/")) {
                    val id = currentRoute.substringAfter("/flashcards/")
                    val groupId = id.toLongOrNull()
                    if (groupId != null) {
                        FlashcardScreen(
                            groupId = groupId,
                            onFinish = { onRouteChange("/groups") }
                        )
                    }
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

@Composable
private fun AppNavigationBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    val navigationButtons = remember {
        listOf(
            Triple("Dashboard", Icons.Default.Dashboard, "/dashboard"),
            Triple("Study Activities", Icons.Default.School, "/study-activities"),
            Triple("Words", Icons.Default.Translate, "/words"),
            Triple("Word Groups", Icons.Default.Category, "/groups"),
            Triple("Sessions", Icons.Default.Timeline, "/sessions"),
            Triple("Settings", Icons.Default.Settings, "/settings")
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        navigationButtons.forEach { (text, icon, route) ->
            NavButton(
                text = text,
                icon = icon,
                route = route,
                currentRoute = currentRoute,
                onRouteSelected = onRouteSelected
            )
        }
    }
}

@Composable
private fun NavButton(
    text: String,
    icon: ImageVector,
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text)
            Text(text)
        }
    }
}

@Composable
private fun CompactNavButton(
    icon: ImageVector,
    text: String, 
    route: String,
    currentRoute: String,
    onRouteSelected: (String) -> Unit
) {
    Button(
        onClick = { onRouteSelected(route) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (currentRoute == route)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.surface
        ),
        modifier = Modifier.height(56.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(icon, contentDescription = text)
            Text(text, style = MaterialTheme.typography.caption)
        }
    }
}