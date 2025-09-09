package com.karaskiewicz.scribely.network

import com.karaskiewicz.scribely.utils.PreferencesDataStore
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Manages dynamic API service instances that can update server URLs at runtime.
 */
class ApiServiceManager(
  private val preferencesDataStore: PreferencesDataStore,
) {
  private val loggingInterceptor =
    HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

  private val okHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(120, TimeUnit.SECONDS)
      .writeTimeout(120, TimeUnit.SECONDS)
      .build()

  /**
   * Creates a new ApiService instance with the current server URL from settings.
   */
  suspend fun createApiService(): ApiService {
    val serverUrl = preferencesDataStore.getServerUrl()
    return createRetrofitForUrl(serverUrl).create(ApiService::class.java)
  }

  private fun createRetrofitForUrl(baseUrl: String): Retrofit {
    return Retrofit.Builder()
      .baseUrl(ensureBaseUrlEndsWithSlash(baseUrl))
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  private fun ensureBaseUrlEndsWithSlash(url: String): String {
    return if (url.endsWith("/")) url else "$url/"
  }
}
