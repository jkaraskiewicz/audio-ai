# External Whisper Service Configuration

This guide shows how to configure your Audio-AI project to connect to an existing Whisper service instead of spinning up a new one.

## 🎯 Use Cases

- **Already running Whisper elsewhere** - Connect to existing Whisper instances
- **Shared Whisper service** - Multiple projects using the same Whisper service
- **External hosting** - Whisper running on different machine/server
- **Development efficiency** - Avoid spinning up multiple Whisper containers

## 🚀 Quick Setup

### Option 1: Environment Variable
```bash
# Set the external Whisper service URL
export WHISPER_SERVICE_URL=http://your-whisper-host:8001

# Run Audio-AI (connects to external Whisper)
docker-compose -f docker-compose.external-whisper.yml up -d
```

### Option 2: .env File
```bash
# Create .env file with Whisper service URL
echo "WHISPER_SERVICE_URL=http://your-whisper-host:8001" >> .env
echo "GEMINI_API_KEY=your_api_key" >> .env

# Run Audio-AI
docker-compose -f docker-compose.external-whisper.yml up -d
```

### Option 3: Direct Command
```bash
# One-time configuration
WHISPER_SERVICE_URL=http://192.168.1.100:8001 docker-compose -f docker-compose.external-whisper.yml up -d
```

## 📋 Configuration Examples

### Local Whisper Service
```bash
# Connect to Whisper running locally on different port
WHISPER_SERVICE_URL=http://localhost:8001
```

### Remote Whisper Service
```bash
# Connect to Whisper on another machine
WHISPER_SERVICE_URL=http://192.168.1.100:8001

# Connect to Whisper on different server
WHISPER_SERVICE_URL=http://whisper-server.company.com:8001
```

### Docker Network Whisper
```bash
# Connect to Whisper in same Docker network
WHISPER_SERVICE_URL=http://whisper-service:8001
```

### Cloud/Remote Whisper
```bash
# Connect to cloud-hosted Whisper service
WHISPER_SERVICE_URL=https://my-whisper-api.herokuapp.com
```

## 🔧 Docker Compose Files

### For External Whisper (Recommended)
```bash
# Uses existing Whisper service
docker-compose -f docker-compose.external-whisper.yml up -d
```

### For Self-Hosted Whisper
```bash
# Spins up both Audio-AI and Whisper
docker-compose -f docker-compose.whisper.yml up -d
```

### For Local Development Only
```bash
# Local development without Docker
npm run dev
```

## 🧪 Testing Connection

### 1. Check Whisper Service Health
```bash
# Test if external Whisper is accessible
curl http://your-whisper-host:8001/health

# Expected response:
# {"status":"healthy","service":"whisper-transcription","model":"base"}
```

### 2. Test Audio-AI Connection
```bash
# Check Audio-AI logs for Whisper connection
docker-compose -f docker-compose.external-whisper.yml logs audio-ai

# Look for:
# "Docker Whisper service is available"
```

### 3. End-to-End Test
```bash
# Test full transcription pipeline
curl -X POST http://localhost:3000/process-file -F "file=@test-audio.mp3"
```

## ⚙️ Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `WHISPER_SERVICE_URL` | `http://localhost:8001` | External Whisper service URL |
| `TRANSCRIPTION_PROVIDER` | `docker_whisper` | Provider type (auto-set) |
| `GEMINI_API_KEY` | (required) | Your Gemini API key |

## 🔀 Switching Between Configurations

### Switch to External Whisper
```bash
# Stop current services
docker-compose down

# Start with external Whisper
WHISPER_SERVICE_URL=http://external-host:8001 docker-compose -f docker-compose.external-whisper.yml up -d
```

### Switch to Self-Hosted Whisper
```bash
# Stop external configuration
docker-compose -f docker-compose.external-whisper.yml down

# Start with self-hosted Whisper
docker-compose -f docker-compose.whisper.yml up -d
```

### Switch to Local Development
```bash
# Stop Docker services
docker-compose down

# Set environment for local development
export WHISPER_SERVICE_URL=http://localhost:8001
export TRANSCRIPTION_PROVIDER=docker_whisper

# Run locally
npm run dev
```

## 🏗️ Advanced Configurations

### Multiple Whisper Services (Load Balancing)
```bash
# Use environment-specific Whisper services
WHISPER_SERVICE_URL=http://whisper-prod.company.com:8001  # Production
WHISPER_SERVICE_URL=http://whisper-dev.company.com:8001   # Development
```

### Custom Whisper Models via External Service
```bash
# If your external Whisper supports model configuration
WHISPER_SERVICE_URL=http://whisper-large.company.com:8001  # Large model
WHISPER_SERVICE_URL=http://whisper-small.company.com:8001  # Small model
```

### Secure Whisper Connection
```bash
# HTTPS connection to secure Whisper service
WHISPER_SERVICE_URL=https://secure-whisper.company.com:443
```

## 🐛 Troubleshooting

### Connection Issues
```bash
# Check if Whisper service is reachable
ping whisper-host

# Check if port is open
telnet whisper-host 8001

# Check firewall/security groups
curl -v http://whisper-host:8001/health
```

### Service Discovery Issues
```bash
# Check Docker network connectivity
docker network ls
docker inspect bridge

# Test from inside Audio-AI container
docker exec -it audio-ai-container curl http://whisper-service:8001/health
```

### Configuration Issues
```bash
# Check environment variables
docker-compose -f docker-compose.external-whisper.yml config

# Check logs for configuration errors
docker-compose -f docker-compose.external-whisper.yml logs
```

## 📊 Performance Considerations

### Network Latency
- **Local network**: ~1-5ms overhead
- **Same datacenter**: ~5-10ms overhead  
- **Cross-region**: ~50-200ms overhead
- **Internet**: ~100-500ms overhead

### Bandwidth Usage
- **Audio upload**: ~1MB per minute of audio
- **Response**: ~1KB per minute of transcribed text
- **Keep-alive**: Minimal bandwidth

### Recommended Setup
```bash
# For best performance, use local network Whisper
WHISPER_SERVICE_URL=http://192.168.1.100:8001  # Same network

# For production, use dedicated Whisper server
WHISPER_SERVICE_URL=http://whisper-prod.company.com:8001
```

## 🔒 Security Notes

1. **Use HTTPS** for external/public Whisper services
2. **Network security** - Ensure Whisper service is not publicly exposed unless intended
3. **Authentication** - Current setup assumes no authentication (add if needed)
4. **Firewall rules** - Allow only necessary traffic between services

## ✅ Quick Checklist

Before using external Whisper configuration:

- [ ] External Whisper service is running and accessible
- [ ] Health endpoint returns 200 OK: `curl http://whisper-host:8001/health`
- [ ] Network connectivity verified between Audio-AI and Whisper
- [ ] Environment variables set correctly
- [ ] Firewall/security groups allow traffic on port 8001
- [ ] GEMINI_API_KEY configured for AI processing

---

## 💡 Pro Tips

1. **Use local network** Whisper services for best performance
2. **Monitor Whisper logs** to troubleshoot transcription issues
3. **Scale Whisper independently** - run multiple Whisper instances for load balancing
4. **Cache strategy** - Consider implementing caching between Audio-AI and Whisper
5. **Health monitoring** - Set up monitoring for both services

Happy transcribing! 🎵➡️📝