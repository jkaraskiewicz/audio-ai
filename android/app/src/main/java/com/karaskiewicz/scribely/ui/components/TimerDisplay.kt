package com.karaskiewicz.scribely.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.ui.theme.UIConfig
import kotlinx.coroutines.delay

/**
 * 🎨 DEVELOPER-FRIENDLY: Timer Display Component
 *
 * Single Responsibility: Shows recording duration with animation
 * Easy to customize: All styling and behavior from UIConfig
 *
 * Quick Customization Guide:
 * - Timer size: UIConfig.Sizing.TimerTextSize
 * - Timer color: UIConfig.Colors.TimerTextColor
 * - Font: UIConfig.Typography.TimerLetterSpacing
 * - Animation: tick scale animation (1.0 to 1.04)
 * - Wave position: UIConfig.Spacing.TimerSectionHeight
 */
@Composable
fun TimerDisplay(
  recordingDuration: Long,
  recordingState: RecordingState,
  showWave: Boolean = true,
  modifier: Modifier = Modifier,
) {
  // 🎨 Timer Tick Animation (Uncle Bob: Single responsibility for animation)
  val timerScale = useTimerTickAnimation(recordingState, recordingDuration)

  Box(
    modifier = modifier.height(UIConfig.Spacing.TimerSectionHeight),
    contentAlignment = Alignment.Center,
  ) {
    // 🎨 Animated wave (shown above timer during recording)
    if (showWave && recordingState == RecordingState.RECORDING) {
      AnimatedWave(
        modifier = Modifier.align(Alignment.TopCenter),
      )
    }

    // 🎨 Large timer display
    Text(
      text = formatDuration(recordingDuration),
      style = MaterialTheme.typography.displayLarge.copy(
        fontSize = UIConfig.Sizing.TimerTextSize,
        fontWeight = FontWeight.Light,
        letterSpacing = UIConfig.Typography.TimerLetterSpacing,
        color = UIConfig.Colors.TimerTextColor,
      ),
      textAlign = TextAlign.Center,
      modifier = Modifier.scale(timerScale),
    )
  }
}

/**
 * 🎨 DEVELOPER-FRIENDLY: Timer Animation Hook
 * Single Responsibility: Handles only the tick animation logic
 * Easy to modify: Change scale values, duration, or conditions
 */
@Composable
private fun useTimerTickAnimation(
  recordingState: RecordingState,
  recordingDuration: Long,
): Float {
  var tickTrigger by remember { mutableIntStateOf(0) }
  val timerScale by animateFloatAsState(
    targetValue = if (tickTrigger % 2 == 0) 1f else 1.04f, // 🎨 Customize scale here
    animationSpec = tween(200), // 🎨 Customize duration here
    finishedListener = { if (recordingState == RecordingState.RECORDING) tickTrigger++ },
    label = "timerTick",
  )

  // Trigger timer animation every second during recording
  LaunchedEffect(recordingState, recordingDuration) {
    if (recordingState == RecordingState.RECORDING) {
      delay(1000) // 🎨 Customize tick interval here
      tickTrigger++
    }
  }

  return timerScale
}

/**
 * 🎨 DEVELOPER-FRIENDLY: Duration Formatter
 * Single Responsibility: Formats time display
 * Easy to modify: Change format (add hours, milliseconds, etc.)
 */
private fun formatDuration(totalSeconds: Long): String {
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return String.format("%02d:%02d", minutes, seconds) // 🎨 Customize format here
}
