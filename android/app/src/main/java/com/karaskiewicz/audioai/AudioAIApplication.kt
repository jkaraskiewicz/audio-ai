package com.karaskiewicz.audioai

import android.app.Application

class AudioAIApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  companion object {
    lateinit var instance: AudioAIApplication
      private set
  }
}