package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MediTealPrimary,
    onPrimary = MediTealOnPrimary,
    primaryContainer = MediTealContainer,
    onPrimaryContainer = MediTealOnContainer,
    secondary = MediTerracottaSecondary,
    onSecondary = MediTerracottaOnSecondary,
    secondaryContainer = MediTerracottaContainer,
    onSecondaryContainer = MediTerracottaOnContainer,
    tertiary = MediAmberTertiary,
    tertiaryContainer = MediAmberContainer,
    onTertiaryContainer = MediAmberOnContainer,
    error = MediEmergencyRed,
    errorContainer = MediEmergencyContainer,
    onErrorContainer = MediEmergencyOnContainer,
    background = MediBackgroundLight,
    onBackground = MediOnSurfaceLight,
    surface = MediSurfaceLight,
    onSurface = MediOnSurfaceLight,
    surfaceVariant = MediSurfaceVariantLight,
    onSurfaceVariant = MediOnSurfaceVariantLight
)

private val DarkHighContrastColorScheme = darkColorScheme(
    primary = MediTealPrimaryDark,
    onPrimary = MediTealOnPrimaryDark,
    primaryContainer = MediTealContainerDark,
    onPrimaryContainer = MediTealOnContainerDark,
    secondary = MediTerracottaSecondaryDark,
    onSecondary = MediTerracottaOnSecondaryDark,
    secondaryContainer = MediTerracottaContainerDark,
    onSecondaryContainer = MediTerracottaOnContainerDark,
    error = MediEmergencyContainer,
    errorContainer = MediEmergencyRed,
    onErrorContainer = MediTealOnPrimary,
    background = MediBackgroundDark,
    onBackground = MediOnSurfaceDark,
    surface = MediSurfaceDark,
    onSurface = MediOnSurfaceDark,
    surfaceVariant = MediSurfaceVariantDark,
    onSurfaceVariant = MediOnSurfaceVariantDark
)

@Composable
fun MediReachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrastOverride: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme || highContrastOverride) {
        DarkHighContrastColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
