package com.karaskiewicz.audioai.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karaskiewicz.audioai.R
import com.karaskiewicz.audioai.ui.theme.AudioAITheme
import com.karaskiewicz.audioai.ui.theme.UIConfig
import com.karaskiewicz.audioai.domain.model.RecordingState
import com.karaskiewicz.audioai.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
  onNavigateToSettings: () -> Unit,
  viewModel: MainViewModel = viewModel(),
) {
  val context = LocalContext.current
  val serverUrl by viewModel.serverUrl.collectAsState()
  val isConfigured by viewModel.isConfigured.collectAsState()

  // Recording state
  val recordingState by viewModel.recordingState.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()
  val recordingDuration by viewModel.recordingDuration.collectAsState()

  // Permission handling
  val hasAudioPermission = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.RECORD_AUDIO,
  ) == PackageManager.PERMISSION_GRANTED

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { isGranted ->
    if (isGranted) {
      viewModel.startRecording(context)
    }
  }

  LaunchedEffect(context) {
    viewModel.loadConfiguration(context)
  }

  // Handle messages
  LaunchedEffect(successMessage) {
    successMessage?.let {
      delay(3000)
      viewModel.clearMessages()
    }
  }

  LaunchedEffect(errorMessage) {
    errorMessage?.let {
      delay(5000)
      viewModel.clearMessages()
    }
  }

  Surface(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.statusBars)
      .windowInsetsPadding(WindowInsets.navigationBars),
    color = if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
      UIConfig.Colors.RecordingBackground
    } else {
      MaterialTheme.colorScheme.background
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      // 🎨 CUSTOMIZABLE: Logo placement and styling
      ModernLogoText(
        modifier = Modifier.padding(bottom = UIConfig.Spacing.LogoPadding),
        position = UIConfig.LayoutPresets.currentLogoPosition,
      )

      // OnePlus-style timeline for recording/paused states
      if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
        OnePlusStyleTimeline(
          recordingDuration = recordingDuration,
          recordingState = recordingState,
        )
      } else {
        // Original layout for idle state
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          // Recording Status
          Text(
            text = when (recordingState) {
              RecordingState.IDLE -> "Tap to record"
              RecordingState.PROCESSING -> "Processing..."
              RecordingState.FINISHED -> "Complete"
              else -> ""
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 48.dp),
          )
        }
      }

      // OnePlus-style controls for recording states
      if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
        CleanRecordingControls(
          recordingState = recordingState,
          onPauseResumeClick = {
            if (recordingState == RecordingState.RECORDING) {
              viewModel.pauseRecording(context)
            } else {
              viewModel.resumeRecording(context)
            }
          },
          onStopClick = { viewModel.finishRecording(context) },
        )
      } else {
        // Original controls for idle/processing states
        Column(
          modifier = Modifier.padding(bottom = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Main record button (only for idle state)
          if (recordingState == RecordingState.IDLE || recordingState == RecordingState.FINISHED) {
            MainRecordButton(
              recordingState = recordingState,
              onStartRecording = {
                if (hasAudioPermission) {
                  viewModel.startRecording(context)
                } else {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
              },
            )
          }

          // Processing state - just show indicator, no FAB
          if (recordingState == RecordingState.PROCESSING) {
            Box(
              modifier = Modifier.size(72.dp),
              contentAlignment = Alignment.Center,
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
              )
            }
          }

          // Permission message
          if (!hasAudioPermission && recordingState == RecordingState.IDLE) {
            Text(
              text = stringResource(R.string.permission_required),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(top = 16.dp),
            )
          }
        }
      }

      // Settings Icon in top right corner (Pixel style)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
      ) {
        IconButton(
          onClick = onNavigateToSettings,
          modifier = Modifier.size(48.dp),
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      // 🎨 CUSTOMIZABLE: Error/Success Messages styling
      errorMessage?.let { message ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              horizontal = UIConfig.Spacing.MessageCardHorizontalPadding,
              vertical = UIConfig.Spacing.MessageCardVerticalPadding,
            ),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
          ),
          shape = RoundedCornerShape(UIConfig.Sizing.MessageCardCornerRadius),
        ) {
          Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(UIConfig.Spacing.MessageCardContentPadding),
            textAlign = TextAlign.Center,
          )
        }
      }

      successMessage?.let { message ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              horizontal = UIConfig.Spacing.MessageCardHorizontalPadding,
              vertical = UIConfig.Spacing.MessageCardVerticalPadding,
            ),
          colors = CardDefaults.cardColors(
            containerColor = UIConfig.Colors.ScribelyGreen.copy(alpha = 0.1f),
          ),
          shape = RoundedCornerShape(UIConfig.Sizing.MessageCardCornerRadius),
        ) {
          Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = UIConfig.Colors.ScribelyGreen,
            modifier = Modifier.padding(UIConfig.Spacing.MessageCardContentPadding),
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

private fun formatDuration(durationMs: Long): String {
  val seconds = (durationMs / 1000) % 60
  val minutes = (durationMs / (1000 * 60)) % 60
  val hours = (durationMs / (1000 * 60 * 60))

  return if (hours > 0) {
    String.format("%02d:%02d:%02d", hours, minutes, seconds)
  } else {
    String.format("%02d:%02d", minutes, seconds)
  }
}

@Composable
private fun RecordingControls(
  recordingState: RecordingState,
  onStartPauseClick: () -> Unit,
  onStopClick: () -> Unit,
) {
  if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 40.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      StartPauseButton(
        recordingState = recordingState,
        onClick = onStartPauseClick,
        modifier = Modifier.weight(1f),
      )

      StopSendButton(
        onClick = onStopClick,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun StartPauseButton(
  recordingState: RecordingState,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val startPauseScale by animateFloatAsState(
    targetValue = 1.0f,
    animationSpec = tween(200),
    label = "startPauseScale",
  )

  val startPauseColor by animateColorAsState(
    targetValue = if (recordingState == RecordingState.RECORDING) {
      MaterialTheme.colorScheme.tertiary
    } else {
      MaterialTheme.colorScheme.primary
    },
    animationSpec = tween(300),
    label = "startPauseColor",
  )

  Box(
    modifier = modifier
      .height(72.dp)
      .clip(RoundedCornerShape(36.dp))
      .background(startPauseColor)
      .clickable(
        indication = rememberRipple(),
        interactionSource = remember { MutableInteractionSource() },
      ) { onClick() }
      .scale(startPauseScale),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        imageVector = if (recordingState == RecordingState.RECORDING) {
          Icons.Default.Pause
        } else {
          Icons.Default.PlayArrow
        },
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(24.dp),
      )
      Text(
        text = if (recordingState == RecordingState.RECORDING) "Pause" else "Resume",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
      )
    }
  }
}

@Composable
private fun StopSendButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val stopScale by animateFloatAsState(
    targetValue = 1.0f,
    animationSpec = tween(200),
    label = "stopScale",
  )

  Box(
    modifier = modifier
      .height(72.dp)
      .clip(RoundedCornerShape(36.dp))
      .background(MaterialTheme.colorScheme.error)
      .clickable(
        indication = rememberRipple(),
        interactionSource = remember { MutableInteractionSource() },
      ) { onClick() }
      .scale(stopScale),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        imageVector = Icons.Default.Stop,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(24.dp),
      )
      Text(
        text = "Send",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
      )
    }
  }
}

