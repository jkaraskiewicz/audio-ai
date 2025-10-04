package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.theme.UIConfig

/**
 * Recording controls composable - displays pixel art buttons based on recording state
 * Follows Single Responsibility Principle - only handles control button presentation
 */
@Composable
fun RecordingControlsPixel(
  recordingState: RecordingState,
  onStartRecording: () -> Unit,
  onPauseRecording: () -> Unit,
  onResumeRecording: () -> Unit,
  onStopRecording: () -> Unit,
  onDiscardRecording: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    when (recordingState) {
      RecordingState.IDLE -> {
        PixelButton(
          text = "REC",
          backgroundColor = UIConfig.PixelColors.ButtonRed,
          shadowColor = UIConfig.PixelColors.ButtonRedShadow,
          onClick = onStartRecording,
        )
      }
      RecordingState.RECORDING -> {
        PixelButton(
          text = "PAUSE",
          backgroundColor = UIConfig.PixelColors.ButtonBlue,
          shadowColor = UIConfig.PixelColors.ButtonBlueShadow,
          onClick = onPauseRecording,
          modifier = Modifier.padding(end = 16.dp),
        )
        PixelButton(
          text = "STOP",
          backgroundColor = UIConfig.PixelColors.ButtonRed,
          shadowColor = UIConfig.PixelColors.ButtonRedShadow,
          onClick = onStopRecording,
        )
      }
      RecordingState.PAUSED -> {
        PixelButton(
          text = "RESUME",
          backgroundColor = UIConfig.PixelColors.ButtonGreen,
          shadowColor = UIConfig.PixelColors.ButtonGreenShadow,
          onClick = onResumeRecording,
          modifier = Modifier.padding(end = 16.dp),
        )
        PixelButton(
          text = "STOP",
          backgroundColor = UIConfig.PixelColors.ButtonRed,
          shadowColor = UIConfig.PixelColors.ButtonRedShadow,
          onClick = onStopRecording,
          modifier = Modifier.padding(end = 16.dp),
        )
        PixelButton(
          text = "DISCARD",
          backgroundColor = UIConfig.PixelColors.ButtonGray,
          shadowColor = UIConfig.PixelColors.ButtonGrayShadow,
          onClick = onDiscardRecording,
        )
      }
      RecordingState.PROCESSING -> {
        PixelButton(
          text = "PROCESSING...",
          backgroundColor = UIConfig.PixelColors.ButtonGray,
          shadowColor = UIConfig.PixelColors.ButtonGrayShadow,
          onClick = { /* Processing, no action */ },
          enabled = false,
        )
      }
      RecordingState.FINISHED -> {
        PixelButton(
          text = "NEW REC",
          backgroundColor = UIConfig.PixelColors.ButtonGreen,
          shadowColor = UIConfig.PixelColors.ButtonGreenShadow,
          onClick = { /* Reset to IDLE for new recording */ },
        )
      }
    }
  }
}
