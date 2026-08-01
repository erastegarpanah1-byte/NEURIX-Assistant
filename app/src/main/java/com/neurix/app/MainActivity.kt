package com.neurix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixTheme
import com.neurix.core.design.NeurixSystemUi
import com.neurix.app.navigation.NeurixNavHost
import com.neurix.core.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeurixTheme {
                NeurixSystemUi()
                NeurixApp()
            }
        }
    }
}

@Composable
fun NeurixApp() {
    var showSplash by remember { mutableStateOf(true) }
    val navController = rememberNavController()
    LaunchedEffect(Unit) { delay(2000); showSplash = false }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = showSplash, enter = fadeIn(tween(400)), exit = fadeOut(tween(600))) {
            Box(modifier = Modifier.fillMaxSize().background(NeurixColors.Background), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.size(80.dp).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)), shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                        Text("N", style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("NEURIX", style = MaterialTheme.typography.headlineMedium, color = NeurixColors.OnBackground, fontWeight = FontWeight.Bold, letterSpacing = 8.sp)
                }
            }
        }
        if (!showSplash) MainScaffold(navController)
    }
}

@Composable
fun MainScaffold(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarRoutes = listOf(Screen.Home.route, Screen.Chat.route, Screen.Settings.route)
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NeurixColors.Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = NeurixColors.Surface, tonalElevation = 0.dp) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = { if (currentRoute != Screen.Home.route) navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f))
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Chat.route,
                        onClick = { if (currentRoute != Screen.Chat.route) navController.navigate(Screen.Chat.route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Filled.Chat, "Chat") },
                        label = { Text("Chat") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f))
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = { if (currentRoute != Screen.Settings.route) navController.navigate(Screen.Settings.route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true } },
                        icon = { Icon(Icons.Filled.Settings, "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f))
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            NeurixNavHost(navController)
        }
    }
}