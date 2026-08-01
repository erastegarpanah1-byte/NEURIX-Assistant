package com.neurix.feature.home.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixDimens
import com.neurix.core.ui.composables.FadeInView

@Composable
fun HomeScreen(onNavigateToChat: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.effect.collect { effect -> when (effect) { HomeEffect.NavigateToChat -> onNavigateToChat() } } }

    val t = rememberInfiniteTransition(label = "glow")
    val gs by t.animateFloat(1f, 1.08f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), "gs")
    val ga by t.animateFloat(0.3f, 0.6f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), "ga")

    Box(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        Column(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(NeurixColors.Background, NeurixColors.Background, NeurixColors.Surface))),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            FadeInView { Text(state.greeting, style = MaterialTheme.typography.displayMedium, color = NeurixColors.OnBackground, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(NeurixDimens.PaddingSmall))
            FadeInView { Text("What can I do for you today?", style = MaterialTheme.typography.titleMedium, color = NeurixColors.OnSurfaceMuted) }
            Spacer(Modifier.height(NeurixDimens.PaddingXXLarge))

            Box(Modifier.size(NeurixDimens.GlowRingSize), contentAlignment = Alignment.Center) {
                Box(Modifier.size(NeurixDimens.GlowRingSize).scale(gs).clip(CircleShape).background(Brush.radialGradient(listOf(NeurixColors.Primary.copy(alpha = ga), NeurixColors.Primary.copy(alpha = 0.1f), Color.Transparent))))
                Box(Modifier.size(NeurixDimens.GlowRingSize * 0.7f).scale(gs * 1.1f).clip(CircleShape).background(Brush.radialGradient(listOf(NeurixColors.Accent.copy(alpha = ga * 0.7f), NeurixColors.Accent.copy(alpha = 0.05f), Color.Transparent))))
                IconButton(
                    onClick = { viewModel.handleIntent(HomeIntent.TapMicrophone) },
                    modifier = Modifier.size(NeurixDimens.MicButtonSize).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)))
                ) {
                    Icon(Icons.Filled.Mic, "Tap to speak", tint = Color.White, modifier = Modifier.size(NeurixDimens.IconSizeLarge))
                }
            }
        }
    }
}