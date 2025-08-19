package com.karaskiewicz.scribely.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karaskiewicz.scribely.ui.screen.SettingsScreen
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme

class SettingsActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      ScribelyTheme {
        SettingsScreen(
          onNavigateBack = { finish() },
        )
      }
    }
  }
}
