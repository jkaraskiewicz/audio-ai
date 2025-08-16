# 🎵➡️📝 Audio-AI

AI-powered audio transcription and analysis system that transforms voice recordings into structured, actionable markdown documents.

## 🚀 Quick Start

### Option 1: Automatic Setup (Recommended)
```bash
git clone https://github.com/your-repo/audio-ai.git
cd audio-ai
./setup.sh
```

The setup script will guide you through:
- 🆕 Full setup with local Whisper service
- 🔗 Connect to existing Whisper service  
- ☁️ Use cloud services (Hugging Face, etc.)
- 🧪 Development setup

### Option 2: Manual Setup

1. **Copy configuration**:
   ```bash
   cp .env.example .env
   # Edit .env and add your GEMINI_API_KEY
   ```

2. **Choose your setup**:

   **Local Whisper (Recommended)**:
   ```bash
   docker-compose --profile full up -d
   ```

   **External Whisper Service**:
   ```bash
   # Edit .env: WHISPER_SERVICE_URL=http://your-whisper-host:port
   docker-compose up audio-ai -d
   ```

   **Cloud Services**:
   ```bash
   # Edit .env: TRANSCRIPTION_PROVIDER=huggingface
   # Add your API tokens
   docker-compose up audio-ai -d
   ```

## 🧪 Test Your Setup

```bash
# Health check
curl http://localhost:3000/health

# Process audio file
curl -X POST http://localhost:3000/process-file -F "file=@audio.mp3"

# Process text directly
curl -X POST http://localhost:3000/process \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Meeting notes: discuss project timeline"}'
```

## 📁 Output

Files are automatically saved to `./processed/category/YYYY-MM-DD_filename.md`:

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

## 🔧 Configuration Options

### Transcription Providers

| Provider | Use Case | Setup |
|----------|----------|-------|
| **openai_whisper_webservice** | Production, existing Whisper | `WHISPER_SERVICE_URL=http://your-host:port` |
| **huggingface** | Cloud processing | `HUGGINGFACE_API_TOKEN=your_token` |
| **gemini_audio** | Google ecosystem | Uses same GEMINI_API_KEY |
| **free_web_speech** | Testing, development | No additional setup |

### Port Configuration

```bash
# Default ports (customizable in .env)
AUDIO_AI_PORT=3000      # Audio-AI backend
WHISPER_PORT=9000       # Local Whisper service
AUDIO_AI_DEV_PORT=3001  # Development server
```

### Volume Mounting

```yaml
volumes:
  - ./processed:/usr/src/app/processed  # Your transcribed files
  - ./backend/.env:/usr/src/app/.env:ro # Configuration
```

## 🔗 External Whisper Integration

### Connect to Existing Service
If you have [ahmetoner/whisper-asr-webservice](https://github.com/ahmetoner/whisper-asr-webservice) running:

```bash
# In .env file
TRANSCRIPTION_PROVIDER=openai_whisper_webservice
WHISPER_SERVICE_URL=http://localhost:1991

# Or via environment
WHISPER_SERVICE_URL=http://localhost:1991 docker-compose up audio-ai -d
```

### Supported Whisper Configurations
- **Local Docker**: `http://whisper:9000`
- **Host Service**: `http://host.docker.internal:1991`
- **Remote Service**: `http://your-server-ip:9000`
- **Local Network**: `http://192.168.1.100:9000`

## 📱 Android App

```bash
cd android
./gradlew assembleDebug
./gradlew installDebug
# Configure server URL in app: http://your-server-ip:3000
```

## 🛠️ Development

```bash
# Development with hot reload
docker-compose --profile dev up -d

# Backend development
npm run dev          # Start development server
npm run test         # Run all tests
npm run build        # Production build

# Android development
cd android && ./gradlew test
```

## 🎯 Advanced Features

- **🔄 Audio Format Conversion**: Automatic m4a→mp3 conversion for Whisper compatibility
- **🧠 AI Analysis**: Rich content structuring with action items, ideas, and commentary  
- **📂 Smart Organization**: Auto-categorization by content type
- **🔌 Provider Flexibility**: Easy switching between transcription services
- **🐳 Docker-First**: Complete containerized deployment
- **📊 Health Monitoring**: Built-in health checks and logging

## 📖 Documentation

- **[DEVELOPER.md](DEVELOPER.md)** - Complete developer guide
- **[setup.sh](setup.sh)** - Interactive setup script
- **[.env.example](.env.example)** - Configuration examples

## 🌟 Use Cases

- **Meeting Notes**: Transform recordings into action items
- **Voice Memos**: Convert ideas into structured documents
- **Interviews**: Generate transcripts with key insights
- **Lectures**: Create searchable study materials
- **Brainstorming**: Capture and organize creative sessions

Transform your voice into actionable intelligence! 🎯