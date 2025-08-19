package com.karaskiewicz.scribely.ui.theme

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

// Updated color scheme for new Scribely design
private val LightColorScheme = lightColorScheme(
  primary = UIConfig.Colors.ScribelyRed,
  onPrimary = UIConfig.Colors.WhiteBackground,
  primaryContainer = UIConfig.Colors.ScribelyRedLight,
  onPrimaryContainer = UIConfig.Colors.PrimaryTextColor,
  secondary = UIConfig.Colors.ScribelyGray,
  onSecondary = UIConfig.Colors.WhiteBackground,
  secondaryContainer = UIConfig.Colors.ScribelyGrayLight,
  onSecondaryContainer = UIConfig.Colors.PrimaryTextColor,
  tertiary = UIConfig.Colors.ScribelyRedLight,
  onTertiary = UIConfig.Colors.PrimaryTextColor,
  error = UIConfig.Colors.ScribelyRed,
  onError = UIConfig.Colors.WhiteBackground,
  errorContainer = UIConfig.Colors.ScribelyRedLight,
  onErrorContainer = UIConfig.Colors.PrimaryTextColor,
  background = UIConfig.Colors.DefaultBackground,
  onBackground = UIConfig.Colors.PrimaryTextColor,
  surface = UIConfig.Colors.WhiteBackground,
  onSurface = UIConfig.Colors.PrimaryTextColor,
  surfaceVariant = UIConfig.Colors.DefaultBackground,
  onSurfaceVariant = UIConfig.Colors.SecondaryTextColor,
)

@Composable
fun ScribelyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = ScribelyTypography,
    content = content,
  )
}
