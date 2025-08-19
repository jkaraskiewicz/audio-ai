package com.karaskiewicz.scribely.domain.model

object RecordingConstants {
  // Audio Configuration
  const val AUDIO_ENCODING_BIT_RATE = 128000
  const val AUDIO_SAMPLING_RATE = 16000 // AMR-WB sampling rate
  const val AUDIO_FORMAT_RECORDING = "audio/3gpp" // For recording segments
  const val AUDIO_FORMAT_UPLOAD = "audio/m4a" // For server upload

  // File Configuration
  const val RECORDING_FILE_PREFIX = "scribely_recording_"
  const val SEGMENT_FILE_PREFIX = "scribely_segment_"
  const val LOCAL_FILE_PREFIX = "scribely_"
  const val RECORDING_FILE_EXTENSION = ".3gp" // For recording segments
  const val UPLOAD_FILE_EXTENSION = ".m4a" // For final upload file
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
