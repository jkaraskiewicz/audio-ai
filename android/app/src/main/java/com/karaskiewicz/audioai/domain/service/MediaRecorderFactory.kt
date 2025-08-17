package com.karaskiewicz.audioai.domain.service

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.karaskiewicz.audioai.domain.model.RecordingConstants
import java.io.File

/**
 * Factory for creating MediaRecorder instances with proper configuration.
 * Follows Factory pattern and handles API version differences.
 */
class MediaRecorderFactory {

  /**
   * Creates and configures a MediaRecorder instance.
   *
   * @param context Android context (required for API 31+)
   * @param outputFile File where recording will be saved
   * @return Configured MediaRecorder instance
   */
  fun createMediaRecorder(context: Context, outputFile: File): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      createModernMediaRecorder(context, outputFile)
    } else {
      createLegacyMediaRecorder(outputFile)
    }
  }

  private fun createModernMediaRecorder(context: Context, outputFile: File): MediaRecorder {
    return MediaRecorder(context).apply {
      configureRecorder(outputFile)
    }
  }

  @Suppress("DEPRECATION")
  private fun createLegacyMediaRecorder(outputFile: File): MediaRecorder {
    return MediaRecorder().apply {
      configureRecorder(outputFile)
    }
  }

  private fun MediaRecorder.configureRecorder(outputFile: File) {
    setAudioSource(MediaRecorder.AudioSource.MIC)
    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    setOutputFile(outputFile.absolutePath)
    setAudioEncodingBitRate(RecordingConstants.AUDIO_ENCODING_BIT_RATE)
    setAudioSamplingRate(RecordingConstants.AUDIO_SAMPLING_RATE)
  }
}
