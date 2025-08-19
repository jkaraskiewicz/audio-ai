package com.karaskiewicz.scribely.ui.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()
  private lateinit var viewModel: MainViewModel
  private lateinit var context: Context

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    viewModel = MainViewModel()
    context = ApplicationProvider.getApplicationContext()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `loadConfiguration updates state correctly`() =
    runTest {
      // When
      viewModel.loadConfiguration(context)

      // Wait for the Flow collection to emit at least one value
      testScheduler.advanceUntilIdle()

      // Then
      // Default state should be configured with default URL
      assertTrue(viewModel.isConfigured.value)
    }

  @Test
  fun `isApiConfigured returns true for context with default URL`() {
    // When
    val isConfigured = viewModel.isApiConfigured(context)

    // Then
    assertTrue(isConfigured)
  }
}
