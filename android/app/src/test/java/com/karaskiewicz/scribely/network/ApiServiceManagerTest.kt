package com.karaskiewicz.scribely.network

import com.karaskiewicz.scribely.utils.PreferencesDataStore
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull

class ApiServiceManagerTest {
  @Mock
  private lateinit var mockPreferencesDataStore: PreferencesDataStore

  private lateinit var apiServiceManager: ApiServiceManager

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    apiServiceManager = ApiServiceManager(mockPreferencesDataStore)
  }

  @Test
  fun `createApiService returns service with default URL`() =
    runTest {
      // Given
      whenever(mockPreferencesDataStore.getServerUrl()).thenReturn("http://localhost:3000")

      // When
      val apiService = apiServiceManager.createApiService()

      // Then
      assertNotNull(apiService)
    }

  @Test
  fun `createApiService handles URL without trailing slash`() =
    runTest {
      // Given
      whenever(mockPreferencesDataStore.getServerUrl()).thenReturn("http://localhost:3000")

      // When
      val apiService = apiServiceManager.createApiService()

      // Then
      assertNotNull(apiService)
    }

  @Test
  fun `createApiService handles URL with trailing slash`() =
    runTest {
      // Given
      whenever(mockPreferencesDataStore.getServerUrl()).thenReturn("http://localhost:3000/")

      // When
      val apiService = apiServiceManager.createApiService()

      // Then
      assertNotNull(apiService)
    }
}
