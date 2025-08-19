package com.karaskiewicz.scribely.domain.repository

import com.karaskiewicz.scribely.domain.model.RecordingConstants
import com.karaskiewicz.scribely.domain.model.UploadResult
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.network.ApiService
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber

/**
 * Implementation of RecordingRepository.
 * Handles uploading recordings and fallback to local storage.
 */
class RecordingRepositoryImpl(
  private val fileManager: FileManager,
  private val apiService: ApiService,
) : RecordingRepository {
  override suspend fun uploadRecording(audioFile: File): UploadResult {
    return try {
      val response = uploadToServer(apiService, audioFile)

      if (response.isSuccessful && response.body()?.isSuccess == true) {
        Timber.d("Recording uploaded successfully")
        UploadResult.UploadSuccess
      } else {
        Timber.w("Upload failed, saving locally")
        saveLocally(audioFile)
      }
    } catch (e: Exception) {
      Timber.e(e, "Upload failed with exception")
      saveLocally(audioFile)
    }
  }

  private suspend fun uploadToServer(
    apiService: ApiService,
    audioFile: File,
  ) = try {
    val requestFile = audioFile.asRequestBody(RecordingConstants.AUDIO_FORMAT.toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
    apiService.processFile(filePart)
  } catch (e: Exception) {
    Timber.e(e, "Network upload failed")
    throw e
  }

  private fun saveLocally(audioFile: File): UploadResult =
    try {
      val localFile = fileManager.createPublicFile()
      audioFile.copyTo(localFile, overwrite = true)

      Timber.d("Recording saved locally: ${localFile.absolutePath}")
      UploadResult.LocalSave(localFile.absolutePath)
    } catch (e: Exception) {
      Timber.e(e, "Failed to save recording locally")
      UploadResult.Error("Failed to save recording: ${e.message}", e)
    }
}
