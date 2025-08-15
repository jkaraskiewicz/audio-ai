package com.karaskiewicz.audioai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karaskiewicz.audioai.BuildConfig
import com.karaskiewicz.audioai.R
import com.karaskiewicz.audioai.ui.theme.AudioAITheme
import com.karaskiewicz.audioai.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  viewModel: SettingsViewModel = viewModel()
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val serverUrl by viewModel.serverUrl.collectAsState()
  val connectionTestState by viewModel.connectionTestState.collectAsState()

  var urlInputValue by remember { mutableStateOf("") }

  LaunchedEffect(context) {
    viewModel.loadSettings(context)
  }

  LaunchedEffect(serverUrl) {
    if (urlInputValue.isEmpty()) {
      urlInputValue = serverUrl
    }
  }

  LaunchedEffect(connectionTestState) {
    connectionTestState.error?.let { error ->
      scope.launch {
        snackbarHostState.showSnackbar(error)
        viewModel.clearConnectionTestState()
      }
    }
    if (connectionTestState.isSuccess == true) {
      scope.launch {
        snackbarHostState.showSnackbar("Connection successful!")
        viewModel.clearConnectionTestState()
      }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back"
            )
          }
        }
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Server Configuration Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = stringResource(R.string.server_configuration),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          OutlinedTextField(
            value = urlInputValue,
            onValueChange = { urlInputValue = it },
            label = { Text(stringResource(R.string.server_url)) },
            supportingText = { Text(stringResource(R.string.server_url_summary)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Button(
            onClick = {
              if (urlInputValue != serverUrl) {
                viewModel.updateServerUrl(context, urlInputValue)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = urlInputValue != serverUrl && urlInputValue.isNotBlank()
          ) {
            Text("Save URL")
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = { viewModel.testConnection(context) },
              enabled = !connectionTestState.isLoading && serverUrl.isNotBlank()
            ) {
              Text(stringResource(R.string.test_connection))
            }

            if (connectionTestState.isLoading) {
              CircularProgressIndicator()
            }
          }
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      // About Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = stringResource(R.string.about),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyMedium
          )

          Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
  AudioAITheme {
    SettingsScreen(onNavigateBack = {})
  }
}