package com.karaskiewicz.scribely.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.karaskiewicz.scribely.ui.theme.UIConfig

@Composable
fun AnimatedWave(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "waveAnimation")

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(UIConfig.Spacing.WaveSpacing),
    verticalAlignment = Alignment.Bottom,
  ) {
    // Create 5 animated bars with different delays
    repeat(5) { index ->
      val animatedHeight by infiniteTransition.animateFloat(
        initialValue = UIConfig.Sizing.WaveBarMinHeight.value,
        targetValue = UIConfig.Sizing.WaveBarMaxHeight.value,
        animationSpec =
          infiniteRepeatable(
            animation =
              tween(
                durationMillis = UIConfig.Animations.WaveAnimationDuration,
              ),
            repeatMode = RepeatMode.Reverse,
          ),
        label = "waveBar$index",
      )

      Box(
        modifier =
          Modifier
            .width(UIConfig.Sizing.WaveBarWidth)
            .height(animatedHeight.dp)
            .clip(RoundedCornerShape(UIConfig.Sizing.WaveBarCornerRadius))
            .background(UIConfig.Colors.WaveformColor),
      )
    }
  }
}
