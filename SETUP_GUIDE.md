# Audio AI - Complete Setup Guide

> **Get your complete audio transcription system running in under 10 minutes**

## 🎯 What You'll Have Running

After this setup, you'll have:
- ✅ **Backend API** processing audio files with AI-powered organization
- ✅ **Android share target app** for easy content sharing
- ✅ **Local Whisper transcription** (free, high-quality, private)
- ✅ **Structured markdown output** with summaries and action items

## 🚀 Quick Start (Choose Your Path)

### Path A: Fastest Setup (5 minutes) ⚡
*Uses free web speech transcription - good for demos and testing*

```bash
# 1. Clone and setup backend
git clone <repo-url> && cd audio-ai
npm install
cp backend/.env.example backend/.env

# 2. Add your Gemini API key
echo "GEMINI_API_KEY=your_gemini_api_key_here" >> backend/.env

# 3. Start server
npm run dev
# ✅ Server running at http://localhost:3000

# 4. Test it works
curl -X POST http://localhost:3000/process-file -F "file=@test-audio.mp3"
```

### Path B: Production Setup (10 minutes) 🎯
*With local Whisper for excellent transcription quality*

```bash
# 1. Setup backend (same as above)
git clone <repo-url> && cd audio-ai
npm install
cp backend/.env.example backend/.env
echo "GEMINI_API_KEY=your_gemini_api_key_here" >> backend/.env

# 2. Install local Whisper
pip install openai-whisper

# 3. Configure for local Whisper
echo "TRANSCRIPTION_PROVIDER=local_whisper" >> backend/.env

# 4. Start server
npm run dev

# 5. Test with high-quality transcription
curl -X POST http://localhost:3000/process-file -F "file=@test-audio.mp3"
```

### Path C: Docker Setup (8 minutes) 🐳
*Containerized with isolated Whisper service*

```bash
# 1. Clone repository  
git clone <repo-url> && cd audio-ai

# 2. Set environment variable
export GEMINI_API_KEY=your_gemini_api_key_here

# 3. Start with Docker Whisper
docker-compose -f docker-compose.whisper.yml up -d

# ✅ Both backend and Whisper service running
# Backend: http://localhost:3000
# Whisper: http://localhost:8001
```

## 📱 Android App Setup

**After backend is running:**

```bash
# 1. Build Android app
cd android
./gradlew assembleDebug

# 2. Install on device
./gradlew installDebug
# OR: adb install app/build/outputs/apk/debug/app-debug.apk

# 3. Configure in app
# Open Audio AI → Settings
# Server URL: http://YOUR_IP:3000 (e.g., http://192.168.1.100:3000)
# Tap "Test Connection" → Should show ✅ Success
```

## 🎵 Test Your Complete System

### 1. Backend Test
```bash
# Test transcription + AI processing
curl -X POST http://localhost:3000/process-file \
  -F "file=@sample-audio.mp3"

# Response should include:
# {"message": "File processed successfully", "filename": "..."}
```

### 2. Android App Test
1. **Record audio** in any voice recording app
2. **Tap Share** → Select "Audio AI" 
3. **Watch processing** - you'll get a success notification
4. **Check results** - files saved in `saved_ideas/` directory

### 3. Check Generated Output
```bash
# View generated files
ls -la saved_ideas/

# Example output structure:
# saved_ideas/meetings/2025/01/team-standup-jan-15.md
```

## 🔧 Configuration Options

### Transcription Providers

**Switch providers anytime:**
```bash
# Use free web speech (fastest setup)
echo "TRANSCRIPTION_PROVIDER=free_web_speech" >> backend/.env

# Use local Whisper (best quality)
echo "TRANSCRIPTION_PROVIDER=local_whisper" >> backend/.env

# Use Docker Whisper (best isolation)
echo "TRANSCRIPTION_PROVIDER=docker_whisper" >> backend/.env
```

### Whisper Model Selection

**Balance speed vs quality:**
```typescript
// Edit backend/src/services/transcription-providers/LocalWhisperProvider.ts
// Line 62:
'--model', 'base',    // Fast, good quality
'--model', 'small',   // Slower, better quality  
'--model', 'medium',  // Slow, great quality
```

