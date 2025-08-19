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
    when (intent.action) {
      Intent.ACTION_SEND -> {
        if (intent.type?.startsWith("text/") == true) {
          handleTextShare(context, intent)
        } else {
          handleFileShare(context, intent)
        }
      }
      Intent.ACTION_SEND_MULTIPLE -> {
        handleMultipleFilesShare(context, intent)
      }
      else -> {
        _shareState.value = ShareState(error = "No content to share")
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
    if (uri == null) {
      _shareState.value = ShareState(error = "No file found")
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
    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing file...")

      val fileResult =
        safeFileOperation("copy URI to temp file") {
          FileUtils.copyUriToTempFile(context, uri)
        }

      fileResult.mapToResult(
        onSuccess = { file ->
          if (file == null) {
            _shareState.value = ShareState(error = "Failed to read file")
            return@mapToResult
          }

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
          _shareState.value =
            ShareState(
              error = "File error: ${exception.message ?: "Unknown error"}",
            )
        },
      )
    }
  }
}
