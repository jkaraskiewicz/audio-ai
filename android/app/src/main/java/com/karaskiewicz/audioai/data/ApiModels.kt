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
  @SerializedName("success")
  val success: Boolean,

  @SerializedName("message")
  val message: String?,

  @SerializedName("savedPath")
  val savedPath: String?,

  @SerializedName("error")
  val error: String?,
)

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
