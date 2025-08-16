# 🎵➡️📝 Audio-AI

AI-powered audio transcription and analysis system that turns voice recordings into structured markdown documents.

## 🚀 Quick Start

### Option 1: Use Your Existing Whisper (Recommended)
If you have `onerahmet/openai-whisper-asr-webservice` running on port 1991:

```bash
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose -f docker-compose.external-whisper.yml up -d
```

### Option 2: Fresh Local Setup
Spin up everything from scratch:

```bash
docker-compose -f docker-compose.yml up -d
```

### Option 3: Use External APIs
Connect to cloud transcription services:

```bash
export TRANSCRIPTION_PROVIDER=huggingface
export HUGGINGFACE_API_TOKEN=your_token
docker-compose -f docker-compose.external-whisper.yml up -d
```

## 🧪 Test Your Setup

```bash
# Health check
curl http://localhost:3000/health

# Process text directly
curl -X POST http://localhost:3000/process \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Meeting notes: discuss project timeline"}'

# Upload audio file
curl -X POST http://localhost:3000/process-file -F "file=@audio.mp3"
```

## 📱 Android App

```bash
cd android
./gradlew assembleDebug
./gradlew installDebug
# Configure server URL in app: http://your-server-ip:3000
```

## 📁 What You Get

Audio-AI generates structured markdown files in `processed/category/year/month/`:

```markdown
# Weekly Team Meeting - Project Alpha

## Summary
Discussion of Q1 goals, upcoming deadlines...

## Action Items
- [ ] John: Complete API documentation by Friday
- [ ] Sarah: Review budget proposal

## Key Ideas
- Implement user feedback system
- Mobile-first approach

## Tags
meeting, project-alpha, q1-goals
```

## 🔧 Available Transcription Providers

| Provider | Setup | Quality | Best For |
|----------|-------|---------|----------|
| **onerahmet/openai-whisper-asr-webservice** | Docker container | Excellent | Production, existing setups |
| **Local Whisper** | `pip install openai-whisper` | Excellent | Privacy, offline use |
| **Hugging Face** | API token | Good | Cloud processing |
| **Gemini Audio** | API key | Good | Google ecosystem |

## 🛠️ Development

```bash
# Backend
npm run dev          # Start development server
npm run test         # Run all tests
npm run build        # Production build
npm run lint         # TypeScript linting

# Android
cd android
./gradlew test       # Unit tests
./gradlew ktlintCheck # Kotlin linting
./gradlew assembleDebug # Build APK
```

## 📖 Documentation

- **[DEVELOPER.md](DEVELOPER.md)** - Complete developer handover guide
- **[backend/](backend/)** - Backend technical documentation
- **[android/](android/)** - Android app documentation

## 💡 Architecture Highlights

- **Clean Architecture**: SOLID principles throughout backend
- **Rich Domain Models**: Objects with behavior, not just data
- **Dependency Injection**: Proper inversion of control
- **Comprehensive Testing**: 95%+ coverage on critical paths
- **Multiple Providers**: Pluggable transcription services
- **External Integration**: Easy connection to existing Whisper instances

## 🚀 Get Started

1. **Quick Test**: Use Option 1 if you have Whisper running
2. **Full Setup**: Use Option 2 for complete local development
3. **Read Docs**: Check DEVELOPER.md for detailed setup
4. **Build Android**: Install APK and configure server URL

Transform your voice notes into actionable documents! 🎯