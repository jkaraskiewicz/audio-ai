package com.karaskiewicz.scribely.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.scribely.domain.usecase.ProcessFileUseCase
import com.karaskiewicz.scribely.domain.usecase.ProcessTextUseCase
import timber.log.Timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State for share screen
 */
data class ShareState(
  val isLoading: Boolean = false,
  val isSuccess: Boolean = false,
  val error: String? = null,
  val message: String = "",
)

/**
 * ViewModel for handling shared content from other apps
 * Follows Clean Architecture - delegates processing to use cases
 *
 * Orchestrates share workflow by delegating to:
 * - ProcessTextUseCase: text content processing
 * - ProcessFileUseCase: file content processing
 */
class ShareViewModel(
  private val processTextUseCase: ProcessTextUseCase,
  private val processFileUseCase: ProcessFileUseCase,
) : ViewModel() {
  private val _shareState = MutableStateFlow(ShareState(message = "Preparing..."))
  val shareState: StateFlow<ShareState> = _shareState.asStateFlow()

  /**
   * Handles shared content based on intent action and type
   */
  fun handleSharedContent(
    context: Context,
    intent: Intent,
  ) {
    Timber.d("Handling shared content - Action: ${intent.action}, Type: ${intent.type}")

    when (intent.action) {
      Intent.ACTION_SEND -> handleSingleShare(context, intent)
      Intent.ACTION_SEND_MULTIPLE -> handleMultipleShare(context, intent)
      else -> {
        Timber.w("Unsupported action: ${intent.action}")
        _shareState.value = ShareState(error = "Unsupported share action: ${intent.action}")
      }
    }
  }

  private fun handleSingleShare(
    context: Context,
    intent: Intent,
  ) {
    if (intent.type?.startsWith("text/") == true) {
      Timber.d("Handling text share")
      handleTextShare(context, intent)
    } else {
      Timber.d("Handling file share with type: ${intent.type}")
      handleFileShare(context, intent)
    }
  }

  private fun handleMultipleShare(
    context: Context,
    intent: Intent,
  ) {
    Timber.d("Handling multiple files share")
    val uris = extractUriList(intent)
    if (uris.isNullOrEmpty()) {
      _shareState.value = ShareState(error = "No files found")
      return
    }

    // For now, just process the first file
    processFile(context, uris[0])
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
    val uri = extractUri(intent)
    Timber.d("File share URI: $uri")

    if (uri == null) {
      Timber.w("No URI found in share intent")
      _shareState.value = ShareState(error = "No file found in share")
      return
    }

    processFile(context, uri)
  }

  private fun processText(
    context: Context,
    text: String,
  ) {
    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing text...")

      when (val result = processTextUseCase.processText(context, text)) {
        is ProcessTextUseCase.TextProcessingResult.Success -> {
          _shareState.value = ShareState(isSuccess = true, message = result.message)
        }
        is ProcessTextUseCase.TextProcessingResult.Error -> {
          _shareState.value = ShareState(error = result.errorMessage)
        }
      }
    }
  }

  private fun processFile(
    context: Context,
    uri: Uri,
  ) {
    viewModelScope.launch {
      _shareState.value = ShareState(isLoading = true, message = "Processing file...")

      when (val result = processFileUseCase.processFile(context, uri)) {
        is ProcessFileUseCase.FileProcessingResult.Success -> {
          _shareState.value = ShareState(isSuccess = true, message = result.message)
        }
        is ProcessFileUseCase.FileProcessingResult.Error -> {
          _shareState.value = ShareState(error = result.errorMessage)
        }
      }
    }
  }

  // Android version-safe URI extraction helpers

  private fun extractUri(intent: Intent): Uri? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
    } else {
      @Suppress("DEPRECATION")
      intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
    }

  private fun extractUriList(intent: Intent): List<Uri>? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
    } else {
      @Suppress("DEPRECATION")
      intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
    }
}
