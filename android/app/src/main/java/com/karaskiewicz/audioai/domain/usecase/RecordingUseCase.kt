package com.karaskiewicz.audioai.domain.usecase

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.karaskiewicz.audioai.domain.model.RecordingConstants
import com.karaskiewicz.audioai.domain.model.RecordingResult
import com.karaskiewicz.audioai.domain.model.UploadResult
import com.karaskiewicz.audioai.domain.repository.RecordingRepository
import com.karaskiewicz.audioai.domain.service.FileManager
import com.karaskiewicz.audioai.domain.service.MediaRecorderFactory
import java.io.File

/**
 * Use case for managing audio recording operations.
 * Encapsulates business logic and follows Clean Architecture principles.
 */
class RecordingUseCase(
  private val recordingRepository: RecordingRepository,
  private val mediaRecorderFactory: MediaRecorderFactory = MediaRecorderFactory(),
  private val fileManager: FileManager = FileManager(),
) {

  private var mediaRecorder: MediaRecorder? = null
  private var outputFile: File? = null
  private val recordingSegments = mutableListOf<File>()
  private var finalRecordingFile: File? = null

  /**
   * Starts a new recording session.
   */
  fun startRecording(context: Context): RecordingResult {
    return try {
      // Reset state for new recording
      recordingSegments.clear()
      finalRecordingFile = null

      outputFile = fileManager.createRecordingFile(context)

      // Ensure parent directory exists
      outputFile!!.parentFile?.let { parentDir ->
        if (!parentDir.exists()) {
          parentDir.mkdirs()
        }
      }

      try {
        mediaRecorder = mediaRecorderFactory.createMediaRecorder(context, outputFile!!)
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
      } catch (e: Exception) {
        Log.e(RecordingConstants.LOG_TAG, "Failed to setup MediaRecorder", e)
        throw e
      }

      recordingSegments.add(outputFile!!)

      // Brief pause to ensure file is created
      Thread.sleep(100)
      RecordingResult.Success
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Failed to start recording", e)
      // Only cleanup if we actually created a recorder
      if (mediaRecorder != null) {
        cleanupRecorder()
      }
      // Reset state on failure
      recordingSegments.clear()
      outputFile = null
      RecordingResult.Error("Failed to start recording: ${e.message}", e)
    }
  }

  /**
   * Pauses the current recording.
   */
  fun pauseRecording(): RecordingResult {
    return try {
      mediaRecorder?.apply {
        stop()
        release()
      }
      mediaRecorder = null

      RecordingResult.Success
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Failed to pause recording", e)
      RecordingResult.Error("Failed to pause recording: ${e.message}", e)
    }
  }

  /**
   * Resumes recording by creating a new segment.
   */
  fun resumeRecording(context: Context): RecordingResult {
    return try {
      val segmentFile = fileManager.createSegmentFile(context)

      // Ensure parent directory exists
      segmentFile.parentFile?.let { parentDir ->
        if (!parentDir.exists()) {
          parentDir.mkdirs()
        }
      }

      mediaRecorder = mediaRecorderFactory.createMediaRecorder(context, segmentFile)
      mediaRecorder!!.prepare()
      mediaRecorder!!.start()
      recordingSegments.add(segmentFile)
      RecordingResult.Success
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Failed to resume recording", e)
      RecordingResult.Error("Failed to resume recording: ${e.message}", e)
    }
  }

  /**
   * Finishes recording and returns the final file.
   */
  fun finishRecording(): RecordingResult {
    return try {
      mediaRecorder?.apply {
        stop()
        release()
      }
      mediaRecorder = null

      // Wait a moment for file system to sync
      Thread.sleep(100)

      finalRecordingFile = if (recordingSegments.size > 1) {
        combineAudioSegments(recordingSegments)
      } else {
        recordingSegments.firstOrNull()
      }

      if (finalRecordingFile == null) {
        return if (recordingSegments.isEmpty()) {
          RecordingResult.Error("No recording segments found - recording may not have started properly")
        } else {
          RecordingResult.Error("Failed to process recording segments")
        }
      }

      if (!finalRecordingFile!!.exists()) {
        Log.e(RecordingConstants.LOG_TAG, "Recording file not found: ${finalRecordingFile!!.absolutePath}")
        return RecordingResult.Error("Recording file not found")
      }

      if (finalRecordingFile!!.length() == 0L) {
        Log.e(RecordingConstants.LOG_TAG, "Recording file is empty")
        return RecordingResult.Error("Recording file is empty")
      }
      RecordingResult.Success
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Failed to finish recording", e)
      cleanupRecorder()
      RecordingResult.Error("Failed to finish recording: ${e.message}", e)
    }
  }

  /**
   * Uploads the completed recording.
   */
  suspend fun uploadRecording(): UploadResult {
    return if (finalRecordingFile != null && finalRecordingFile!!.exists()) {
      val result = recordingRepository.uploadRecording(finalRecordingFile!!)

      // Clean up files after upload attempt
      if (result is UploadResult.UploadSuccess) {
        fileManager.deleteFileIfExists(finalRecordingFile)
      }
      cleanupSegments()
      finalRecordingFile = null

      result
    } else {
      Log.e(RecordingConstants.LOG_TAG, "Upload failed - no recording file available")
      UploadResult.Error("No recording file available for upload")
    }
  }

  /**
   * Resets the recording state and cleans up resources.
   */
  fun resetRecording() {
    cleanupRecorder()
    cleanupSegments()
    outputFile = null
    finalRecordingFile = null
  }

  private fun cleanupRecorder() {
    mediaRecorder?.apply {
      try {
        stop()
      } catch (e: Exception) {
        Log.w(RecordingConstants.LOG_TAG, "Error stopping recorder", e)
      }
      release()
    }
    mediaRecorder = null
  }

  private fun cleanupSegments() {
    fileManager.deleteFiles(recordingSegments)
    recordingSegments.clear()
  }

  private fun combineAudioSegments(segments: List<File>): File? {
    // For simplicity, we'll use the first segment as the final file
    // In a production app, you'd want to properly combine the audio segments
    // using FFmpeg or similar
    return segments.firstOrNull()
  }
}
