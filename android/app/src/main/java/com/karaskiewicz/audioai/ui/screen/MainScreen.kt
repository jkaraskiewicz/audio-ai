package com.karaskiewicz.audioai.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karaskiewicz.audioai.BuildConfig
import com.karaskiewicz.audioai.R
import com.karaskiewicz.audioai.ui.theme.AudioAITheme
import com.karaskiewicz.audioai.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
  onNavigateToSettings: () -> Unit,
  viewModel: MainViewModel = viewModel(),
) {
  val context = LocalContext.current
  val serverUrl by viewModel.serverUrl.collectAsState()
  val isConfigured by viewModel.isConfigured.collectAsState()

  LaunchedEffect(context) {
    viewModel.loadConfiguration(context)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Spacer(modifier = Modifier.height(32.dp))

    // App Icon
    Image(
      painter = painterResource(id = R.drawable.ic_launcher),
      contentDescription = stringResource(R.string.app_name),
      modifier = Modifier.size(80.dp),
    )

    // App Title
    Text(
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
    )

    // App Description
    Text(
      text = stringResource(R.string.app_description),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 24.dp),
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Instructions Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(
          text = "How to use:",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
        )

        val instructions = listOf(
          "1. Select text in any app and tap Share",
          "2. Select files in file manager and tap Share",
          "3. Choose 'Audio AI' from the list",
          "4. Content will be processed by your backend",
        )

        instructions.forEach { instruction ->
          Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Settings Button
    Button(
      onClick = onNavigateToSettings,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = null,
        modifier = Modifier.padding(end = 8.dp),
      )
      Text(stringResource(R.string.settings))
    }

    // Version Info
    Text(
      text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  AudioAITheme {
    MainScreen(onNavigateToSettings = {})
  }
}
