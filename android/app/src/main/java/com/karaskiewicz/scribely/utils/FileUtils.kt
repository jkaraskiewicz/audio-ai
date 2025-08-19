package com.karaskiewicz.scribely.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import timber.log.Timber

object FileUtils {
  fun copyUriToTempFile(context: Context, uri: Uri): File? {
    return try {
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
    } catch (e: Exception) {
      Timber.e(e, "Error copying file")
      null
    }
  }

  private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
      val cursor = context.contentResolver.query(uri, null, null, null, null)
      cursor?.use {
        if (it.moveToFirst()) {
          val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex >= 0) {
            name = it.getString(nameIndex)
          }
        }
      }
    }
    if (name == null) {
      name = uri.path
      val cut = name?.lastIndexOf('/') ?: -1
      if (cut != -1) {
        name = name?.substring(cut + 1)
      }
    }
    return name
  }
}
