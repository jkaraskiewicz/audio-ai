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

data class ConnectionTestState(
  val isLoading: Boolean = false,
  val isSuccess: Boolean? = null,
  val error: String? = null,
)

class SettingsViewModel(
  private val preferencesDataStore: PreferencesDataStore,
) : ViewModel() {

  private val _serverUrl = MutableStateFlow("")
  val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

  private val _connectionTestState = MutableStateFlow(ConnectionTestState())
  val connectionTestState: StateFlow<ConnectionTestState> = _connectionTestState.asStateFlow()

  fun loadSettings() {
    viewModelScope.launch {
      preferencesDataStore.serverUrl.collect { url ->
        _serverUrl.value = url
      }
    }
  }

  fun updateServerUrl(url: String) {
    viewModelScope.launch {
      preferencesDataStore.updateServerUrl(url.trim())
    }
  }

  fun testConnection(context: Context) {
    val apiClient = ApiClient.getInstance()

    if (!apiClient.isConfigured(context)) {
      _connectionTestState.value = ConnectionTestState(
        error = "Please configure server URL first",
      )
      return
    }

    val apiService = apiClient.getApiService(context)
    if (apiService == null) {
      _connectionTestState.value = ConnectionTestState(
        error = "Failed to create API service",
      )
      return
    }

    viewModelScope.launch {
      _connectionTestState.value = ConnectionTestState(isLoading = true)

      try {
        val response = apiService.healthCheck()
        if (response.isSuccessful) {
          _connectionTestState.value = ConnectionTestState(isSuccess = true)
        } else {
          _connectionTestState.value = ConnectionTestState(
            error = "HTTP ${response.code()}: ${response.message()}",
          )
        }
      } catch (e: Exception) {
        _connectionTestState.value = ConnectionTestState(
          error = e.message ?: "Unknown error occurred",
        )
      }
    }
  }

  fun clearConnectionTestState() {
    _connectionTestState.value = ConnectionTestState()
  }
}
