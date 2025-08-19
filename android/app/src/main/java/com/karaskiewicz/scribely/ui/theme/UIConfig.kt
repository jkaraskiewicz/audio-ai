package com.karaskiewicz.scribely.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎨 SCRIBELY UI CONFIGURATION
 *
 * DEVELOPER-FRIENDLY DESIGN SYSTEM
 * Uncle Bob's Clean Code Principles Applied:
 * ✅ Single Responsibility: Each object handles one aspect
 * ✅ DRY: No duplication, everything centralized here
 * ✅ Easy to Modify: Change once, affects entire app
 *
 * 🚀 QUICK START GUIDE FOR DEVELOPERS:
 * 1. Want different colors? → Modify UIConfig.Colors
 * 2. Want bigger buttons? → Modify UIConfig.Sizing
 * 3. Want more spacing? → Modify UIConfig.Spacing
 * 4. Want different animations? → Modify UIConfig.Animations
 * 5. Want to reposition elements? → Modify UIConfig.Layout
 */
object UIConfig {

  // ============================================
  // 🎨 COLORS - Brand & UI Colors
  // ============================================

  object Colors {
    // 🔴 PRIMARY BRAND COLORS
    val ScribelyRed = Color(0xFFDC2626) // Main brand red
    val ScribelyRedLight = Color(0xFFF87171) // Lighter red for backgrounds
    val ScribelyGray = Color(0xFF4A5568) // Dark gray for text/icons
    val ScribelyGrayLight = Color(0xFF6B7280) // Lighter gray

    // ⚪ BACKGROUND COLORS
    val DefaultBackground = Color(0xFFF9FAFB) // Light app background
    val WhiteBackground = Color.White // Pure white for cards/buttons

    // 📝 TEXT COLORS (Monospace Theme)
    val PrimaryTextColor = Color(0xFF2D3748) // Main text (dark)
    val SecondaryTextColor = Color(0xFF4A5568) // Secondary text (gray)
    val TimerTextColor = Color(0xFF374151) // Large timer display
    val PausedTextColor = Color(0xFF6B7280) // Muted/disabled text

    // 🎵 AUDIO UI COLORS
    val WaveformColor = ScribelyRedLight // Animated wave bars
    val PlayheadColor = ScribelyRed // Timeline indicators
    val TimeMarkerColor = Color(0xFF9CA3AF) // Time labels

    // 🔧 SYSTEM COLORS
    val BorderColor = Color(0xFFD1D5DB) // Input borders, dividers
    val ShadowColor = Color.Black.copy(alpha = 0.1f) // Button shadows
  }

  // ============================================
  // 📏 SPACING - Layout Dimensions
  // ============================================

  object Spacing {
    // 📱 SCREEN LAYOUT
    val ScreenPadding = 16.dp // Main screen padding
    val HeaderHeight = 70.dp // Fixed header height
    val FooterHeight = 40.dp // Bottom spacer height

    // 🏷️ LOGO & HEADER
    val LogoPadding = 20.dp // Distance from screen edges
    val HeaderPadding = 20.dp // Internal header padding

    // ⏰ TIMER SECTION
    val TimerSectionHeight = 128.dp // Fixed timer area height
    val TimerBottomMargin = 64.dp // Space below timer

    // 🎛️ CONTROLS SECTION
    val ControlsHeight = 112.dp // Fixed controls area height
    val ButtonSpacing = 16.dp // Space between buttons
    val ControlsBottomPadding = 40.dp // Bottom padding for controls

    // 🌊 WAVE ANIMATION
    val WaveSpacing = 7.dp // Space between wave bars
    val WaveTopOffset = (-30).dp // Wave position above timer

    // ⚙️ SETTINGS SCREEN
    val SettingsVerticalSpacing = 32.dp // Between setting sections
    val SettingsInputSpacing = 8.dp // Input to button spacing
    val SettingsMaxWidth = 400.dp // Max width for settings

    // 🎯 GENERAL SPACING (Use these for consistency)
    val SmallSpacing = 8.dp // Tight spacing
    val MediumSpacing = 16.dp // Standard spacing
    val LargeSpacing = 24.dp // Wide spacing
    val XLargeSpacing = 32.dp // Extra wide spacing
  }

  // ============================================
  // 📐 SIZING - Component Dimensions
  // ============================================

  object Sizing {
    // 🏷️ LOGO DIMENSIONS
    val LogoWidth = 180.dp // Logo total width
    val LogoHeight = 42.dp // Logo total height
    val LogoTextSize = 24.sp // "Scribely" text size

    // ⏰ TIMER DISPLAY
    val TimerTextSize = 56.sp // Large timer text
    val TimerTextSizeLarge = 64.sp // Even larger for tablets

    // 🎛️ BUTTON DIMENSIONS
    val MainButtonSize = 80.dp // Standard control buttons
    val SecondaryButtonSize = 64.dp // Smaller action buttons
    val RecordButtonSize = 96.dp // Large record button
    val ButtonElevation = 8.dp // Button shadow depth
    val ButtonCornerRadius = 50.dp // Fully rounded buttons

