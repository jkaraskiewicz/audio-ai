package com.karaskiewicz.audioai.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaskiewicz.audioai.ui.theme.UIConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Test connection status enum
sealed class TestStatus {
  object Idle : TestStatus()
  object Testing : TestStatus()
  object Success : TestStatus()
  object Error : TestStatus()
}

@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit = {},
) {
  var serverUrl by remember { mutableStateOf("https://api.example.com/upload") }
  var saveDir by remember { mutableStateOf("/storage/emulated/0/Scribely/") }
  var testStatus by remember { mutableStateOf<TestStatus>(TestStatus.Idle) }
  var saveStatus by remember { mutableStateOf("") }

  val scope = rememberCoroutineScope()

  // Helper function to handle test connection
  fun handleTestConnection() {
    scope.launch {
      testStatus = TestStatus.Testing
      delay(1500) // Simulate API call
      testStatus = if (Math.random() > 0.3) TestStatus.Success else TestStatus.Error
      delay(2000) // Show result
      testStatus = TestStatus.Idle
    }
  }

  // Helper function to handle save
  fun handleSave() {
    scope.launch {
      saveStatus = "Settings saved!"
      delay(2000)
      saveStatus = ""
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(UIConfig.Colors.WhiteBackground)
      .padding(UIConfig.Spacing.ScreenPadding),
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(UIConfig.Spacing.HeaderHeight)
          .padding(bottom = UIConfig.Spacing.MediumSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UIConfig.Spacing.MediumSpacing),
      ) {
        IconButton(
          onClick = onNavigateBack,
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = UIConfig.Colors.SecondaryTextColor,
            modifier = Modifier.size(UIConfig.Sizing.SettingsIconSize),
          )
        }

        Text(
          text = "Settings",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = UIConfig.Colors.PrimaryTextColor,
          ),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
        )

        // Empty space for balance
        Spacer(modifier = Modifier.size(UIConfig.Sizing.SettingsIconSize + 16.dp))
      }

      // Settings content
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = UIConfig.Spacing.SettingsMaxWidth)
          .weight(1f)
          .padding(horizontal = UIConfig.Spacing.MediumSpacing),
        verticalArrangement = Arrangement.spacedBy(UIConfig.Spacing.SettingsVerticalSpacing),
      ) {
        // Server Endpoint Section
        Column(
          verticalArrangement = Arrangement.spacedBy(UIConfig.Spacing.SettingsInputSpacing),
        ) {
          Text(
            text = "Server Endpoint",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Normal,
              color = UIConfig.Colors.SecondaryTextColor,
            ),
          )

          Row(
            horizontalArrangement = Arrangement.spacedBy(UIConfig.Spacing.SettingsInputSpacing),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            OutlinedTextField(
              value = serverUrl,
              onValueChange = { serverUrl = it },
              modifier = Modifier.weight(1f),
              textStyle = MaterialTheme.typography.bodyMedium,
              singleLine = true,
              shape = RoundedCornerShape(UIConfig.Sizing.InputCornerRadius),
            )

            Button(
              onClick = { handleTestConnection() },
              enabled = testStatus == TestStatus.Idle,
              colors = ButtonDefaults.buttonColors(
                containerColor = UIConfig.Colors.ScribelyGray,
                contentColor = UIConfig.Colors.WhiteBackground,
              ),
              shape = RoundedCornerShape(UIConfig.Sizing.InputCornerRadius),
            ) {
              when (testStatus) {
                TestStatus.Testing -> {
                  CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = UIConfig.Colors.WhiteBackground,
                    strokeWidth = 2.dp,
                  )
                }
                else -> {
                  Text(
                    text = "Test",
                    style = MaterialTheme.typography.bodyMedium,
                  )
                }
              }
            }
          }

          // Status message
          when (testStatus) {
            TestStatus.Success -> {
              Text(
                text = "Connection successful!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF059669), // Green color
              )
            }
            TestStatus.Error -> {
              Text(
                text = "Connection failed.",
                style = MaterialTheme.typography.bodySmall,
                color = UIConfig.Colors.ScribelyRed,
              )
            }
            else -> {}
          }
        }

        // Save Directory Section
        Column(
          verticalArrangement = Arrangement.spacedBy(UIConfig.Spacing.SettingsInputSpacing),
        ) {
          Text(
            text = "Save Directory",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Normal,
              color = UIConfig.Colors.SecondaryTextColor,
            ),
          )

          Row(
            horizontalArrangement = Arrangement.spacedBy(UIConfig.Spacing.SettingsInputSpacing),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            OutlinedTextField(
              value = saveDir,
              onValueChange = { saveDir = it },
              modifier = Modifier.weight(1f),
              textStyle = MaterialTheme.typography.bodyMedium,
              singleLine = true,
              shape = RoundedCornerShape(UIConfig.Sizing.InputCornerRadius),
            )

            IconButton(
              onClick = {
                // TODO: Trigger native directory picker
                println("Triggering native directory picker...")
              },
              modifier = Modifier
                .background(
                  color = UIConfig.Colors.DefaultBackground,
                  shape = RoundedCornerShape(UIConfig.Sizing.InputCornerRadius),
                )
                .border(
                  1.dp,
                  UIConfig.Colors.BorderColor,
                  RoundedCornerShape(UIConfig.Sizing.InputCornerRadius),
                )
                .size(UIConfig.Sizing.InputHeight),
            ) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Browse",
                tint = UIConfig.Colors.SecondaryTextColor,
                modifier = Modifier.size(20.dp),
              )
            }
          }
        }

        // Save Button
        Column(
          modifier = Modifier.padding(top = UIConfig.Spacing.LargeSpacing),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(UIConfig.Spacing.MediumSpacing),
        ) {
          Button(
            onClick = { handleSave() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
              containerColor = UIConfig.Colors.ScribelyRed,
              contentColor = UIConfig.Colors.WhiteBackground,
            ),
            shape = RoundedCornerShape(UIConfig.Sizing.CardCornerRadius),
          ) {
            Text(
              text = "Save Settings",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
              ),
              modifier = Modifier.padding(vertical = UIConfig.Spacing.SmallSpacing),
            )
          }

          // Save status message
          if (saveStatus.isNotEmpty()) {
            Text(
              text = saveStatus,
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF059669), // Green color
              textAlign = TextAlign.Center,
            )
          }
        }
      }
    }
  }
}
