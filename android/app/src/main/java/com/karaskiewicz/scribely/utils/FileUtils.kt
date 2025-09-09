package com.karaskiewicz.scribely.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import timber.log.Timber

object FileUtils {
  fun copyUriToTempFile(
    context: Context,
    uri: Uri,
  ): File? {
    return runCatching {
      Timber.d("Copying URI to temp file: $uri")

      val fileName = getFileName(context, uri) ?: "shared_file"
      Timber.d("Using filename: $fileName")

      val tempFile = File(context.cacheDir, fileName)
      Timber.d("Temp file path: ${tempFile.absolutePath}")

      val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
      if (inputStream == null) {
        Timber.e("Cannot open input stream for URI: $uri")
        return null
      }

      val outputStream = FileOutputStream(tempFile)

      inputStream.use { input ->
        outputStream.use { output ->
          val bytesWritten = input.copyTo(output)
          Timber.d("Copied $bytesWritten bytes to temp file")
        }
      }

      if (!tempFile.exists() || tempFile.length() == 0L) {
        Timber.e("Temp file creation failed or is empty")
        return null
      }

      Timber.d("Successfully created temp file: ${tempFile.absolutePath}, size: ${tempFile.length()}")
      tempFile
    }.getOrElse { exception ->
      Timber.e(exception, "Failed to copy URI to temp file")
      null
    }
  }

  private fun getFileName(
    context: Context,
    uri: Uri,
  ): String? {
    // Try to get filename from content resolver first
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex >= 0) {
            cursor.getString(nameIndex)?.let { fileName ->
              Timber.d("Got filename from content resolver: $fileName")
              return fileName
            }
          }
        }
      }
    }

    // Fallback to extracting from URI path
    val fallbackName =
      uri.path?.let { path ->
        val lastSlash = path.lastIndexOf('/')
        if (lastSlash != -1 && lastSlash < path.length - 1) {
          path.substring(lastSlash + 1)
        } else {
          path
        }
      }

    // If we still don't have a good filename, generate one based on MIME type
    if (fallbackName.isNullOrEmpty() || fallbackName == "/" || !fallbackName.contains(".")) {
      val mimeType = context.contentResolver.getType(uri)
      val extension =
        when {
          mimeType?.startsWith("audio/") == true -> {
            when {
              mimeType.contains("mp3") || mimeType.contains("mpeg") -> ".mp3"
              mimeType.contains("m4a") || mimeType.contains("mp4") -> ".m4a"
              mimeType.contains("wav") -> ".wav"
              mimeType.contains("ogg") -> ".ogg"
              mimeType.contains("flac") -> ".flac"
              mimeType.contains("3gp") -> ".3gp"
              else -> ".audio"
            }
          }
          else -> ".file"
        }
      val generatedName = "shared_audio_${System.currentTimeMillis()}$extension"
      Timber.d("Generated filename: $generatedName (MIME: $mimeType)")
      return generatedName
    }

    Timber.d("Using fallback filename: $fallbackName")
    return fallbackName
  }
}
