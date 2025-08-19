package com.karaskiewicz.scribely.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karaskiewicz.scribely.ui.screen.MainScreen
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      ScribelyTheme {
        MainScreen(
          onNavigateToSettings = {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
          },
        )
      }
    }
  }
}
