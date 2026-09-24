package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TvDarkColorScheme = darkColorScheme(
    primary = TvPrimaryGlow,
    secondary = TvSecondaryBlue,
    tertiary = TvWarningAmber,
    background = TvDarkBackground,
    surface = TvSurfaceDark,
    surfaceVariant = TvSurfaceVariantDark,
    onPrimary = TvDarkBackground,
    onSecondary = TvDarkBackground,
    onBackground = TvTextPrimary,
    onSurface = TvTextPrimary,
    onError = TvTextPrimary,
    error = TvDangerRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TvDarkColorScheme,
        typography = Typography,
        content = content
    )
}
