package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PodoTealPrimary,
    onPrimary = Color.White,
    primaryContainer = PodoTealLight,
    onPrimaryContainer = PodoTealDark,
    secondary = PodoBluePrimary,
    onSecondary = Color.White,
    secondaryContainer = PodoBlueLight,
    onSecondaryContainer = PodoBlueDark,
    tertiary = PodoSoftMintDark,
    onTertiary = Color.White,
    tertiaryContainer = PodoSoftMint,
    onTertiaryContainer = PodoSoftMintDark,
    error = EmergencyRed,
    onError = Color.White,
    errorContainer = EmergencyRedContainer,
    onErrorContainer = EmergencyRedDark,
    background = BackgroundClean,
    onBackground = TextTitle,
    surface = SurfacePureWhite,
    onSurface = TextTitle,
    surfaceVariant = SurfaceCardSubtle,
    onSurfaceVariant = TextBody,
    outline = BorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = PodoTealContainer,
    onPrimary = Color(0xFF00373D),
    primaryContainer = PodoTealDark,
    onPrimaryContainer = PodoTealLight,
    secondary = PodoBlueContainer,
    onSecondary = Color(0xFF00325B),
    secondaryContainer = PodoBlueDark,
    onSecondaryContainer = PodoBlueLight,
    tertiary = Color(0xFFA5D6A7),
    onTertiary = Color(0xFF1B5E20),
    error = EmergencyRedLight,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = EmergencyRedLight,
    background = Color(0xFF0E1A1D),
    onBackground = Color(0xFFE0EAEB),
    surface = Color(0xFF142427),
    onSurface = Color(0xFFE0EAEB),
    surfaceVariant = Color(0xFF22363A),
    onSurfaceVariant = Color(0xFFBACCCF),
    outline = Color(0xFF435A5E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
