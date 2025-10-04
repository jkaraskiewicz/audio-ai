package com.karaskiewicz.scribely.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.components.MainScreenContent
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme
import com.karaskiewicz.scribely.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen - entry point for audio recording
 * Follows Single Responsibility Principle - only handles screen orchestration and lifecycle
 *
 * This file is now minimal and focused on screen-level concerns:
 * - ViewModel state collection
 * - Configuration initialization
 * - Delegation to MainScreenContent for presentation
 *
 * UI components extracted to:
 * - MainScreenContent.kt (screen layout and composition)
 * - RecordingControlsPixel.kt (recording button logic)
 * - DurationFormatter.kt (time formatting utility)
 */
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

  // Delegate to MainScreenContent for presentation
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

// Preview functions
@Preview(showBackground = true)
@Composable
fun MainScreenIdlePreview() {
  ScribelyTheme {
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
  ScribelyTheme {
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
  ScribelyTheme {
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
  ScribelyTheme {
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
