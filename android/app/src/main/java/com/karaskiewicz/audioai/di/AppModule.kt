package com.karaskiewicz.audioai.di

import com.karaskiewicz.audioai.data.ApiClient
import com.karaskiewicz.audioai.data.PreferencesDataStore
import com.karaskiewicz.audioai.data.repository.RecordingRepositoryImpl
import com.karaskiewicz.audioai.domain.repository.RecordingRepository
import com.karaskiewicz.audioai.domain.service.FileManager
import com.karaskiewicz.audioai.domain.service.MediaRecorderFactory
import com.karaskiewicz.audioai.domain.usecase.RecordingUseCase
import com.karaskiewicz.audioai.ui.viewmodel.MainViewModel
import com.karaskiewicz.audioai.ui.viewmodel.SettingsViewModel
import com.karaskiewicz.audioai.ui.viewmodel.ShareViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

  // Data Layer
  single { PreferencesDataStore(androidContext()) }
  single { ApiClient.getInstance() }

  // Domain Layer - Services
  single { FileManager() }
  single { MediaRecorderFactory() }

  // Domain Layer - Repository
  single<RecordingRepository> {
    RecordingRepositoryImpl(
      context = androidContext(),
      fileManager = get(),
    )
  }

  // Domain Layer - Use Cases
  single {
    RecordingUseCase(
      recordingRepository = get(),
      mediaRecorderFactory = get(),
      fileManager = get(),
    )
  }

  // Presentation Layer - ViewModels
  viewModel {
    MainViewModel(
      recordingUseCase = get(),
      preferencesDataStore = get(),
    )
  }

  viewModel {
    SettingsViewModel(
      preferencesDataStore = get(),
    )
  }

  viewModel {
    ShareViewModel(
      recordingUseCase = get(),
    )
  }
}
