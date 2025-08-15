package com.karaskiewicz.audioai.ui.viewmodel

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
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
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
  fun `loadConfiguration updates state correctly`() = runTest {
    // When
    viewModel.loadConfiguration(context)

    // Then
    // Default state should have empty server URL
    assertFalse(viewModel.isConfigured.value)
  }

  @Test
  fun `isApiConfigured returns false for unconfigured context`() {
    // When
    val isConfigured = viewModel.isApiConfigured(context)

    // Then
    assertFalse(isConfigured)
  }
}