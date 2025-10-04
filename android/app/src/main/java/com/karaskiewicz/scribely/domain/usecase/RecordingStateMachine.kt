package com.karaskiewicz.scribely.domain.usecase

import android.media.MediaRecorder
import java.io.File

/**
 * Recording state machine - manages recording lifecycle states
 * Follows State Pattern and Single Responsibility Principle
 */
internal sealed class RecordingState {
  object Idle : RecordingState()

  data class Recording(val mediaRecorder: MediaRecorder, val outputFile: File) : RecordingState()

  data class Paused(val mediaRecorder: MediaRecorder, val outputFile: File) : RecordingState()

  data class Finished(val outputFile: File) : RecordingState()
}

/**
 * State machine for managing recording state transitions
 */
internal class RecordingStateMachine {
  private var state: RecordingState = RecordingState.Idle

  fun getCurrentState(): RecordingState = state

  fun transitionToRecording(
    mediaRecorder: MediaRecorder,
    outputFile: File,
  ) {
    state = RecordingState.Recording(mediaRecorder, outputFile)
  }

  fun transitionToPaused(
    mediaRecorder: MediaRecorder,
    outputFile: File,
  ) {
    state = RecordingState.Paused(mediaRecorder, outputFile)
  }

  fun transitionToFinished(outputFile: File) {
    state = RecordingState.Finished(outputFile)
  }

  fun transitionToIdle() {
    state = RecordingState.Idle
  }

  fun isIdle(): Boolean = state is RecordingState.Idle

  fun isRecording(): Boolean = state is RecordingState.Recording

  fun isPaused(): Boolean = state is RecordingState.Paused

  fun isFinished(): Boolean = state is RecordingState.Finished
}
