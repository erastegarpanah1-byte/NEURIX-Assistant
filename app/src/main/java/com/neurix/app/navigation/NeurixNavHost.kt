package com.neurix.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neurix.core.navigation.Screen
import com.neurix.feature.chat.presentation.ChatScreen
import com.neurix.feature.home.presentation.HomeScreen
import com.neurix.feature.settings.presentation.*

@Composable
fun NeurixNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(Screen.Home.route) { HomeScreen(onNavigateToChat = { navController.navigate(Screen.Chat.route) }) }
        composable(Screen.Chat.route) { ChatScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onNavigateToDetail = { navController.navigate(it.route) }) }
        composable(Screen.Theme.route) { ThemeScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Language.route) { LanguageScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Voice.route) { VoiceScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Memory.route) { MemoryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Permissions.route) { PermissionsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.About.route) { AboutScreen(onBack = { navController.popBackStack() }) }
    }
}
