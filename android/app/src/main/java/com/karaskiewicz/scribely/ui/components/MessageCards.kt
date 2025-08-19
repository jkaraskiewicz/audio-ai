package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.ui.theme.UIConfig

/**
 * 🎨 DEVELOPER-FRIENDLY: Message Cards Component
 *
 * Single Responsibility: Shows error and success messages
 * Easy to customize: Colors, spacing, corner radius, text style
 *
 * Quick Customization Guide:
 * - Card spacing: UIConfig.Spacing.MediumSpacing
 * - Content padding: UIConfig.Sizing.ContainerPadding
 * - Success color: UIConfig.Colors.ScribelyRedLight
 * - Text color: UIConfig.Colors.PrimaryTextColor
 * - Card elevation: Change the 4.dp value below
 */
@Composable
fun MessageCards(
  errorMessage: String? = null,
  successMessage: String? = null,
  modifier: Modifier = Modifier,
) {
  // 🎨 Error message card
  errorMessage?.let { message ->
    Card(
      modifier =
        modifier
          .fillMaxWidth()
          .padding(UIConfig.Spacing.MediumSpacing),
      colors =
        CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
      // 🎨 CUSTOMIZE: Elevation
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.padding(UIConfig.Sizing.ContainerPadding),
        textAlign = TextAlign.Center,
      )
    }
  }

  // 🎨 Success message card
  successMessage?.let { message ->
    Card(
      modifier =
        modifier
          .fillMaxWidth()
          .padding(UIConfig.Spacing.MediumSpacing),
      colors =
        CardDefaults.cardColors(
          // 🎨 CUSTOMIZE: Success color
          containerColor = UIConfig.Colors.ScribelyRedLight,
        ),
      // 🎨 CUSTOMIZE: Elevation
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        // 🎨 CUSTOMIZE: Text color
        color = UIConfig.Colors.PrimaryTextColor,
        modifier = Modifier.padding(UIConfig.Sizing.ContainerPadding),
        textAlign = TextAlign.Center,
      )
    }
  }
}

/**
 * 🎨 DEVELOPER-FRIENDLY: Individual Message Card Components
 * Use these for more granular control
 */
@Composable
fun ErrorCard(
  message: String,
  modifier: Modifier = Modifier,
  backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
  textColor: Color = MaterialTheme.colorScheme.onErrorContainer,
) {
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(UIConfig.Spacing.MediumSpacing),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium,
      color = textColor,
      modifier = Modifier.padding(UIConfig.Sizing.ContainerPadding),
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
fun SuccessCard(
  message: String,
  modifier: Modifier = Modifier,
  backgroundColor: Color = UIConfig.Colors.ScribelyRedLight,
  textColor: Color = UIConfig.Colors.PrimaryTextColor,
) {
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(UIConfig.Spacing.MediumSpacing),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium,
      color = textColor,
      modifier = Modifier.padding(UIConfig.Sizing.ContainerPadding),
      textAlign = TextAlign.Center,
    )
  }
}