### Server Configuration

**Customize server behavior:**
```bash
# Backend .env options
PORT=3000                              # Server port
BASE_DIRECTORY=saved_ideas            # Output directory
LOG_LEVEL=info                        # debug, info, warn, error
MAX_FILE_SIZE_MB=100                  # Upload limit
CONCURRENT_TRANSCRIPTIONS=3           # Parallel processing
```

## 📊 Verify Everything Works

### Health Checks
```bash
# Backend health
curl http://localhost:3000/health
# Response: {"status": "healthy", "timestamp": "..."}

# Whisper service health (if using Docker)
curl http://localhost:8001/health
# Response: {"status": "ready", "model": "base"}
```

### Performance Test
```bash
# Test with larger file
curl -X POST http://localhost:3000/process-file \
  -F "file=@large-meeting-recording.mp3"

# Monitor processing
tail -f backend/logs/app.log
```

## 🎯 Common Use Cases

### Meeting Notes
```
1. Record meeting on phone
2. Share to Audio AI app
3. Get organized output:
   - Meeting summary
   - Action items with ownership
   - Key decisions
   - Follow-up tasks
```

### Voice Brainstorming
```
1. Record ideas while walking/driving
2. Share recording to Audio AI
3. Get structured thoughts:
   - Categorized ideas
   - Actionable next steps
   - Tagged for easy searching
```

### Voice Messages
```
1. Long WhatsApp voice messages
2. Share to Audio AI
3. Get text summaries:
   - Key points extracted
   - Important information highlighted
   - Organized by topic
```

## 🆘 Troubleshooting

### Backend Issues

**"Server won't start"**
```bash
# Check if port is in use
lsof -i :3000

# Check environment
cat backend/.env

# Check logs
npm run dev | tee debug.log
```

**"Transcription fails"**
```bash
# Check transcription provider
TRANSCRIPTION_PROVIDER=free_web_speech npm run dev

# Test different file
curl -X POST http://localhost:3000/process-file -F "file=@simple.wav"

# Check provider-specific logs
LOG_LEVEL=debug npm run dev
```

### Android Issues

**"Can't connect to server"**
```
1. Check server URL format: http://192.168.1.100:3000
2. Verify both on same WiFi network
3. Test server in browser: http://192.168.1.100:3000/health
4. Check firewall settings
```

**"App crashes on share"**
```
1. Check Android logs: adb logcat | grep AudioAI
2. Reinstall app: ./gradlew installDebug
3. Clear app data and reconfigure
```

### Whisper Issues

**"Whisper not found"**
```bash
# Check installation
which whisper
pip list | grep whisper

# Reinstall if needed
pip install --upgrade openai-whisper
```

**"Slow processing"**
```bash
# Use faster model
# Edit LocalWhisperProvider.ts: '--model', 'base'

# Check system resources
htop
nvidia-smi (if using GPU)
```

## 🔄 Updates & Maintenance

### Update System
```bash
# Update backend dependencies
cd audio-ai && npm update

# Update Android app
cd android && ./gradlew clean build

# Update Whisper
pip install --upgrade openai-whisper
```

### Backup Configuration
```bash
# Backup important files
cp backend/.env backend/.env.backup
cp -r saved_ideas saved_ideas.backup

# Export Android app settings
# (saved in DataStore - automatic backup with Android backup)
```

## 🎉 You're Ready!

Your complete Audio AI system is now running with:

- ✅ **Backend API** for processing audio and text
- ✅ **Android app** for sharing from any app
- ✅ **High-quality transcription** (local Whisper)
- ✅ **AI-powered organization** with Gemini
- ✅ **Structured output** in markdown format

**Start using it:**
1. Record a voice note about your day
2. Share to Audio AI from your recording app
3. Check `saved_ideas/personal/` for organized output
4. Experience the magic of AI-organized content! ✨

---

**Need help?** Check the detailed documentation:
- [Complete README](README.md) - Full system overview
- [Backend Guide](backend/PROJECT_OVERVIEW.md) - Technical details  
- [Android User Guide](android/USER_GUIDE.md) - App usage instructions
- [Android Architecture](android/ARCHITECTURE.md) - Development guide