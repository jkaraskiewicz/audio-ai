package com.karaskiewicz.scribely.domain.handler

import com.karaskiewicz.scribely.domain.model.RecordingResult
import com.karaskiewicz.scribely.domain.service.FileManager
import timber.log.Timber
import java.io.File

/**
 * Recording file handler - manages file operations for recordings
 * Follows Single Responsibility Principle - only handles file operations
 */
internal class RecordingFileHandler(
  private val fileManager: FileManager,
) {
  /**
   * Creates and prepares a recording output file
   */
  fun createRecordingFile(): File {
    val outputFile = fileManager.createRecordingFile()
    outputFile.parentFile?.let { parentDir ->
      if (!parentDir.exists()) {
        parentDir.mkdirs()
      }
    }
    return outputFile
  }

  /**
   * Validates a finished recording file
   */
  fun validateRecordingFile(outputFile: File): RecordingResult {
    if (!outputFile.exists()) {
      Timber.e("Recording file not found: ${outputFile.absolutePath}")
      return RecordingResult.Error("Recording file not found")
    }

    if (outputFile.length() == 0L) {
      Timber.e("Recording file is empty")
      return RecordingResult.Error("Recording file is empty")
    }

    Timber.d(
      "Recording finished successfully: ${outputFile.absolutePath}, " +
        "size: ${outputFile.length()} bytes",
    )
    return RecordingResult.Success
  }

  /**
   * Deletes a file if it exists
   */
  fun deleteFileIfExists(file: File) {
    fileManager.deleteFileIfExists(file)
  }

  /**
   * Creates an upload file for converted audio
   */
  fun createUploadFile(): File = fileManager.createUploadFile()
}
