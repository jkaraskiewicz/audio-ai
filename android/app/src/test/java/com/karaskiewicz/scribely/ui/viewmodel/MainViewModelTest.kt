package com.karaskiewicz.scribely.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.karaskiewicz.scribely.domain.usecase.RecordingUseCase
import com.karaskiewicz.scribely.utils.PreferencesDataStore
import com.karaskiewicz.scribely.domain.model.RecordingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class MainViewModelTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()

  @Mock
  private lateinit var mockRecordingUseCase: RecordingUseCase

  @Mock
  private lateinit var mockPreferencesDataStore: PreferencesDataStore

  private lateinit var viewModel: MainViewModel

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)

    viewModel = MainViewModel(mockRecordingUseCase, mockPreferencesDataStore)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `initial state is correct`() =
    runTest {
      // Then
      assertEquals(RecordingState.IDLE, viewModel.recordingState.value)
      assertEquals("", viewModel.serverUrl.value)
      assertFalse(viewModel.isConfigured.value)
      assertEquals(0L, viewModel.recordingDuration.value)
      assertNull(viewModel.errorMessage.value)
      assertNull(viewModel.successMessage.value)
    }

  @Test
  fun `viewModel is properly initialized with dependencies`() {
    // Given & When - ViewModel is created in setup()

    // Then - Should not crash and have proper initial state
    assertEquals(RecordingState.IDLE, viewModel.recordingState.value)
  }
}
