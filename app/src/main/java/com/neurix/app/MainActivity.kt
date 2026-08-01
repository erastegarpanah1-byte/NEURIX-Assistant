package com.neurix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.sin
import kotlin.random.Random

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
    LaunchedEffect(Unit) { delay(3000); showSplash = false }
    Box(Modifier.fillMaxSize()) {
        if (!showSplash) MainScaffold(navController = navController)
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(tween(600)),
            exit = fadeOut(tween(800))
        ) { SplashScreen() }
    }
}

@Composable
fun SplashScreen() {
    val tt = rememberInfiniteTransition(label = "splash")
    val logoAlpha by tt.animateFloat(0f, 1f, tween(1200, easing = FastOutSlowInEasing), "logo")
    val logoScale by tt.animateFloat(0.7f, 1f, tween(1200, easing = FastOutSlowInEasing), "logoScale")

    val particles = remember {
        List(35) {
            ParticleData(
                x = Random.nextFloat(), y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1.5f,
                speedX = (Random.nextFloat() - 0.5f) * 0.003f,
                speedY = (Random.nextFloat() - 0.5f) * 0.003f,
                alpha = Random.nextFloat() * 0.5f + 0.2f,
                color = if (Random.nextBoolean()) Color(0xFF00D4FF) else Color(0xFF3B82F6),
                delay = Random.nextInt(0, 2000),
                pulseSpeed = Random.nextInt(2000, 4000)
            )
        }
    }
    val pa = rememberInfiniteTransition(label = "particles")
    val pt by pa.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing)), "pt")

    Box(Modifier.fillMaxSize().background(NeurixColors.Background), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().alpha(logoAlpha)) {
            val w = size.width; val h = size.height
            particles.forEach { p ->
                val ct = ((pt * 3000 + p.delay) % 3000) / 3000f
                val px = (p.x + p.speedX * ct * 1000f) % 1f
                val py = (p.y - p.speedY * ct * 1000f) % 1f
                val pulse = 0.5f + 0.5f * sin(ct * Math.PI.toFloat() * 2f * (3000f / p.pulseSpeed))
                drawCircle(p.color.copy(alpha = p.alpha * pulse), p.radius * 3f, Offset(px * w, py * h))
            }
            val cx = w / 2f; val cy = h / 2f
            val gr = 120f + 15f * sin(pt * Math.PI.toFloat() * 2f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3B82F6).copy(alpha = 0.08f), Color.Transparent), Offset(cx, cy), gr * 4), gr * 4, Offset(cx, cy))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF00D4FF).copy(alpha = 0.05f), Color.Transparent), Offset(cx, cy), gr * 3), gr * 3, Offset(cx, cy))
        }
        Column(Modifier.alpha(logoAlpha).scale(logoScale), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(90.dp).background(Brush.linearGradient(listOf(Color(0xFF00D4FF), Color(0xFF3B82F6), Color(0xFF7C3AED))), RoundedCornerShape(26.dp)), contentAlignment = Alignment.Center) {
                Text("N", style = MaterialTheme.typography.displayMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(28.dp))
            Text("NEURIX", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, letterSpacing = 12.sp, brush = Brush.linearGradient(listOf(Color(0xFF00D4FF), Color(0xFF3B82F6))))
            Spacer(Modifier.height(10.dp))
            Text("AI ASSISTANT", style = MaterialTheme.typography.labelLarge, color = NeurixColors.OnSurfaceMuted.copy(alpha = 0.6f), letterSpacing = 6.sp, fontWeight = FontWeight.Light)
        }
    }
}

private data class ParticleData(var x: Float, var y: Float, val radius: Float, val speedX: Float, val speedY: Float, val alpha: Float, val color: Color, val delay: Int, val pulseSpeed: Int)

@Composable
fun MainScaffold(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarRoutes = listOf(Screen.Home.route, Screen.Chat.route, Screen.Settings.route)
    val showBottomBar = currentRoute in bottomBarRoutes
    Scaffold(Modifier.fillMaxSize(), containerColor = NeurixColors.Background, bottomBar = {
        if (showBottomBar) NavigationBar(containerColor = NeurixColors.Surface, tonalElevation = 0.dp) {
            NavigationBarItem(currentRoute == Screen.Home.route, { if (currentRoute != Screen.Home.route) navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, icon = { Icon(Icons.Filled.Home, "Home") }, label = { Text("Home") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f)))
            NavigationBarItem(currentRoute == Screen.Chat.route, { if (currentRoute != Screen.Chat.route) navController.navigate(Screen.Chat.route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true } }, icon = { Icon(Icons.Filled.Chat, "Chat") }, label = { Text("Chat") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f)))
            NavigationBarItem(currentRoute == Screen.Settings.route, { if (currentRoute != Screen.Settings.route) navController.navigate(Screen.Settings.route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true } }, icon = { Icon(Icons.Filled.Settings, "Settings") }, label = { Text("Settings") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = NeurixColors.Primary, selectedTextColor = NeurixColors.Primary, unselectedIconColor = NeurixColors.OnSurfaceMuted, unselectedTextColor = NeurixColors.OnSurfaceMuted, indicatorColor = NeurixColors.Primary.copy(alpha = 0.12f)))
        }
    }) { innerPadding -> Box(Modifier.fillMaxSize().padding(innerPadding)) { NeurixNavHost(navController = navController) } }
}
