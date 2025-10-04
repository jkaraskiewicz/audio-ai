package com.karaskiewicz.scribely.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import com.karaskiewicz.scribely.ui.components.ShareDialog
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme
import com.karaskiewicz.scribely.ui.viewmodel.ShareState
import com.karaskiewicz.scribely.ui.viewmodel.ShareViewModel
import kotlinx.coroutines.delay

/**
 * Share screen - entry point for handling shared content
 * Follows Single Responsibility Principle - only handles screen orchestration and lifecycle
 *
 * This file is now minimal and focused on screen-level concerns:
 * - Intent handling
 * - ViewModel state collection
 * - Auto-dismiss logic
 * - Delegation to ShareDialog for presentation
 *
 * UI components extracted to:
 * - ShareDialog.kt (dialog presentation with animations)
 * - ShareStatusIcon.kt (status icon logic)
 * - ShareActionButtons.kt (action button logic)
 */
@Composable
fun ShareScreen(
  intent: android.content.Intent,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
  viewModel: ShareViewModel = koinViewModel(),
) {
  val context = LocalContext.current
  val shareState by viewModel.shareState.collectAsState()

  // Handle shared content on launch
  LaunchedEffect(intent) {
    viewModel.handleSharedContent(context, intent)
  }

  // Auto-dismiss after successful processing
  LaunchedEffect(shareState.isSuccess) {
    if (shareState.isSuccess) {
      delay(2000)
      onDismiss()
    }
  }

  // Delegate to ShareDialog for presentation
  ShareDialog(
    state = shareState,
    onDismiss = onDismiss,
    onNavigateToSettings = onNavigateToSettings,
  )
}

// Preview functions
@Preview(showBackground = true)
@Composable
fun ShareDialogPreview() {
  ScribelyTheme {
    ShareDialog(
      state = ShareState(isLoading = true, message = "Processing content..."),
      onDismiss = {},
      onNavigateToSettings = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
fun ShareDialogErrorPreview() {
  ScribelyTheme {
    ShareDialog(
      state = ShareState(error = "Please configure your server URL in settings first"),
      onDismiss = {},
      onNavigateToSettings = {},
    )
  }
}
