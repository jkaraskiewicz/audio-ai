package com.karaskiewicz.scribely

import android.app.Application
import com.karaskiewicz.scribely.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class ScribelyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    startKoin {
      androidLogger(Level.ERROR)
      androidContext(this@ScribelyApplication)
      modules(modules)
    }
  }
}
