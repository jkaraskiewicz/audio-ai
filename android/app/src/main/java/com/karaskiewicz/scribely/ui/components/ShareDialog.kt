package com.karaskiewicz.scribely.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.viewmodel.ShareState

/**
 * Share dialog composable - displays processing status with animations
 * Follows Single Responsibility Principle - only handles dialog presentation
 */
@Composable
fun ShareDialog(
  state: ShareState,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  val animationScale by animateFloatAsState(
    targetValue = 1f,
    animationSpec =
      spring(
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
    onDismissRequest =
      if (!state.isLoading) {
        onDismiss
      } else {
        {}
      },
    properties =
      DialogProperties(
        dismissOnBackPress = !state.isLoading,
        dismissOnClickOutside = !state.isLoading,
      ),
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = backgroundAlpha)),
      contentAlignment = Alignment.Center,
    ) {
      Surface(
        modifier =
          Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .scale(animationScale),
        shape = RoundedCornerShape(UIConfig.Sizing.CardCornerRadius),
        tonalElevation = 0.dp,
        shadowElevation = UIConfig.Sizing.ButtonElevation,
        color = UIConfig.Colors.WhiteBackground,
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // Status Icon
          AnimatedVisibility(
            visible = true,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
          ) {
            ShareStatusIcon(state = state)
          }

          // Title
          Text(
            text =
              when {
                state.isSuccess -> "Complete!"
                state.error != null -> "Error"
                else -> "Processing..."
              },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = UIConfig.Typography.BoldWeight,
            color = UIConfig.Colors.PrimaryTextColor,
            textAlign = TextAlign.Center,
          )

          // Progress Indicator
          AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.5.dp,
              color = UIConfig.Colors.ScribelyRed,
            )
          }

          // Message
          Text(
            text = state.error ?: state.message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color =
              when {
                state.error != null -> UIConfig.Colors.ScribelyRed
                state.isSuccess -> UIConfig.Colors.ScribelyGray
                else -> UIConfig.Colors.SecondaryTextColor
              },
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
          )

          // Action Buttons
          AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn(tween(400)) + scaleIn(spring()),
            exit = fadeOut() + scaleOut(),
          ) {
            ShareActionButtons(
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
