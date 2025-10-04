# Architecture and Design Patterns

## Backend Architecture (TypeScript)

### Clean Architecture Layers
```
Controllers (HTTP layer)
    ↓
Services (Business logic)
    ↓
Domain (Models, entities)
    ↓
Interfaces (Contracts)
```

### Key Patterns Used

#### Strategy Pattern
- **TranscriptionProviders**: Multiple implementations (`OpenAIWhisperWebserviceProvider`, `HuggingFaceTranscriptionProvider`, `GeminiAudioTranscriptionProvider`, etc.)
- Interface: `AudioTranscriptionProvider`
- Factory: `TranscriptionProviderFactory`
- Registry: `ProviderRegistry`

#### Factory Pattern
- `TranscriptionProviderFactory`: Creates appropriate provider based on configuration
- `ServiceFactory`: Creates service instances with dependencies

#### Repository Pattern (implied)
- Service layer acts as repositories for business logic
- Clear separation between data access and business logic

#### Configuration Builder Pattern
- `ApplicationConfigBuilder`
- `TranscriptionConfigBuilder`
- `EnvironmentConfigLoader`

### Key Services
- `AIService`: AI-powered text analysis
- `AudioTranscriptionService`: Coordinates transcription
- `FileProcessorService`: File handling orchestration
- `TranscriptProcessorService`: Transcript processing
- `FileTypeDetectionService`: Detects file types
- `TextFileProcessorService`: Text file processing

## Android Architecture (Kotlin)

### MVVM + Clean Architecture
```
UI Layer (Compose + ViewModels)
    ↓
Domain Layer (Use Cases + Repository Interfaces)
    ↓
Data Layer (Repository Implementations + API Services)
```

### Layers Explained

#### UI Layer (`ui/`)
- **Activities**: Entry points (`MainActivity`, `ShareActivity`, `SettingsActivity`)
- **Screens**: Composable screens (`MainScreen`, `ShareScreen`, `SettingsScreen`)
- **Components**: Reusable UI components (`RecordingControls`, `TimerDisplay`, `AnimatedWave`)
- **ViewModels**: UI state management (`MainViewModel`, `ShareViewModel`, `SettingsViewModel`)
- **Theme**: Centralized theming (`UIConfig.kt` - single source of truth)

#### Domain Layer (`domain/`)
- **Models**: Domain entities (`RecordingResult`, `RecordingState`, `RecordingConstants`)
- **Repositories**: Repository interfaces (`RecordingRepository`)
- **Use Cases**: Business logic (`RecordingUseCase`)
- **Services**: Domain services (`MediaRecorderFactory`, `FileManager`, `AudioComposer`)

#### Data Layer
- **`data/`**: API models (`ApiModels.kt`)
- **`network/`**: Network layer (`ApiService`, `ApiServiceManager`)
- **`domain/repository/`**: Repository implementations (`RecordingRepositoryImpl`)

### Key Patterns Used

#### MVVM (Model-View-ViewModel)
- ViewModels manage UI state
- Composables observe state via `StateFlow`
- Events bubble up from UI to ViewModel
- State flows down from ViewModel to UI

#### Repository Pattern
- `RecordingRepository` interface (domain layer)
- `RecordingRepositoryImpl` implementation (data layer)
- Abstracts data access from business logic

#### Use Case Pattern
- `RecordingUseCase`: Encapsulates recording business logic
- Single responsibility: manage recording lifecycle

#### Factory Pattern
- `MediaRecorderFactory`: Creates configured MediaRecorder instances

#### State Pattern
- `RecordingState`: Sealed class representing recording states
  - `Idle`, `Recording`, `Paused`, `Finished`

#### Dependency Injection (Koin)
- Constructor injection
- Module definition in `AppModule.kt`
- ViewModels injected with `koinViewModel()`

#### Result Monad Pattern
- `Result<T>` for operations that can fail
- `runCatching` for exception handling
- Extension functions: `mapToResult`, `safeSuspendNetworkCall`, `safeFileOperation`

### UI Configuration Pattern
- **UIConfig.kt**: Single source of truth for all UI customization
  - Colors, spacing, sizing, animations, layout
  - Modify once, affects entire app
  - Developer-friendly with extensive comments

## Cross-Cutting Concerns

### Error Handling
- **Backend**: Try/catch with structured error responses, logging
- **Android**: Result monad, sealed classes for state, Timber logging

### Async Operations
- **Backend**: async/await with Promises
- **Android**: Kotlin coroutines with structured concurrency, Dispatchers.IO for blocking operations

### Logging
- **Backend**: Custom logger utility (`utils/logger.ts`)
- **Android**: Timber

### Validation
- **Backend**: Middleware (`fileValidation.ts`, `validation.ts`)
- **Android**: Domain-level validation in use cases

## Design Principles Applied

### SOLID
- **Single Responsibility**: Each class/file has one concern
- **Open/Closed**: Extensible via interfaces (e.g., AudioTranscriptionProvider)
- **Liskov Substitution**: All providers implement same interface
- **Interface Segregation**: Small, focused interfaces
- **Dependency Inversion**: Depend on abstractions (interfaces), not implementations

### DRY (Don't Repeat Yourself)
- Shared utilities in `utils/`
- Reusable UI components
- Configuration centralized

### Separation of Concerns
- Clear layer boundaries
- No mixing of abstraction levels
- UI doesn't call services directly (goes through ViewModel → UseCase → Repository)

## Anti-Patterns to Avoid
- **God objects**: Large classes doing too much
- **Manager/Helper/Util suffixes**: Often indicate poor design
- **Mixing abstraction layers**: e.g., UI calling API directly
- **Deep nesting**: Use early returns and functional approaches
- **Mutable state**: Prefer immutability