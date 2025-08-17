package com.karaskiewicz.audioai.data.repository

import android.content.Context
import android.util.Log
import com.karaskiewicz.audioai.data.ApiClient
import com.karaskiewicz.audioai.data.PreferencesDataStore
import com.karaskiewicz.audioai.domain.model.RecordingConstants
import com.karaskiewicz.audioai.domain.model.UploadResult
import com.karaskiewicz.audioai.domain.repository.RecordingRepository
import com.karaskiewicz.audioai.domain.service.FileManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Implementation of RecordingRepository.
 * Handles uploading recordings and fallback to local storage.
 */
class RecordingRepositoryImpl(
  private val context: Context,
  private val fileManager: FileManager = FileManager(),
) : RecordingRepository {

  override suspend fun uploadRecording(audioFile: File): UploadResult {
    return try {
      val preferencesDataStore = PreferencesDataStore(context)
      val serverUrl = preferencesDataStore.getServerUrl()

      if (serverUrl.isBlank()) {
        return saveLocally(audioFile)
      }

      val apiService = ApiClient.getInstance().getApiService(context)
        ?: return saveLocally(audioFile)

      val response = uploadToServer(apiService, audioFile)

      if (response.isSuccessful && response.body()?.isSuccess == true) {
        Log.d(RecordingConstants.LOG_TAG, "Recording uploaded successfully")
        UploadResult.UploadSuccess
      } else {
        Log.w(RecordingConstants.LOG_TAG, "Upload failed, saving locally")
        saveLocally(audioFile)
      }
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Upload failed with exception", e)
      saveLocally(audioFile)
    }
  }

  private suspend fun uploadToServer(apiService: com.karaskiewicz.audioai.data.ApiService, audioFile: File) =
    try {
      val requestFile = audioFile.asRequestBody(RecordingConstants.AUDIO_FORMAT.toMediaTypeOrNull())
      val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
      apiService.processFile(filePart)
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Network upload failed", e)
      throw e
    }

  private fun saveLocally(audioFile: File): UploadResult {
    return try {
      val localFile = fileManager.createPublicFile()
      audioFile.copyTo(localFile, overwrite = true)

      Log.d(RecordingConstants.LOG_TAG, "Recording saved locally: ${localFile.absolutePath}")
      UploadResult.LocalSave(localFile.absolutePath)
    } catch (e: Exception) {
      Log.e(RecordingConstants.LOG_TAG, "Failed to save recording locally", e)
      UploadResult.Error("Failed to save recording: ${e.message}", e)
    }
  }
}
