# Known PROGRAMMING_V2.md Violations

This project has SEVERE violations of the 150-line file limit guideline. These violations need to be addressed through comprehensive refactoring.

## Backend TypeScript Violations

### Critical Violations (> 300 lines)
1. **prompts.ts** (553 lines) - Contains all AI prompts, needs split into separate prompt files
2. **ProviderRegistry.ts** (301 lines) - Provider registration logic, needs pattern refactoring

### Severe Violations (> 150 lines)
3. **LocalWhisperProvider.ts** (188 lines)
4. **FileTypeDetectionService.ts** (181 lines)
5. **ConfigurationService.ts** (181 lines)
6. **OpenAIWhisperWebserviceProvider.ts** (172 lines)
7. **FreeWebSpeechProvider.ts** (170 lines)
8. **fileValidation.ts** (167 lines)
9. **audioConverter.ts** (156 lines)

**Total: 9 backend files exceeding 150 lines**

## Android Kotlin Violations

### Severe Violations (> 250 lines)
1. **ShareScreen.kt** (330 lines) - UI screen with dialog logic
2. **MainScreen.kt** (311 lines) - Main UI screen
3. **UIConfig.kt** (272 lines) - Theme configuration (acceptable exception - config file)
4. **SettingsScreen.kt** (269 lines) - Settings UI screen
5. **ShareViewModel.kt** (268 lines) - ViewModel with multiple responsibilities
6. **RecordingUseCase.kt** (252 lines) - Use case with state machine logic
7. **MainViewModel.kt** (237 lines) - ViewModel managing recording state
8. **RecordingControls.kt** (228 lines) - UI component
9. **PixelComponents.kt** (181 lines) - UI components collection
10. **AudioComposer.kt** (180 lines) - Audio processing service

**Total: 10 Android files exceeding 150 lines** (UIConfig.kt is acceptable as config file)

## Refactoring Strategy

### Backend Priority Order
1. **prompts.ts** → Split into individual prompt files by category
2. **ProviderRegistry.ts** → Apply Strategy + Factory patterns more cleanly
3. **TranscriptionProviders** → Extract common functionality to base classes
4. **Services** → Apply SRP more strictly, extract helper methods

### Android Priority Order
1. **ShareScreen.kt** → Extract dialog composables to separate files
2. **MainScreen.kt** → Extract composables and state handling
3. **ViewModels** → Extract state management and use cases
4. **RecordingUseCase.kt** → Extract state machine to separate class
5. **UI Components** → Split into smaller focused components

## Non-Violations Worth Noting

### Backend - Good Files (< 150 lines)
- Most domain models
- Controllers
- Utility files
- Middleware (except fileValidation.ts)

### Android - Good Files (< 150 lines)
- Most UI components
- Domain models
- Repository interfaces
- DI configuration
- Utilities