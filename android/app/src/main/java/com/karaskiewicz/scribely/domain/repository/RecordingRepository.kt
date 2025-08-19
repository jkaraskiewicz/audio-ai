package com.karaskiewicz.scribely.domain.repository

import com.karaskiewicz.scribely.domain.model.UploadResult
import java.io.File

/**
 * Repository interface for recording operations.
 * Follows Repository pattern and Dependency Inversion Principle.
 */
interface RecordingRepository {
  suspend fun uploadRecording(audioFile: File): UploadResult
}
