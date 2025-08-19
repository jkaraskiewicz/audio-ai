package com.karaskiewicz.scribely.di

import com.karaskiewicz.scribely.domain.repository.RecordingRepository
import com.karaskiewicz.scribely.domain.repository.RecordingRepositoryImpl
import com.karaskiewicz.scribely.domain.service.FileManager
import com.karaskiewicz.scribely.domain.service.MediaRecorderFactory
import com.karaskiewicz.scribely.domain.usecase.RecordingUseCase
import com.karaskiewicz.scribely.network.ApiService
import com.karaskiewicz.scribely.ui.viewmodel.MainViewModel
import com.karaskiewicz.scribely.ui.viewmodel.SettingsViewModel
import com.karaskiewicz.scribely.ui.viewmodel.ShareViewModel
import com.karaskiewicz.scribely.utils.PreferencesDataStore
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val utilsModule = module {
  single { PreferencesDataStore(androidContext()) }
  single { FileManager(androidContext()) }
  single { MediaRecorderFactory(androidContext()) }
}

val networkingModule = module {
  single<Retrofit> { createRetrofit(get()) }
  single<ApiService> { get<Retrofit>().create(ApiService::class.java) }
  single<RecordingRepository> { RecordingRepositoryImpl(get(), get()) }
}

val useCasesModule = module {
  single { RecordingUseCase(get(), get(), get()) }
}

val viewModelsModule = module {
  viewModel { MainViewModel(get(), get()) }
  viewModel { SettingsViewModel(get()) }
  viewModel { ShareViewModel(get()) }
}

private fun createRetrofit(preferencesDataStore: PreferencesDataStore): Retrofit {
  val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
  }

  val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  val serverUrl = runBlocking {
    preferencesDataStore.getServerUrl()
  }

  return Retrofit.Builder()
    .baseUrl(serverUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
}

val modules: List<Module> = listOf(viewModelsModule, useCasesModule, networkingModule, utilsModule)
