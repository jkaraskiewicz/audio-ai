package com.karaskiewicz.scribely.domain.service

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.karaskiewicz.scribely.domain.model.RecordingConstants
import java.io.File

class MediaRecorderFactory(private val context: Context) {
  fun createMediaRecorder(outputFile: File): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      createModernMediaRecorder(outputFile)
    } else {
      createLegacyMediaRecorder(outputFile)
    }
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun createModernMediaRecorder(outputFile: File): MediaRecorder {
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
