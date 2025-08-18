package com.karaskiewicz.audioai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// JetBrains Mono font family (using system monospace as fallback)
// TODO: Add JetBrains Mono font files to res/font/ for exact design match
val JetBrainsMono = FontFamily.Monospace

// Custom typography using JetBrains Mono
val ScribelyTypography = Typography(
  // Large timer display
  displayLarge = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Light,
    fontSize = UIConfig.Sizing.TimerTextSize,
    letterSpacing = UIConfig.Typography.TimerLetterSpacing,
    color = UIConfig.Colors.TimerTextColor,
  ),

  // App title/logo
  headlineLarge = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Bold,
    fontSize = UIConfig.Sizing.LogoTextSize,
    letterSpacing = UIConfig.Typography.DefaultLetterSpacing,
    color = UIConfig.Colors.PrimaryTextColor,
  ),

  // Section headers
  headlineMedium = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    letterSpacing = UIConfig.Typography.DefaultLetterSpacing,
    color = UIConfig.Colors.PrimaryTextColor,
  ),

  // Button text
  labelLarge = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    letterSpacing = UIConfig.Typography.DefaultLetterSpacing,
    color = UIConfig.Colors.SecondaryTextColor,
  ),

  // Body text
  bodyLarge = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    letterSpacing = UIConfig.Typography.MonospaceLetterSpacing,
    color = UIConfig.Colors.SecondaryTextColor,
  ),

  // Smaller body text
  bodyMedium = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    letterSpacing = UIConfig.Typography.MonospaceLetterSpacing,
    color = UIConfig.Colors.SecondaryTextColor,
  ),

  // Small labels and captions
  bodySmall = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    letterSpacing = UIConfig.Typography.MonospaceLetterSpacing,
    color = UIConfig.Colors.PausedTextColor,
  ),
)
