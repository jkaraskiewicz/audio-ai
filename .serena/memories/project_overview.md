# Audio-AI Project Overview

## Purpose
AI-powered audio transcription and analysis system that transforms voice recordings into structured, actionable markdown documents.

**Two main components:**
1. **Backend (TypeScript/Node.js)**: Audio processing and AI analysis service
2. **Android App (Kotlin)**: Mobile recording app called "Scribely"

## Key Features
- Audio transcription using multiple providers (Whisper, Hugging Face, Gemini, etc.)
- AI-powered content analysis and structuring
- Auto-categorization and markdown output
- Android app for recording and uploading
- Docker-based deployment

## Tech Stack

### Backend
- **Language**: TypeScript
- **Runtime**: Node.js
- **Framework**: Express
- **Architecture**: Clean Architecture (controllers, services, domain, utils)
- **AI**: Google Gemini API
- **Transcription**: Multiple providers (Whisper webservice, Hugging Face, Gemini Audio)
- **Testing**: Jest
- **Linting**: ESLint + Prettier
- **Containerization**: Docker

### Android
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Koin
- **Navigation**: Compose Navigation
- **Network**: Retrofit (via ApiService)
- **Audio**: MediaRecorder with native pause/resume
- **Testing**: JUnit
- **Linting**: ktlint
- **Build**: Gradle with Kotlin DSL

## Project Structure
```
audio-ai/
├── backend/               # TypeScript backend service
│   ├── src/
│   │   ├── config/        # Configuration management
│   │   ├── controllers/   # HTTP request handlers
│   │   ├── domain/        # Domain models
│   │   ├── interfaces/    # TypeScript interfaces
│   │   ├── middleware/    # Express middleware
│   │   ├── services/      # Business logic
│   │   │   └── transcription-providers/  # Multiple transcription providers
│   │   ├── types/         # TypeScript type definitions
│   │   └── utils/         # Utilities
│   └── tests/             # Jest tests
│
├── android/               # Kotlin Android app (Scribely)
│   └── app/src/main/java/com/karaskiewicz/scribely/
│       ├── data/          # API models
│       ├── di/            # Dependency injection (Koin)
│       ├── domain/        # Business logic layer
│       │   ├── model/     # Domain models
│       │   ├── repository/# Data repositories
│       │   ├── service/   # Services (audio, file management)
│       │   └── usecase/   # Use cases
│       ├── network/       # API service layer
│       ├── ui/            # Compose UI
│       │   ├── activity/  # Activities
│       │   ├── components/# Reusable UI components
│       │   ├── screen/    # Screen composables
│       │   ├── theme/     # Theme configuration (UIConfig.kt)
│       │   └── viewmodel/ # ViewModels
│       └── utils/         # Utilities
│
├── .github/workflows/     # GitHub Actions CI/CD
├── docker-compose.yml     # Local development
└── docker-compose.production.yml  # Production deployment
```

## Development Philosophy
- **Clean Code principles** (Robert C. Martin)
- **SOLID principles**
- **DRY (Don't Repeat Yourself)**
- **Single Responsibility Principle** - each component has one concern
- **Clean Architecture** - clear separation of concerns