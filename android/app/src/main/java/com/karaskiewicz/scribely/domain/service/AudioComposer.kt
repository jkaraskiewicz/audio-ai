package com.karaskiewicz.scribely.domain.service

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import timber.log.Timber

/**
 * Handles audio file composition operations including combining segments
 * and converting between formats.
 */
class AudioComposer {
  /**
   * Combines multiple 3GP audio files into a single 3GP file
   */
  fun combineAudioFiles(
    segments: List<File>,
    outputFile: File,
  ): Boolean {
    if (segments.isEmpty()) return false
    if (segments.size == 1) {
      return copyFile(segments.first(), outputFile)
    }

    return try {
      combine3GPFiles(segments, outputFile)
    } catch (e: Exception) {
      Timber.e(e, "Failed to combine audio files")
      false
    }
  }

  /**
   * Converts a 3GP file to M4A format for upload
   */
  fun convertToM4A(
    inputFile: File,
    outputFile: File,
  ): Boolean {
    return try {
      convertWith3GPToM4A(inputFile, outputFile)
    } catch (e: Exception) {
      Timber.e(e, "Failed to convert 3GP to M4A")
      false
    }
  }

  private fun combine3GPFiles(
    segments: List<File>,
    outputFile: File,
  ): Boolean {
    return try {
      val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_3GPP)
      var trackIndex = -1
      var totalDurationUs = 0L

      // Process each segment
      for ((index, segment) in segments.withIndex()) {
        val extractor = MediaExtractor()
        extractor.setDataSource(segment.absolutePath)

        if (extractor.trackCount == 0) {
          Timber.w("Segment $index has no tracks, skipping")
          extractor.release()
          continue
        }

        // Get the first audio track
        val format = extractor.getTrackFormat(0)
        extractor.selectTrack(0)

        // Add track to muxer for first segment
        if (trackIndex == -1) {
          trackIndex = muxer.addTrack(format)
          muxer.start()
        }

        // Copy data from this segment
        val buffer = ByteBuffer.allocate(1024 * 1024) // 1MB buffer
        val info = MediaCodec.BufferInfo()

        while (true) {
          val sampleSize = extractor.readSampleData(buffer, 0)
          if (sampleSize < 0) break

          info.presentationTimeUs = extractor.sampleTime + totalDurationUs
          info.flags = extractor.sampleFlags
          info.size = sampleSize
          info.offset = 0

          muxer.writeSampleData(trackIndex, buffer, info)
          extractor.advance()
        }

        // Update total duration for next segment
        if (format.containsKey(MediaFormat.KEY_DURATION)) {
          totalDurationUs += format.getLong(MediaFormat.KEY_DURATION)
        }

        extractor.release()
      }

      muxer.stop()
      muxer.release()
      true
    } catch (e: Exception) {
      Timber.e(e, "Error in combine3GPFiles")
      false
    }
  }

  private fun convertWith3GPToM4A(
    inputFile: File,
    outputFile: File,
  ): Boolean {
    return try {
      val extractor = MediaExtractor()
      extractor.setDataSource(inputFile.absolutePath)

      if (extractor.trackCount == 0) {
        extractor.release()
        return false
      }

      val format = extractor.getTrackFormat(0)
      extractor.selectTrack(0)

      val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
      val trackIndex = muxer.addTrack(format)
      muxer.start()

      val buffer = ByteBuffer.allocate(1024 * 1024)
      val info = MediaCodec.BufferInfo()

      while (true) {
        val sampleSize = extractor.readSampleData(buffer, 0)
        if (sampleSize < 0) break

        info.presentationTimeUs = extractor.sampleTime
        info.flags = extractor.sampleFlags
        info.size = sampleSize
        info.offset = 0

        muxer.writeSampleData(trackIndex, buffer, info)
        extractor.advance()
      }

      muxer.stop()
      muxer.release()
      extractor.release()
      true
    } catch (e: Exception) {
      Timber.e(e, "Error converting 3GP to M4A")
      false
    }
  }

  private fun copyFile(
    source: File,
    destination: File,
  ): Boolean {
    return try {
      FileInputStream(source).use { input ->
        FileOutputStream(destination).use { output ->
          input.copyTo(output)
        }
      }
      true
    } catch (e: Exception) {
      Timber.e(e, "Failed to copy file")
      false
    }
  }
}
