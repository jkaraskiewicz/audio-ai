package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.utils.DurationFormatter

/**
 * Main screen content composable - displays the complete UI layout
 * Follows Single Responsibility Principle - only handles screen layout and composition
 */
@Composable
fun MainScreenContent(
  recordingState: RecordingState,
  recordingDuration: Long,
  errorMessage: String?,
  successMessage: String?,
  onNavigateToSettings: () -> Unit,
  onStartRecording: () -> Unit,
  onPauseRecording: () -> Unit,
  onResumeRecording: () -> Unit,
  onStopRecording: () -> Unit,
  onDiscardRecording: () -> Unit,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(UIConfig.PixelColors.Background)
        .statusBarsPadding()
        .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Header with Scribely title and Settings
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PixelHeaderText(text = "Scribely")
      PixelClickableText(
        text = "[Settings]",
        onClick = onNavigateToSettings,
      )
    }

    // Spacer to push content to center
    Spacer(modifier = Modifier.weight(1f))

    // Timer Display
    PixelTimerDisplay(
      time = DurationFormatter.formatDuration(recordingDuration),
      modifier = Modifier.padding(bottom = 32.dp),
    )

    // Animated wave visualization
    if (recordingState == RecordingState.RECORDING) {
      AnimatedWave(
        modifier =
          Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 32.dp),
      )
    } else {
      Spacer(modifier = Modifier.height(64.dp))
    }

    // Spacer to push controls to bottom
    Spacer(modifier = Modifier.weight(1f))

    // Recording Controls
    RecordingControlsPixel(
      recordingState = recordingState,
      onStartRecording = onStartRecording,
      onPauseRecording = onPauseRecording,
      onResumeRecording = onResumeRecording,
      onStopRecording = onStopRecording,
      onDiscardRecording = onDiscardRecording,
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Message cards
    MessageCards(
      errorMessage = errorMessage,
      successMessage = successMessage,
    )
  }
}
