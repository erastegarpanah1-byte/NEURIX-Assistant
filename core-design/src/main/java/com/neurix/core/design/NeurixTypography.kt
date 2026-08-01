package com.neurix.core.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NeurixTypography = Typography(
    displayLarge = TextStyle(FontFamily.Default, FontWeight.Bold, 57.sp, 64.sp, (-0.25).sp),
    displayMedium = TextStyle(FontFamily.Default, FontWeight.Bold, 45.sp, 52.sp),
    displaySmall = TextStyle(FontFamily.Default, FontWeight.Bold, 36.sp, 44.sp),
    headlineLarge = TextStyle(FontFamily.Default, FontWeight.SemiBold, 32.sp, 40.sp),
    headlineMedium = TextStyle(FontFamily.Default, FontWeight.SemiBold, 28.sp, 36.sp),
    headlineSmall = TextStyle(FontFamily.Default, FontWeight.SemiBold, 24.sp, 32.sp),
    titleLarge = TextStyle(FontFamily.Default, FontWeight.Medium, 22.sp, 28.sp),
    titleMedium = TextStyle(FontFamily.Default, FontWeight.Medium, 16.sp, 24.sp, 0.15.sp),
    titleSmall = TextStyle(FontFamily.Default, FontWeight.Medium, 14.sp, 20.sp, 0.1.sp),
    bodyLarge = TextStyle(FontFamily.Default, FontWeight.Normal, 16.sp, 24.sp, 0.5.sp),
    bodyMedium = TextStyle(FontFamily.Default, FontWeight.Normal, 14.sp, 20.sp, 0.25.sp),
    bodySmall = TextStyle(FontFamily.Default, FontWeight.Normal, 12.sp, 16.sp, 0.4.sp),
    labelLarge = TextStyle(FontFamily.Default, FontWeight.Medium, 14.sp, 20.sp, 0.1.sp),
    labelMedium = TextStyle(FontFamily.Default, FontWeight.Medium, 12.sp, 16.sp, 0.5.sp),
    labelSmall = TextStyle(FontFamily.Default, FontWeight.Medium, 11.sp, 16.sp, 0.5.sp)
)