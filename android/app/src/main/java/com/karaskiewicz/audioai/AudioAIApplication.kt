package com.karaskiewicz.audioai

import android.app.Application
import com.karaskiewicz.audioai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AudioAIApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    instance = this

    startKoin {
      androidLogger(Level.ERROR)
      androidContext(this@AudioAIApplication)
      modules(appModule)
    }
  }

  companion object {
    lateinit var instance: AudioAIApplication
      private set
  }
}
