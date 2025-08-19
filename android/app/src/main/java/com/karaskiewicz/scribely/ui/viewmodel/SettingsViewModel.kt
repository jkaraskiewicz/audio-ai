package com.karaskiewicz.scribely.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaskiewicz.scribely.network.ApiServiceManager
import com.karaskiewicz.scribely.utils.PreferencesDataStore
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
  private val apiServiceManager: ApiServiceManager,
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

  fun testConnection() {
    viewModelScope.launch {
      _connectionTestState.value = ConnectionTestState(isLoading = true)

      try {
        val apiService = apiServiceManager.createApiService()
        val response = apiService.healthCheck()
        if (response.isSuccessful) {
          _connectionTestState.value = ConnectionTestState(isSuccess = true)
        } else {
          _connectionTestState.value =
            ConnectionTestState(
              error = "HTTP ${response.code()}: ${response.message()}",
            )
        }
      } catch (e: Exception) {
        _connectionTestState.value =
          ConnectionTestState(
            error = e.message ?: "Unknown error occurred",
          )
      }
    }
  }

  fun clearConnectionTestState() {
    _connectionTestState.value = ConnectionTestState()
  }
}
