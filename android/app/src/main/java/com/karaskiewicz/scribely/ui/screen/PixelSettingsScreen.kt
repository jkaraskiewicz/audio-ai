package com.karaskiewicz.scribely.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaskiewicz.scribely.ui.components.PixelButton
import com.karaskiewicz.scribely.ui.components.PixelClickableText
import com.karaskiewicz.scribely.ui.components.PixelHeaderText
import com.karaskiewicz.scribely.ui.components.PixelTextField
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.theme.VT323FontFamily
import com.karaskiewicz.scribely.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PixelSettingsScreen(
  onNavigateBack: () -> Unit,
  viewModel: SettingsViewModel = koinViewModel(),
) {
  val serverUrl by viewModel.serverUrl.collectAsState()
  val connectionTestState by viewModel.connectionTestState.collectAsState()
  var editableServerUrl by remember { mutableStateOf("") }
  var saveDir by remember { mutableStateOf("/scribely/audio/") }

  // Update editable URL when serverUrl changes
  LaunchedEffect(serverUrl) {
    editableServerUrl = serverUrl
  }

  // Load settings on first composition
  LaunchedEffect(Unit) {
    viewModel.loadSettings()
  }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .background(UIConfig.PixelColors.Background)
        .padding(16.dp),
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PixelHeaderText(text = "Settings")
      PixelClickableText(
        text = "[Back]",
        onClick = onNavigateBack,
      )
    }

    Spacer(modifier = Modifier.height(40.dp))

    // Settings Items
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
      // Server URL Setting
      Column {
        PixelTextField(
          label = "Server URL:",
          value = editableServerUrl,
          onValueChange = { editableServerUrl = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Test Connection Button
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          PixelButton(
            text = "TEST",
            backgroundColor = UIConfig.PixelColors.ButtonBlue,
            shadowColor = UIConfig.PixelColors.ButtonBlueShadow,
            onClick = { viewModel.testConnection() },
            enabled = !connectionTestState.isLoading,
          )

          // Connection test status
          when {
            connectionTestState.isLoading -> {
              CircularProgressIndicator(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = UIConfig.PixelColors.TimerText,
              )
              Text(
                text = "Testing...",
                color = UIConfig.PixelColors.Text,
                fontSize = 20.sp,
                fontFamily = VT323FontFamily,
              )
            }
            connectionTestState.isSuccess == true -> {
              Text(
                text = "✓ Connected!",
                color = UIConfig.PixelColors.ButtonGreen,
                fontSize = 20.sp,
                fontFamily = VT323FontFamily,
              )
            }
            connectionTestState.error != null -> {
              Text(
                text = "✗ Failed",
                color = UIConfig.PixelColors.ButtonRed,
                fontSize = 20.sp,
                fontFamily = VT323FontFamily,
              )
            }
          }
        }

        // Error message
        connectionTestState.error?.let { error ->
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = error,
            color = UIConfig.PixelColors.ButtonRed,
            fontSize = 16.sp,
            fontFamily = VT323FontFamily,
          )
        }
      }

      // Save Directory Setting
      PixelTextField(
        label = "Save Directory:",
        value = saveDir,
        onValueChange = { saveDir = it },
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Save Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
      ) {
        PixelButton(
          text = "SAVE",
          backgroundColor = UIConfig.PixelColors.ButtonGreen,
          shadowColor = UIConfig.PixelColors.ButtonGreenShadow,
          onClick = { viewModel.updateServerUrl(editableServerUrl) },
        )
      }
    }
  }
}
