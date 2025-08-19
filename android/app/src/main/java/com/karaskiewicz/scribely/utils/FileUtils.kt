package com.karaskiewicz.scribely.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
  fun copyUriToTempFile(
    context: Context,
    uri: Uri,
  ): File? {
    return safeFileOperation("copy URI to temp file") {
      val fileName = getFileName(context, uri) ?: "shared_file"
      val tempFile = File(context.cacheDir, fileName)

      val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(tempFile)

      inputStream?.use { input ->
        outputStream.use { output ->
          input.copyTo(output)
        }
      }

      tempFile
    }.getOrNull()
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
            cursor.getString(nameIndex)?.let { return it }
          }
        }
      }
    }

    // Fallback to extracting from URI path
    return uri.path?.let { path ->
      val lastSlash = path.lastIndexOf('/')
      if (lastSlash != -1 && lastSlash < path.length - 1) {
        path.substring(lastSlash + 1)
      } else {
        path
      }
    }
  }
}
