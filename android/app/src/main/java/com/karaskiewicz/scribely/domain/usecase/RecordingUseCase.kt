package com.karaskiewicz.scribely.domain.usecase

import android.content.Context
import com.karaskiewicz.scribely.domain.model.RecordingResult
import com.karaskiewicz.scribely.domain.model.UploadResult
import com.karaskiewicz.scribely.domain.repository.RecordingRepository
import com.karaskiewicz.scribely.domain.service.AudioComposer
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.domain.service.MediaRecorderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Use case for managing audio recording operations
 * Follows Clean Architecture and Single Responsibility Principle
 *
 * Orchestrates recording workflow by delegating to:
 * - RecordingStateMachine: state management
 * - MediaRecorderController: MediaRecorder lifecycle
 * - RecordingFileHandler: file operations
 */
class RecordingUseCase(
  private val recordingRepository: RecordingRepository,
  mediaRecorderFactory: MediaRecorderFactory,
  fileManager: FileManager,
) {
  private val stateMachine = RecordingStateMachine()
  private val recorderController = MediaRecorderController(mediaRecorderFactory)
  private val fileHandler = RecordingFileHandler(fileManager)

  /**
   * Starts a new recording session
   */
  fun startRecording(): RecordingResult =
    try {
      resetRecording()
      val outputFile = fileHandler.createRecordingFile()
      val mediaRecorder = recorderController.createAndStart(outputFile)
      stateMachine.transitionToRecording(mediaRecorder, outputFile)
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to start recording")
      resetRecording()
      RecordingResult.Error("Failed to start recording: ${e.message}", e)
    }

  /**
   * Pauses the current recording
   */
  fun pauseRecording(): RecordingResult =
    when (val currentState = stateMachine.getCurrentState()) {
      is RecordingState.Recording -> {
        val result = recorderController.pause(currentState.mediaRecorder)
        if (result is RecordingResult.Success) {
          stateMachine.transitionToPaused(currentState.mediaRecorder, currentState.outputFile)
        }
        result
      }
      else -> {
        Timber.w("Attempted to pause recording when not in recording state")
        RecordingResult.Error("No active recording to pause")
      }
    }

  /**
   * Resumes the current recording
   */
  fun resumeRecording(context: Context): RecordingResult =
    when (val currentState = stateMachine.getCurrentState()) {
      is RecordingState.Paused -> {
        val result = recorderController.resume(currentState.mediaRecorder)
        if (result is RecordingResult.Success) {
          stateMachine.transitionToRecording(currentState.mediaRecorder, currentState.outputFile)
        }
        result
      }
      else -> {
        Timber.w("Attempted to resume recording when not in paused state")
        RecordingResult.Error("No paused recording to resume")
      }
    }

  /**
   * Finishes recording and returns the final file
   */
  fun finishRecording(): RecordingResult =
    when (val currentState = stateMachine.getCurrentState()) {
      is RecordingState.Recording, is RecordingState.Paused -> {
        val (mediaRecorder, outputFile) = extractRecorderAndFile(currentState)
        try {
          recorderController.stopAndRelease(mediaRecorder)
          val validationResult = fileHandler.validateRecordingFile(outputFile)
          if (validationResult is RecordingResult.Success) {
            stateMachine.transitionToFinished(outputFile)
          } else {
            stateMachine.transitionToIdle()
          }
          validationResult
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

  /**
   * Uploads the completed recording
   */
  suspend fun uploadRecording(): UploadResult {
    return when (val currentState = stateMachine.getCurrentState()) {
      is RecordingState.Finished -> {
        val recordingFile = currentState.outputFile
        if (!recordingFile.exists()) {
          Timber.e("Upload failed - recording file no longer exists")
          return UploadResult.Error("Recording file no longer exists")
        }

        Timber.d(
          "Starting file conversion for upload: ${recordingFile.absolutePath}, " +
            "size: ${recordingFile.length()} bytes",
        )

        val uploadFile = fileHandler.createUploadFile()
        val conversionSuccess = convertRecordingToM4A(recordingFile, uploadFile)

        if (!conversionSuccess || !uploadFile.exists()) {
          Timber.e("Failed to convert recording to M4A format")
          fileHandler.deleteFileIfExists(uploadFile)
          UploadResult.Error("Failed to prepare recording for upload")
        } else {
          Timber.d(
            "Conversion successful, uploading file: ${uploadFile.absolutePath}, " +
              "size: ${uploadFile.length()} bytes",
          )

          val result = recordingRepository.uploadRecording(uploadFile)
          cleanupAfterUpload(uploadFile, recordingFile, result)
          result
        }
      }
      else -> {
        Timber.e("Upload failed - no finished recording available")
        UploadResult.Error("No finished recording available for upload")
      }
    }
  }

  /**
   * Resets the recording state and cleans up resources
   */
  fun resetRecording() {
    when (val currentState = stateMachine.getCurrentState()) {
      is RecordingState.Recording -> recorderController.safeRelease(currentState.mediaRecorder)
      is RecordingState.Paused -> recorderController.safeRelease(currentState.mediaRecorder)
      else -> {} // No cleanup needed
    }
    stateMachine.transitionToIdle()
  }

  // Private helper methods

  private fun extractRecorderAndFile(state: RecordingState): Pair<android.media.MediaRecorder, java.io.File> =
    when (state) {
      is RecordingState.Recording -> state.mediaRecorder to state.outputFile
      is RecordingState.Paused -> state.mediaRecorder to state.outputFile
      else -> throw IllegalStateException("Unexpected state")
    }

  private suspend fun convertRecordingToM4A(
    recordingFile: java.io.File,
    uploadFile: java.io.File,
  ): Boolean =
    withContext(Dispatchers.IO) {
      try {
        val audioComposer = AudioComposer()
        audioComposer.convertToM4A(recordingFile, uploadFile)
      } catch (e: Exception) {
        Timber.e(e, "Exception during audio conversion")
        false
      }
    }

  private fun cleanupAfterUpload(
    uploadFile: java.io.File,
    recordingFile: java.io.File,
    result: UploadResult,
  ) {
    fileHandler.deleteFileIfExists(uploadFile)
    if (result is UploadResult.UploadSuccess) {
      fileHandler.deleteFileIfExists(recordingFile)
      stateMachine.transitionToIdle()
    }
  }
}