    // 🎵 ICON SIZES
    val ButtonIconSize = 32.dp // Standard button icons
    val RecordIconSize = 48.dp // Large record icon
    val SettingsIconSize = 28.dp // Header settings icon

    // 🌊 WAVE ANIMATION
    val WaveBarWidth = 4.dp // Individual wave bar width
    val WaveBarMaxHeight = 20.dp // Maximum wave bar height
    val WaveBarMinHeight = 4.dp // Minimum wave bar height
    val WaveBarCornerRadius = 2.dp // Wave bar rounded corners

    // 📝 INPUT FIELDS (Settings)
    val InputHeight = 48.dp // Standard input height
    val InputCornerRadius = 6.dp // Input field corners
    val InputPadding = 12.dp // Internal input padding

    // 📦 CONTAINERS
    val CardCornerRadius = 8.dp // Card corner radius
    val ContainerPadding = 16.dp // Standard container padding
  }

  // ============================================
  // 🔤 TYPOGRAPHY - Text Styling
  // ============================================

  object Typography {
    // 📝 LETTER SPACING (JetBrains Mono optimized)
    val MonospaceLetterSpacing = 0.1.sp // Tight monospace spacing
    val TimerLetterSpacing = 0.2.sp // Timer display spacing
    val DefaultLetterSpacing = 0.sp // Standard spacing
    val WideLetterSpacing = 1.sp // Emphasis spacing

    // 🎯 FONT WEIGHTS (JetBrains Mono supported)
    val LightWeight = FontWeight.Light // 300 - Timer display
    val NormalWeight = FontWeight.Normal // 400 - Body text
    val BoldWeight = FontWeight.Bold // 700 - Headers
  }

  // ============================================
  // 🎬 ANIMATIONS - Motion & Timing
  // ============================================

  object Animations {
    // ⏰ TIMER ANIMATIONS
    val TimerTickDuration = 200 // Timer scale animation (ms)
    val TimerTickScale = 1.04f // Scale factor for tick
    val TimerTickInterval = 1000L // Tick every second (ms)

    // 🌊 WAVE ANIMATIONS
    val WaveAnimationDuration = 1200 // Full wave cycle (ms)
    val WaveBarsCount = 5 // Number of animated bars

    // 🎛️ BUTTON ANIMATIONS
    val ButtonAnimationDuration = 200 // Button press feedback (ms)
    val ButtonRippleDuration = 300 // Ripple effect (ms)

    // 🎨 COLOR TRANSITIONS
    val ColorAnimationDuration = 300 // Color change transitions (ms)
  }

  // ============================================
  // 📱 LAYOUT - Positioning & Behavior
  // ============================================

  object Layout {
    // 🏷️ LOGO POSITIONING (Easy to change!)
    enum class LogoPosition {
      TOP_LEFT, // Current: Logo in top left
      TOP_CENTER, // Center the logo
      TOP_RIGHT, // Logo in top right
    }
    val logoPosition = LogoPosition.TOP_LEFT // 🎨 CUSTOMIZE: Change logo position

    // ⚙️ SETTINGS BUTTON POSITIONING
    enum class SettingsPosition {
      TOP_RIGHT, // Current: Settings in top right
      TOP_LEFT, // Settings in top left
      HIDDEN, // Hide settings button
    }
    val settingsPosition = SettingsPosition.TOP_RIGHT // 🎨 CUSTOMIZE: Change settings position

    // 🎛️ CONTROLS LAYOUT
    enum class ControlsStyle {
      MODERN_THREE_BUTTON, // Current: 3-button layout
      SIMPLE_TWO_BUTTON, // Classic 2-button layout
      MINIMAL_SINGLE, // Just record button
    }
    val controlsStyle = ControlsStyle.MODERN_THREE_BUTTON // 🎨 CUSTOMIZE: Change controls

    // ⏰ TIMER POSITIONING
    enum class TimerPosition {
      CENTER, // Current: Centered timer
      TOP, // Timer at top
      BOTTOM, // Timer at bottom
    }
    val timerPosition = TimerPosition.CENTER // 🎨 CUSTOMIZE: Change timer position
  }
}

/**
 * 🚀 DEVELOPER QUICK REFERENCE CHEAT SHEET:
 *
 * 🎨 Common Customizations:
 *
 * Change App Colors:
 * → UIConfig.Colors.ScribelyRed = Color(0xFF1976D2)
 *
 * Make Buttons Bigger:
 * → UIConfig.Sizing.MainButtonSize = 100.dp
 *
 * Add More Spacing:
 * → UIConfig.Spacing.ButtonSpacing = 24.dp
 *
 * Move Logo to Center:
 * → UIConfig.Layout.logoPosition = LogoPosition.TOP_CENTER
 *
 * Change Timer Size:
 * → UIConfig.Sizing.TimerTextSize = 72.sp
 *
 * Faster Animations:
 * → UIConfig.Animations.ButtonAnimationDuration = 100
 *
 * Different Background:
 * → UIConfig.Colors.DefaultBackground = Color(0xFF121212)
 */
