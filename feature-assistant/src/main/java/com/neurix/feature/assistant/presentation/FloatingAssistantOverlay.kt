package com.neurix.feature.assistant.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixDimens

@Composable
fun FloatingAssistantOverlay(
    onDismiss: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AssistantEffect.Speak -> { }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(AssistantIntent.StartListening)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = NeurixDimens.PaddingXXLarge + 32.dp,
                    start = NeurixDimens.PaddingMedium,
                    end = NeurixDimens.PaddingMedium
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NeurixDimens.PaddingMedium)
        ) {
            if (state.recognizedText.isNotEmpty()) {
                Text(
                    text = "\"${state.recognizedText}\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                )
            }

            if (state.aiResponse.isNotEmpty()) {
                Text(
                    text = state.aiResponse,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(NeurixColors.Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                )
            }

            ListeningButton(
                isListening = state.isListening,
                isThinking = state.isThinking,
                onClick = {
                    if (state.isListening) viewModel.handleIntent(AssistantIntent.StopListening)
                    else viewModel.handleIntent(AssistantIntent.StartListening)
                }
            )
        }
    }
}

@Composable
fun ListeningButton(isListening: Boolean, isThinking: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(1f, 1.15f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "ps")
    val pulseAlpha by infiniteTransition.animateFloat(0.4f, 0.1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "pa")

    Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        if (isListening || isThinking) {
            Box(
                modifier = Modifier.size(96.dp).scale(pulseScale).clip(CircleShape).background(
                    Brush.radialGradient(listOf(NeurixColors.Primary.copy(alpha = pulseAlpha), NeurixColors.Primary.copy(alpha = 0.05f), Color.Transparent))
                )
            )
        }
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(
                Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isThinking) "..." else if (isListening) "●" else "N",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}