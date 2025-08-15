# Audio-AI: Multi-Platform Audio Transcription System

A multi-platform AI-powered audio transcription and analysis system with backend API service and Android share target app.

## 🏗️ Project Structure

```
audio-ai/
├── backend/          # Node.js/TypeScript API service
├── android/          # Android share target app
├── Dockerfile        # Docker configuration
├── docker-compose.yml
└── README.md
```

## 🚀 Quick Start

```bash
# Install all dependencies
npm run install:all

# Start the backend development server
npm run dev

# Or run backend-specific commands
npm run backend:dev

# Set up environment (add your Gemini API key)
cp backend/.env.example backend/.env

# Test with an audio file
curl -X POST http://localhost:3000/process-file -F "file=@your-audio.mp3"
```

## ✨ What It Does

- **🎤 Audio → Text**: Transcribes audio files (MP3, WAV, M4A, etc.)
- **🧠 AI Processing**: Uses Gemini AI to structure content
- **📝 Smart Organization**: Creates summaries, action items, and tags
- **📁 Auto-Filing**: Saves to organized directories

## 📊 Current Status

- ✅ **Production Ready** - 51/54 tests passing
- ✅ **Cost-Free** - No expensive API dependencies
- ✅ **Multiple Providers** - Flexible transcription options
- ✅ **Clean Architecture** - TypeScript, ESLint, comprehensive testing

## 🔧 Transcription Providers

| Provider | Cost | Setup | Quality |
|----------|------|-------|---------|
| **Free Web Speech** (default) | Free | None | Good |
| **Docker Whisper** | Free | Docker | Excellent |
| **Hugging Face** | Free tier | Optional API key | Excellent |
| **Local Whisper** | Free | pip install | Excellent |

## 🛠️ Development Commands

### Root Level (All Platforms)
```bash
npm run dev           # Start backend dev server
npm run build         # Build backend
npm run test          # Run backend tests
npm run lint          # Lint backend code
npm run config        # Backend configuration tools
```

### Backend Specific
```bash
npm run backend:dev   # Start backend dev server
npm run backend:build # Build backend
npm run backend:test  # Run backend tests
```

### Android App
```bash
cd android
./gradlew build       # Build Android app
./gradlew installDebug # Install on connected device
```

## 📖 Documentation

See [backend/PROJECT_OVERVIEW.md](backend/PROJECT_OVERVIEW.md) for complete backend documentation.

## 🧪 Example Output

Input: Audio discussing project ideas  
Output: Structured markdown with summaries, action items, and intelligent categorization

---

**Ready to turn your voice notes into actionable documents!** 🎯