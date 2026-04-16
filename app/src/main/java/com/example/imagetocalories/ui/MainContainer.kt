package com.example.imagetocalories.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.imagetocalories.ui.screens.*
import com.example.imagetocalories.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object AddMeal : Screen("add_meal", "Add Meal", Icons.Default.CameraAlt)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)

    Scaffold(
        bottomBar = {
            if (currentUser != null) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val items = listOf(Screen.AddMeal, Screen.Dashboard, Screen.Profile)

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.route == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (currentUser == null) "welcome" else Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("welcome") {
                if (currentUser != null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                } else {
                    WelcomeScreen(
                        onLoginClick = { navController.navigate("auth_login") },
                        onSignUpClick = { navController.navigate("auth_signup") }
                    )
                }
            }
            composable("auth_login") {
                AuthScreen(
                    initialIsLogin = true,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
            composable("auth_signup") {
                AuthScreen(
                    initialIsLogin = false,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.AddMeal.route) {
                if (currentUser == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("welcome") {
                            popUpTo(0)
                        }
                    }
                } else {
                    CameraScreen()
                }
            }
            composable(Screen.Dashboard.route) {
                if (currentUser == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("welcome") {
                            popUpTo(0)
                        }
                    }
                } else {
                    DashboardScreen()
                }
            }
            composable(Screen.Profile.route) {
                if (currentUser == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    ProfileScreen(
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}