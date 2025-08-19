package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaskiewicz.scribely.ui.theme.UIConfig

/**
 * 🎨 DEVELOPER-FRIENDLY: App Header Component
 *
 * Single Responsibility: Shows app logo and settings button
 * Easy to customize: Logo position, settings icon, spacing
 *
 * Quick Customization Guide:
 * - Header height: UIConfig.Spacing.HeaderHeight
 * - Logo position: Change alignment (CenterStart = left, Center = center)
 * - Settings icon: UIConfig.Sizing.SettingsIconSize
 * - Padding: UIConfig.Spacing.LogoPadding
 * - Colors: All from ScribelyLogo and UIConfig.Colors
 */
@Composable
fun AppHeader(
  onSettingsClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(UIConfig.Spacing.HeaderHeight),
  ) {
    // 🎨 Logo positioning (EASY TO CHANGE)
    // To center logo: change to Alignment.Center
    // To move right: change to Alignment.CenterEnd
    ScribelyLogo(
      modifier = Modifier
        .align(Alignment.CenterStart) // 🎨 CUSTOMIZE: Logo position here
        .padding(start = UIConfig.Spacing.LogoPadding),
    )

    // 🎨 Settings button positioning (EASY TO CHANGE)
    // To move left: change to Alignment.CenterStart
    // To hide: comment out this section
    IconButton(
      onClick = onSettingsClick,
      modifier = Modifier
        .align(Alignment.CenterEnd) // 🎨 CUSTOMIZE: Settings position here
        .padding(end = UIConfig.Spacing.LogoPadding),
    ) {
      Icon(
        imageVector = Icons.Default.Settings, // 🎨 CUSTOMIZE: Change icon here
        contentDescription = "Settings",
        tint = UIConfig.Colors.SecondaryTextColor,
        modifier = Modifier.size(UIConfig.Sizing.SettingsIconSize),
      )
    }
  }
}
