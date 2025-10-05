package com.karaskiewicz.scribely.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.scribely.domain.model.RecordingConstants
import com.karaskiewicz.scribely.domain.model.RecordingResult
import com.karaskiewicz.scribely.domain.model.RecordingState
import com.karaskiewicz.scribely.domain.model.UploadResult
import com.karaskiewicz.scribely.domain.handler.PermissionHandler
import com.karaskiewicz.scribely.utils.RecordingDurationTracker
import com.karaskiewicz.scribely.domain.usecase.RecordingUseCase
import com.karaskiewicz.scribely.utils.PreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main recording screen.
 * Follows MVVM pattern and delegates business logic to use cases.
 */
class MainViewModel(
  private val recordingUseCase: RecordingUseCase,
  private val preferencesDataStore: PreferencesDataStore,
  private val permissionHandler: PermissionHandler,
  private val durationTracker: RecordingDurationTracker,
) : ViewModel() {
  // Configuration state
  private val _serverUrl = MutableStateFlow("")
  val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

  private val _isConfigured = MutableStateFlow(false)
  val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

  // Recording state
  private val _recordingState = MutableStateFlow(RecordingState.IDLE)
  val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _successMessage = MutableStateFlow<String?>(null)
  val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

  // Expose duration from tracker
  val recordingDuration: StateFlow<Long> = durationTracker.duration

  fun loadConfiguration() {
    viewModelScope.launch {
      preferencesDataStore.serverUrl.collect { url ->
        _serverUrl.value = url
        _isConfigured.value = url.isNotBlank()
      }
    }
  }

  fun isApiConfigured(): Boolean {
    return runCatching {
      // Use blocking call for simple boolean check
      kotlinx.coroutines.runBlocking {
        preferencesDataStore.getServerUrl().isNotBlank()
      }
    }.getOrDefault(false)
  }

  /**
   * Starts a new recording session.
   */
  fun startRecording(context: Context) {
    clearMessages()

    // Check for audio recording permission
    when (val permissionResult = permissionHandler.checkAudioRecordingPermission(context)) {
      is PermissionHandler.PermissionResult.Granted -> {
        // Permission granted, proceed with recording
      }
      is PermissionHandler.PermissionResult.Denied -> {
        _errorMessage.value = permissionResult.message
        return
      }
    }

    when (val result = recordingUseCase.startRecording()) {
      is RecordingResult.Success -> {
        _recordingState.value = RecordingState.RECORDING
        durationTracker.start()
        durationTracker.startDurationTimer(viewModelScope) {
          _recordingState.value == RecordingState.RECORDING
        }
      }
      is RecordingResult.Error -> {
        Log.e(RecordingConstants.LOG_TAG, "Recording failed: ${result.message}")
        _errorMessage.value = result.message
        _recordingState.value = RecordingState.IDLE
      }
    }
  }

  /**
   * Pauses the current recording.
   */
  fun pauseRecording(context: Context) {
    when (val result = recordingUseCase.pauseRecording()) {
      is RecordingResult.Success -> {
        durationTracker.pause()
        _recordingState.value = RecordingState.PAUSED
      }
      is RecordingResult.Error -> {
        _errorMessage.value = result.message
      }
    }
  }

  /**
   * Resumes recording after a pause.
   */
  fun resumeRecording(context: Context) {
    when (val result = recordingUseCase.resumeRecording(context)) {
      is RecordingResult.Success -> {
        durationTracker.resume()
        _recordingState.value = RecordingState.RECORDING
        // Restart the timer for the recording state
        durationTracker.startDurationTimer(viewModelScope) {
          _recordingState.value == RecordingState.RECORDING
        }
      }
      is RecordingResult.Error -> {
        _errorMessage.value = result.message
      }
    }
  }

  /**
   * Finishes the current recording and processes it.
   */
  fun finishRecording(context: Context) {
    when (val result = recordingUseCase.finishRecording()) {
      is RecordingResult.Success -> {
        _recordingState.value = RecordingState.PROCESSING
        processRecording(context)
      }
      is RecordingResult.Error -> {
        Log.e(RecordingConstants.LOG_TAG, "Recording finish failed: ${result.message}")
        _errorMessage.value = result.message
        _recordingState.value = RecordingState.IDLE
      }
    }
  }

  /**
   * Processes and uploads the completed recording.
   */
  private fun processRecording(context: Context) {
    viewModelScope.launch {
      when (val result = recordingUseCase.uploadRecording()) {
        is UploadResult.UploadSuccess -> {
          _successMessage.value = "Recording uploaded successfully!"
          resetToIdleAfterDelay()
        }
        is UploadResult.LocalSave -> {
          _successMessage.value = "Recording saved locally (server unavailable)"
          resetToIdleAfterDelay()
        }
        is UploadResult.Error -> {
          // Check if this is an upload error vs recording error
          val message =
            if (result.message.contains("500") || result.message.contains("server")) {
              "Recording saved locally (server error: ${result.message})"
            } else {
              result.message
            }
          _successMessage.value = message
          resetToIdleAfterDelay()
        }
      }
    }
  }

  /**
   * Clears error and success messages.
   */
  fun clearMessages() {
    _errorMessage.value = null
    _successMessage.value = null
  }

  /**
   * Resets the recording state to idle after a delay.
   */
  private fun resetToIdleAfterDelay() {
    viewModelScope.launch {
      kotlinx.coroutines.delay(RecordingConstants.RESET_DELAY_MS)
      resetRecordingState()
    }
  }

  /**
   * Resets all recording-related state.
   */
  fun resetRecording(context: Context) {
    recordingUseCase.resetRecording()
    resetRecordingState()
  }

  /**
   * Resets the UI state to idle.
   */
  private fun resetRecordingState() {
    _recordingState.value = RecordingState.IDLE
    durationTracker.reset()
    _errorMessage.value = null
    _successMessage.value = null
  }

  override fun onCleared() {
    super.onCleared()
    // Note: We can't call resetRecording here without context
    // The use case will handle cleanup when it's garbage collected
  }
}
