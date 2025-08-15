package com.karaskiewicz.audioai.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karaskiewicz.audioai.ui.theme.AudioAITheme
import com.karaskiewicz.audioai.ui.viewmodel.ShareState
import com.karaskiewicz.audioai.ui.viewmodel.ShareViewModel
import kotlinx.coroutines.delay

@Composable
fun ShareScreen(
  intent: android.content.Intent,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
  viewModel: ShareViewModel = viewModel(),
) {
  val context = LocalContext.current
  val shareState by viewModel.shareState.collectAsState()

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

  ShareDialog(
    state = shareState,
    onDismiss = onDismiss,
    onNavigateToSettings = onNavigateToSettings,
  )
}

@Composable
private fun ShareDialog(
  state: ShareState,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.8f)),
      contentAlignment = Alignment.Center,
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp)
          .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // Title
          Text(
            text = when {
              state.isSuccess -> "Success"
              state.error != null -> "Error"
              else -> "Processing Content"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
          )

          // Progress or Status
          if (state.isLoading) {
            CircularProgressIndicator()
          }

          // Message
          Text(
            text = state.error ?: state.message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (state.error != null && state.error.contains("configure")) {
              Arrangement.SpaceBetween
            } else {
              Arrangement.End
            },
          ) {
            if (state.error != null && state.error.contains("configure")) {
              Button(onClick = onNavigateToSettings) {
                Text("Settings")
              }
            }

            if (!state.isLoading) {
              TextButton(onClick = onDismiss) {
                Text("Close")
              }
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ShareDialogPreview() {
  AudioAITheme {
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
  AudioAITheme {
    ShareDialog(
      state = ShareState(error = "Please configure your server URL in settings first"),
      onDismiss = {},
      onNavigateToSettings = {},
    )
  }
}
