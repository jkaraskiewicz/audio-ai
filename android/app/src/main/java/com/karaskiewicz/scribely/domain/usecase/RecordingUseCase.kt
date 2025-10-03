package com.karaskiewicz.scribely.domain.usecase

import android.content.Context
import android.media.MediaRecorder
import com.karaskiewicz.scribely.domain.model.RecordingResult
import com.karaskiewicz.scribely.domain.model.UploadResult
import com.karaskiewicz.scribely.domain.repository.RecordingRepository
import com.karaskiewicz.scribely.domain.service.AudioComposer
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.domain.service.MediaRecorderFactory
import java.io.File
import timber.log.Timber

/**
 * Use case for managing audio recording operations.
 * Encapsulates business logic and follows Clean Architecture principles.
 */
class RecordingUseCase(
  private val recordingRepository: RecordingRepository,
  private val mediaRecorderFactory: MediaRecorderFactory,
  private val fileManager: FileManager,
) {
  private sealed class RecordingState {
    object Idle : RecordingState()

    data class Recording(val mediaRecorder: MediaRecorder, val outputFile: File) : RecordingState()

    data class Paused(val mediaRecorder: MediaRecorder, val outputFile: File) : RecordingState()

    data class Finished(val outputFile: File) : RecordingState()
  }

  private var state: RecordingState = RecordingState.Idle

  /**
   * Starts a new recording session.
   */
  fun startRecording(): RecordingResult {
    return try {
      resetRecording()
      val outputFile = fileManager.createRecordingFile()

      // Ensure parent directory exists
      outputFile.parentFile?.let { parentDir ->
        if (!parentDir.exists()) {
          parentDir.mkdirs()
        }
      }

      val mediaRecorder =
        try {
          val recorder = mediaRecorderFactory.createMediaRecorder(outputFile)
          recorder.prepare()
          recorder.start()
          recorder
        } catch (e: Exception) {
          Timber.e(e, "Failed to setup MediaRecorder")
          throw e
        }

      Thread.sleep(100)
      state = RecordingState.Recording(mediaRecorder, outputFile)
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to start recording")
      resetRecording()
      RecordingResult.Error("Failed to start recording: ${e.message}", e)
    }
  }

  /**
   * Pauses the current recording using MediaRecorder's native pause() method.
   */
  fun pauseRecording(): RecordingResult =
    when (val currentState = state) {
      is RecordingState.Recording ->
        try {
          currentState.mediaRecorder.pause()
          state = RecordingState.Paused(currentState.mediaRecorder, currentState.outputFile)
          RecordingResult.Success
        } catch (e: Exception) {
          Timber.e(e, "Failed to pause recording")
          RecordingResult.Error("Failed to pause recording: ${e.message}", e)
        }
      else -> {
        Timber.w("Attempted to pause recording when not in recording state")
        RecordingResult.Error("No active recording to pause")
      }
    }

  /**
   * Resumes the current recording using MediaRecorder's native resume() method.
   */
  fun resumeRecording(context: Context): RecordingResult =
    when (val currentState = state) {
      is RecordingState.Paused ->
        try {
          currentState.mediaRecorder.resume()
          state = RecordingState.Recording(currentState.mediaRecorder, currentState.outputFile)
          RecordingResult.Success
        } catch (e: Exception) {
          Timber.e(e, "Failed to resume recording")
          RecordingResult.Error("Failed to resume recording: ${e.message}", e)
        }
      else -> {
        Timber.w("Attempted to resume recording when not in paused state")
        RecordingResult.Error("No paused recording to resume")
      }
    }

  /**
   * Finishes recording and returns the final file.
   */
  fun finishRecording(): RecordingResult {
    return when (val currentState = state) {
      is RecordingState.Recording, is RecordingState.Paused -> {
        val outputFile =
          when (currentState) {
            is RecordingState.Recording -> currentState.outputFile
            is RecordingState.Paused -> currentState.outputFile
            else -> throw IllegalStateException("Unexpected state")
          }
        val mediaRecorder =
          when (currentState) {
            is RecordingState.Recording -> currentState.mediaRecorder
            is RecordingState.Paused -> currentState.mediaRecorder
            else -> throw IllegalStateException("Unexpected state")
          }

        try {
          // Stop and release MediaRecorder safely
          try {
            mediaRecorder.stop()
          } catch (stopException: Exception) {
            Timber.e(stopException, "Error stopping MediaRecorder")
            // Continue anyway - try to save what we have
          }

          mediaRecorder.release()

          // Wait a moment for file system to sync
          Thread.sleep(200)

          if (!outputFile.exists()) {
            Timber.e("Recording file not found: ${outputFile.absolutePath}")
            state = RecordingState.Idle
            return RecordingResult.Error("Recording file not found")
          }

          if (outputFile.length() == 0L) {
            Timber.e("Recording file is empty")
            state = RecordingState.Idle
            return RecordingResult.Error("Recording file is empty")
          }

          Timber.d("Recording finished successfully: ${outputFile.absolutePath}, size: ${outputFile.length()} bytes")
          state = RecordingState.Finished(outputFile)
          RecordingResult.Success
        } catch (e: Exception) {
          Timber.e(e, "Failed to finish recording")
          resetRecording()
          RecordingResult.Error("Failed to finish recording: ${e.message}", e)
        }
      }
      else -> {
        Timber.w("Attempted to finish recording when not recording")
        RecordingResult.Error("No active recording to finish")
      }
    }
  }

  /**
   * Uploads the completed recording.
   * Performs file conversion on IO dispatcher to avoid blocking.
   */
  suspend fun uploadRecording(): UploadResult {
    return when (val currentState = state) {
      is RecordingState.Finished -> {
        val recordingFile = currentState.outputFile
        if (!recordingFile.exists()) {
          Timber.e("Upload failed - recording file no longer exists")
          return UploadResult.Error("Recording file no longer exists")
        }

        Timber.d("Starting file conversion for upload: ${recordingFile.absolutePath}, size: ${recordingFile.length()} bytes")

        // Convert to M4A for upload on IO dispatcher
        val uploadFile = fileManager.createUploadFile()
        val audioComposer = AudioComposer()

        val conversionSuccess = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
          try {
            audioComposer.convertToM4A(recordingFile, uploadFile)
          } catch (e: Exception) {
            Timber.e(e, "Exception during audio conversion")
            false
          }
        }

        if (!conversionSuccess || !uploadFile.exists()) {
          Timber.e("Failed to convert recording to M4A format")
          fileManager.deleteFileIfExists(uploadFile)
          return UploadResult.Error("Failed to prepare recording for upload")
        }

        Timber.d("Conversion successful, uploading file: ${uploadFile.absolutePath}, size: ${uploadFile.length()} bytes")

        val result = recordingRepository.uploadRecording(uploadFile)

        // Cleanup files
        fileManager.deleteFileIfExists(uploadFile)
        if (result is UploadResult.UploadSuccess) {
          fileManager.deleteFileIfExists(recordingFile)
          state = RecordingState.Idle
        }
        result
      }
      else -> {
        Timber.e("Upload failed - no finished recording available")
        UploadResult.Error("No finished recording available for upload")
      }
    }
  }

  /**
   * Resets the recording state and cleans up resources.
   */
  fun resetRecording() {
    when (val currentState = state) {
      is RecordingState.Recording -> {
        runCatching {
          currentState.mediaRecorder.stop()
          currentState.mediaRecorder.release()
        }.onFailure { e ->
          Timber.w(e, "Error stopping recorder during reset")
        }
      }
      is RecordingState.Paused -> {
        runCatching {
          currentState.mediaRecorder.stop()
          currentState.mediaRecorder.release()
        }.onFailure { e ->
          Timber.w(e, "Error stopping recorder during reset")
        }
      }
      else -> {
        // No cleanup needed for Idle or Finished states
      }
    }
    state = RecordingState.Idle
  }
}
