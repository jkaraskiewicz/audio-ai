package com.karaskiewicz.scribely.ui.utils

/**
 * Duration formatting utility
 * Follows Single Responsibility Principle - only handles time formatting
 */
object DurationFormatter {
  /**
   * Formats milliseconds to MM:SS format
   */
  fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
  }
}
