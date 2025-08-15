package com.karaskiewicz.audioai.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.audioai.data.ApiClient
import com.karaskiewicz.audioai.data.PreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

  private val _serverUrl = MutableStateFlow("")
  val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

  private val _isConfigured = MutableStateFlow(false)
  val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

  fun loadConfiguration(context: Context) {
    val preferencesDataStore = PreferencesDataStore(context)
    viewModelScope.launch {
      preferencesDataStore.serverUrl.collect { url ->
        _serverUrl.value = url
        _isConfigured.value = url.isNotBlank()
      }
    }
  }

  fun isApiConfigured(context: Context): Boolean {
    return ApiClient.getInstance().isConfigured(context)
  }
}