package org.pawlowski.karoo_cgm.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.pawlowski.karoo_cgm.ui.screens.main.MainScreen
import org.pawlowski.karoo_cgm.ui.screens.settings.SettingsScreen
import org.pawlowski.karoo_cgm.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Settings : Screen("settings")
    object Main : Screen("main")
}

@Composable
fun NavGraph(
    userPreferencesRepository: UserPreferencesRepository
) {
    val navController = rememberNavController()
    val startDestination = runBlocking {
        val prefs = userPreferencesRepository.userPreferencesFlow.firstOrNull()
        if (prefs?.authToken != null && prefs.patientId != null) {
            Screen.Main.route
        } else {
            Screen.Settings.route
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
    }
}
