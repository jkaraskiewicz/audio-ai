package com.karaskiewicz.scribely.domain.usecase

import android.media.MediaRecorder
import com.karaskiewicz.scribely.domain.model.RecordingResult
import com.karaskiewicz.scribely.domain.service.MediaRecorderFactory
import timber.log.Timber
import java.io.File

/**
 * MediaRecorder controller - handles MediaRecorder lifecycle
 * Follows Single Responsibility Principle - only manages MediaRecorder
 */
internal class MediaRecorderController(
  private val mediaRecorderFactory: MediaRecorderFactory,
) {
  /**
   * Creates, prepares, and starts a MediaRecorder
   */
  fun createAndStart(outputFile: File): MediaRecorder {
    val recorder = mediaRecorderFactory.createMediaRecorder(outputFile)
    recorder.prepare()
    recorder.start()
    // Small delay to ensure recorder is fully started
    Thread.sleep(100)
    return recorder
  }

  /**
   * Pauses recording
   */
  fun pause(mediaRecorder: MediaRecorder): RecordingResult =
    try {
      mediaRecorder.pause()
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to pause recording")
      RecordingResult.Error("Failed to pause recording: ${e.message}", e)
    }

  /**
   * Resumes recording
   */
  fun resume(mediaRecorder: MediaRecorder): RecordingResult =
    try {
      mediaRecorder.resume()
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to resume recording")
      RecordingResult.Error("Failed to resume recording: ${e.message}", e)
    }

  /**
   * Stops and releases MediaRecorder
   */
  fun stopAndRelease(mediaRecorder: MediaRecorder) {
    try {
      mediaRecorder.stop()
    } catch (stopException: Exception) {
      Timber.e(stopException, "Error stopping MediaRecorder")
    }
    mediaRecorder.release()
    // Wait for file system to sync
    Thread.sleep(200)
  }

  /**
   * Safely releases MediaRecorder without throwing exceptions
   */
  fun safeRelease(mediaRecorder: MediaRecorder) {
    runCatching {
      mediaRecorder.stop()
      mediaRecorder.release()
    }.onFailure { e ->
      Timber.w(e, "Error stopping recorder during reset")
    }
  }
}
