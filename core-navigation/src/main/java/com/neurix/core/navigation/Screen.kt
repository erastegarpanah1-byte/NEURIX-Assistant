package com.neurix.core.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object Settings : Screen("settings")
    data object Theme : Screen("settings/theme")
    data object Language : Screen("settings/language")
    data object Voice : Screen("settings/voice")
    data object Memory : Screen("settings/memory")
    data object Permissions : Screen("settings/permissions")
    data object About : Screen("settings/about")
}