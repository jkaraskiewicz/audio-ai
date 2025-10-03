package com.karaskiewicz.scribely.domain.repository

import com.karaskiewicz.scribely.domain.model.RecordingConstants
import com.karaskiewicz.scribely.domain.model.UploadResult
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.utils.safeSuspendNetworkCall
import com.karaskiewicz.scribely.utils.safeFileOperation
import com.karaskiewicz.scribely.utils.mapToResult
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
  private val apiServiceManager: ApiServiceManager,
) : RecordingRepository {
  override suspend fun uploadRecording(audioFile: File): UploadResult {
    val uploadResult =
      safeSuspendNetworkCall("upload recording") {
        val apiService = apiServiceManager.createApiService()
        uploadToServer(apiService, audioFile)
      }

    return uploadResult.mapToResult(
      onSuccess = { response ->
        when {
          // Backend returns 200 with status='processing' for async processing
          response.isSuccessful && response.body()?.status == "processing" -> {
            Timber.d("Recording uploaded successfully (async processing)")
            UploadResult.UploadSuccess
          }
          // Legacy: backend returns isSuccess=true for synchronous processing
          response.isSuccessful && response.body()?.isSuccess == true -> {
            Timber.d("Recording uploaded successfully (sync processing)")
            UploadResult.UploadSuccess
          }
          else -> {
            Timber.w("Upload failed with status: ${response.code()}, saving locally")
            saveLocally(audioFile)
          }
        }
      },
      onFailure = { exception ->
        Timber.e(exception, "Upload failed with exception")
        saveLocally(audioFile)
      },
    )
  }

  private suspend fun uploadToServer(
    apiService: com.karaskiewicz.scribely.network.ApiService,
    audioFile: File,
  ) = runCatching {
    val requestFile = audioFile.asRequestBody(RecordingConstants.AUDIO_FORMAT_UPLOAD.toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
    apiService.processFile(filePart)
  }.getOrThrow()

  private fun saveLocally(audioFile: File): UploadResult {
    return safeFileOperation("save recording locally") {
      val localFile = fileManager.createPublicFile()
      audioFile.copyTo(localFile, overwrite = true)
      localFile
    }.mapToResult(
      onSuccess = { localFile ->
        Timber.d("Recording saved locally: ${localFile.absolutePath}")
        UploadResult.LocalSave(localFile.absolutePath)
      },
      onFailure = { exception ->
        Timber.e(exception, "Failed to save recording locally")
        UploadResult.Error("Failed to save recording: ${exception.message}", exception)
      },
    )
  }
}
