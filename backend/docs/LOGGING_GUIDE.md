# Audio AI Backend - Logging Guide

## 📋 Overview

The Audio AI backend now includes comprehensive logging for transcription results, making it easy to debug transcription quality, monitor AI processing inputs, and track the complete audio-to-text pipeline.

## 🎯 What's Logged

### 1. Transcription Results
- **Provider-level logging** in each transcription provider
- **Service-level logging** in AudioTranscriptionService
- **Processing-level logging** in TranscriptProcessorService

### 2. Log Levels

**INFO Level (Default):**
- File processing start/completion
- Transcription completion with preview
- AI processing inputs with preview
- Performance metrics (file size, processing time, etc.)

**DEBUG Level (Detailed):**
- Full transcription text
- Complete AI processing input
- Provider-specific details
- Error details and stack traces

## 🔍 Log Output Examples

### Audio File Processing
```json
{
  "level": "info",
  "message": "Local Whisper transcription completed",
  "filename": "meeting-recording.mp3",
  "transcriptionLength": 2847,
  "transcriptionPreview": "Good morning everyone, let's start today's standup. First, I'd like to go over the sprint goals that we discussed last week. We have three main objectives for this iteration: completing the user authentication flow...",
  "timestamp": "2025-01-15T10:30:45.123Z"
}
```

### AI Processing Input
```json
{
  "level": "info", 
  "message": "File processed successfully, extracted text for AI processing",
  "filename": "meeting-recording.mp3",
  "fileType": "AUDIO",
  "method": "local_whisper_base",
  "extractedLength": 2847,
  "textPreview": "Good morning everyone, let's start today's standup. First, I'd like to go over the sprint goals that we discussed last week. We have three main objectives for this iteration: completing the user authentication flow, implementing the new dashboard layout, and fixing the reported performance issues...",
  "timestamp": "2025-01-15T10:30:47.456Z"
}
```

### Debug Level - Full Text
```json
{
  "level": "debug",
  "message": "Local Whisper full transcription result", 
  "filename": "meeting-recording.mp3",
  "fullTranscription": "Good morning everyone, let's start today's standup. First, I'd like to go over the sprint goals that we discussed last week. We have three main objectives for this iteration: completing the user authentication flow, implementing the new dashboard layout, and fixing the reported performance issues with the data loading. Sarah, would you like to start with your updates? Sure, I've been working on the authentication system and I'm about 80% complete. I've implemented the login and registration flows, and I'm currently working on the password reset functionality. I expect to have everything done by Thursday. The main challenge I've been facing is with the OAuth integration for Google and Facebook login...",
  "timestamp": "2025-01-15T10:30:45.456Z"
}
```

## ⚙️ Configuration

### Setting Log Level

**Environment Variable:**
```bash
# Show only info-level logs and above (default)
LOG_LEVEL=info

# Show all logs including debug (shows full transcription text)
LOG_LEVEL=debug

# Show only warnings and errors
LOG_LEVEL=warn
```

**Runtime Configuration:**
```typescript
import { logger } from '../utils/logger';

// Temporarily change log level
logger.level = 'debug';
```

### Viewing Logs

**Console Output:**
```bash
npm run dev
# Logs will appear in console with structured JSON format
```

**File Output (if configured):**
```bash
# Logs can be redirected to file
npm run dev > logs/app.log 2>&1

# Or use a process manager like PM2
pm2 start npm --name "audio-ai" -- run dev
pm2 logs audio-ai
```

## 🔧 Logging by Component

### AudioTranscriptionService
**Location:** `src/services/AudioTranscriptionService.ts`

**What's logged:**
- Transcription start with provider info
- Transcription completion with preview
- Full transcription text (debug level)
- Processing errors with context

### TranscriptProcessorService  
**Location:** `src/services/TranscriptProcessorService.ts`

**What's logged:**
- File processing results
- Text preparation for AI
- Direct transcript inputs
- AI processing completion

### Transcription Providers
**Locations:** `src/services/transcription-providers/`

**What's logged:**
- Provider-specific processing details
- Model/configuration information
- Transcription results with previews
- Provider-specific error handling

## 📊 Monitoring & Debugging

### Common Use Cases

**1. Debug Transcription Quality**
```bash
# Set debug level to see full transcriptions
LOG_LEVEL=debug npm run dev

# Process a test file
curl -X POST http://localhost:3000/process-file -F "file=@test.mp3"

# Check logs for full transcription accuracy
```

**2. Monitor Processing Pipeline**
```bash
# Watch logs in real-time
npm run dev | grep "transcription\|AI processing"

# Check for specific files
npm run dev | grep "meeting-recording.mp3"
```

**3. Performance Analysis**
```bash
# Monitor processing times and file sizes
npm run dev | grep "transcriptionLength\|processingTime"
```

### Log Filtering

**Using jq for JSON log parsing:**
```bash
# Extract just transcription previews
npm run dev | jq -r 'select(.transcriptionPreview) | .transcriptionPreview'

# Show processing times and file sizes
npm run dev | jq 'select(.processingTimeMs) | {filename, processingTimeMs, fileSize}'

# Filter by provider
npm run dev | jq 'select(.provider == "Local Whisper (OpenAI)")'
```

**Using grep for quick filtering:**
```bash
# Show only transcription completions
npm run dev | grep "transcription completed"

# Show only AI processing inputs
npm run dev | grep "extracted text for AI processing"

# Show errors only
npm run dev | grep '"level":"error"'
```

## 🎯 Best Practices

### For Development
1. **Use DEBUG level** when developing new transcription providers
2. **Monitor transcription previews** to quickly assess quality
3. **Check full transcriptions** when debugging specific audio files
4. **Use structured logging** for easy parsing and filtering

### For Production
1. **Use INFO level** for normal operation (avoids logging sensitive content)
2. **Rotate logs regularly** to manage disk space
3. **Monitor for errors** and performance issues
4. **Set up alerting** on transcription failures

### For Debugging
1. **Compare transcription previews** across different providers
2. **Check AI input text** to understand processing pipeline
3. **Use debug level temporarily** for troubleshooting specific files
4. **Track processing times** to identify performance bottlenecks

## 🔒 Privacy Considerations

### LOG_LEVEL=info (Default)
- Shows **transcription previews** (first 200 characters)
- Shows **text previews** for AI processing (first 300 characters)
- Balances debugging capability with privacy

### LOG_LEVEL=debug (Full Logging)
- Shows **complete transcription text**
- Shows **full AI processing input**
- Use only when debugging specific issues
- Not recommended for production with sensitive content

### LOG_LEVEL=warn (Minimal Logging)
- Shows only warnings and errors
- No transcription content logged
- Maximum privacy, minimal debugging capability

## 📈 Example Debugging Session

```bash
# 1. Enable debug logging
export LOG_LEVEL=debug

# 2. Start server
npm run dev

# 3. Process test file
curl -X POST http://localhost:3000/process-file -F "file=@problem-audio.mp3"

# 4. Check logs for:
#    - Transcription provider used
#    - Full transcription quality  
#    - Text passed to AI
#    - Processing errors

# 5. Compare with different provider
export TRANSCRIPTION_PROVIDER=free_web_speech
npm run dev
curl -X POST http://localhost:3000/process-file -F "file=@problem-audio.mp3"

# 6. Compare transcription results in logs
```

This logging system provides complete visibility into the audio-to-text pipeline while maintaining flexibility for different privacy and debugging needs.