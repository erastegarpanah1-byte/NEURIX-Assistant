package com.neurix.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(onBack: () -> Unit) {
    var sel by remember { mutableStateOf("Dark") }
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("Theme", "Customize Neurix appearance", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium)) {
            listOf("Dark", "Light", "System Default").forEach { t ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(NeurixDimens.CornerMedium)).background(NeurixColors.Surface).clickable { sel = t }.padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(sel == t, { sel = t }, colors = RadioButtonDefaults.colors(selectedColor = NeurixColors.Primary))
                    Spacer(Modifier.width(12.dp))
                    Text(t, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    var sel by remember { mutableStateOf("English") }
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("Language", "Change application language", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium)) {
            listOf("English", "Persian", "Arabic", "Turkish").forEach { l ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(NeurixDimens.CornerMedium)).background(NeurixColors.Surface).clickable { sel = l }.padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(sel == l, { sel = l }, colors = RadioButtonDefaults.colors(selectedColor = NeurixColors.Primary))
                    Spacer(Modifier.width(12.dp))
                    Text(l, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(onBack: () -> Unit) {
    var wake by remember { mutableStateOf(true) }
    var tts by remember { mutableStateOf(true) }
    var stt by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("Voice", "Configure speech and audio", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleRow("Wake Word", "Hey Neurix detection", wake) { wake = it }
            ToggleRow("Voice Output", "Neurix speaks aloud", tts) { tts = it }
            ToggleRow("Speech Input", "Voice to text", stt) { stt = it }
            HorizontalDivider(color = NeurixColors.Outline, thickness = 0.5.dp)
            LinkRow(Icons.Filled.SettingsVoice, "Voice Preferences", "Rate, pitch, accent", NeurixColors.Primary) {}
            LinkRow(Icons.Filled.Mic, "Microphone", "Select device", NeurixColors.Secondary) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(onBack: () -> Unit) {
    var mem by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("Memory", "Manage what Neurix remembers", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleRow("Enable Memory", "Remember past conversations", mem) { mem = it }
            HorizontalDivider(color = NeurixColors.Outline, thickness = 0.5.dp)
            LinkRow(Icons.Filled.DeleteForever, "Clear All Memory", "Remove stored conversations", Color(0xFFEF4444)) {}
            LinkRow(Icons.Filled.Storage, "Memory Usage", "12.4 MB used", NeurixColors.OnSurfaceMuted) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("Permissions", "Control device access", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PermCard("Microphone", "Voice input and wake word", true)
            PermCard("Notifications", "Show alerts", true)
            PermCard("Overlay", "Floating assistant", false)
            PermCard("Storage", "Save chat history", true)
        }
    }
}

@Composable
fun PermCard(name: String, desc: String, granted: Boolean) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(NeurixDimens.CornerMedium)).background(NeurixColors.Surface).padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.SemiBold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted)
        }
        Text(if (granted) "Granted" else "Denied", style = MaterialTheme.typography.labelMedium, color = if (granted) Color(0xFF22C55E) else Color(0xFFEF4444))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        DetailTopBar("About", "About Neurix", onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(NeurixDimens.PaddingMedium), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))), contentAlignment = Alignment.Center) { Text("N", style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
            Text("Neurix AI Assistant", style = MaterialTheme.typography.titleLarge, color = NeurixColors.OnSurface, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Version 1.0.0 (Bootstrap)", style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted)
            Spacer(Modifier.height(24.dp))
            OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(NeurixDimens.CornerLarge), colors = CardDefaults.outlinedCardColors(containerColor = NeurixColors.Surface)) {
                Column(Modifier.padding(NeurixDimens.PaddingMedium)) {
                    AboutRow("Developer", "Eilya Rastegarpanah")
                    AboutRow("Platform", "Android 7.0+")
                    AboutRow("AI Engine", "OpenRouter GPT-4o-mini")
                    AboutRow("Build", "Bootstrap v1")
                }
            }
        }
    }
}

@Composable
fun AboutRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurfaceMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(title: String, subtitle: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(NeurixColors.Surface).padding(start = 4.dp, end = NeurixDimens.PaddingMedium, top = NeurixDimens.PaddingSmall, bottom = NeurixDimens.PaddingMedium)) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeurixColors.OnSurface) }
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = NeurixDimens.PaddingMedium))
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted, modifier = Modifier.padding(horizontal = NeurixDimens.PaddingMedium))
    }
}

@Composable
fun ToggleRow(title: String, sub: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(NeurixDimens.CornerMedium)).background(NeurixColors.Surface).padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.Medium)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted)
        }
        Switch(checked, onToggle, colors = SwitchDefaults.colors(checkedTrackColor = NeurixColors.Primary))
    }
}

@Composable
fun LinkRow(icon: ImageVector, title: String, sub: String, tint: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(NeurixDimens.CornerMedium)).background(NeurixColors.Surface).clickable { onClick() }.padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = NeurixColors.OnSurfaceMuted, modifier = Modifier.size(18.dp))
    }
}
