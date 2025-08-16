# 🔌 External Service Integration Guide

## Connecting to Your Existing Whisper

If you already have `onerahmet/openai-whisper-asr-webservice` running:

```bash
# Your Whisper on port 1991
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
export TRANSCRIPTION_PROVIDER=openai_whisper_webservice
docker-compose -f docker-compose.external-whisper.yml up -d
```

**Key points:**
- Uses `host.docker.internal` for Docker-to-host communication
- Handles both JSON and plain text responses automatically
- No code changes needed - just environment variables

## Adding New External Services

### 1. Provider Interface
All providers implement `AudioTranscriptionProvider`:

```typescript
interface AudioTranscriptionProvider {
  transcribe(file: Express.Multer.File): Promise<FileProcessingResult>;
  getSupportedFormats(): string[];
  getMaxFileSize(): number;
  getProviderName(): string;
  isReady(): boolean;
}
```

### 2. Example External API Provider

```typescript
export class ExternalAPIProvider implements AudioTranscriptionProvider {
  constructor(private apiKey: string, private endpoint: string) {}

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    const formData = new FormData();
    formData.append('audio', file.buffer, file.originalname);
    
    const response = await fetch(`${this.endpoint}/transcribe`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${this.apiKey}` },
      body: formData,
    });
    
    const result = await response.json();
    
    return {
      extractedText: result.text,
      fileType: FileType.AUDIO,
      processingMethod: 'external_api',
    };
  }
  
  getSupportedFormats(): string[] { return ['mp3', 'wav', 'm4a']; }
  getMaxFileSize(): number { return 25 * 1024 * 1024; }
  getProviderName(): string { return 'External API'; }
  isReady(): boolean { return !!this.apiKey; }
}
```

### 3. Register Provider

```typescript
// In ProviderRegistry.ts
case 'external_api':
  return new ExternalAPIProvider(
    process.env.EXTERNAL_API_KEY!,
    process.env.EXTERNAL_API_ENDPOINT!
  );
```

### 4. Environment Configuration

```bash
TRANSCRIPTION_PROVIDER=external_api
EXTERNAL_API_KEY=your_api_key
EXTERNAL_API_ENDPOINT=https://api.example.com
```

## Available Providers

| Provider | Environment Variable | Requirements |
|----------|---------------------|--------------|
| **onerahmet/openai-whisper-asr-webservice** | `openai_whisper_webservice` | Docker container on accessible port |
| **Local Whisper** | `local_whisper` | `pip install openai-whisper` |
| **Hugging Face** | `huggingface` | `HUGGINGFACE_API_TOKEN` |
| **Gemini Audio** | `gemini_audio` | `GEMINI_API_KEY` |

## Testing External Connections

```bash
# Test your external service directly
curl -X POST http://your-service:port/transcribe \
  -F "audio=@test.mp3"

# Test through Audio-AI
curl -X POST http://localhost:3000/process-file \
  -F "file=@test.mp3"
```

## Docker Networking

### Container-to-Container
```yaml
# docker-compose.yml
networks:
  app_network:
    driver: bridge

services:
  whisper:
    networks: [app_network]
  audio-ai:
    networks: [app_network]
    environment:
      - WHISPER_SERVICE_URL=http://whisper:9000
```

### Container-to-Host
```bash
# For services running on host
export WHISPER_SERVICE_URL=http://host.docker.internal:1991
```

### External Network
```bash
# For services on other machines
export WHISPER_SERVICE_URL=http://192.168.1.100:1991
```

## Error Handling

The system automatically handles:
- Network timeouts (30s default)
- JSON vs plain text responses
- Service unavailability (falls back to error message)
- Invalid file formats (returns helpful error)

## Load Balancing Multiple Services

```typescript
export class LoadBalancedProvider implements AudioTranscriptionProvider {
  constructor(private endpoints: string[]) {}
  
  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    const endpoint = this.selectHealthyEndpoint();
    // Send request to selected endpoint
  }
  
  private selectHealthyEndpoint(): string {
    // Round-robin or health-based selection
  }
}
```

This integration approach allows you to easily connect to any existing transcription service without code modifications.