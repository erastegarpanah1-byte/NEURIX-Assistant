package com.neurix.feature.assistant.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neurix.core.design.NeurixColors

@Composable
fun FloatingAssistantOverlay(onDismiss: () -> Unit, viewModel: AssistantViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.effect.collect { } }
    LaunchedEffect(Unit) { viewModel.handleIntent(AssistantIntent.StartListening) }

    val pt = rememberInfiniteTransition(label = "p")
    val ps by pt.animateFloat(0.95f, 1.05f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), "ps")
    val ra by pt.animateFloat(0.3f, 0.1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), "ra")

    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)), contentAlignment = Alignment.BottomCenter) {
        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f))) {
            Icon(Icons.Filled.Close, "Close", tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.fillMaxWidth().padding(bottom = 120.dp, start = 32.dp, end = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.recognizedText.isNotEmpty()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))) {
                    Text("\"${state.recognizedText}\"", Modifier.padding(20.dp), style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = TextAlign.Center, lineHeight = 28.sp)
                }
                Spacer(Modifier.height(32.dp))
            }
            if (state.aiResponse.isNotEmpty()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f))) {
                    Text(state.aiResponse, Modifier.padding(20.dp), style = MaterialTheme.typography.bodyLarge, color = Color.White, textAlign = TextAlign.Center, lineHeight = 28.sp)
                }
                Spacer(Modifier.height(32.dp))
            }
            if (state.isThinking) {
                TypingDots(NeurixColors.Primary)
                Spacer(Modifier.height(16.dp))
                Text("Thinking...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(24.dp))
            }
            Box(Modifier.size(if (state.isListening) 160.dp else 120.dp).then(if (state.isListening) Modifier.scale(ps) else Modifier), contentAlignment = Alignment.Center) {
                if (state.isListening) Box(Modifier.size(160.dp).clip(CircleShape).background(Brush.radialGradient(listOf(NeurixColors.Primary.copy(alpha = ra), NeurixColors.Primary.copy(alpha = 0f)))))
                Box(Modifier.size(90.dp).clip(CircleShape).background(brush = if (state.isListening) Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626))) else Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Mic, "Mic", tint = Color.White, modifier = Modifier.size(42.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(if (state.isListening) "Listening..." else if (state.isThinking) "Processing..." else "Tap to speak", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TypingDots(color: Color) {
    val t = rememberInfiniteTransition(label = "d")
    val a1 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400), RepeatMode.Reverse), "a1")
    val a2 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 150), RepeatMode.Reverse), "a2")
    val a3 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 300), RepeatMode.Reverse), "a3")
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        listOf(a1, a2, a3).forEach { a -> Box(Modifier.size(10.dp).padding(3.dp).clip(CircleShape).background(color.copy(alpha = a))) }
    }
}
