package com.karaskiewicz.audioai.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.audioai.data.ApiClient
import com.karaskiewicz.audioai.data.ProcessTextRequest
import com.karaskiewicz.audioai.utils.FileUtils
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

class ShareViewModel : ViewModel() {

  private val _shareState = MutableStateFlow(ShareState(message = "Preparing..."))
  val shareState: StateFlow<ShareState> = _shareState.asStateFlow()

  fun handleSharedContent(context: Context, intent: Intent) {
    val apiClient = ApiClient.getInstance()

    if (!apiClient.isConfigured(context)) {
      _shareState.value = ShareState(error = "Please configure your server URL in settings first")
      return
    }

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

  private fun handleTextShare(context: Context, intent: Intent) {
    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
    if (sharedText.isNullOrBlank()) {
      _shareState.value = ShareState(error = "No text content found")
      return
    }

    processText(context, sharedText)
  }

  private fun handleFileShare(context: Context, intent: Intent) {
    val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
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

  private fun handleMultipleFilesShare(context: Context, intent: Intent) {
    val uris = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
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

  private fun processText(context: Context, text: String) {
    val apiService = ApiClient.getInstance().getApiService(context)
    if (apiService == null) {
      _shareState.value = ShareState(error = "Failed to create API connection")
      return
    }

    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing text...")

      try {
        val request = ProcessTextRequest(text)
        val response = apiService.processText(request)

        if (response.isSuccessful) {
          val body = response.body()
          if (body?.isSuccess == true) {
            _shareState.value = ShareState(
              isSuccess = true,
              message = "Text processed successfully!\nSaved to: ${body.savedTo}",
            )
          } else {
            _shareState.value = ShareState(
              error = "Processing failed: ${body?.error ?: "Unknown error"}",
            )
          }
        } else {
          _shareState.value = ShareState(
            error = "Server error: HTTP ${response.code()}",
          )
        }
      } catch (e: Exception) {
        _shareState.value = ShareState(
          error = "Network error: ${e.message ?: "Unknown error"}",
        )
      }
    }
  }

  private fun processFile(context: Context, uri: Uri) {
    val apiService = ApiClient.getInstance().getApiService(context)
    if (apiService == null) {
      _shareState.value = ShareState(error = "Failed to create API connection")
      return
    }

    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing file...")

      try {
        val file = FileUtils.copyUriToTempFile(context, uri)
        if (file == null) {
          _shareState.value = ShareState(error = "Failed to read file")
          return@launch
        }

        val requestFile = file.asRequestBody("*/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = apiService.processFile(body)

        // Clean up temp file
        file.delete()

        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody?.isSuccess == true) {
            _shareState.value = ShareState(
              isSuccess = true,
              message = "File processed successfully!\nSaved to: ${responseBody.savedTo}",
            )
          } else {
            _shareState.value = ShareState(
              error = "Processing failed: ${responseBody?.error ?: "Unknown error"}",
            )
          }
        } else {
          _shareState.value = ShareState(
            error = "Server error: HTTP ${response.code()}",
          )
        }
      } catch (e: Exception) {
        _shareState.value = ShareState(
          error = "Network error: ${e.message ?: "Unknown error"}",
        )
      }
    }
  }
}