@Composable
private fun MainRecordButton(
  recordingState: RecordingState,
  onStartRecording: () -> Unit,
) {
  if (recordingState == RecordingState.IDLE || recordingState == RecordingState.FINISHED) {
    val fabSize by animateFloatAsState(
      targetValue = 72f,
      animationSpec = tween(300),
      label = "fabSize",
    )

    val micScale by animateFloatAsState(
      targetValue = 1.0f,
      animationSpec = tween(200),
      label = "micScale",
    )

    Surface(
      modifier = Modifier
        .size(fabSize.dp)
        .clip(CircleShape)
        .clickable(
          indication = rememberRipple(),
          interactionSource = remember { MutableInteractionSource() },
        ) { onStartRecording() }
        .scale(micScale),
      color = MaterialTheme.colorScheme.primary,
      shadowElevation = 8.dp,
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
      ) {
        Icon(
          imageVector = Icons.Default.Mic,
          contentDescription = "Start Recording",
          tint = Color.White,
          modifier = Modifier.size(32.dp),
        )
      }
    }
  }
}

@Composable
private fun OnePlusStyleTimeline(
  recordingDuration: Long,
  recordingState: RecordingState,
) {
  if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(top = 40.dp),
    ) {
      // Top header with Cancel and Save functionality
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // 🎨 CUSTOMIZABLE: Cancel button styling
        Text(
          text = "Cancel",
          style = MaterialTheme.typography.bodyLarge,
          color = UIConfig.Colors.ScribelyRed,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.clickable {
            // Cancel recording functionality could be added here
            // For now, we'll just reset the recording
          },
        )

        // Recording indicator
        if (recordingState == RecordingState.RECORDING) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(Color(0xFFE53935), CircleShape),
            )
            Text(
              text = "REC",
              style = MaterialTheme.typography.bodyMedium,
              color = UIConfig.Colors.ScribelyRed,
              fontWeight = FontWeight.Bold,
            )
          }
        } else {
          Text(
            text = "PAUSED",
            style = MaterialTheme.typography.bodyMedium,
            color = UIConfig.Colors.PausedTextColor,
            fontWeight = FontWeight.Medium,
          )
        }

        // 🎨 CUSTOMIZABLE: Save button styling
        Text(
          text = "Save",
          style = MaterialTheme.typography.bodyLarge,
          color = if (recordingState == RecordingState.PAUSED) {
            UIConfig.Colors.ScribelyRed
          } else {
            UIConfig.Colors.PausedTextColor
          },
          fontWeight = FontWeight.Medium,
          modifier = Modifier.clickable {
            if (recordingState == RecordingState.PAUSED) {
              // Save functionality could be implemented here
              // For now, this just shows the recording can be saved when paused
            }
          },
        )
      }

      Spacer(modifier = Modifier.height(40.dp))

      // Timeline with time markers
      TimelineWithWaveform(recordingDuration = recordingDuration)

      Spacer(modifier = Modifier.height(60.dp))

      // 🎨 CUSTOMIZABLE: Timer display styling
      Text(
        text = formatDuration(recordingDuration, includeMilliseconds = true),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Light,
        color = UIConfig.Colors.TimerTextColor,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
      )
    }
  }
}

