# 🎵➡️📝 Audio-to-Text Setup Guide

Simple guide to configure audio transcription for your Audio-AI project.

## 🎯 **Three Simple Options**

### **Option 1: Use Your Existing Whisper Service** (Recommended)
```bash
# If you have Whisper already running (like your port 1991 setup)
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose up -d
```

### **Option 2: Quick Local Whisper**
```bash
# Spins up Audio-AI + fresh Whisper service
docker-compose -f docker-compose.whisper.yml up -d
```

### **Option 3: No Whisper (Free Web Speech)**
```bash
# Uses browser-based speech recognition (no setup required)
export TRANSCRIPTION_PROVIDER=free_web_speech
docker-compose up -d
```

## ⚙️ **Easy Configuration**

### **Environment Variables**
```bash
# Core settings (create .env file)
GEMINI_API_KEY=your_api_key_here
TRANSCRIPTION_PROVIDER=openai_whisper_webservice
WHISPER_SERVICE_URL=http://host.docker.internal:1991

# Optional settings
WHISPER_MODEL=base
BASE_DIRECTORY=processed
```

### **Quick Switch Between Providers**
```bash
# Switch to existing Whisper
echo "TRANSCRIPTION_PROVIDER=openai_whisper_webservice" >> .env

# Switch to free web speech
echo "TRANSCRIPTION_PROVIDER=free_web_speech" >> .env

# Switch to self-hosted Whisper
docker-compose -f docker-compose.whisper.yml up -d
```

## 🧪 **Testing Your Setup**

### **1. Check Service Health**
```bash
curl http://localhost:3000/health
# Should return: {"status":"healthy",...}
```

### **2. Test Transcription**
```bash
# Upload an audio file
curl -X POST http://localhost:3000/process-file -F "file=@test-audio.mp3"
```

### **3. Check Logs**
```bash
docker-compose logs audio-ai
# Look for: "Whisper service is available" or "transcription completed"
```

## 🚨 **Common Issues & Fixes**

### **"Connection failed" to Whisper**
```bash
# Problem: Docker can't reach localhost
# Fix: Use host.docker.internal instead
WHISPER_SERVICE_URL=http://host.docker.internal:1991

# Or use host network
docker run --network=host your-audio-ai
```

### **"Unknown provider" error**
```bash
# Problem: Typo in provider name
# Fix: Use exact provider names
TRANSCRIPTION_PROVIDER=openai_whisper_webservice  # ✅ Correct
TRANSCRIPTION_PROVIDER=whisper_webservice         # ❌ Wrong
```

### **Whisper service not starting**
```bash
# Check if port is available
netstat -an | grep :1991

# Check Whisper logs
docker logs your-whisper-container

# Common fix: Use valid model names
docker run -e ASR_MODEL=base your-whisper-image  # ✅
docker run -e ASR_MODEL=invalid-model            # ❌
```

## 📊 **Provider Comparison**

| Provider | Setup | Quality | Speed | Cost |
|----------|-------|---------|-------|------|
| **Your Existing Whisper** | ✅ Done | 🟢 High | 🟢 Fast | 🟢 Free |
| **Self-hosted Whisper** | 🟡 Easy | 🟢 High | 🟢 Fast | 🟢 Free |
| **Free Web Speech** | ✅ None | 🟡 Good | 🟢 Fast | 🟢 Free |
| **Remote APIs** | 🟡 API Key | 🟢 High | 🟡 Medium | 🔴 Paid |

## 🎯 **Recommended Workflow**

### **For Development**
```bash
# Use free web speech for quick testing
TRANSCRIPTION_PROVIDER=free_web_speech
docker-compose up -d
```

### **For Production**
```bash
# Use your existing Whisper for best quality
TRANSCRIPTION_PROVIDER=openai_whisper_webservice
WHISPER_SERVICE_URL=http://your-whisper-host:port
docker-compose up -d
```

### **For New Users**
```bash
# Start with self-hosted Whisper
docker-compose -f docker-compose.whisper.yml up -d
```

## 🔧 **One-Command Setup**

### **Complete Setup Script**
```bash
#!/bin/bash
# setup-audio-ai.sh

echo "🎵 Setting up Audio-AI..."

# Create .env file
cat > .env << EOF
GEMINI_API_KEY=your_api_key_here
TRANSCRIPTION_PROVIDER=openai_whisper_webservice
WHISPER_SERVICE_URL=http://host.docker.internal:1991
BASE_DIRECTORY=processed
EOF

echo "✅ Configuration created in .env"
echo "📝 Edit .env with your API key"
echo "🚀 Run: docker-compose up -d"
```

### **Quick Test Script**
```bash
#!/bin/bash
# test-audio-ai.sh

echo "🧪 Testing Audio-AI setup..."

# Test health
echo "Checking service health..."
curl -s http://localhost:3000/health | jq '.status'

# Test with sample transcript
echo "Testing transcript processing..."
curl -X POST http://localhost:3000/process-transcript \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Hello world test"}' | jq '.message'

echo "✅ Test complete!"
```

## 💡 **Pro Tips**

1. **Use your existing Whisper** - it's already working!
2. **Create .env file** - avoid typing long environment variables
3. **Test with health endpoint** - always verify before uploading audio
4. **Use host.docker.internal** - for Docker-to-host communication
5. **Check logs first** - most issues show up in container logs

---

## 🆘 **Need Help?**

1. **Check logs**: `docker-compose logs audio-ai`
2. **Verify config**: `docker-compose config`
3. **Test connectivity**: `curl http://localhost:3000/health`
4. **Reset everything**: `docker-compose down && docker-compose up -d`

Choose the option that fits your needs and you'll have audio transcription working in minutes! 🎉