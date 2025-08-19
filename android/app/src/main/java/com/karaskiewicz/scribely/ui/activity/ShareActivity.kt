package com.karaskiewicz.scribely.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karaskiewicz.scribely.ui.screen.ShareScreen
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme

class ShareActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      ScribelyTheme {
        ShareScreen(
          intent = intent,
          onDismiss = { finish() },
          onNavigateToSettings = {
            startActivity(Intent(this@ShareActivity, SettingsActivity::class.java))
            finish()
          },
        )
      }
    }
  }
}