/**
 * 🎨 CUSTOMIZABLE TIMELINE COMPONENT
 *
 * Easy customization points:
 * - Width: Change UIConfig.Sizing.TimelineWidth
 * - Duration: Change UIConfig.Behavior.TimelineWindowDuration
 * - Waveform bars: Change UIConfig.Behavior.WaveformBarsCount
 * - Colors: Change UIConfig.Colors.WaveformColor and PlayheadColor
 */
@Composable
private fun TimelineWithWaveform(
  recordingDuration: Long,
  timelineWidth: androidx.compose.ui.unit.Dp = UIConfig.Sizing.TimelineWidth,
  maxDuration: Long = UIConfig.Behavior.TimelineWindowDuration,
  waveformBarsCount: Int = UIConfig.Behavior.WaveformBarsCount,
) {
  val currentPosition = (recordingDuration % maxDuration) / maxDuration.toFloat()

  Column {
    // Time markers
    Row(
      modifier = Modifier.width(timelineWidth),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      for (i in 0..4) {
        val timeSeconds = i * 2
        Text(
          text = String.format("%02d:%02d", timeSeconds / 60, timeSeconds % 60),
          style = MaterialTheme.typography.bodySmall,
          color = UIConfig.Colors.TimeMarkerColor,
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Waveform with timeline
    Box(
      modifier = Modifier
        .width(timelineWidth)
        .height(UIConfig.Sizing.TimelineHeight),
    ) {
      // Background dotted line
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .align(Alignment.Center)
          .background(UIConfig.Colors.DottedLineColor),
      )

      // Waveform bars
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center),
        horizontalArrangement = Arrangement.SpaceEvenly,
      ) {
        repeat(waveformBarsCount) { index ->
          val barHeight = remember { (8..60).random().dp }
          val animatedHeight by animateFloatAsState(
            targetValue = if (index < (currentPosition * waveformBarsCount).toInt()) barHeight.value else 4f,
            animationSpec = tween(200),
            label = "waveformBar$index",
          )

          Box(
            modifier = Modifier
              .width(UIConfig.Sizing.WaveformBarWidth)
              .height(animatedHeight.dp)
              .background(UIConfig.Colors.WaveformColor),
          )
        }
      }

      // Red playhead
      Box(
        modifier = Modifier
          .width(UIConfig.Sizing.PlayheadWidth)
          .fillMaxHeight()
          .background(UIConfig.Colors.PlayheadColor)
          .offset(x = (timelineWidth * currentPosition)),
      )
    }
  }
}

