package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.viewmodel.ShareState

/**
 * Action buttons composable - displays close/settings buttons based on state
 * Follows Single Responsibility Principle - only handles action button presentation
 */
@Composable
fun ShareActionButtons(
  state: ShareState,
  onDismiss: () -> Unit,
  onNavigateToSettings: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement =
      if (state.error != null && state.error.contains("configure")) {
        Arrangement.spacedBy(8.dp)
      } else {
        Arrangement.Center
      },
  ) {
    // Settings button (only shown for configuration errors)
    if (state.error != null && state.error.contains("configure")) {
      FilledTonalButton(
        onClick = onNavigateToSettings,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(UIConfig.Sizing.ButtonCornerRadius),
        colors =
          ButtonDefaults.filledTonalButtonColors(
            containerColor = UIConfig.Colors.ScribelyGrayLight,
            contentColor = UIConfig.Colors.WhiteBackground,
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
          fontWeight = UIConfig.Typography.BoldWeight,
        )
      }
    }

    // Close/Done button
    Button(
      onClick = onDismiss,
      modifier =
        if (state.error != null && state.error.contains("configure")) {
          Modifier.weight(1f)
        } else {
          Modifier
        },
      shape = RoundedCornerShape(UIConfig.Sizing.ButtonCornerRadius),
      colors =
        ButtonDefaults.buttonColors(
          containerColor =
            if (state.isSuccess) {
              UIConfig.Colors.ScribelyRed
            } else {
              UIConfig.Colors.ScribelyGrayLight
            },
          contentColor = UIConfig.Colors.WhiteBackground,
        ),
    ) {
      Text(
        text = if (state.isSuccess) "Done" else "Close",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = UIConfig.Typography.BoldWeight,
      )
    }
  }
}
