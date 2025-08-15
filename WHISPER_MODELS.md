# Whisper Model Configuration Guide

## 📊 Available Whisper Models

OpenAI Whisper offers different models with varying trade-offs between speed, accuracy, and resource usage:

| Model | Size | Parameters | VRAM | Speed | Quality | Use Case |
|-------|------|------------|------|-------|---------|----------|
| `tiny` | 39 MB | 39M | ~1 GB | ~32x realtime | Basic | Quick demos, low-resource |
| `base` | 74 MB | 74M | ~1 GB | ~16x realtime | Good | Development, balanced |
| `small` | 244 MB | 244M | ~2 GB | ~6x realtime | Better | Production, good quality |
| `medium` | 769 MB | 769M | ~5 GB | ~2x realtime | Great | High accuracy needs |
| `large` | 1550 MB | 1550M | ~10 GB | ~1x realtime | Excellent | Maximum quality |

## 🚀 How to Configure Models

### Docker Whisper Service

**Option 1: Environment Variable**
```bash
# Use small model for better quality
export WHISPER_MODEL=small
docker-compose -f docker-compose.whisper.yml up -d

# Use large model for maximum accuracy  
export WHISPER_MODEL=large
docker-compose -f docker-compose.whisper.yml up -d
```

**Option 2: Direct Command**
```bash
# One-time model selection
WHISPER_MODEL=medium docker-compose -f docker-compose.whisper.yml up -d

# For Windows PowerShell
$env:WHISPER_MODEL="small"; docker-compose -f docker-compose.whisper.yml up -d
```

**Option 3: .env File**
```bash
# Create .env file in project root
echo "WHISPER_MODEL=small" >> .env
docker-compose -f docker-compose.whisper.yml up -d
```

### Local Whisper Provider

Edit `backend/src/services/transcription-providers/LocalWhisperProvider.ts`:

```typescript
// Line ~62: Change model size
const args = [
  tempFilePath,
  '--output_dir', outputDir,
  '--output_format', 'txt',
  '--model', 'small', // Change this: tiny, base, small, medium, large
  '--language', 'en'
];
```

## 🎯 Model Selection Guide

### For Development & Testing
```bash
# Fast iteration, basic quality
WHISPER_MODEL=tiny docker-compose -f docker-compose.whisper.yml up -d
```

### For Production (Balanced)
```bash
# Good quality, reasonable speed
WHISPER_MODEL=small docker-compose -f docker-compose.whisper.yml up -d
```

### For High Accuracy
```bash
# Best quality, slower processing
WHISPER_MODEL=large docker-compose -f docker-compose.whisper.yml up -d
```

### For Low-Resource Systems
```bash
# Minimal requirements
WHISPER_MODEL=base docker-compose -f docker-compose.whisper.yml up -d
```

## 📈 Performance Comparison

### Processing Time Examples
*For a 5-minute audio file on typical hardware:*

| Model | CPU Time | GPU Time | Quality Score |
|-------|----------|----------|---------------|
| `tiny` | ~10 seconds | ~3 seconds | 7/10 |
| `base` | ~20 seconds | ~6 seconds | 8/10 |
| `small` | ~50 seconds | ~15 seconds | 8.5/10 |
| `medium` | ~2 minutes | ~25 seconds | 9/10 |
| `large` | ~5 minutes | ~45 seconds | 9.5/10 |

### Memory Requirements

| Model | RAM Usage | GPU Memory | Disk Space |
|-------|-----------|------------|------------|
| `tiny` | ~390 MB | ~1 GB | ~40 MB |
| `base` | ~550 MB | ~1 GB | ~75 MB |
| `small` | ~1 GB | ~2 GB | ~245 MB |
| `medium` | ~2 GB | ~5 GB | ~770 MB |
| `large` | ~4 GB | ~10 GB | ~1.6 GB |

## 🔧 Advanced Configuration

### Language-Specific Models
```bash
# For non-English audio, specify language for better accuracy
# (Add to whisper-service.py if needed)
WHISPER_MODEL=small
WHISPER_LANGUAGE=es  # Spanish
WHISPER_LANGUAGE=fr  # French
WHISPER_LANGUAGE=de  # German
```

### GPU Acceleration
```bash
# Enable GPU support (requires NVIDIA GPU + CUDA)
# Models will automatically use GPU if available
WHISPER_MODEL=large docker-compose -f docker-compose.whisper.yml up -d
```

### Custom Resource Limits
```yaml
# docker-compose.whisper.yml
whisper-service:
  deploy:
    resources:
      limits:
        memory: 8G        # Adjust based on model
        cpus: '4.0'
      reservations:
        memory: 2G
        cpus: '2.0'
  environment:
    - WHISPER_MODEL=medium
```

## 🧪 Testing Different Models

### Quick Model Comparison
```bash
# Test with different models
models=("tiny" "base" "small" "medium")

for model in "${models[@]}"; do
  echo "Testing with $model model..."
  
  # Stop existing service
  docker-compose -f docker-compose.whisper.yml down
  
  # Start with new model
  WHISPER_MODEL=$model docker-compose -f docker-compose.whisper.yml up -d
  
  # Wait for service to be ready
  sleep 30
  
  # Test transcription
  time curl -X POST http://localhost:3000/process-file -F "file=@test-audio.mp3"
  
  echo "Completed test with $model model"
done
```

### Health Check with Model Info
```bash
# Check which model is currently running
curl http://localhost:8001/health

# Example response:
# {
#   "status": "healthy",
#   "service": "whisper-transcription", 
#   "model": "small"
# }
```

## 📝 Model Selection Decision Tree

```
Need maximum accuracy? 
├─ Yes → Use `large`
└─ No
   ├─ Limited resources (< 4GB RAM)?
   │  ├─ Yes → Use `tiny` or `base`
   │  └─ No → Continue
   ├─ Need fast processing?
   │  ├─ Yes → Use `base` or `small`
   │  └─ No → Use `medium` or `large`
   └─ Balanced quality/speed needed?
      └─ Yes → Use `small` (recommended)
```

## 🔄 Changing Models

### Runtime Model Change
```bash
# Change model without rebuilding
docker-compose -f docker-compose.whisper.yml down
WHISPER_MODEL=medium docker-compose -f docker-compose.whisper.yml up -d
```

### Permanent Configuration
```bash
# Add to your .env file
echo "WHISPER_MODEL=small" >> .env

# Or update docker-compose.whisper.yml directly:
environment:
  - WHISPER_MODEL=small  # Change default here
```

## 🎯 Recommended Configurations

### Development Setup
```bash
WHISPER_MODEL=base  # Fast iteration, good enough quality
```

### Production Setup  
```bash
WHISPER_MODEL=small  # Best balance of speed and quality
```

### High-Accuracy Setup
```bash
WHISPER_MODEL=large  # Maximum transcription quality
```

### Resource-Constrained Setup
```bash
WHISPER_MODEL=tiny   # Minimum resource usage
```

---

## 💡 Tips

1. **Start with `base`** for testing, then upgrade to `small` for production
2. **Use `large`** only if you need maximum accuracy and have sufficient resources
3. **GPU acceleration** significantly improves processing speed for larger models
4. **First run** takes longer as the model downloads and loads
5. **Model persistence** - models are cached after first download

Choose the model that best fits your quality requirements and available resources!