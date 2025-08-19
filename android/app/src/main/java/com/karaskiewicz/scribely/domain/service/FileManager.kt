package com.karaskiewicz.scribely.domain.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import com.karaskiewicz.scribely.domain.model.RecordingConstants
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val context: Context) {
  fun createRecordingFile(): File {
    val timestamp = TIMESTAMP_FORMAT.format(Date())
    val fileName =
      "${RecordingConstants.RECORDING_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(context.cacheDir, fileName)
  }

  fun createSegmentFile(): File {
    val timestamp = SEGMENT_TIMESTAMP_FORMAT.format(Date())
    val fileName =
      "${RecordingConstants.SEGMENT_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(context.cacheDir, fileName)
  }

  fun createPublicRecordingsDirectory(): File {
    val downloadsDir =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val recordingsDir = File(downloadsDir, RecordingConstants.RECORDINGS_FOLDER)

    if (!recordingsDir.exists()) {
      recordingsDir.mkdirs()
    }

    return recordingsDir
  }

  fun createPublicFile(): File {
    val publicDir = createPublicRecordingsDirectory()
    val timestamp = TIMESTAMP_FORMAT.format(Date())
    val fileName =
      "${RecordingConstants.LOCAL_FILE_PREFIX}$timestamp${RecordingConstants.FILE_EXTENSION}"
    return File(publicDir, fileName)
  }

  fun deleteFileIfExists(file: File?) {
    file?.takeIf { it.exists() }?.delete()
  }

  fun deleteFiles(files: List<File>) {
    files.forEach { deleteFileIfExists(it) }
  }

  @SuppressLint("ConstantLocale")
  private companion object {
    val TIMESTAMP_FORMAT =
      SimpleDateFormat(
        RecordingConstants.TIMESTAMP_FORMAT,
        Locale.getDefault(),
      )

    val SEGMENT_TIMESTAMP_FORMAT =
      SimpleDateFormat(
        RecordingConstants.SEGMENT_TIMESTAMP_FORMAT,
        Locale.getDefault(),
      )
  }
}
