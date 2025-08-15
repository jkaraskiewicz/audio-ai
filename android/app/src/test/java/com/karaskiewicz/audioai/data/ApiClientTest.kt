package com.karaskiewicz.audioai.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ApiClientTest {

  @Mock
  private lateinit var mockContext: Context

  @Mock
  private lateinit var mockPreferencesDataStore: PreferencesDataStore

  private lateinit var apiClient: ApiClient

  @Before
  fun setup() {
    apiClient = ApiClient.getInstance()
  }

  @Test
  fun `getApiService returns null when server URL is blank`() {
    // Given
    val context = ApplicationProvider.getApplicationContext<Context>()

    // When
    val apiService = apiClient.getApiService(context)

    // Then - will be null because default context has no saved URL
    assertNull(apiService)
  }

  @Test
  fun `isConfigured returns false when server URL is blank`() {
    // Given
    val context = ApplicationProvider.getApplicationContext<Context>()

    // When
    val isConfigured = apiClient.isConfigured(context)

    // Then
    assertFalse(isConfigured)
  }

  @Test
  fun `getInstance returns singleton`() {
    // Given
    val instance1 = ApiClient.getInstance()
    val instance2 = ApiClient.getInstance()

    // Then
    assertTrue(instance1 === instance2)
  }
}