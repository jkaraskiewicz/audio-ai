# Docker Deployment Guide

Complete instructions for deploying Audio-AI using Docker in any environment.

## 🐳 Option 1: Quick Docker Deployment (Recommended)

### Prerequisites
- Docker installed
- Docker Compose installed
- Gemini API key

### Steps

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   cd audio-ai
   ```

2. **Set up environment variables:**
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` and add your Gemini API key:
   ```bash
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   TRANSCRIPTION_PROVIDER=free_web_speech
   BASE_DIRECTORY=processed
   PORT=3000
   ```

3. **Build and start the application:**
   ```bash
   docker-compose up --build
   ```

4. **Test the deployment:**
   ```bash
   # In another terminal
   curl http://localhost:3000/health
   ```

5. **Test with a file:**
   ```bash
   curl -X POST http://localhost:3000/process-file \
     -F "file=@test-audio.m4a"
   ```

**That's it! Your application is running at http://localhost:3000**

---

## 🎯 Option 2: Docker with Whisper Service (High Accuracy)

For the best transcription quality using OpenAI Whisper in Docker:

### Steps

1. **Set up environment (same as Option 1):**
   ```bash
   git clone <your-repository-url>
   cd audio-ai
   cp .env.example .env
   # Edit .env with your GEMINI_API_KEY
   ```

2. **Start the complete stack with Whisper:**
   ```bash
   docker-compose -f docker-compose.whisper.yml up --build
   ```

   This will:
   - Build and start the Whisper service (port 8001)
   - Build and start the Audio-AI app (port 3000)
   - Configure automatic provider switching

3. **Wait for services to be ready:**
   ```bash
   # Check Whisper service
   curl http://localhost:8001/health
   
   # Check main app
   curl http://localhost:3000/health
   ```

4. **Test with high-quality transcription:**
   ```bash
   curl -X POST http://localhost:3000/process-file \
     -F "file=@your-audio.mp3"
   ```

---

## 🛠️ Option 3: Manual Docker Build

If you prefer to build and run containers manually:

### Build the main application:
```bash
# Build the image
docker build -t audio-ai .

# Run with environment variables
docker run -d \
  --name audio-ai-app \
  -p 3000:3000 \
  -e GEMINI_API_KEY=your_api_key_here \
  -e TRANSCRIPTION_PROVIDER=free_web_speech \
  -v $(pwd)/processed:/usr/src/app/processed \
  audio-ai
```

### Optional: Build and run Whisper service:
```bash
# Build Whisper service
docker build -f whisper.Dockerfile -t audio-ai-whisper .

# Run Whisper service
docker run -d \
  --name whisper-service \
  -p 8001:8001 \
  audio-ai-whisper

# Update main app to use Docker Whisper
docker run -d \
  --name audio-ai-app \
  -p 3000:3000 \
  -e GEMINI_API_KEY=your_api_key_here \
  -e TRANSCRIPTION_PROVIDER=docker_whisper \
  -e WHISPER_SERVICE_URL=http://whisper-service:8001 \
  --link whisper-service \
  -v $(pwd)/processed:/usr/src/app/processed \
  audio-ai
```

---

## 📋 Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `GEMINI_API_KEY` | ✅ Yes | - | Your Google Gemini API key |
| `TRANSCRIPTION_PROVIDER` | No | `free_web_speech` | Transcription service to use |
| `TRANSCRIPTION_API_KEY` | No | - | API key for transcription service (if needed) |
| `BASE_DIRECTORY` | No | `processed` | Directory for saving markdown files |
| `PORT` | No | `3000` | Port for the web server |
| `WHISPER_SERVICE_URL` | No | `http://localhost:8001` | URL for Docker Whisper service |

---

## 🚀 Available Transcription Providers

You can switch providers by changing the `TRANSCRIPTION_PROVIDER` environment variable:

