package com.karaskiewicz.audioai.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

  /**
   * Health check endpoint
   */
  @GET("health")
  suspend fun healthCheck(): Response<HealthResponse>

  /**
   * Process text content
   */
  @POST("process")
  @Headers("Content-Type: application/json")
  suspend fun processText(
    @Body request: ProcessTextRequest,
  ): Response<ProcessResponse>

  /**
   * Process file content
   */
  @Multipart
  @POST("process-file")
  suspend fun processFile(
    @Part file: MultipartBody.Part,
    @Part("transcript") transcript: RequestBody? = null,
  ): Response<ProcessResponse>
}
