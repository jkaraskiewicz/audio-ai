package com.karaskiewicz.scribely.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.karaskiewicz.scribely.ui.screen.MainScreen
import com.karaskiewicz.scribely.ui.theme.ScribelyTheme

class MainActivity : ComponentActivity() {
  // Permission launcher
  private val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
      if (!isGranted) {
        // Show error message if permission denied
        // You could show a dialog here explaining why the permission is needed
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Check and request audio recording permission
    checkAndRequestAudioPermission()

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

  private fun checkAndRequestAudioPermission() {
    when {
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO,
      ) == PackageManager.PERMISSION_GRANTED -> {
        // Permission already granted
      }
      else -> {
        // Request permission
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      }
    }
  }
}
