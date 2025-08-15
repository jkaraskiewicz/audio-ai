package com.karaskiewicz.audioai.data

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient private constructor() {

  private var retrofit: Retrofit? = null
  private var apiService: ApiService? = null

  companion object {
    @Volatile
    private var INSTANCE: ApiClient? = null

    fun getInstance(): ApiClient {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: ApiClient().also { INSTANCE = it }
      }
    }
  }

  fun getApiService(context: Context): ApiService? {
    val preferencesDataStore = PreferencesDataStore(context)
    val serverUrl = runBlocking {
      preferencesDataStore.getServerUrl()
    }

    if (serverUrl.isBlank()) {
      return null
    }

    // Ensure URL ends with /
    val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"

    if (retrofit?.baseUrl().toString() != baseUrl) {
      retrofit = createRetrofit(baseUrl)
      apiService = null
    }

    if (apiService == null) {
      apiService = retrofit?.create(ApiService::class.java)
    }

    return apiService
  }

  private fun createRetrofit(baseUrl: String): Retrofit {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .build()

    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  fun isConfigured(context: Context): Boolean {
    val preferencesDataStore = PreferencesDataStore(context)
    return runBlocking {
      preferencesDataStore.getServerUrl().isNotBlank()
    }
  }
}