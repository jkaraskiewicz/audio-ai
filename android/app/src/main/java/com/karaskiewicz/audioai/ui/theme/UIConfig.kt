package com.karaskiewicz.audioai.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * UI Configuration for Scribely App
 *
 * 🎨 DEVELOPER NOTE: This file contains all UI customization constants.
 * Modify these values to quickly change the app's appearance without hunting through code.
 */
object UIConfig {

  // ===========================================
  // 🎨 COLORS - Easy to modify app colors
  // ===========================================

  object Colors {
    // Primary brand colors
    val ScribelyRed = Color(0xFFE53935) // Main brand color
    val ScribelyGreen = Color(0xFF4CAF50) // Success/Send actions

    // Recording states
    val RecordingBackground = Color(0xFFF5F5F5) // Light gray for recording mode
    val WaveformColor = Color.Black // Waveform bars color
    val PlayheadColor = ScribelyRed // Moving timeline indicator

    // UI accents
    val PausedTextColor = Color.Gray
    val TimeMarkerColor = Color.Gray
    val DottedLineColor = Color.Gray.copy(alpha = 0.3f)
    val TimerTextColor = Color.Black
  }

  // ===========================================
  // 📏 SPACING & SIZING - Layout dimensions
  // ===========================================

  object Spacing {
    // Logo and header spacing
    val LogoPadding = 40.dp
    val HeaderVerticalSpacing = 40.dp

    // Timeline layout
    val TimelineHorizontalPadding = 24.dp
    val TimelineVerticalSpacing = 60.dp
    val TimeMarkersSpacing = 8.dp

    // Controls spacing
    val ControlsBottomPadding = 40.dp
    val ControlsHorizontalPadding = 40.dp
    val ButtonSpacing = 16.dp

    // Message cards spacing
    val MessageCardHorizontalPadding = 16.dp
    val MessageCardVerticalPadding = 8.dp
    val MessageCardContentPadding = 16.dp
  }

  object Sizing {
    // Logo dimensions
    val LogoIconSize = 28.dp
    val LogoIconCornerRadius = 8.dp

    // Timeline dimensions
    val TimelineWidth = 320.dp
    val TimelineHeight = 120.dp
    val WaveformBarWidth = 1.5.dp
    val PlayheadWidth = 2.dp

    // Button dimensions
    val MainButtonSize = 64.dp
    val RecordButtonSize = 72.dp
    val ButtonElevation = 4.dp

    // Icon sizes
    val ButtonIconSize = 28.dp
    val RecordIconSize = 32.dp

    // Message cards sizing
    val MessageCardCornerRadius = 12.dp
  }

  // ===========================================
  // 🔤 TYPOGRAPHY - Text styling
  // ===========================================

  object Typography {
    // Logo styling
    val LogoLetterSpacing = (-0.5).sp

    // Timer styling (can be customized for different timer looks)
    val TimerStyle = "large" // Options: "large", "medium", "compact"
  }

  // ===========================================
  // ⚙️ BEHAVIOR - UI behavior settings
  // ===========================================

  object Behavior {
    // Timeline behavior
    val TimelineWindowDuration = 10000L // 10 seconds visible window
    val WaveformBarsCount = 80 // Number of waveform bars

    // Animation durations (milliseconds)
    val ButtonAnimationDuration = 200
    val ColorAnimationDuration = 300
    val WaveformAnimationDuration = 200
  }

  // ===========================================
  // 📱 LAYOUT PRESETS - Quick layout changes
  // ===========================================

  object LayoutPresets {
    // Logo position options
    enum class LogoPosition { TOP_CENTER, TOP_LEFT, TOP_RIGHT }
    val currentLogoPosition = LogoPosition.TOP_CENTER

    // Controls layout options
    enum class ControlsLayout { TWO_BUTTONS, SINGLE_BUTTON, THREE_BUTTONS }
    val currentControlsLayout = ControlsLayout.TWO_BUTTONS

    // Timeline position options
    enum class TimelinePosition { CENTER, TOP, BOTTOM }
    val currentTimelinePosition = TimelinePosition.CENTER
  }
}

/**
 * 🚀 DEVELOPER QUICK REFERENCE:
 *
 * Want to change the app's main color?
 * → Modify UIConfig.Colors.ScribelyRed
 *
 * Want to make buttons bigger?
 * → Modify UIConfig.Sizing.MainButtonSize
 *
 * Want more space around the logo?
 * → Modify UIConfig.Spacing.LogoPadding
 *
 * Want to change timeline duration?
 * → Modify UIConfig.Behavior.TimelineWindowDuration
 *
 * Want to move the logo position?
 * → Change UIConfig.LayoutPresets.currentLogoPosition
 */
