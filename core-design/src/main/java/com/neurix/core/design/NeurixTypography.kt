package com.neurix.core.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun neurixTextStyle(
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Int,
    lineHeight: Int,
    letterSpacing: Float = 0f
): TextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp
)

val NeurixTypography = Typography(
    displayLarge = neurixTextStyle(FontWeight.Bold, 57, 64, -0.25f),
    displayMedium = neurixTextStyle(FontWeight.Bold, 45, 52),
    displaySmall = neurixTextStyle(FontWeight.Bold, 36, 44),
    headlineLarge = neurixTextStyle(FontWeight.SemiBold, 32, 40),
    headlineMedium = neurixTextStyle(FontWeight.SemiBold, 28, 36),
    headlineSmall = neurixTextStyle(FontWeight.SemiBold, 24, 32),
    titleLarge = neurixTextStyle(FontWeight.Medium, 22, 28),
    titleMedium = neurixTextStyle(FontWeight.Medium, 16, 24, 0.15f),
    titleSmall = neurixTextStyle(FontWeight.Medium, 14, 20, 0.1f),
    bodyLarge = neurixTextStyle(FontWeight.Normal, 16, 24, 0.5f),
    bodyMedium = neurixTextStyle(FontWeight.Normal, 14, 20, 0.25f),
    bodySmall = neurixTextStyle(FontWeight.Normal, 12, 16, 0.4f),
    labelLarge = neurixTextStyle(FontWeight.Medium, 14, 20, 0.1f),
    labelMedium = neurixTextStyle(FontWeight.Medium, 12, 16, 0.5f),
    labelSmall = neurixTextStyle(FontWeight.Medium, 11, 16, 0.5f)
)