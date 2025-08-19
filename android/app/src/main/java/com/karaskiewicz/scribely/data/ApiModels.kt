package com.karaskiewicz.scribely.data

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
