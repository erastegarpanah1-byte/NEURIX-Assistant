package com.neurix.feature.chat.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigateBack: () -> Unit, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1)
    }

    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))))
                    Spacer(Modifier.width(NeurixDimens.PaddingSmall))
                    Text("Neurix", style = MaterialTheme.typography.titleMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.SemiBold)
                }
            },
            navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeurixColors.OnSurface) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = NeurixColors.Surface)
        )

        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(), state = listState,
            contentPadding = PaddingValues(horizontal = NeurixDimens.PaddingMedium, vertical = NeurixDimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(NeurixDimens.PaddingSmall)
        ) {
            items(state.messages, key = { it.id }) { msg -> MessageBubble(msg) }
            if (state.isTyping) item { TypingIndicator() }
        }

        ChatInputBar(state.inputText, { viewModel.handleIntent(ChatIntent.UpdateInput(it)) }, { viewModel.handleIntent(ChatIntent.SendMessage) }, { viewModel.handleIntent(ChatIntent.TapMicrophone) })
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(Modifier.fillMaxWidth(), if (message.isUser) Arrangement.End else Arrangement.Start) {
        if (!message.isUser) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))))
            Spacer(Modifier.width(NeurixDimens.PaddingSmall))
        }
        Column(Modifier.widthIn(max = 300.dp), if (message.isUser) Alignment.End else Alignment.Start) {
            Box(
                Modifier.clip(RoundedCornerShape(if (message.isUser) 20.dp else 4.dp, if (message.isUser) 4.dp else 20.dp, 20.dp, 20.dp))
                    .background(if (message.isUser) Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)) else Brush.linearGradient(listOf(NeurixColors.SurfaceHigh, NeurixColors.SurfaceHigh)))
                    .padding(horizontal = NeurixDimens.PaddingMedium, vertical = NeurixDimens.PaddingSmall)
            ) {
                Text(message.text, style = MaterialTheme.typography.bodyMedium, color = if (message.isUser) Color.White else NeurixColors.OnSurface)
            }
            if (message.timestamp.isNotEmpty()) Text(message.timestamp, style = MaterialTheme.typography.labelSmall, color = NeurixColors.OnSurfaceMuted, modifier = Modifier.padding(top = 2.dp, start = NeurixDimens.PaddingSmall, end = NeurixDimens.PaddingSmall))
        }
        if (message.isUser) { Spacer(Modifier.width(NeurixDimens.PaddingSmall)); Box(Modifier.size(32.dp).clip(CircleShape).background(NeurixColors.SurfaceHigh)) }
    }
}

@Composable
fun TypingIndicator() {
    val t = rememberInfiniteTransition(label = "typing")
    val d1 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400), RepeatMode.Reverse), "d1")
    val d2 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 150), RepeatMode.Reverse), "d2")
    val d3 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 300), RepeatMode.Reverse), "d3")
    Row(Modifier.clip(RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)).background(NeurixColors.SurfaceHigh).padding(horizontal = NeurixDimens.PaddingMedium, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).alpha(d1).clip(CircleShape).background(NeurixColors.OnSurfaceMuted))
        Spacer(Modifier.width(4.dp))
        Box(Modifier.size(8.dp).alpha(d2).clip(CircleShape).background(NeurixColors.OnSurfaceMuted))
        Spacer(Modifier.width(4.dp))
        Box(Modifier.size(8.dp).alpha(d3).clip(CircleShape).background(NeurixColors.OnSurfaceMuted))
    }
}

@Composable
fun ChatInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit, onMicTap: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(NeurixColors.Surface).padding(horizontal = NeurixDimens.PaddingMedium, vertical = NeurixDimens.PaddingSmall), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text, onValueChange = onTextChange, modifier = Modifier.weight(1f),
            placeholder = { Text("Message Neurix...", style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurfaceMuted) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeurixColors.Outline, unfocusedBorderColor = NeurixColors.Outline, focusedContainerColor = NeurixColors.SurfaceHigh, unfocusedContainerColor = NeurixColors.SurfaceHigh, cursorColor = NeurixColors.Primary, focusedTextColor = NeurixColors.OnSurface, unfocusedTextColor = NeurixColors.OnSurface),
            shape = RoundedCornerShape(NeurixDimens.CornerLarge), maxLines = 4
        )
        Spacer(Modifier.width(NeurixDimens.PaddingSmall))
        IconButton(onClick = onSend, modifier = Modifier.size(44.dp).clip(CircleShape).background(NeurixColors.Primary)) {
            Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}