package com.karaskiewicz.audioai.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.surfaceContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Hero Section
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      ) {
        Column(
          modifier = Modifier.padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // App Icon with background
          Box(
            modifier = Modifier
              .size(80.dp)
              .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.mipmap.ic_launcher),
              contentDescription = stringResource(R.string.app_name),
              modifier = Modifier.size(64.dp),
            )
          }

          // App Title
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // App Description
          Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
          )
        }
      }

      // Feature Cards
      val features = listOf(
        Triple(Icons.Default.Share, "Share Content", "Select text or files and share to Audio AI"),
        Triple(Icons.Default.Mic, "AI Processing", "Advanced transcription and content analysis"),
        Triple(Icons.Default.PlayArrow, "Auto-Save", "Processed content saved as organized notes")
      )

      features.forEach { (icon, title, description) ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        ) {
          Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                  RoundedCornerShape(10.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            
            Column {
              Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Settings Button
      Button(
        onClick = onNavigateToSettings,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = ButtonDefaults.ContentPadding
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          stringResource(R.string.settings),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Medium
        )
      }

      // Version Info
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
      ) {
        Text(
          text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
          fontWeight = FontWeight.Medium
        )
      }
      
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  AudioAITheme {
    MainScreen(onNavigateToSettings = {})
  }
}
