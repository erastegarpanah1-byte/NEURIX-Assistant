package com.neurix.core.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neurix.core.design.NeurixColors
import com.neurix.core.design.NeurixDimens

@Composable
fun NeurixGradientCircle(modifier: Modifier = Modifier, size: Dp = NeurixDimens.AvatarSize, colors: List<androidx.compose.ui.graphics.Color> = listOf(NeurixColors.Primary, NeurixColors.Secondary)) {
    Box(modifier = modifier.size(size).clip(CircleShape).background(Brush.linearGradient(colors)))
}

@Composable
fun NeurixCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier), shape = RoundedCornerShape(NeurixDimens.CornerLarge), colors = CardDefaults.cardColors(containerColor = NeurixColors.Surface), content = content)
}

@Composable
fun PlaceholderScreen(featureName: String, description: String, onBack: (() -> Unit)? = null) {
    Box(Modifier.fillMaxSize().background(NeurixColors.Background)) {
        if (onBack != null) NeurixTopBar(featureName, onBack)
        Column(Modifier.fillMaxSize().padding(horizontal = NeurixDimens.PaddingLarge), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Filled.Construction, null, tint = NeurixColors.OnSurfaceMuted, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(NeurixDimens.PaddingLarge))
            Text(featureName, style = MaterialTheme.typography.headlineSmall, color = NeurixColors.OnSurface, textAlign = TextAlign.Center)
            Spacer(Modifier.height(NeurixDimens.PaddingSmall))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = NeurixColors.OnSurfaceMuted, textAlign = TextAlign.Center)
            Spacer(Modifier.height(NeurixDimens.PaddingXLarge))
            Text("Coming Soon", style = MaterialTheme.typography.labelLarge, color = NeurixColors.Primary, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeurixTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(title = { Text(title, style = MaterialTheme.typography.titleLarge, color = NeurixColors.OnSurface) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = NeurixColors.OnSurface) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = NeurixColors.Background))
}

@Composable
fun FadeInView(visible: Boolean = true, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn(tween(500)), exit = fadeOut(tween(300)), modifier = modifier) { content() }
}