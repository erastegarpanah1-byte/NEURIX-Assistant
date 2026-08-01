package com.neurix.core.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme = darkColorScheme(
    primary = NeurixColors.Primary, onPrimary = NeurixColors.OnPrimary,
    primaryContainer = NeurixColors.PrimaryVariant, secondary = NeurixColors.Secondary,
    onSecondary = NeurixColors.OnSecondary, secondaryContainer = NeurixColors.SecondaryVariant,
    tertiary = NeurixColors.Accent, background = NeurixColors.Background,
    onBackground = NeurixColors.OnBackground, surface = NeurixColors.Surface,
    onSurface = NeurixColors.OnSurface, surfaceVariant = NeurixColors.SurfaceVariant,
    onSurfaceVariant = NeurixColors.OnSurfaceMuted, outline = NeurixColors.Outline,
    outlineVariant = NeurixColors.OutlineVariant, error = NeurixColors.Error
)

@Composable
fun NeurixTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, typography = NeurixTypography, shapes = NeurixShapes, content = content)
}