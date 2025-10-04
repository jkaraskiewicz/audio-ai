package com.karaskiewicz.scribely.domain.usecase

import android.media.MediaRecorder
import java.io.File

/**
 * Internal machine state - holds MediaRecorder and File references
 * Follows State Pattern and Single Responsibility Principle
 */
internal sealed class MachineState {
  object Idle : MachineState()

  data class Recording(val mediaRecorder: MediaRecorder, val outputFile: File) : MachineState()

  data class Paused(val mediaRecorder: MediaRecorder, val outputFile: File) : MachineState()

  data class Finished(val outputFile: File) : MachineState()
}

/**
 * State machine for managing recording state transitions
 */
internal class RecordingStateMachine {
  private var state: MachineState = MachineState.Idle

  fun getCurrentState(): MachineState = state

  fun transitionToRecording(
    mediaRecorder: MediaRecorder,
    outputFile: File,
  ) {
    state = MachineState.Recording(mediaRecorder, outputFile)
  }

  fun transitionToPaused(
    mediaRecorder: MediaRecorder,
    outputFile: File,
  ) {
    state = MachineState.Paused(mediaRecorder, outputFile)
  }

  fun transitionToFinished(outputFile: File) {
    state = MachineState.Finished(outputFile)
  }

  fun transitionToIdle() {
    state = MachineState.Idle
  }

  fun isIdle(): Boolean = state is MachineState.Idle

  fun isRecording(): Boolean = state is MachineState.Recording

  fun isPaused(): Boolean = state is MachineState.Paused

  fun isFinished(): Boolean = state is MachineState.Finished
}
