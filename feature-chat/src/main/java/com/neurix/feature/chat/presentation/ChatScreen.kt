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
fun ChatScreen(onNavigateBack: () -> Unit, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size) { if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1) }
    if (state.showNetworkError || (!state.isOnline && state.messages.size <= 5)) { NoInternetScreen({ viewModel.handleIntent(ChatIntent.DismissNetworkError) }, onNavigateBack); return }
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        if (!state.isOnline) Row(Modifier.fillMaxWidth().background(Color(0xFFEAB308)).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) { Icon(Icons.Filled.SignalWifiOff, null, tint = Color.Black, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text("No internet connection", color = Color.Black, fontSize = 13.sp) }
        TopAppBar(title = { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)))); Spacer(Modifier.width(12.dp)); Text("Neurix", style = MaterialTheme.typography.titleMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.SemiBold) } }, navigationIcon = { IconButton(onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeurixColors.OnSurface) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = NeurixColors.Surface))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = listState, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) { items(state.messages, key = { it.id }) { MessageBubble(it) }; if (state.isLoading) item { TypingIndicator() } }
        ChatInputBar(state.inputText, state.isListening, { viewModel.handleIntent(ChatIntent.UpdateInput(it)) }, { viewModel.handleIntent(ChatIntent.SendMessage) }, { viewModel.handleIntent(ChatIntent.TapMicrophone) })
    }
}

@Composable
fun NoInternetScreen(onRetry: () -> Unit, onBack: () -> Unit) = Box(Modifier.fillMaxSize().background(NeurixColors.Background)) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.SignalWifiOff, null, tint = NeurixColors.OnSurfaceMuted, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(24.dp)); Text("No Internet Connection", style = MaterialTheme.typography.headlineSmall, color = NeurixColors.OnSurface, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp)); Text("Please check your internet connection and try again.", style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurfaceMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp)); Button(onRetry, colors = ButtonDefaults.buttonColors(containerColor = NeurixColors.Primary), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Try Again", fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(12.dp)); TextButton(onBack) { Text("Go Back", color = NeurixColors.OnSurfaceMuted) }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val a = if(msg.isUser) Alignment.End else Alignment.Start
    Box(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth(), horizontalArrangement = if(msg.isUser) Arrangement.End else Arrangement.Start) {
        if(!msg.isUser) { Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)))); Spacer(Modifier.width(8.dp)) }
        Column(Modifier.widthIn(max = 300.dp), horizontalAlignment = a) {
            Box(Modifier.clip(RoundedCornerShape(if(msg.isUser) 20.dp else 4.dp, if(msg.isUser) 4.dp else 20.dp, 20.dp, 20.dp)).background(if(msg.isUser) Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary)) else NeurixColors.SurfaceHigh).padding(horizontal = 16.dp, vertical = 10.dp)) { Text(msg.text, style = MaterialTheme.typography.bodyMedium, color = if(msg.isUser) Color.White else NeurixColors.OnSurface) }
            if(msg.timestamp.isNotEmpty()) Text(msg.timestamp, style = MaterialTheme.typography.labelSmall, color = NeurixColors.OnSurfaceMuted, modifier = Modifier.padding(top = 2.dp, start = 8.dp, end = 8.dp))
        }
        if(msg.isUser) { Spacer(Modifier.width(8.dp)); Box(Modifier.size(32.dp).clip(CircleShape).background(NeurixColors.SurfaceHigh)) }
    } }
}

@Composable
fun TypingIndicator() {
    val t = rememberInfiniteTransition(label = "t"); val a1 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400), RepeatMode.Reverse), "a1"); val a2 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 150), RepeatMode.Reverse), "a2"); val a3 by t.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, 300), RepeatMode.Reverse), "a3")
    Row(Modifier.clip(RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)).background(NeurixColors.SurfaceHigh).padding(horizontal = 16.dp, vertical = 12.dp)) { Box(Modifier.size(8.dp).alpha(a1).clip(CircleShape).background(NeurixColors.OnSurfaceMuted)); Spacer(Modifier.width(4.dp)); Box(Modifier.size(8.dp).alpha(a2).clip(CircleShape).background(NeurixColors.OnSurfaceMuted)); Spacer(Modifier.width(4.dp)); Box(Modifier.size(8.dp).alpha(a3).clip(CircleShape).background(NeurixColors.OnSurfaceMuted)) }
}

@Composable
fun ChatInputBar(text: String, isListening: Boolean, onTextChange: (String) -> Unit, onSend: () -> Unit, onMicTap: () -> Unit) {
    val tt = rememberInfiniteTransition(label = "mp"); val pulse by tt.animateFloat(1f, 1.18f, infiniteRepeatable(tween(600), RepeatMode.Reverse), "p")
    Row(Modifier.fillMaxWidth().background(NeurixColors.Surface).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).clip(CircleShape).then(if(isListening) Modifier.scale(pulse) else Modifier).background(if(isListening) Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626))) else NeurixColors.SurfaceHigh).clickable { onMicTap() }, contentAlignment = Alignment.Center) { Icon(Icons.Filled.Mic, if(isListening) "Listening..." else "Tap to speak", tint = if(isListening) Color.White else NeurixColors.OnSurfaceMuted, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.width(8.dp)); OutlinedTextField(text, onTextChange, Modifier.weight(1f), placeholder = { Text(if(isListening) "Listening..." else "Message Neurix...", style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurfaceMuted) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeurixColors.Outline, unfocusedBorderColor = NeurixColors.Outline, focusedContainerColor = NeurixColors.SurfaceHigh, unfocusedContainerColor = NeurixColors.SurfaceHigh, cursorColor = NeurixColors.Primary, focusedTextColor = NeurixColors.OnSurface, unfocusedTextColor = NeurixColors.OnSurface), shape = RoundedCornerShape(16.dp), maxLines = 4)
        Spacer(Modifier.width(8.dp)); IconButton(onSend, Modifier.size(48.dp).clip(CircleShape).background(NeurixColors.Primary)) { Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White, modifier = Modifier.size(22.dp)) }
    }
}
