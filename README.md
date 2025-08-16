# 🎵➡️📝 Audio-AI

AI-powered audio transcription and analysis system with multiple provider support.

## 🚀 Quick Start

```bash
# 1. Connect to your existing Whisper (recommended)
export WHISPER_SERVICE_URL=http://localhost:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose -f docker-compose.external-whisper.yml up -d

# 2. OR spin up fresh Whisper service
docker-compose -f docker-compose.yml up -d

# 3. Test it works
curl -X POST http://localhost:3000/process-transcript \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Test meeting notes"}'
```

## 📋 Three Simple Options

### **Option 1: Use Your Existing Whisper** ⭐
Perfect if you already have `onerahmet/openai-whisper-asr-webservice` running:
```bash
# Your Whisper on port 1991
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose -f docker-compose.external-whisper.yml up -d
```

### **Option 2: Fresh Local Whisper**
Spins up Audio-AI + new Whisper service:
```bash
docker-compose -f docker-compose.yml up -d
```

### **Option 3: External Services**
Use Hugging Face, Gemini, or other providers:
```bash
export TRANSCRIPTION_PROVIDER=huggingface
export HUGGINGFACE_API_TOKEN=your_token
docker-compose -f docker-compose.external-whisper.yml up -d
```

## 🧪 Test Your Setup

```bash
# Health check
curl http://localhost:3000/health

# Upload audio file
curl -X POST http://localhost:3000/process-file -F "file=@audio.mp3"

# Process text directly
curl -X POST http://localhost:3000/process-transcript \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Meeting notes: discuss project timeline"}'
```

## 📱 Android App

```bash
cd android
./gradlew assembleDebug
./gradlew installDebug
# Configure server URL in app settings: http://your-server-ip:3000
```

## 🔧 Transcription Providers

| Provider | Setup | Quality | Best For |
|----------|-------|---------|----------|
| **onerahmet/openai-whisper-asr-webservice** | Docker container | Excellent | Production, existing setups |
| **Local Whisper** | `pip install openai-whisper` | Excellent | Privacy, offline |
| **Hugging Face** | API token | Good | Cloud processing |
| **Gemini Audio** | API key | Good | Google ecosystem |

## 📁 Generated Output

Files saved to `processed/category/year/month/`:

```markdown
# Meeting Discussion - Project Planning

## Summary
Brief overview of the conversation...

## Action Items
- [ ] John: Complete API documentation by Friday
- [ ] Sarah: Review budget proposal

## Key Ideas
- Implement user feedback system
- Mobile-first approach

## Tags
meeting, project-planning, team-coordination
```

## 📖 Documentation

- **[DEVELOPER.md](DEVELOPER.md)** - Developer handover guide
- **[backend/](backend/)** - Backend API documentation
- **[android/](android/)** - Android app documentation

## 🔄 Development

```bash
# Backend development
npm run dev
npm run test
npm run build

# Android development
cd android
./gradlew build
./gradlew test
```

Transform your voice notes into actionable documents! 🎯