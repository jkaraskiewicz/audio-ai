package com.karaskiewicz.audioai.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesDataStore(private val context: Context) {

  companion object {
    val SERVER_URL_KEY = stringPreferencesKey("server_url")
    private const val DEFAULT_SERVER_URL = "http://192.168.1.100:1993"
  }

  val serverUrl: Flow<String> = context.dataStore.data
    .map { preferences ->
      preferences[SERVER_URL_KEY] ?: DEFAULT_SERVER_URL
    }

  suspend fun updateServerUrl(url: String) {
    context.dataStore.edit { settings ->
      settings[SERVER_URL_KEY] = url
    }
  }

  suspend fun getServerUrl(): String {
    return serverUrl.first()
  }
}
