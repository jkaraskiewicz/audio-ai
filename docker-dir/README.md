# 🐳 Audio-AI Docker Stack

Complete Audio-AI setup with Whisper ASR service in Docker.

## 🚀 Quick Start

1. **Setup environment**:
   ```bash
   cp .env.example .env
   # Edit .env and add your GEMINI_API_KEY
   ```

2. **Start the stack**:
   ```bash
   ./start.sh
   # OR
   docker-compose up -d
   ```

3. **Test it works**:
   ```bash
   curl http://localhost:3000/health
   ```

## 📋 Services

- **Audio-AI Backend**: http://localhost:3000
- **Whisper ASR Service**: http://localhost:9000

## 🧪 Quick Test

```bash
# Process text
curl -X POST http://localhost:3000/process \
  -H "Content-Type: application/json" \
  -d '{"transcript":"Meeting notes: discuss project timeline"}'

# Upload audio file
curl -X POST http://localhost:3000/process-file -F "file=@audio.mp3"
```

## 📁 Output

Processed files are saved to `../processed/category/YYYY-MM-DD_filename.md`

## 🛑 Stop Services

```bash
docker-compose down
```

## 🔧 Configuration

Edit `.env` file to customize:
- `GEMINI_API_KEY`: Required for AI processing
- `ASR_MODEL`: Whisper model (tiny, base, small, medium, large)
- `ASR_ENGINE`: Whisper engine (openai_whisper, faster_whisper)

## 📊 Monitoring

```bash
# Check logs
docker-compose logs

# Check specific service
docker-compose logs audio-ai
docker-compose logs whisper

# Service status
docker-compose ps
```