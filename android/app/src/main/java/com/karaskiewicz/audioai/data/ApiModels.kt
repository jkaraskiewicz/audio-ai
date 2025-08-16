package com.karaskiewicz.audioai.data

import com.google.gson.annotations.SerializedName

/**
 * Request model for text processing
 */
data class ProcessTextRequest(
  @SerializedName("transcript")
  val transcript: String,
)

/**
 * Response model for processing requests
 */
data class ProcessResponse(
  @SerializedName("result")
  val result: String?,

  @SerializedName("message")
  val message: String?,

  @SerializedName("saved_to")
  val savedTo: String?,

  @SerializedName("error")
  val error: String?,
) {
  // Helper property to check if processing was successful
  val isSuccess: Boolean
    get() = result != null && savedTo != null && error == null
}

/**
 * Health check response model
 */
data class HealthResponse(
  @SerializedName("status")
  val status: String,

  @SerializedName("message")
  val message: String?,

  @SerializedName("timestamp")
  val timestamp: String?,
)
