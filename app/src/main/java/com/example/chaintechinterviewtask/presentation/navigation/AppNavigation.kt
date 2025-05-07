package com.example.chaintechinterviewtask.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chaintechinterviewtask.data.repository.PasswordRepository
import com.example.chaintechinterviewtask.presentation.screen.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
}

@Composable
fun AppNavigation(
    passwordRepository: PasswordRepository
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                passwordRepository = passwordRepository,
                )
        }

    }
}