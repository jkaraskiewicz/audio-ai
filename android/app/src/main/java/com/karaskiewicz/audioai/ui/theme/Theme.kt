package com.karaskiewicz.audioai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = AudioBlueDark,
  onPrimary = AudioBackground,
  primaryContainer = AudioBlueVariantDark,
  onPrimaryContainer = AudioBackground,
  secondary = AudioSecondaryDark,
  onSecondary = AudioBackground,
  secondaryContainer = AudioAccentDark,
  onSecondaryContainer = AudioBackgroundDark,
  tertiary = AudioSuccessDark,
  onTertiary = AudioBackgroundDark,
  error = AudioErrorDark,
  onError = AudioBackgroundDark,
  errorContainer = RecordingActive,
  onErrorContainer = AudioBackground,
  background = AudioBackgroundDark,
  onBackground = AudioBlueDark,
  surface = AudioSurfaceDark,
  onSurface = AudioBlueDark,
  surfaceVariant = AudioSecondaryDark,
  onSurfaceVariant = AudioAccentDark,
)

private val LightColorScheme = lightColorScheme(
  primary = AudioBlue,
  onPrimary = AudioBackground,
  primaryContainer = AudioBlueVariant,
  onPrimaryContainer = AudioBackground,
  secondary = AudioSecondary,
  onSecondary = AudioBackground,
  secondaryContainer = AudioAccent,
  onSecondaryContainer = AudioBackgroundDark,
  tertiary = AudioSuccess,
  onTertiary = AudioBackgroundDark,
  error = AudioError,
  onError = AudioBackground,
  errorContainer = RecordingActive,
  onErrorContainer = AudioBackground,
  background = AudioBackground,
  onBackground = AudioSecondary,
  surface = AudioSurface,
  onSurface = AudioSecondary,
  surfaceVariant = AudioSecondary,
  onSurfaceVariant = AudioBlueVariant,
)

@Composable
fun AudioAITheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}
