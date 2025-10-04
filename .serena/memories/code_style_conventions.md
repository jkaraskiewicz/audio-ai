# Code Style and Conventions

## General Principles (Both Backend & Android)
- **Clean Code** principles (Robert C. Martin)
- **SOLID principles** 
- **DRY (Don't Repeat Yourself)**
- **Single Responsibility Principle** - each file/class has ONE concern
- **Small functions** - prefer functions under 50 lines
- **Small files** - target maximum 150 lines (guideline, can be exceeded with good reason)
- **Immutability by default** - `const` in TypeScript, `val` in Kotlin
- **Functional programming** - prefer `map`, `filter`, `reduce` over loops
- **Strong null safety** - avoid nullable types when possible
- **Early returns / guard clauses** - avoid deep nesting

## Backend (TypeScript) Conventions

### File Organization
- **Controllers**: HTTP request handlers (`src/controllers/`)
- **Services**: Business logic (`src/services/`)
- **Domain**: Domain models (`src/domain/`)
- **Interfaces**: TypeScript interfaces (`src/interfaces/`)
- **Middleware**: Express middleware (`src/middleware/`)
- **Config**: Configuration management (`src/config/`)
- **Utils**: Utility functions (`src/utils/`)
- **Types**: Type definitions (`src/types/`)

### Naming
- **Files**: camelCase (e.g., `TranscriptController.ts`, `prompts.ts`)
- **Classes**: PascalCase (e.g., `TranscriptController`, `AIService`)
- **Functions/Methods**: camelCase (e.g., `processTranscript()`, `validateFile()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_FILE_SIZE`)
- **Interfaces**: PascalCase, often starts with `I` or descriptive name (e.g., `AudioTranscriptionProvider`)

### TypeScript Style
- Use `const` over `let`, avoid `var`
- Use `async/await` over Promise chains
- Prefer `interface` over `type` for object shapes
- Use strict mode (`strict: true` in tsconfig)
- Avoid `any`, prefer `unknown` when type is truly unknown
- Use functional array methods: `map`, `filter`, `reduce`

### Error Handling
- Return structured error responses
- Use try/catch for async operations
- Log errors with context using the logger utility
- Bubble up errors to top-level handlers

### Import Style
```typescript
// External dependencies first
import express from 'express';
import dotenv from 'dotenv';

// Internal imports next
import { logger } from './utils/logger';
import { AIService } from './services/AIService';
```

## Android (Kotlin) Conventions

### File Organization
- **UI Layer**: `ui/` (activities, screens, components, viewmodels, theme)
- **Domain Layer**: `domain/` (models, repositories, services, use cases)
- **Data Layer**: `data/`, `network/` (API models, network services)
- **DI**: `di/` (Koin modules)
- **Utils**: `utils/` (utilities, extensions)

### Naming
- **Files**: PascalCase (e.g., `MainScreen.kt`, `RecordingUseCase.kt`)
- **Classes**: PascalCase (e.g., `MainViewModel`, `RecordingRepository`)
- **Functions**: camelCase (e.g., `startRecording()`, `uploadFile()`)
- **Properties**: camelCase (e.g., `isRecording`, `recordingState`)
- **Constants**: UPPER_SNAKE_CASE or camelCase for compile-time constants
- **Composables**: PascalCase (e.g., `MainScreen()`, `RecordingControls()`)

### Kotlin Style
- Use `val` over `var` (immutability)
- Use `data class` for domain models
- Prefer extension functions over utility classes
- Use `when` instead of long if-else chains
- Use coroutines for async operations with structured concurrency
- Use `runCatching` and `Result<T>` for error handling
- Strong null safety - use `?.` and `?:`, avoid `!!`

### Jetpack Compose Style
- **Composable functions** start with capital letter
- **State hoisting** - state lives in ViewModel, events bubble up
- **Side effects** use `LaunchedEffect`, `DisposableEffect`, etc.
- **Theme customization** centralized in `UIConfig.kt`
- **Modifiers** - order matters (size → padding → background → border)

### Clean Architecture Layers
1. **UI Layer** (`ui/`): Composables, ViewModels, Activities
2. **Domain Layer** (`domain/`): Use cases, repositories (interfaces), models
3. **Data Layer** (`data/`, `network/`): Repository implementations, API services

### Dependency Injection (Koin)
- Module definition in `di/AppModule.kt`
- Use constructor injection
- ViewModels injected with `koinViewModel()`
- Services/repositories injected via constructor

### Error Handling
- Use `Result<T>` monad pattern for operations that can fail
- Use sealed classes for complex state/result types
- Handle errors at ViewModel level, expose UI state
- Use Timber for logging

## Documentation

### When to Comment
- **Public API** documentation when it adds value
- **Complex algorithms** - explain the "why" not the "what"
- **Do NOT comment obvious code**
- **TODOs** only when explicitly asked to leave for later

### Documentation Style
- **Backend**: JSDoc/TSDoc for public APIs
- **Android**: KDoc for public APIs
- Prefer self-documenting code (good naming) over comments

## Testing

### Backend Tests (Jest)
- Location: `backend/tests/` mirroring `src/` structure
- Naming: `*.test.ts`
- Test all public methods
- Use `describe()` and `it()` blocks
- Mock external dependencies

### Android Tests (JUnit)
- Location: `android/app/src/test/`
- Naming: `*Test.kt`
- Test ViewModels, repositories, use cases
- Use MockK for mocking
- Tests can exceed 150 line limit

## Linting Configuration

### Backend
- **ESLint** with TypeScript plugin
- **Prettier** for formatting
- Config: `.eslintrc.js`, `.prettierrc`
- Strict mode enabled

### Android
- **ktlint** for Kotlin linting
- **Gradle plugin** version 13.0.0
- Config: `.editorconfig`
- Auto-format with `ktlintFormat`

## Version Control (Git)

### Commit Message Format
Use **Conventional Commits**:
- `feat:` new feature
- `fix:` bug fix
- `refactor:` code refactoring
- `test:` adding/updating tests
- `docs:` documentation changes
- `chore:` maintenance tasks

Example: `feat: add audio conversion for Whisper compatibility`