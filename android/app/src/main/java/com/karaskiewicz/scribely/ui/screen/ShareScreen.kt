package com.karaskiewicz.scribely.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.androidx.compose.koinViewModel
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme
import com.karaskiewicz.scribely.ui.viewmodel.ShareState
import com.karaskiewicz.scribely.ui.viewmodel.ShareViewModel
import kotlinx.coroutines.delay

@Composable
fun ShareScreen(
  intent: android.content.Intent,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
  viewModel: ShareViewModel = koinViewModel(),
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
  val animationScale by animateFloatAsState(
    targetValue = 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow,
    ),
    label = "dialogScale",
  )

  val backgroundAlpha by animateFloatAsState(
    targetValue = 0.6f,
    animationSpec = tween(300),
    label = "backgroundAlpha",
  )

  Dialog(
    onDismissRequest = if (!state.isLoading) onDismiss else { {} },
    properties = DialogProperties(
      dismissOnBackPress = !state.isLoading,
      dismissOnClickOutside = !state.isLoading,
    ),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = backgroundAlpha)),
      contentAlignment = Alignment.Center,
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp)
          .scale(animationScale),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface,
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // Status Icon with Animation
          AnimatedVisibility(
            visible = true,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
          ) {
            StatusIcon(state = state)
          }

          // Title - much more compact
          Text(
            text = when {
              state.isSuccess -> "Complete!"
              state.error != null -> "Error"
              else -> "Processing..."
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
          )

          // Progress Indicator - smaller
          AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.5.dp,
              color = MaterialTheme.colorScheme.primary,
            )
          }

          // Message - more compact
          Text(
            text = state.error ?: state.message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = when {
              state.error != null -> MaterialTheme.colorScheme.error
              state.isSuccess -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.1,
          )

          // Action Buttons
          AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn(tween(400)) + scaleIn(spring()),
            exit = fadeOut() + scaleOut(),
          ) {
            ActionButtons(
              state = state,
              onDismiss = onDismiss,
              onNavigateToSettings = onNavigateToSettings,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatusIcon(state: ShareState) {
  val icon = when {
    state.isSuccess -> Icons.Default.CheckCircle
    state.error != null -> Icons.Default.Error
    else -> null
  }

  val iconColor = when {
    state.isSuccess -> Color(0xFF4CAF50)
    state.error != null -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.primary
  }

  icon?.let {
    Icon(
      imageVector = it,
      contentDescription = null,
      modifier = Modifier.size(32.dp),
      tint = iconColor,
    )
  }
}

@Composable
private fun ActionButtons(
  state: ShareState,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (state.error != null && state.error.contains("configure")) {
      Arrangement.spacedBy(8.dp)
    } else {
      Arrangement.Center
    },
  ) {
    if (state.error != null && state.error.contains("configure")) {
      FilledTonalButton(
        onClick = onNavigateToSettings,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          "Settings",
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }

    Button(
      onClick = onDismiss,
      modifier = if (state.error != null && state.error.contains("configure")) {
        Modifier.weight(1f)
      } else {
        Modifier
      },
      colors = ButtonDefaults.buttonColors(
        containerColor = if (state.isSuccess) {
          MaterialTheme.colorScheme.primary
        } else {
          MaterialTheme.colorScheme.secondary
        },
      ),
    ) {
      Text(
        text = if (state.isSuccess) "Done" else "Close",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium,
      )
    }
  }
}

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