| Provider | Value | Requirements | Quality |
|----------|-------|--------------|---------|
| **Free Web Speech** | `free_web_speech` | None | Good |
| **Hugging Face** | `hugging_face` | Optional API key | Excellent |
| **Docker Whisper** | `docker_whisper` | Whisper container | Excellent |
| **Mock** | `mock` | None (testing only) | N/A |

### Example: Switch to Hugging Face
```bash
# Update .env file
TRANSCRIPTION_PROVIDER=hugging_face
TRANSCRIPTION_API_KEY=your_hugging_face_token  # Optional

# Restart container
docker-compose restart
```

---

## 🧪 Testing Your Deployment

### Automated Testing
Run the included test script:
```bash
# Make sure the app is running, then:
./test-deployment.sh
```

### Manual Testing

1. **Health check:**
   ```bash
   curl http://localhost:3000/health
   ```
   Expected: `{"status":"healthy","transcriptionProvider":"free_web_speech"}`

2. **Text processing:**
   ```bash
   curl -X POST http://localhost:3000/process \
     -H "Content-Type: application/json" \
     -d '{"transcript": "This is a test about a new mobile app idea"}'
   ```

3. **File processing:**
   ```bash
   curl -X POST http://localhost:3000/process-file \
     -F "file=@your-audio.mp3"
   ```

4. **Check output:**
   ```bash
   ls processed/
   cat processed/projects/[latest-file].md
   ```

---

## 📁 File Persistence

### Volume Mounting
The Docker setup automatically mounts the `processed` directory so your generated markdown files persist outside the container:

```bash
# Files are saved to:
./processed/projects/
./processed/daily/tasks/
./processed/personal/
# etc.
```

### Custom Output Directory
To use a different directory:
```bash
# Option 1: Environment variable
BASE_DIRECTORY=my_custom_output

# Option 2: Docker volume
docker run -v /path/to/your/dir:/usr/src/app/processed audio-ai
```

---

## 🔧 Troubleshooting

### Common Issues

**1. Port already in use:**
```bash
# Change port in .env or docker command
PORT=3001
```

**2. Gemini API key error:**
```bash
# Verify your API key in .env
GEMINI_API_KEY=your_actual_key_here

# Check container logs
docker-compose logs audio-ai
```

**3. Whisper service not starting:**
```bash
# Check Whisper logs
docker-compose -f docker-compose.whisper.yml logs whisper-service

# Ensure sufficient memory (Whisper needs ~2GB)
docker stats
```

**4. File upload fails:**
```bash
# Check file size (max 30-100MB depending on provider)
ls -lh your-audio.mp3

# Check container logs
docker-compose logs
```

### Debugging Commands

```bash
# View container logs
docker-compose logs -f

# Access container shell
docker-compose exec audio-ai sh

# Check running containers
docker-compose ps

# Restart services
docker-compose restart

# Rebuild and restart
docker-compose up --build --force-recreate
```

---

## 🛑 Stopping the Application

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop Whisper setup
docker-compose -f docker-compose.whisper.yml down
```

---

## 🚀 Production Deployment Tips

### For Production Use:

1. **Use environment variables for secrets:**
   ```bash
   # Don't put keys in .env file in production
   export GEMINI_API_KEY=your_key
   docker-compose up
   ```

2. **Use Docker Swarm or Kubernetes:**
   ```bash
   # Docker Swarm example
   docker stack deploy -c docker-compose.yml audio-ai
   ```

3. **Add reverse proxy (nginx):**
   ```bash
   # Add nginx container for SSL/domain handling
   # See docker-compose.production.yml (create as needed)
   ```

4. **Monitor with logs:**
   ```bash
   # Centralized logging
   docker-compose logs -f | tee audio-ai.log
   ```

---

## ✅ Quick Start Summary

**Fastest way to get running:**

```bash
git clone <repo>
cd audio-ai
cp .env.example .env
# Edit .env with your GEMINI_API_KEY
docker-compose up --build
curl http://localhost:3000/health
```

**Your Audio-AI application is now running at http://localhost:3000** 🎉