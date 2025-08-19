package com.karaskiewicz.scribely.di

import com.karaskiewicz.scribely.domain.repository.RecordingRepository
import com.karaskiewicz.scribely.domain.repository.RecordingRepositoryImpl
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.domain.service.MediaRecorderFactory
import com.karaskiewicz.scribely.domain.usecase.RecordingUseCase
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.ui.viewmodel.MainViewModel
import com.karaskiewicz.scribely.ui.viewmodel.SettingsViewModel
import com.karaskiewicz.scribely.ui.viewmodel.ShareViewModel
import com.karaskiewicz.scribely.utils.PreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val utilsModule =
  module {
    single { PreferencesDataStore(androidContext()) }
    single { FileManager(androidContext()) }
    single { MediaRecorderFactory(androidContext()) }
  }

val networkingModule =
  module {
    single { ApiServiceManager(get()) }
    single<RecordingRepository> { RecordingRepositoryImpl(get(), get()) }
  }

val useCasesModule =
  module {
    single { RecordingUseCase(get(), get(), get()) }
  }

val viewModelsModule =
  module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { ShareViewModel(get(), get()) }
  }

val modules: List<Module> = listOf(viewModelsModule, useCasesModule, networkingModule, utilsModule)
