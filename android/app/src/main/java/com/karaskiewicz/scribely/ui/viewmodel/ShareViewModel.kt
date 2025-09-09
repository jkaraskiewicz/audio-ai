package com.karaskiewicz.scribely.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.scribely.data.ProcessTextRequest
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.domain.usecase.RecordingUseCase
import com.karaskiewicz.scribely.utils.FileUtils
import com.karaskiewicz.scribely.utils.safeSuspendNetworkCall
import com.karaskiewicz.scribely.utils.safeFileOperation
import com.karaskiewicz.scribely.utils.mapToResult
import timber.log.Timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

data class ShareState(
  val isLoading: Boolean = false,
  val isSuccess: Boolean = false,
  val error: String? = null,
  val message: String = "",
)

class ShareViewModel(
  private val recordingUseCase: RecordingUseCase,
  private val apiServiceManager: ApiServiceManager,
) : ViewModel() {
  private val _shareState = MutableStateFlow(ShareState(message = "Preparing..."))
  val shareState: StateFlow<ShareState> = _shareState.asStateFlow()

  fun handleSharedContent(
    context: Context,
    intent: Intent,
  ) {
    Timber.d("Handling shared content - Action: ${intent.action}, Type: ${intent.type}")

    when (intent.action) {
      Intent.ACTION_SEND -> {
        if (intent.type?.startsWith("text/") == true) {
          Timber.d("Handling text share")
          handleTextShare(context, intent)
        } else {
          Timber.d("Handling file share with type: ${intent.type}")
          handleFileShare(context, intent)
        }
      }
      Intent.ACTION_SEND_MULTIPLE -> {
        Timber.d("Handling multiple files share")
        handleMultipleFilesShare(context, intent)
      }
      else -> {
        Timber.w("Unsupported action: ${intent.action}")
        _shareState.value = ShareState(error = "Unsupported share action: ${intent.action}")
      }
    }
  }

  private fun handleTextShare(
    context: Context,
    intent: Intent,
  ) {
    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
    if (sharedText.isNullOrBlank()) {
      _shareState.value = ShareState(error = "No text content found")
      return
    }

    processText(context, sharedText)
  }

  private fun handleFileShare(
    context: Context,
    intent: Intent,
  ) {
    val uri =
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
      } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
      }

    Timber.d("File share URI: $uri")

    if (uri == null) {
      Timber.w("No URI found in share intent")
      _shareState.value = ShareState(error = "No file found in share")
      return
    }

    // Log file information
    try {
      val contentResolver = context.contentResolver
      val mimeType = contentResolver.getType(uri)
      Timber.d("Shared file - URI: $uri, MIME type: $mimeType")

      // Check if we can access the file
      contentResolver.openInputStream(uri)?.use {
        Timber.d("Successfully opened input stream for file")
      }
    } catch (e: Exception) {
      Timber.e(e, "Failed to access shared file")
      _shareState.value = ShareState(error = "Cannot access shared file: ${e.message}")
      return
    }

    processFile(context, uri)
  }

  private fun handleMultipleFilesShare(
    context: Context,
    intent: Intent,
  ) {
    val uris =
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
      } else {
        @Suppress("DEPRECATION")
        intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
      }
    if (uris.isNullOrEmpty()) {
      _shareState.value = ShareState(error = "No files found")
      return
    }

    // For now, just process the first file
    processFile(context, uris[0])
  }

  private fun processText(
    context: Context,
    text: String,
  ) {
    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing text...")

      val result =
        safeSuspendNetworkCall("process text") {
          val apiService = apiServiceManager.createApiService()
          val request = ProcessTextRequest(text)
          apiService.processText(request)
        }

      result.mapToResult(
        onSuccess = { response ->
          when {
            response.isSuccessful -> {
              val body = response.body()
              if (body?.isSuccess == true) {
                _shareState.value =
                  ShareState(
                    isSuccess = true,
                    message = "Text processed successfully!\nSaved to: ${body.savedTo}",
                  )
              } else {
                _shareState.value =
                  ShareState(
                    error = "Processing failed: ${body?.error ?: "Unknown error"}",
                  )
              }
            }
            else -> {
              _shareState.value =
                ShareState(
                  error = "Server error: HTTP ${response.code()}",
                )
            }
          }
        },
        onFailure = { exception ->
          _shareState.value =
            ShareState(
              error = "Network error: ${exception.message ?: "Unknown error"}",
            )
        },
      )
    }
  }

  private fun processFile(
    context: Context,
    uri: Uri,
  ) {
    Timber.d("Starting to process file: $uri")
    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing file...")

      val fileResult =
        safeFileOperation("copy URI to temp file") {
          Timber.d("Copying URI to temp file: $uri")
          val result = FileUtils.copyUriToTempFile(context, uri)
          Timber.d("Copy result: ${result?.absolutePath}")
          result
        }

      fileResult.mapToResult(
        onSuccess = { file ->
          if (file == null) {
            Timber.w("FileUtils returned null file")
            _shareState.value = ShareState(error = "Failed to read file")
            return@mapToResult
          }

          Timber.d("Successfully created temp file: ${file.absolutePath}, size: ${file.length()} bytes")

          val networkResult =
            safeSuspendNetworkCall("process file") {
              val apiService = apiServiceManager.createApiService()
              val requestFile = file.asRequestBody("*/*".toMediaTypeOrNull())
              val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
              apiService.processFile(body)
            }

          // Clean up temp file
          file.delete()

          networkResult.mapToResult(
            onSuccess = { response ->
              when {
                response.isSuccessful -> {
                  val responseBody = response.body()
                  if (responseBody?.isSuccess == true) {
                    _shareState.value =
                      ShareState(
                        isSuccess = true,
                        message = "File processed successfully!\nSaved to: ${responseBody.savedTo}",
                      )
                  } else {
                    _shareState.value =
                      ShareState(
                        error = "Processing failed: ${responseBody?.error ?: "Unknown error"}",
                      )
                  }
                }
                else -> {
                  _shareState.value =
                    ShareState(
                      error = "Server error: HTTP ${response.code()}",
                    )
                }
              }
            },
            onFailure = { exception ->
              _shareState.value =
                ShareState(
                  error = "Network error: ${exception.message ?: "Unknown error"}",
                )
            },
          )
        },
        onFailure = { exception ->
          Timber.e(exception, "Failed to process file")
          _shareState.value =
            ShareState(
              error = "File error: ${exception.message ?: "Unknown error"}",
            )
        },
      )
    }
  }
}
