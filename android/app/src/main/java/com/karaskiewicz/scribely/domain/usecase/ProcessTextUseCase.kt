package com.karaskiewicz.scribely.domain.usecase

import android.content.Context
import com.karaskiewicz.scribely.data.ProcessTextRequest
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.utils.safeSuspendNetworkCall
import com.karaskiewicz.scribely.utils.mapToResult
import timber.log.Timber

/**
 * Use case for processing shared text content
 * Follows Single Responsibility Principle - only handles text processing
 */
class ProcessTextUseCase(
  private val apiServiceManager: ApiServiceManager,
) {
  sealed class TextProcessingResult {
    data class Success(val message: String) : TextProcessingResult()

    data class Error(val errorMessage: String) : TextProcessingResult()
  }

  suspend fun processText(
    context: Context,
    text: String,
  ): TextProcessingResult {
    if (text.isBlank()) {
      return TextProcessingResult.Error("No text content found")
    }

    Timber.d("Processing text content, length: ${text.length}")

    val result =
      safeSuspendNetworkCall("process text") {
        val apiService = apiServiceManager.createApiService()
        val request = ProcessTextRequest(text)
        apiService.processText(request)
      }

    var processingResult: TextProcessingResult = TextProcessingResult.Error("Unknown error")

    result.mapToResult(
      onSuccess = { response ->
        processingResult =
          when {
            response.isSuccessful -> {
              val body = response.body()
              if (body?.isSuccess == true) {
                TextProcessingResult.Success(
                  "Text processed successfully!\nSaved to: ${body.savedTo}",
                )
              } else {
                TextProcessingResult.Error("Processing failed: ${body?.error ?: "Unknown error"}")
              }
            }
            else -> TextProcessingResult.Error("Server error: HTTP ${response.code()}")
          }
      },
      onFailure = { exception ->
        processingResult =
          TextProcessingResult.Error("Network error: ${exception.message ?: "Unknown error"}")
      },
    )

    return processingResult
  }
}
