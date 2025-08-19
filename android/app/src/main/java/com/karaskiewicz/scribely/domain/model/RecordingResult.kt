package com.karaskiewicz.scribely.domain.model

sealed class RecordingResult {
  data object Success : RecordingResult()

  data class Error(val message: String, val exception: Throwable? = null) : RecordingResult()
}

sealed class UploadResult {
  data object UploadSuccess : UploadResult()

  data class LocalSave(val filePath: String) : UploadResult()

  data class Error(val message: String, val exception: Throwable? = null) : UploadResult()
}
