package com.karaskiewicz.scribely.domain.usecase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Use case for handling Android permissions
 * Follows Single Responsibility Principle - only handles permission checks
 */
class PermissionHandler {
  sealed class PermissionResult {
    object Granted : PermissionResult()

    data class Denied(val message: String) : PermissionResult()
  }

  /**
   * Checks if audio recording permission is granted
   */
  fun checkAudioRecordingPermission(context: Context): PermissionResult {
    return if (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO,
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      PermissionResult.Granted
    } else {
      PermissionResult.Denied("Audio recording permission is required")
    }
  }

  /**
   * Checks if a specific permission is granted
   */
  fun checkPermission(
    context: Context,
    permission: String,
  ): PermissionResult {
    return if (ContextCompat.checkSelfPermission(
        context,
        permission,
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      PermissionResult.Granted
    } else {
      PermissionResult.Denied("Permission $permission is required")
    }
  }
}
