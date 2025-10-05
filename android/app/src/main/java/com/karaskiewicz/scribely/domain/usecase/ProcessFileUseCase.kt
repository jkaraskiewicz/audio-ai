package com.karaskiewicz.scribely.domain.usecase

import android.content.Context
import android.net.Uri
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.utils.FileUtils
import com.karaskiewicz.scribely.utils.safeSuspendNetworkCall
import com.karaskiewicz.scribely.utils.safeFileOperation
import com.karaskiewicz.scribely.utils.mapToResult
import timber.log.Timber
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Use case for processing shared file content
 * Follows Single Responsibility Principle - only handles file processing
 */
class ProcessFileUseCase(
  private val apiServiceManager: ApiServiceManager,
) {
  sealed class FileProcessingResult {
    data class Success(val message: String) : FileProcessingResult()

    data class Error(val errorMessage: String) : FileProcessingResult()
  }

  suspend fun processFile(
    context: Context,
    uri: Uri,
  ): FileProcessingResult {
    Timber.d("Starting to process file: $uri")

    // Validate file accessibility
    val validationError = validateFileAccess(context, uri)
    if (validationError != null) {
      return FileProcessingResult.Error(validationError)
    }

    // Copy URI to temp file
    val fileResult =
      safeFileOperation("copy URI to temp file") {
        Timber.d("Copying URI to temp file: $uri")
        val result = FileUtils.copyUriToTempFile(context, uri)
        Timber.d("Copy result: ${result?.absolutePath}")
        result
      }

    var processingResult: FileProcessingResult = FileProcessingResult.Error("Unknown error")

    fileResult.mapToResult(
      onSuccess = { file ->
        if (file == null) {
          Timber.w("FileUtils returned null file")
          processingResult = FileProcessingResult.Error("Failed to read file")
          return@mapToResult
        }

        Timber.d("Successfully created temp file: ${file.absolutePath}, size: ${file.length()} bytes")

        // Upload file
        processingResult = uploadFile(context, file)

        // Clean up temp file
        file.delete()
      },
      onFailure = { exception ->
        Timber.e(exception, "Failed to process file")
        processingResult =
          FileProcessingResult.Error("File error: ${exception.message ?: "Unknown error"}")
      },
    )

    return processingResult
  }

  private fun validateFileAccess(
    context: Context,
    uri: Uri,
  ): String? {
    try {
      val contentResolver = context.contentResolver
      val mimeType = contentResolver.getType(uri)
      Timber.d("Shared file - URI: $uri, MIME type: $mimeType")

      // Check if we can access the file
      contentResolver.openInputStream(uri)?.use {
        Timber.d("Successfully opened input stream for file")
      }
      return null
    } catch (e: Exception) {
      Timber.e(e, "Failed to access shared file")
      return "Cannot access shared file: ${e.message}"
    }
  }

  private suspend fun uploadFile(
    context: Context,
    file: File,
  ): FileProcessingResult {
    val networkResult =
      safeSuspendNetworkCall("process file") {
        val apiService = apiServiceManager.createApiService()
        val requestFile = file.asRequestBody("*/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        apiService.processFile(body)
      }

    var uploadResult: FileProcessingResult = FileProcessingResult.Error("Unknown error")

    networkResult.mapToResult(
      onSuccess = { response ->
        uploadResult =
          when {
            response.isSuccessful -> {
              val responseBody = response.body()
              if (responseBody?.isSuccess == true) {
                FileProcessingResult.Success(
                  "File processed successfully!\nSaved to: ${responseBody.savedTo}",
                )
              } else {
                FileProcessingResult.Error(
                  "Processing failed: ${responseBody?.error ?: "Unknown error"}",
                )
              }
            }
            else -> FileProcessingResult.Error("Server error: HTTP ${response.code()}")
          }
      },
      onFailure = { exception ->
        uploadResult =
          FileProcessingResult.Error("Network error: ${exception.message ?: "Unknown error"}")
      },
    )

    return uploadResult
  }
}
