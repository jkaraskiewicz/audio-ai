package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.theme.UIConfig

/**
 * 🎨 DEVELOPER-FRIENDLY: Recording Controls Component
 *
 * Single Responsibility: Handles only the recording control buttons
 * Easy to customize: All styling comes from UIConfig
 *
 * Quick Customization Guide:
 * - Button sizes: UIConfig.Sizing.MainButtonSize / RecordButtonSize
 * - Colors: UIConfig.Colors.ScribelyRed / ScribelyGray / WhiteBackground
 * - Spacing: UIConfig.Spacing.ButtonSpacing
 * - Icons: Modify the icons directly in this component
 */
@Composable
fun RecordingControls(
  recordingState: RecordingState,
  onStartRecording: () -> Unit,
  onPauseRecording: () -> Unit,
  onResumeRecording: () -> Unit,
  onStopRecording: () -> Unit,
  onDiscardRecording: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center,
  ) {
    when (recordingState) {
      RecordingState.IDLE -> {
        // 🎨 Single Record Button (Idle State)
        RecordButton(onClick = onStartRecording)
      }

      RecordingState.RECORDING -> {
        // 🎨 Three Buttons: [Empty Space] [Pause] [Stop]
        ThreeButtonLayout(
          leftButton = { Spacer(modifier = Modifier.size(UIConfig.Sizing.MainButtonSize)) },
          centerButton = { PauseButton(onClick = onPauseRecording) },
          rightButton = { StopButton(onClick = onStopRecording) },
        )
      }

      RecordingState.PAUSED -> {
        // 🎨 Three Buttons: [Discard] [Resume] [Stop]
        ThreeButtonLayout(
          leftButton = { DiscardButton(onClick = onDiscardRecording) },
          centerButton = { ResumeButton(onClick = onResumeRecording) },
          rightButton = { StopButton(onClick = onStopRecording) },
        )
      }

      else -> {
        // Default fallback
        RecordButton(onClick = onStartRecording)
      }
    }
  }
}

// 🎨 DEVELOPER-FRIENDLY: Individual Button Components
// Each button has a single responsibility and consistent styling

@Composable
private fun RecordButton(onClick: () -> Unit) {
  Surface(
    modifier =
      Modifier
        .size(UIConfig.Sizing.RecordButtonSize)
        .clickable(
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onClick() },
    shape = CircleShape,
    color = UIConfig.Colors.ScribelyRed,
    shadowElevation = UIConfig.Sizing.ButtonElevation,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Default.Mic,
        contentDescription = "Record",
        tint = Color.White,
        modifier = Modifier.size(UIConfig.Sizing.RecordIconSize),
      )
    }
  }
}

@Composable
private fun PauseButton(onClick: () -> Unit) {
  Surface(
    modifier =
      Modifier
        .size(UIConfig.Sizing.MainButtonSize)
        .clickable(
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onClick() },
    shape = CircleShape,
    color = UIConfig.Colors.WhiteBackground,
    shadowElevation = UIConfig.Sizing.ButtonElevation,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Default.Pause,
        contentDescription = "Pause",
        tint = UIConfig.Colors.ScribelyGray,
        modifier = Modifier.size(UIConfig.Sizing.ButtonIconSize),
      )
    }
  }
}

@Composable
private fun ResumeButton(onClick: () -> Unit) {
  Surface(
    modifier =
      Modifier
        .size(UIConfig.Sizing.MainButtonSize)
        .clickable(
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onClick() },
    shape = CircleShape,
    color = UIConfig.Colors.WhiteBackground,
    shadowElevation = UIConfig.Sizing.ButtonElevation,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Resume",
        tint = UIConfig.Colors.ScribelyGray,
        modifier = Modifier.size(UIConfig.Sizing.ButtonIconSize),
      )
    }
  }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
  Surface(
    modifier =
      Modifier
        .size(UIConfig.Sizing.MainButtonSize)
        .clickable(
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onClick() },
    shape = CircleShape,
    color = UIConfig.Colors.ScribelyRed,
    shadowElevation = UIConfig.Sizing.ButtonElevation,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Default.Stop,
        contentDescription = "Stop",
        tint = Color.White,
        modifier = Modifier.size(UIConfig.Sizing.ButtonIconSize),
      )
    }
  }
}

@Composable
private fun DiscardButton(onClick: () -> Unit) {
  Surface(
    modifier =
      Modifier
        .size(UIConfig.Sizing.MainButtonSize)
        .clickable(
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onClick() },
    shape = CircleShape,
    color = UIConfig.Colors.WhiteBackground,
    shadowElevation = UIConfig.Sizing.ButtonElevation,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Discard",
        tint = UIConfig.Colors.ScribelyRed,
        modifier = Modifier.size(UIConfig.Sizing.ButtonIconSize),
      )
    }
  }
}

@Composable
private fun ThreeButtonLayout(
  leftButton: @Composable () -> Unit,
  centerButton: @Composable () -> Unit,
  rightButton: @Composable () -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(UIConfig.Spacing.ButtonSpacing),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leftButton()
    centerButton()
    rightButton()
  }
}
