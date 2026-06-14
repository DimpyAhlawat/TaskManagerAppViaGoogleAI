package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = Color.White,
    primaryContainer = ProfessionalAccentContainer,
    onPrimaryContainer = ProfessionalAccentText,
    secondary = ProfessionalPrimary,
    onSecondary = Color.White,
    secondaryContainer = ProfessionalAccentContainer,
    onSecondaryContainer = ProfessionalAccentText,
    background = ProfessionalBackground,
    onBackground = ProfessionalTextPrimary,
    surface = Color.White,
    onSurface = ProfessionalTextPrimary,
    surfaceVariant = ProfessionalBottomNavBg,
    onSurfaceVariant = ProfessionalSecondaryText,
    outline = ProfessionalOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkPrimary,
    onSecondary = Color(0xFF381E72),
    secondaryContainer = DarkPrimaryContainer,
    onSecondaryContainer = DarkOnPrimaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to prioritize custom brand styling
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
