# 🛠️ Developer Handover Guide

## Project Overview

Audio-AI is a TypeScript/Node.js backend + Android Kotlin app for AI-powered transcription and analysis. The system processes audio files or text through various transcription providers and generates structured markdown documents.

## Architecture

### Backend (Node.js/TypeScript)
- **Clean Architecture**: Domain models, services, controllers following SOLID principles
- **Transcription Providers**: Pluggable system for different audio-to-text services
- **AI Processing**: Gemini API for content structuring and analysis
- **File Organization**: Auto-categorized output in `processed/` directory

### Android (Kotlin/Jetpack Compose)
- **Share Target**: Receives audio/text from any Android app
- **Material 3 UI**: Modern Android design patterns
- **MVVM**: ViewModels + StateFlow for reactive UI

## Key Files to Know

### Backend Core
```
backend/src/
├── controllers/CleanTranscriptController.ts   # Main API endpoints
├── services/TranscriptProcessorService.ts     # Core business logic
├── services/transcription-providers/          # All transcription providers
│   ├── OpenAIWhisperWebserviceProvider.ts    # External Whisper support
│   ├── LocalWhisperProvider.ts               # Local Whisper integration
│   └── ProviderRegistry.ts                   # Provider factory
├── domain/                                    # Domain models (Uncle Bob approved)
│   ├── TranscriptText.ts                     # Rich text object with behavior
│   └── ProcessingResult.ts                   # Processing result value object
└── config/                                   # Configuration management
    └── ConfigurationService.ts               # Clean config injection
```

### Android Core
```
android/app/src/main/java/com/karaskiewicz/audioai/
├── ShareActivity.kt                           # Share target entry point
├── ui/screen/ShareScreen.kt                   # Main processing UI
├── ui/viewmodel/ShareViewModel.kt             # Business logic (needs refactoring)
└── data/ApiClient.kt                          # Backend communication
```

## Quick Development Setup

### Backend Development
```bash
cd backend
npm install
cp .env.example .env  # Add your GEMINI_API_KEY
npm run dev          # Starts on localhost:3000
```

### Android Development
```bash
cd android
./gradlew assembleDebug
./gradlew installDebug
```

## Adding New Transcription Providers

### 1. Create Provider Class
```typescript
// backend/src/services/transcription-providers/MyProvider.ts
export class MyProvider implements AudioTranscriptionProvider {
  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    // Your transcription logic
    return {
      extractedText: "transcribed text",
      fileType: FileType.AUDIO,
      processingMethod: 'my_provider'
    };
  }
  
  getSupportedFormats(): string[] { return ['mp3', 'wav']; }
  getMaxFileSize(): number { return 50 * 1024 * 1024; }
  getProviderName(): string { return 'My Provider'; }
  isReady(): boolean { return true; }
}
```

### 2. Register in Factory
```typescript
// backend/src/services/transcription-providers/ProviderRegistry.ts
case 'my_provider':
  return new MyProvider();
```

### 3. Add Environment Variable
```bash
# .env
TRANSCRIPTION_PROVIDER=my_provider
```

## External Whisper Integration

The system supports connecting to existing Whisper instances (like `onerahmet/openai-whisper-asr-webservice`):

```bash
# Connect to external Whisper on port 1991
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose -f docker-compose.external-whisper.yml up -d
```

Key implementation: `OpenAIWhisperWebserviceProvider.ts` handles both JSON and plain text responses.

## Configuration System

The backend uses a clean configuration architecture (Uncle Bob approved):

- `EnvironmentConfigLoader` - Loads environment variables
- `FeatureFlagManager` - Manages feature flags
- `TranscriptionConfigBuilder` - Builds transcription config
- `ConfigurationService` - Coordinates all config

## Testing

### Backend Tests
```bash
npm run test              # All tests
npm run test:integration  # Integration tests only
npm run lint              # TypeScript linting
```

### Android Tests
```bash
./gradlew test            # Unit tests
./gradlew ktlintCheck     # Kotlin linting
```

## Docker Deployments

### Standard Deployment
```bash
docker-compose -f docker-compose.yml up -d
```

### External Whisper
```bash
docker-compose -f docker-compose.external-whisper.yml up -d
```

### With Fresh Whisper Service
```bash
docker-compose -f docker-compose.whisper.yml up -d
```

## Common Debugging

### Backend Issues
- Check logs: `docker logs audio-ai-backend`
- Verify provider: `curl http://localhost:3000/health`
- Test transcription: Use `test-audio-ai.sh` script

### Android Issues
- Check server URL in app settings
- Verify network connectivity
- Review Android logs in logcat

## Environment Variables Reference

### Required
```bash
GEMINI_API_KEY=your_api_key_here
```

### Transcription Provider Selection
```bash
TRANSCRIPTION_PROVIDER=openai_whisper_webservice  # or: local_whisper, huggingface, etc.
WHISPER_SERVICE_URL=http://host.docker.internal:1991
```

### Optional
```bash
PORT=3000
LOG_LEVEL=info
MAX_FILE_SIZE_MB=100
BASE_DIRECTORY=processed
```

## Code Quality Standards

This project follows Uncle Bob (Clean Code) principles:
- **Single Responsibility**: Each class has one reason to change
- **Dependency Injection**: Services depend on interfaces, not concrete classes
- **Rich Domain Models**: Objects have behavior, not just data
- **Thin Controllers**: HTTP coordination only, no business logic

See `UNCLE_BOB_REPORT.md` for detailed architecture analysis.

## Known Issues & Limitations

### Backend
- ✅ Clean architecture implemented
- ✅ External Whisper integration working
- ✅ Comprehensive test coverage

### Android
- ⚠️ ShareViewModel is a god object (185 lines, multiple responsibilities)
- ⚠️ No dependency injection framework (should add Hilt)
- ⚠️ No repository pattern (ViewModels talk directly to API)

## Next Steps for New Developers

1. **Immediate**: Start with backend development - it's clean and well-structured
2. **Short-term**: Add new transcription providers or enhance AI prompts
3. **Medium-term**: Refactor Android app following Clean Architecture
4. **Long-term**: Add comprehensive monitoring, metrics, and performance optimization

## Support

- Backend architecture is solid and ready for production
- External Whisper integration is battle-tested
- Focus future work on Android refactoring for long-term maintainability

The system is designed for easy extension - adding new providers or AI enhancements should be straightforward following the existing patterns.