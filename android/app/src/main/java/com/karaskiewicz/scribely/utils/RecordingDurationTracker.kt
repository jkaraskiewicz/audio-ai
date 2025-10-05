package com.karaskiewicz.scribely.utils

import com.karaskiewicz.scribely.domain.model.RecordingConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Tracks recording duration with pause/resume support
 * Follows Single Responsibility Principle - only handles duration tracking
 */
class RecordingDurationTracker {
  private var recordingStartTime: Long = 0
  private var pausedDuration: Long = 0
  private var lastPauseTime: Long = 0

  private val _duration = MutableStateFlow(0L)
  val duration: StateFlow<Long> = _duration

  /**
   * Starts tracking from zero
   */
  fun start() {
    recordingStartTime = System.currentTimeMillis()
    pausedDuration = 0
    lastPauseTime = 0
    _duration.value = 0L
  }

  /**
   * Marks the pause time
   */
  fun pause() {
    lastPauseTime = System.currentTimeMillis()
  }

  /**
   * Resumes tracking by accumulating paused duration
   */
  fun resume() {
    pausedDuration += System.currentTimeMillis() - lastPauseTime
  }

  /**
   * Resets all tracking values
   */
  fun reset() {
    recordingStartTime = 0
    pausedDuration = 0
    lastPauseTime = 0
    _duration.value = 0L
  }

  /**
   * Starts a coroutine to update duration while recording
   * @param scope Coroutine scope to launch the timer in
   * @param isRecordingActive Lambda that returns true while recording is active
   */
  fun startDurationTimer(
    scope: CoroutineScope,
    isRecordingActive: () -> Boolean,
  ) {
    scope.launch {
      while (isRecordingActive()) {
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - recordingStartTime - pausedDuration
        _duration.value = elapsed
        delay(RecordingConstants.DURATION_UPDATE_INTERVAL_MS)
      }
    }
  }

  /**
   * Gets the current duration value
   */
  fun getCurrentDuration(): Long = _duration.value
}
