package com.karaskiewicz.audioai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karaskiewicz.audioai.ui.screen.SettingsScreen
import com.karaskiewicz.audioai.ui.theme.AudioAITheme

class SettingsActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      AudioAITheme {
        SettingsScreen(
          onNavigateBack = { finish() }
        )
      }
    }
  }
}