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
  private var mediaRecorder: MediaRecorder? = null
  private var outputFile: File? = null
  private val recordingSegments = mutableListOf<File>()
  private var finalRecordingFile: File? = null

  /**
   * Starts a new recording session.
   */
  fun startRecording(): RecordingResult {
    return try {
      resetRecording()
      outputFile = fileManager.createRecordingFile()

      // Ensure parent directory exists
      outputFile!!.parentFile?.let { parentDir ->
        if (!parentDir.exists()) {
          parentDir.mkdirs()
        }
      }

      try {
        mediaRecorder = mediaRecorderFactory.createMediaRecorder(outputFile!!)
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
      } catch (e: Exception) {
        Timber.e(e, "Failed to setup MediaRecorder")
        throw e
      }

      recordingSegments.add(outputFile!!)

      Thread.sleep(100)
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to start recording")
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

  fun pauseRecording(): RecordingResult =
    try {
      mediaRecorder?.apply {
        stop()
        release()
      }
      mediaRecorder = null

      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to pause recording")
      RecordingResult.Error("Failed to pause recording: ${e.message}", e)
    }

  /**
   * Resumes recording by creating a new segment.
   */
  fun resumeRecording(context: Context): RecordingResult {
    return try {
      val segmentFile = fileManager.createSegmentFile()

      // Ensure parent directory exists
      segmentFile.parentFile?.let { parentDir ->
        if (!parentDir.exists()) {
          parentDir.mkdirs()
        }
      }

      mediaRecorder = mediaRecorderFactory.createMediaRecorder(segmentFile)
      mediaRecorder!!.prepare()
      mediaRecorder!!.start()
      recordingSegments.add(segmentFile)
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to resume recording")
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

      finalRecordingFile =
        if (recordingSegments.size > 1) {
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
        Timber.e("Recording file not found: ${finalRecordingFile!!.absolutePath}")
        return RecordingResult.Error("Recording file not found")
      }

      if (finalRecordingFile!!.length() == 0L) {
        Timber.e("Recording file is empty")
        return RecordingResult.Error("Recording file is empty")
      }
      RecordingResult.Success
    } catch (e: Exception) {
      Timber.e(e, "Failed to finish recording")
      cleanupRecorder()
      RecordingResult.Error("Failed to finish recording: ${e.message}", e)
    }
  }

  /**
   * Uploads the completed recording.
   */
  suspend fun uploadRecording(): UploadResult {
    val finalFile = finalRecordingFile?.takeIf { it.exists() }
    if (finalFile == null) {
      Timber.e("Upload failed - no recording file available")
      return UploadResult.Error("No recording file available for upload")
    }

    // Convert to M4A for upload
    val uploadFile = fileManager.createUploadFile()
    val audioComposer = AudioComposer()
    val conversionSuccess = audioComposer.convertToM4A(finalFile, uploadFile)

    if (!conversionSuccess || !uploadFile.exists()) {
      Timber.e("Failed to convert recording to M4A format")
      return UploadResult.Error("Failed to prepare recording for upload")
    }

    val result = recordingRepository.uploadRecording(uploadFile)

    // Cleanup files
    fileManager.deleteFileIfExists(uploadFile)
    if (result is UploadResult.UploadSuccess) {
      fileManager.deleteFileIfExists(finalRecordingFile)
    }
    cleanupSegments()
    finalRecordingFile = null
    return result
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
    val result =
      runCatching {
        mediaRecorder?.stop()
        mediaRecorder?.release()
      }
    result.onFailure { e ->
      Timber.w(e, "Error stopping recorder")
    }
    mediaRecorder = null
  }

  private fun cleanupSegments() {
    fileManager.deleteFiles(recordingSegments)
    recordingSegments.clear()
  }

  private fun combineAudioSegments(segments: List<File>): File? {
    if (segments.isEmpty()) return null
    if (segments.size == 1) return segments.first()

    return try {
      val outputFile = fileManager.createFinalRecordingFile()
      val audioComposer = AudioComposer()
      val success = audioComposer.combineAudioFiles(segments, outputFile)

      if (success && outputFile.exists() && outputFile.length() > 0) {
        Timber.d("Successfully combined ${segments.size} audio segments into ${outputFile.name}")
        outputFile
      } else {
        Timber.e("Failed to combine audio segments")
        // Fallback to first segment if combining fails
        segments.firstOrNull()
      }
    } catch (e: Exception) {
      Timber.e(e, "Error combining audio segments, using first segment as fallback")
      // Fallback to first segment if combining fails
      segments.firstOrNull()
    }
  }
}
