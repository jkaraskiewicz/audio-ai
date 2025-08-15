package com.karaskiewicz.audioai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karaskiewicz.audioai.ui.screen.ShareScreen
import com.karaskiewicz.audioai.ui.theme.AudioAITheme

class ShareActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      AudioAITheme {
        ShareScreen(
          intent = intent,
          onDismiss = { finish() },
          onNavigateToSettings = {
            startActivity(Intent(this@ShareActivity, SettingsActivity::class.java))
            finish()
          }
        )
      }
    }
  }
}