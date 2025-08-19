package com.karaskiewicz.scribely.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ApiClientTest {

  @Mock
  private lateinit var mockContext: Context

  @Mock
  private lateinit var mockPreferencesDataStore: PreferencesDataStore

  private lateinit var apiClient: ApiClient

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    apiClient = ApiClient.getInstance()
  }

  @Test
  fun `getApiService returns service when context has default URL`() {
    // Given
    val context = ApplicationProvider.getApplicationContext<Context>()

    // When
    val apiService = apiClient.getApiService(context)

    // Then - returns service because default URL is configured
    assertTrue(apiService != null)
  }

  @Test
  fun `isConfigured returns true when context has default URL`() {
    // Given
    val context = ApplicationProvider.getApplicationContext<Context>()

    // When
    val isConfigured = apiClient.isConfigured(context)

    // Then
    assertTrue(isConfigured)
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
