package com.karaskiewicz.scribely.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel
import com.karaskiewicz.scribely.ui.components.AppHeader
import com.karaskiewicz.scribely.ui.components.MessageCards
import com.karaskiewicz.scribely.ui.components.RecordingControls
import com.karaskiewicz.scribely.ui.components.TimerDisplay
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.viewmodel.MainViewModel

/**
 * 🎨 DEVELOPER-FRIENDLY: Clean Main Screen
 *
 * Uncle Bob's Principles Applied:
 * ✅ Single Responsibility: Each component handles one concern
 * ✅ DRY: No code duplication, reusable components
 * ✅ Clean Architecture: Clear separation of concerns
 *
 * Easy UI Customization:
 * - All styling controlled by UIConfig.kt
 * - Modular components for easy modifications
 * - Clear component boundaries
 *
 * Quick Customization Guide:
 * - Background: UIConfig.Colors.DefaultBackground
 * - Layout spacing: UIConfig.Spacing.*
 * - Component positioning: Modify alignment values below
 * - Add/remove features: Comment out component sections
 */
@Composable
fun MainScreen(
  onNavigateToSettings: () -> Unit = {},
  viewModel: MainViewModel = koinViewModel(),
) {
  // 🎨 State Management (Clean Architecture)
  val context = LocalContext.current
  val recordingState by viewModel.recordingState.collectAsState()
  val recordingDuration by viewModel.recordingDuration.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()

  // Initialize ViewModel configuration
  LaunchedEffect(Unit) {
    viewModel.loadConfiguration()
  }

  // 🎨 Permission Handling (Single Responsibility)
  val permissionLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
      if (isGranted) {
        viewModel.startRecording(context)
      } else {
        // TODO: Show permission error message
      }
    }

  // 🎨 Recording Actions (Clean Interface)
  val recordingActions =
    RecordingActions(
      onStart = {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)) {
          android.content.pm.PackageManager.PERMISSION_GRANTED -> {
            viewModel.startRecording(context)
          }
          else -> {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          }
        }
      },
      onPause = { viewModel.pauseRecording(context) },
      onResume = { viewModel.resumeRecording(context) },
      onStop = { viewModel.finishRecording(context) },
      onDiscard = { viewModel.resetRecording(context) },
    )

  // 🎨 Main Layout (Clean Structure)
  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .background(UIConfig.Colors.DefaultBackground) // 🎨 CUSTOMIZE: Background color
        .padding(UIConfig.Spacing.ScreenPadding),
    // 🎨 CUSTOMIZE: Screen padding
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
    ) {
      // 🎨 Header Section (Logo + Settings)
      AppHeader(onSettingsClick = onNavigateToSettings)

      // 🎨 Main Content (Timer + Controls)
      Column(
        modifier =
          Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        // 🎨 Timer Section (with animated wave)
        TimerDisplay(
          recordingDuration = recordingDuration,
          recordingState = recordingState,
        )

        Spacer(modifier = Modifier.height(UIConfig.Spacing.TimerBottomMargin))

        // 🎨 Controls Section
        RecordingControls(
          recordingState = recordingState,
          onStartRecording = recordingActions.onStart,
          onPauseRecording = recordingActions.onPause,
          onResumeRecording = recordingActions.onResume,
          onStopRecording = recordingActions.onStop,
          onDiscardRecording = recordingActions.onDiscard,
          modifier = Modifier.height(UIConfig.Spacing.ControlsHeight),
        )
      }

      // 🎨 Footer Space
      Spacer(modifier = Modifier.height(UIConfig.Spacing.FooterHeight))
    }

    // 🎨 Message Cards (Bottom overlay)
    MessageCards(
      errorMessage = errorMessage,
      successMessage = successMessage,
      modifier = Modifier.align(Alignment.BottomCenter),
    )
  }
}

/**
 * 🎨 DEVELOPER-FRIENDLY: Recording Actions Data Class
 * Single Responsibility: Groups all recording actions
 * Makes the component interface clean and testable
 */
private data class RecordingActions(
  val onStart: () -> Unit,
  val onPause: () -> Unit,
  val onResume: () -> Unit,
  val onStop: () -> Unit,
  val onDiscard: () -> Unit,
)
