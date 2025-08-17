package com.karaskiewicz.audioai.domain.model

object RecordingConstants {
  // Audio Configuration
  const val AUDIO_ENCODING_BIT_RATE = 128000
  const val AUDIO_SAMPLING_RATE = 44100
  const val AUDIO_FORMAT = "audio/m4a"

  // File Configuration
  const val RECORDING_FILE_PREFIX = "scribely_recording_"
  const val SEGMENT_FILE_PREFIX = "scribely_segment_"
  const val LOCAL_FILE_PREFIX = "scribely_"
  const val FILE_EXTENSION = ".m4a"
  const val RECORDINGS_FOLDER = "Scribely"

  // Timing Configuration
  const val DURATION_UPDATE_INTERVAL_MS = 100L
  const val RESET_DELAY_MS = 3000L
  const val MESSAGE_DISPLAY_TIMEOUT_MS = 3000L
  const val ERROR_MESSAGE_TIMEOUT_MS = 5000L

  // Date Formats
  const val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"
  const val SEGMENT_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS"

  // Log Tags
  const val LOG_TAG = "AudioRecording"
}
