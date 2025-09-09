package com.karaskiewicz.scribely

import android.app.Application

/**
 * Test application class that doesn't initialize Koin
 * to avoid conflicts in unit tests.
 */
class TestApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Don't initialize Koin or other DI frameworks in tests
  }
}
