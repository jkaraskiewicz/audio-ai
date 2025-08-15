# Audio AI Android App - Architecture & Maintenance Guide

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Key Components](#key-components)
- [Development Guidelines](#development-guidelines)
- [Testing Strategy](#testing-strategy)
- [Maintenance Tasks](#maintenance-tasks)
- [Troubleshooting](#troubleshooting)

## Overview

The Audio AI Android app is a share target application built with modern Android development practices. It allows users to share text and files from any Android app to a configurable backend server for AI processing.

**Key Technologies:**
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Async**: Kotlin Coroutines + Flow
- **Networking**: Retrofit + OkHttp
- **Storage**: DataStore (replacing SharedPreferences)
- **Testing**: JUnit, Mockito, Compose Testing

## Architecture

### MVVM Pattern

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel      │    │  Data Layer     │
│  (Compose)      │◄──►│   (StateFlow)   │◄──►│ (Repository)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                              ┌─────────────────┐
                                              │   Data Sources  │
                                              │ • API Service   │
                                              │ • DataStore     │
                                              │ • FileUtils     │
                                              └─────────────────┘
```

### Data Flow

1. **UI Layer**: Compose screens observe ViewModel state
2. **ViewModel**: Manages UI state and business logic using StateFlow
3. **Repository Pattern**: ViewModels interact with data layer
4. **Data Sources**: API calls, local storage, file operations

## Project Structure

```
app/src/main/java/com/karaskiewicz/audioai/
├── ui/
│   ├── screen/           # Compose screens
│   │   ├── MainScreen.kt
│   │   ├── SettingsScreen.kt
│   │   └── ShareScreen.kt
│   ├── theme/            # Material Design theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/        # ViewModels
│       ├── MainViewModel.kt
│       ├── SettingsViewModel.kt
│       └── ShareViewModel.kt
├── data/                 # Data layer
│   ├── ApiClient.kt      # Retrofit client
│   ├── ApiService.kt     # API interface
│   ├── ApiModels.kt      # Data classes
│   └── PreferencesDataStore.kt
├── utils/                # Utility classes
│   └── FileUtils.kt
├── MainActivity.kt       # Main entry point
├── SettingsActivity.kt   # Settings screen
├── ShareActivity.kt      # Share target handler
└── AudioAIApplication.kt # Application class
```

## Key Components

### 1. Activities

#### MainActivity
- **Purpose**: App launcher and main interface
- **Framework**: ComponentActivity with Compose
- **Navigation**: Launches SettingsActivity

#### SettingsActivity  
- **Purpose**: Server configuration
- **Features**: URL input, connection testing, validation

#### ShareActivity
- **Purpose**: Handle shared content from other apps
- **Intent Filters**: 
  - `ACTION_SEND` (text/files)
  - `ACTION_SEND_MULTIPLE` (multiple files)

### 2. ViewModels

#### MainViewModel
```kotlin
class MainViewModel : ViewModel() {
  private val _serverUrl = MutableStateFlow("")
  val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()
  
  private val _isConfigured = MutableStateFlow(false)
  val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()
}
```

#### SettingsViewModel
- Manages server URL configuration
- Handles connection testing with loading states
- Provides validation feedback

#### ShareViewModel
- Processes shared content (text/files)
- Manages upload progress and error states
- Handles different content types

### 3. Data Layer

#### ApiClient (Singleton)
```kotlin
class ApiClient private constructor() {
  fun getApiService(context: Context): ApiService?
  fun isConfigured(context: Context): Boolean
}
```

#### PreferencesDataStore
```kotlin
class PreferencesDataStore(private val context: Context) {
  val serverUrl: Flow<String>
  suspend fun updateServerUrl(url: String)
  suspend fun getServerUrl(): String
}
```

#### API Models
- `ProcessTextRequest`: Text processing payload
- `ProcessResponse`: Server response model
- `HealthResponse`: Health check response

### 4. UI Components

#### Compose Screens
- **Material Design 3** components
- **Reactive UI** with StateFlow observation
- **Error handling** with Snackbars
- **Loading states** with progress indicators

## Development Guidelines

### Code Style

1. **Formatting**: 2-space indentation (enforced by ktlint)
2. **Naming**: PascalCase for classes, camelCase for functions/variables
3. **Comments**: KDoc for public APIs, inline for complex logic

### State Management

```kotlin
// ✅ Good: Use StateFlow for UI state
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// ✅ Good: Collect state in Compose
val state by viewModel.uiState.collectAsState()

// ❌ Avoid: Direct state mutation in UI
```

### Error Handling

```kotlin
// ✅ Good: Comprehensive error handling
try {
  val response = apiService.processText(request)
  if (response.isSuccessful) {
    _state.value = State.Success(response.body())
  } else {
    _state.value = State.Error("HTTP ${response.code()}")
  }
} catch (e: Exception) {
  _state.value = State.Error(e.message ?: "Unknown error")
}
```

### Resource Management

```kotlin
// ✅ Good: Clean up resources
val file = FileUtils.copyUriToTempFile(context, uri)
try {
  // Process file
} finally {
  file?.delete() // Always clean up
}
```

## Testing Strategy

### Unit Tests (`src/test/`)
```kotlin
@ExperimentalCoroutinesApi
class MainViewModelTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()
  
  @Test
  fun `loadConfiguration updates state correctly`() = runTest {
    // Test ViewModel logic
  }
}
```

### UI Tests (`src/androidTest/`)
```kotlin
@RunWith(AndroidJUnit4::class)
class MainScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()
  
  @Test
  fun mainScreen_displaysCorrectContent() {
    // Test Compose UI
  }
}
```

### Test Commands
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # UI tests
./gradlew testDebugUnitTest       # Debug unit tests
```

## Maintenance Tasks

### Regular Updates

1. **Dependencies** (Monthly)
```bash
# Check for updates
./gradlew dependencyUpdates

# Update Compose BOM
implementation(platform("androidx.compose:compose-bom:LATEST"))
```

2. **Code Quality** (Weekly)
```bash
./gradlew ktlintCheck    # Check formatting
./gradlew ktlintFormat   # Auto-fix formatting
./gradlew lint           # Android lint
```

3. **Testing** (Before releases)
```bash
./gradlew test
./gradlew connectedAndroidTest
./gradlew lint
```

### Performance Monitoring

1. **Build Performance**
```bash
./gradlew build --profile    # Generate build reports
```

2. **APK Analysis**
```bash
./gradlew assembleDebug
# Analyze APK size and content
```

### Security Updates

1. **Dependency Vulnerabilities**
```bash
./gradlew dependencyCheckAnalyze
```

2. **ProGuard Rules** (for release builds)
```gradle
-keep class com.karaskiewicz.audioai.data.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
```

## Troubleshooting

### Common Issues

#### Build Failures
```bash
# Clean and rebuild
./gradlew clean build

# Check for dependency conflicts
./gradlew dependencies
```

#### Compose Issues
```kotlin
// Enable debug inspection
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

#### Network Issues
```kotlin
// Add network security config for HTTP (debug only)
android:networkSecurityConfig="@xml/network_security_config"
```

#### DataStore Issues
```kotlin
// Clear DataStore (for testing)
context.dataStore.edit { it.clear() }
```

### Debug Tools

1. **Layout Inspector**: For Compose UI debugging
2. **Database Inspector**: For DataStore debugging  
3. **Network Inspector**: For API call debugging
4. **Logcat**: Use structured logging

```kotlin
private companion object {
  private const val TAG = "ShareViewModel"
}

Log.d(TAG, "Processing file: ${uri.path}")
```

### Performance Tips

1. **Compose Performance**
```kotlin
// Use remember for expensive calculations
val expensiveValue = remember(dependency) {
  calculateExpensiveValue(dependency)
}
```

2. **Network Optimization**
```kotlin
// Configure timeouts appropriately
.connectTimeout(30, TimeUnit.SECONDS)
.readTimeout(60, TimeUnit.SECONDS)
```

3. **Memory Management**
```kotlin
// Clean up ViewModels properly
override fun onCleared() {
  super.onCleared()
  // Cancel coroutines, clean resources
}
```

## Adding New Features

### 1. New Screen
1. Create Compose screen in `ui/screen/`
2. Create ViewModel in `ui/viewmodel/`
3. Add navigation in Activity
4. Write tests

### 2. New API Endpoint
1. Add to `ApiService.kt`
2. Create request/response models
3. Update `ApiClient.kt` if needed
4. Add ViewModel integration

### 3. New Settings
1. Add key to `PreferencesDataStore`
2. Update `SettingsScreen.kt`
3. Add validation logic
4. Write tests

## Best Practices Summary

- ✅ Use StateFlow for reactive UI
- ✅ Handle loading/error states
- ✅ Write comprehensive tests
- ✅ Follow Material Design guidelines
- ✅ Use coroutines for async operations
- ✅ Implement proper error handling
- ✅ Clean up resources (files, coroutines)
- ✅ Follow 2-space indentation
- ✅ Use meaningful variable names
- ✅ Document public APIs with KDoc

This architecture provides a solid foundation for maintaining and extending the Audio AI Android app while following modern Android development practices.