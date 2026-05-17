package com.pacetrace.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pacetrace.app.api.AppContext
import com.pacetrace.app.ui.login.LoginScreen
import com.pacetrace.app.ui.home.HomeScreen
import com.pacetrace.app.ui.run.RunScreen
import com.pacetrace.app.ui.club.ClubScreen
import com.pacetrace.app.ui.profile.ProfileScreen
import com.pacetrace.app.api.AuthApi

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "首页")
    data object Run : Screen("run", "跑步")
    data object Club : Screen("club", "俱乐部")
    data object Profile : Screen("profile", "我的")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var isLoggedIn by remember { mutableStateOf(AppContext.user.studentId != 0) }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
        return
    }

    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val screens = listOf(Screen.Home, Screen.Run, Screen.Club, Screen.Profile)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedScreen.label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        AuthApi.logout()
                        isLoggedIn = false
                        selectedScreen = Screen.Home
                    }) {
                        Icon(Icons.Default.ExitToApp, "退出", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen },
                        icon = {
                            Icon(
                                when (screen) {
                                    Screen.Home -> Icons.Default.Home
                                    Screen.Run -> Icons.Default.DirectionsRun
                                    Screen.Club -> Icons.Default.Groups
                                    Screen.Profile -> Icons.Default.Person
                                },
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedScreen) {
                Screen.Home -> HomeScreen()
                Screen.Run -> RunScreen()
                Screen.Club -> ClubScreen()
                Screen.Profile -> ProfileScreen()
            }
        }
    }
}
