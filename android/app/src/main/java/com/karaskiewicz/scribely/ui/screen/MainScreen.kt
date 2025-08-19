package com.karaskiewicz.scribely.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.components.AnimatedWave
import com.karaskiewicz.scribely.ui.components.MessageCards
import com.karaskiewicz.scribely.ui.components.PixelButton
import com.karaskiewicz.scribely.ui.components.PixelClickableText
import com.karaskiewicz.scribely.ui.components.PixelHeaderText
import com.karaskiewicz.scribely.ui.components.PixelTimerDisplay
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
  onNavigateToSettings: () -> Unit,
  viewModel: MainViewModel = koinViewModel(),
) {
  val context = LocalContext.current

  // Collect state from ViewModel
  val recordingState by viewModel.recordingState.collectAsState()
  val recordingDuration by viewModel.recordingDuration.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()

  // Initialize ViewModel configuration
  LaunchedEffect(Unit) {
    viewModel.loadConfiguration()
  }

  MainScreenContent(
    recordingState = recordingState,
    recordingDuration = recordingDuration,
    errorMessage = errorMessage,
    successMessage = successMessage,
    onNavigateToSettings = onNavigateToSettings,
    onStartRecording = { viewModel.startRecording(context) },
    onPauseRecording = { viewModel.pauseRecording(context) },
    onResumeRecording = { viewModel.resumeRecording(context) },
    onStopRecording = { viewModel.finishRecording(context) },
    onDiscardRecording = { viewModel.resetRecording(context) },
  )
}

@Composable
private fun RecordingControlsPixel(
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

private fun formatDuration(milliseconds: Long): String {
  val totalSeconds = milliseconds / 1000
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return String.format("%02d:%02d", minutes, seconds)
}

// Preview functions for Android Studio
@Preview(showBackground = true)
@Composable
fun MainScreenIdlePreview() {
  com.karaskiewicz.scribely.ui.theme.ScribelyTheme {
    MainScreenContent(
      recordingState = RecordingState.IDLE,
      recordingDuration = 0L,
      errorMessage = null,
      successMessage = null,
      onNavigateToSettings = {},
      onStartRecording = {},
      onPauseRecording = {},
      onResumeRecording = {},
      onStopRecording = {},
      onDiscardRecording = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenRecordingPreview() {
  com.karaskiewicz.scribely.ui.theme.ScribelyTheme {
    MainScreenContent(
      recordingState = RecordingState.RECORDING,
      // 2:05 minutes
      recordingDuration = 125000L,
      errorMessage = null,
      successMessage = null,
      onNavigateToSettings = {},
      onStartRecording = {},
      onPauseRecording = {},
      onResumeRecording = {},
      onStopRecording = {},
      onDiscardRecording = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPausedPreview() {
  com.karaskiewicz.scribely.ui.theme.ScribelyTheme {
    MainScreenContent(
      recordingState = RecordingState.PAUSED,
      // 1:07 minutes
      recordingDuration = 67000L,
      errorMessage = null,
      successMessage = null,
      onNavigateToSettings = {},
      onStartRecording = {},
      onPauseRecording = {},
      onResumeRecording = {},
      onStopRecording = {},
      onDiscardRecording = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenWithErrorPreview() {
  com.karaskiewicz.scribely.ui.theme.ScribelyTheme {
    MainScreenContent(
      recordingState = RecordingState.IDLE,
      recordingDuration = 0L,
      errorMessage = "Failed to start recording",
      successMessage = null,
      onNavigateToSettings = {},
      onStartRecording = {},
      onPauseRecording = {},
      onResumeRecording = {},
      onStopRecording = {},
      onDiscardRecording = {},
    )
  }
}

@Composable
private fun MainScreenContent(
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
  // Main container with pixel art background
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(UIConfig.PixelColors.Background)
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

    // Timer Display with pixel art styling
    PixelTimerDisplay(
      time = formatDuration(recordingDuration),
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

    // Recording Controls with pixel art buttons
    RecordingControlsPixel(
      recordingState = recordingState,
      onStartRecording = onStartRecording,
      onPauseRecording = onPauseRecording,
      onResumeRecording = onResumeRecording,
      onStopRecording = onStopRecording,
      onDiscardRecording = onDiscardRecording,
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Message cards for errors/success
    MessageCards(
      errorMessage = errorMessage,
      successMessage = successMessage,
    )
  }
}
