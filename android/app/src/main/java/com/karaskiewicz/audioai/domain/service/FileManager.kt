package com.karaskiewicz.audioai.domain.service

import android.content.Context
import android.os.Environment
import com.karaskiewicz.audioai.domain.model.RecordingConstants
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages file creation and organization for recordings.
 * Follows Single Responsibility Principle.
 */
class FileManager {

  private val timestampFormat = SimpleDateFormat(
    RecordingConstants.TIMESTAMP_FORMAT,
    Locale.getDefault(),
  )

  private val segmentTimestampFormat = SimpleDateFormat(
    RecordingConstants.SEGMENT_TIMESTAMP_FORMAT,
    Locale.getDefault(),
  )

  /**
   * Creates a new recording file in the app's cache directory.
   */
  fun createRecordingFile(context: Context): File {
    val timestamp = timestampFormat.format(Date())
    val fileName = "${RecordingConstants.RECORDING_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(context.cacheDir, fileName)
  }

  /**
   * Creates a new segment file for pause/resume functionality.
   */
  fun createSegmentFile(context: Context): File {
    val timestamp = segmentTimestampFormat.format(Date())
    val fileName = "${RecordingConstants.SEGMENT_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(context.cacheDir, fileName)
  }

  /**
   * Creates the public downloads directory for permanent storage.
   */
  fun createPublicRecordingsDirectory(): File {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val recordingsDir = File(downloadsDir, RecordingConstants.RECORDINGS_FOLDER)

    if (!recordingsDir.exists()) {
      recordingsDir.mkdirs()
    }

    return recordingsDir
  }

  /**
   * Creates a file in the public downloads directory.
   */
  fun createPublicFile(): File {
    val publicDir = createPublicRecordingsDirectory()
    val timestamp = timestampFormat.format(Date())
    val fileName = "${RecordingConstants.LOCAL_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(publicDir, fileName)
  }

  /**
   * Safely deletes a file if it exists.
   */
  fun deleteFileIfExists(file: File?) {
    file?.takeIf { it.exists() }?.delete()
  }

  /**
   * Safely deletes a list of files.
   */
  fun deleteFiles(files: List<File>) {
    files.forEach { deleteFileIfExists(it) }
  }
}
