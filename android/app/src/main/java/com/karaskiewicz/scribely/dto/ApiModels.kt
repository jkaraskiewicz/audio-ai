package com.karaskiewicz.scribely.dto

import com.google.gson.annotations.SerializedName

data class ProcessTextRequest(
  @SerializedName("transcript")
  val transcript: String,
)

data class ProcessResponse(
  @SerializedName("result")
  val result: String?,
  @SerializedName("message")
  val message: String?,
  @SerializedName("saved_to")
  val savedTo: String?,
  @SerializedName("error")
  val error: String?,
  @SerializedName("status")
  val status: String?,
  @SerializedName("filename")
  val filename: String?,
  @SerializedName("timestamp")
  val timestamp: String?,
) {
  val isSuccess: Boolean
    get() = result != null && savedTo != null && error == null
}

data class HealthResponse(
  @SerializedName("status")
  val status: String,
  @SerializedName("message")
  val message: String?,
  @SerializedName("timestamp")
  val timestamp: String?,
)