/**
 * 🎨 CUSTOMIZABLE RECORDING CONTROLS
 *
 * Easy customization points:
 * - Button sizes: Change UIConfig.Sizing.MainButtonSize
 * - Colors: Change UIConfig.Colors.ScribelyRed and ScribelyGreen
 * - Spacing: Change UIConfig.Spacing.ControlsHorizontalPadding
 */
@Composable
private fun CleanRecordingControls(
  recordingState: RecordingState,
  onPauseResumeClick: () -> Unit,
  onStopClick: () -> Unit,
  buttonSize: androidx.compose.ui.unit.Dp = UIConfig.Sizing.MainButtonSize,
  primaryColor: Color = UIConfig.Colors.ScribelyRed,
  secondaryColor: Color = UIConfig.Colors.ScribelyGreen,
) {
  if (recordingState == RecordingState.RECORDING || recordingState == RecordingState.PAUSED) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 40.dp)
        .padding(bottom = 40.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Main pause/play button
      Surface(
        modifier = Modifier
          .size(buttonSize)
          .clickable(
            indication = rememberRipple(),
            interactionSource = remember { MutableInteractionSource() },
          ) { onPauseResumeClick() },
        shape = CircleShape,
        color = primaryColor,
        shadowElevation = UIConfig.Sizing.ButtonElevation,
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize(),
        ) {
          Icon(
            imageVector = if (recordingState == RecordingState.RECORDING) {
              Icons.Default.Pause
            } else {
              Icons.Default.PlayArrow
            },
            contentDescription = if (recordingState == RecordingState.RECORDING) "Pause" else "Resume",
            tint = Color.White,
            modifier = Modifier.size(28.dp),
          )
        }
      }

      // Stop/Send button
      Surface(
        modifier = Modifier
          .size(buttonSize)
          .clickable(
            indication = rememberRipple(),
            interactionSource = remember { MutableInteractionSource() },
          ) { onStopClick() },
        shape = CircleShape,
        color = secondaryColor,
        shadowElevation = UIConfig.Sizing.ButtonElevation,
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize(),
        ) {
          Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop and Send",
            tint = Color.White,
            modifier = Modifier.size(28.dp),
          )
        }
      }
    }
  }
}

/**
 * 🎨 CUSTOMIZABLE LOGO COMPONENT
 *
 * Easy customization points:
 * - Colors: Change UIConfig.Colors.ScribelyRed
 * - Size: Change UIConfig.Sizing.LogoIconSize
 * - Position: Change UIConfig.LayoutPresets.currentLogoPosition
 * - Text: Modify the "cribely" text below
 */
@Composable
private fun ModernLogoText(
  modifier: Modifier = Modifier,
  position: UIConfig.LayoutPresets.LogoPosition = UIConfig.LayoutPresets.LogoPosition.TOP_CENTER,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = when (position) {
      UIConfig.LayoutPresets.LogoPosition.TOP_LEFT -> Arrangement.Start
      UIConfig.LayoutPresets.LogoPosition.TOP_RIGHT -> Arrangement.End
      UIConfig.LayoutPresets.LogoPosition.TOP_CENTER -> Arrangement.spacedBy(8.dp)
    },
  ) {
    // 🎨 CUSTOMIZABLE: Logo icon design
    Box(
      modifier = Modifier
        .size(UIConfig.Sizing.LogoIconSize)
        .background(
          UIConfig.Colors.ScribelyRed,
          RoundedCornerShape(UIConfig.Sizing.LogoIconCornerRadius),
        ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "S", // 🎨 CUSTOMIZABLE: Change logo letter here
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
      )
    }

    // 🎨 CUSTOMIZABLE: App name text
    Text(
      text = "cribely", // 🎨 CUSTOMIZABLE: Change app name here
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Light,
      color = MaterialTheme.colorScheme.onSurface,
      letterSpacing = UIConfig.Typography.LogoLetterSpacing,
    )
  }
}

private fun formatDuration(durationMs: Long, includeMilliseconds: Boolean = false): String {
  val totalMs = durationMs
  val seconds = (totalMs / 1000) % 60
  val minutes = (totalMs / (1000 * 60)) % 60
  val hours = (totalMs / (1000 * 60 * 60))
  val milliseconds = (totalMs % 1000) / 10 // Get two-digit milliseconds

  return if (includeMilliseconds) {
    if (hours > 0) {
      String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    } else {
      String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
  } else {
    if (hours > 0) {
      String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
      String.format("%02d:%02d", minutes, seconds)
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  AudioAITheme {
    MainScreen(
      onNavigateToSettings = {},
    )
  }
}
