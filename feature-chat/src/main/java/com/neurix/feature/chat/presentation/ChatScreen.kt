package com.neurix.feature.chat.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.neurix.core.design.NeurixDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    if (state.showNetworkError || (!state.isOnline && state.messages.size <= 5)) {
        NoInternetScreen(
            onRetry = { viewModel.handleIntent(ChatIntent.DismissNetworkError) },
            onBack = onNavigateBack
        )
        return
    }

    val bgColor = NeurixColors.Background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bgColor)
    ) {
        if (!state.isOnline) {
            val bannerColor = Color(0xFFEAB308)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = bannerColor)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.SignalWifiOff,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "No internet connection",
                    color = Color.Black,
                    fontSize = 13.sp
                )
            }
        }

        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(NeurixColors.Primary, NeurixColors.Secondary)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Neurix",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeurixColors.OnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeurixColors.OnSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NeurixColors.Surface
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }

            if (state.isLoading) {
                item { TypingIndicator() }
            }
        }

        ChatInputBar(
            text = state.inputText,
            isListening = state.isListening,
            onTextChange = { viewModel.handleIntent(ChatIntent.UpdateInput(it)) },
            onSend = { viewModel.handleIntent(ChatIntent.SendMessage) },
            onMicTap = { viewModel.handleIntent(ChatIntent.TapMicrophone) }
        )
    }
}

@Composable
fun NoInternetScreen(
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = NeurixColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.SignalWifiOff,
                contentDescription = null,
                tint = NeurixColors.OnSurfaceMuted,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "No Internet Connection",
                style = MaterialTheme.typography.headlineSmall,
                color = NeurixColors.OnSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Please check your internet connection and try again.",
                style = MaterialTheme.typography.bodyMedium,
                color = NeurixColors.OnSurfaceMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeurixColors.Primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Try Again", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onBack) {
                Text("Go Back", color = NeurixColors.OnSurfaceMuted)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val rowAlignment = if (message.isUser) Alignment.End else Alignment.Start
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(NeurixColors.Primary, NeurixColors.Secondary)
                            )
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 300.dp),
                horizontalAlignment = rowAlignment
            ) {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = if (message.isUser) 20.dp else 4.dp,
                                topEnd = if (message.isUser) 4.dp else 20.dp,
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                        .background(
                            brush = if (message.isUser) Brush.linearGradient(
                                listOf(NeurixColors.Primary, NeurixColors.Secondary)
                            ) else Brush.linearGradient(
                                listOf(NeurixColors.SurfaceHigh, NeurixColors.SurfaceHigh)
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.isUser) Color.White else NeurixColors.OnSurface
                    )
                }
                if (message.timestamp.isNotEmpty()) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeurixColors.OnSurfaceMuted,
                        modifier = Modifier.padding(
                            top = 2.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                    )
                }
            }

            if (message.isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color = NeurixColors.SurfaceHigh)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .background(color = NeurixColors.SurfaceHigh)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(dot1Alpha)
                .clip(CircleShape)
                .background(color = NeurixColors.OnSurfaceMuted)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(dot2Alpha)
                .clip(CircleShape)
                .background(color = NeurixColors.OnSurfaceMuted)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(dot3Alpha)
                .clip(CircleShape)
                .background(color = NeurixColors.OnSurfaceMuted)
        )
    }
}

@Composable
fun ChatInputBar(
    text: String,
    isListening: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicTap: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = NeurixColors.Surface)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .then(
                    if (isListening) {
                        Modifier.scale(micPulse)
                    } else {
                        Modifier
                    }
                )
                .background(
                    brush = if (isListening) Brush.linearGradient(
                        listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                    ) else Brush.linearGradient(
                        listOf(NeurixColors.SurfaceHigh, NeurixColors.SurfaceHigh)
                    )
                )
                .clickable { onMicTap() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = if (isListening) "Listening..." else "Tap to speak",
                tint = if (isListening) Color.White else NeurixColors.OnSurfaceMuted,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    if (isListening) "Listening..." else "Message Neurix...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeurixColors.OnSurfaceMuted
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeurixColors.Outline,
                unfocusedBorderColor = NeurixColors.Outline,
                focusedContainerColor = NeurixColors.SurfaceHigh,
                unfocusedContainerColor = NeurixColors.SurfaceHigh,
                cursorColor = NeurixColors.Primary,
                focusedTextColor = NeurixColors.OnSurface,
                unfocusedTextColor = NeurixColors.OnSurface
            ),
            shape = RoundedCornerShape(16.dp),
            maxLines = 4
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color = NeurixColors.Primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
