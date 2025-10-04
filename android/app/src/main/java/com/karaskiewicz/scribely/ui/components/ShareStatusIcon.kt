package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.viewmodel.ShareState

/**
 * Status icon composable - displays success/error icon based on state
 * Follows Single Responsibility Principle - only handles status icon presentation
 */
@Composable
fun ShareStatusIcon(state: ShareState) {
  val icon =
    when {
      state.isSuccess -> Icons.Default.CheckCircle
      state.error != null -> Icons.Default.Error
      else -> null
    }

  val iconColor =
    when {
      state.isSuccess -> Color(0xFF4CAF50) // Keep success green
      state.error != null -> UIConfig.Colors.ScribelyRed
      else -> UIConfig.Colors.ScribelyRed
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
