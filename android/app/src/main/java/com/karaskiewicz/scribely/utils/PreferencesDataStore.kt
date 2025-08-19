package com.karaskiewicz.scribely.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesDataStore(private val context: Context) {
  val serverUrl: Flow<String> =
    context.dataStore.data
      .map { preferences ->
        preferences[SERVER_URL_KEY] ?: DEFAULT_SERVER_URL
      }

  suspend fun updateServerUrl(url: String) {
    context.dataStore.edit { settings ->
      settings[SERVER_URL_KEY] = url
    }
  }

  suspend fun getServerUrl(): String = serverUrl.first()

  private companion object {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val SERVER_URL_KEY = stringPreferencesKey("server_url")
    const val DEFAULT_SERVER_URL = "http://192.168.1.100:1993"
  }
}
