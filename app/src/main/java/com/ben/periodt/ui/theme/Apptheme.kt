package com.ben.periodt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Elya's shared palette for the ivory, blush, rose, and plum visual identity. */
object ElyaPalette {
    val Ivory = Color(0xFFFFF8F5)
    val Paper = Color(0xFFFFFCFA)
    val Blush = Color(0xFFF4DFE7)
    val Rose = Color(0xFF8F405F)
    val RoseLight = Color(0xFFE7A4BC)
    val Plum = Color(0xFF613248)
    val Ink = Color(0xFF321B28)
    val Muted = Color(0xFF795C6B)
    val Border = Color(0xFFE9D8E0)
    val Night = Color(0xFF171117)
    val NightSurface = Color(0xFF2A1C26)
    val NightVariant = Color(0xFF3C2836)
    val NightText = Color(0xFFFFF2F7)
    val NightMuted = Color(0xFFD4BAC7)
}

/** MainScreen provides the resolved mode from the saved appearance preference. */
val LocalAppIsDark = compositionLocalOf { false }

private val LightColors = lightColorScheme(
    primary = ElyaPalette.Rose,
    onPrimary = Color.White,
    primaryContainer = ElyaPalette.Blush,
    onPrimaryContainer = ElyaPalette.Plum,
    secondary = ElyaPalette.Plum,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0DCE5),
    onSecondaryContainer = ElyaPalette.Ink,
    tertiary = Color(0xFFB96484),
    onTertiary = Color.White,
    background = ElyaPalette.Ivory,
    onBackground = ElyaPalette.Ink,
    surface = ElyaPalette.Paper,
    onSurface = ElyaPalette.Ink,
    surfaceVariant = ElyaPalette.Blush,
    onSurfaceVariant = ElyaPalette.Muted,
    outline = ElyaPalette.Border
)

private val DarkColors = darkColorScheme(
    primary = ElyaPalette.RoseLight,
    onPrimary = ElyaPalette.Night,
    primaryContainer = ElyaPalette.Plum,
    onPrimaryContainer = ElyaPalette.NightText,
    secondary = Color(0xFFDCB4C6),
    onSecondary = ElyaPalette.Night,
    secondaryContainer = ElyaPalette.NightVariant,
    onSecondaryContainer = ElyaPalette.NightText,
    tertiary = ElyaPalette.RoseLight,
    onTertiary = ElyaPalette.Night,
    background = ElyaPalette.Night,
    onBackground = ElyaPalette.NightText,
    surface = ElyaPalette.NightSurface,
    onSurface = ElyaPalette.NightText,
    surfaceVariant = ElyaPalette.NightVariant,
    onSurfaceVariant = ElyaPalette.NightMuted,
    outline = Color(0xFF654558)
)

@Composable
fun PeriodTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
