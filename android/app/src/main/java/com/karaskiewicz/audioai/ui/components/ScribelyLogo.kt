package com.karaskiewicz.audioai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karaskiewicz.audioai.ui.theme.UIConfig

@Composable
fun ScribelyLogo(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // Waveform graphic (scaled down version of design)
    Row(
      horizontalArrangement = Arrangement.spacedBy(1.dp),
      verticalAlignment = Alignment.Bottom,
    ) {
      // Bar 1
      Box(
        modifier = Modifier
          .width(3.dp)
          .height(8.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(UIConfig.Colors.ScribelyGray),
      )
      // Bar 2 (taller, red)
      Box(
        modifier = Modifier
          .width(3.dp)
          .height(20.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(UIConfig.Colors.ScribelyRed),
      )
      // Bar 3
      Box(
        modifier = Modifier
          .width(3.dp)
          .height(12.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(UIConfig.Colors.ScribelyGray),
      )
      // Bar 4 (tallest, red)
      Box(
        modifier = Modifier
          .width(3.dp)
          .height(26.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(UIConfig.Colors.ScribelyRed),
      )
      // Bar 5
      Box(
        modifier = Modifier
          .width(3.dp)
          .height(14.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(UIConfig.Colors.ScribelyGray),
      )
    }

    // "Scribely" text
    Text(
      text = "Scribely",
      style = MaterialTheme.typography.headlineLarge.copy(
        fontSize = UIConfig.Sizing.LogoTextSize,
        fontWeight = FontWeight.Bold,
        color = UIConfig.Colors.PrimaryTextColor,
      ),
    )
  }
}
