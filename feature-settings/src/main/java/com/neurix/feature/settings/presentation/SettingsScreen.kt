package com.neurix.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.neurix.core.navigation.Screen
import com.neurix.core.ui.composables.PlaceholderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateToDetail: (Screen) -> Unit) {
    Column(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        Spacer(Modifier.height(NeurixDimens.PaddingLarge))
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = NeurixDimens.PaddingMedium))
        Spacer(Modifier.height(NeurixDimens.PaddingMedium))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = NeurixDimens.PaddingMedium, vertical = NeurixDimens.PaddingSmall), verticalArrangement = Arrangement.spacedBy(NeurixDimens.PaddingLarge)) {
            ProfileCard()
            SettingsSection("Preferences") {
                SettingsRow(Icons.Filled.DarkMode, "Theme", "Dark", NeurixColors.Accent) { onNavigateToDetail(Screen.Theme) }
                SettingsRow(Icons.Filled.Language, "Language", "English", NeurixColors.Primary) { onNavigateToDetail(Screen.Language) }
            }
            SettingsSection("Features") {
                SettingsRow(Icons.Filled.Mic, "Voice", "Configure speech and audio", NeurixColors.Secondary) { onNavigateToDetail(Screen.Voice) }
                SettingsRow(Icons.Filled.Memory, "Memory", "Manage what Neurix remembers", Color(0xFFF59E0B)) { onNavigateToDetail(Screen.Memory) }
                SettingsRow(Icons.Filled.Security, "Permissions", "Control device access", Color(0xFF22C55E)) { onNavigateToDetail(Screen.Permissions) }
            }
            SettingsSection("About") {
                SettingsRow(Icons.Filled.Info, "About Neurix", "Version 1.0.0", NeurixColors.OnSurfaceMuted) { onNavigateToDetail(Screen.About) }
            }
            Spacer(Modifier.height(NeurixDimens.PaddingLarge))
        }
    }
}

@Composable
fun ProfileCard() {
    OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(NeurixDimens.CornerLarge), colors = CardDefaults.outlinedCardColors(containerColor = NeurixColors.Surface), border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)) {
        Row(Modifier.fillMaxWidth().padding(NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(NeurixDimens.AvatarSizeLarge).clip(CircleShape).background(Brush.linearGradient(listOf(NeurixColors.Primary, NeurixColors.Secondary))), contentAlignment = Alignment.Center) { Text("N", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(NeurixDimens.PaddingMedium))
            Column { Text("Neurix User", style = MaterialTheme.typography.titleMedium, color = NeurixColors.OnSurface, fontWeight = FontWeight.SemiBold); Text("neurix.user@example.com", style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted) }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge, color = NeurixColors.OnSurfaceMuted, modifier = Modifier.padding(bottom = NeurixDimens.PaddingSmall))
        OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(NeurixDimens.CornerLarge), colors = CardDefaults.outlinedCardColors(containerColor = NeurixColors.Surface), border = CardDefaults.outlinedCardBorder().copy(width = 0.dp)) { Column { content() } }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, subtitle: String, iconTint: Color, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(0.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = NeurixDimens.PaddingMedium, vertical = NeurixDimens.PaddingMedium), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(NeurixDimens.CornerSmall)).background(iconTint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(NeurixDimens.PaddingMedium))
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurface); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NeurixColors.OnSurfaceMuted) }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = NeurixColors.OnSurfaceMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingsDetailScreen(featureName: String, description: String, onBack: () -> Unit) {
    PlaceholderScreen(featureName = featureName, description = description, onBack = onBack)
}