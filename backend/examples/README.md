# Audio-AI Examples

This directory contains comprehensive examples and templates for extending the Audio-AI system. These examples demonstrate best practices for adding new transcription providers, customizing AI prompts, and managing configurations.

## Directory Structure

```
examples/
├── providers/           # Audio transcription provider examples
│   ├── ExampleProvider.ts       # Complete template for new providers
│   ├── AssemblyAIProvider.ts    # Real-world API provider example
│   └── WhisperLocalProvider.ts  # Local/offline provider example
├── prompts/            # Custom prompt examples
│   └── CustomPrompts.ts        # Domain-specific prompt templates
├── config/             # Configuration management examples
│   └── DynamicConfig.ts        # Advanced configuration patterns
└── README.md           # This file
```

## Provider Examples

### 1. ExampleProvider.ts - Complete Template

This is the most comprehensive template for creating new providers. It includes:

- Full interface implementation
- Comprehensive error handling
- File validation and format checking
- Logging and metadata
- Health checking capabilities
- Detailed documentation and examples

**Use this when:** You're creating a completely new provider from scratch.

### 2. AssemblyAIProvider.ts - Production API Example

Shows how to implement a real-world provider using AssemblyAI's API:

- Asynchronous transcription workflow (upload → create job → poll for completion)
- Comprehensive configuration options
- Advanced features (speaker labels, custom models)
- Production-ready error handling
- File size and format validation

**Use this when:** You're integrating with a REST API that requires async processing.

### 3. WhisperLocalProvider.ts - Local Processing Example

Demonstrates local/offline transcription using Whisper:

- Subprocess management with Node.js
- Temporary file handling
- Local model management
- Privacy-focused implementation
- Resource usage considerations

**Use this when:** You need offline processing or have privacy requirements.

## Prompt Examples

### CustomPrompts.ts - Domain-Specific Templates

Contains complete prompt systems for different domains:

#### Medical/Healthcare Prompts
- Clinical documentation format
- HIPAA-compliant processing
- Evidence-based medical commentary
- Medical terminology and structure

#### Business/Corporate Prompts  
- Strategic analysis framework
- Business intelligence integration
- Executive summary format
- ROI and feasibility analysis

**Key Features:**
- Domain-specific metadata
- Specialized commentary types
- Professional formatting
- Industry best practices

## Configuration Examples

### DynamicConfig.ts - Advanced Configuration Management

Comprehensive configuration system supporting:

#### Environment-Based Configs
- **Development**: Fast models, detailed logging, relaxed limits
- **Staging**: Production-like with enhanced monitoring  
- **Production**: Optimized for performance and reliability

#### Use Case-Specific Configs
- **High Volume**: Optimized for throughput
- **High Accuracy**: Quality over speed
- **Privacy**: Local-only processing

#### Features
- Runtime configuration updates
- Configuration validation
- File-based config loading/saving
- Feature flag management
- Configuration comparison tools

## Quick Start Guide

### Adding a New Provider

1. **Copy the template:**
   ```bash
   cp examples/providers/ExampleProvider.ts src/services/transcription-providers/YourProvider.ts
   ```

2. **Customize the implementation:**
   - Replace `Example` with your provider name
   - Implement the `performTranscription` method
   - Update supported formats and file size limits
   - Configure API endpoints and authentication

3. **Register the provider:**
   ```typescript
   // In src/interfaces/AudioTranscriptionProvider.ts
   export enum TranscriptionProvider {
     YOUR_PROVIDER = 'your_provider',
   }
   
   // In src/services/transcription-providers/ProviderRegistry.ts
   [TranscriptionProvider.YOUR_PROVIDER, {
     name: TranscriptionProvider.YOUR_PROVIDER,
     description: 'Your provider description',
     constructor: YourProvider,
     requiresApiKey: true,
     // ... other config
   }]
   ```

4. **Add environment variables:**
   ```bash
   # In .env
   YOUR_PROVIDER_API_KEY=your_api_key_here
   ```

### Customizing Prompts

1. **Create custom prompts:**
   ```typescript
   import { CustomPromptEngine, MEDICAL_PROMPTS } from '../examples/prompts/CustomPrompts';
   
   // Use domain-specific prompt
   const medicalPrompt = CustomPromptEngine.getMedicalPrompt(transcript);
   ```

2. **Modify existing prompts:**
   ```typescript
   // In src/config/prompts.ts - modify PROMPTS.base.template
   // Increment version number when making changes
   ```

3. **Add new commentary types:**
   ```typescript
   // In src/config/prompts.ts - add to PROMPTS.commentary
   financial: {
     name: 'financial_commentary',
     template: `Your custom financial analysis prompt...`,
     variables: ['financial_text'],
   }
   ```

### Using Advanced Configuration

1. **Environment-based selection:**
   ```bash
   NODE_ENV=production npm start
   ```

2. **Use case-specific configuration:**
   ```bash
   USE_CASE=high-accuracy npm start
   ```

3. **Runtime configuration:**
   ```typescript
   const configManager = ConfigurationManager.getInstance();
   configManager.updateConfig({
     features: {
       enableCommentary: false, // Disable for speed
     },
   });
   ```

## Testing Your Extensions

### Provider Testing

```bash
# Run the development server
npm run dev

# Test your provider with a sample file
curl -X POST http://localhost:3000/process \
  -F "audio=@path/to/test.mp3" \
  -F "provider=your_provider"
```

### Prompt Testing

```bash
# Test with specific transcript content
curl -X POST http://localhost:3000/process \
  -H "Content-Type: application/json" \
  -d '{"transcript": "Your test transcript here"}'
```

### Configuration Testing

```typescript
// Validate configuration
const config = ConfigurationManager.getInstance();
const validation = config.validateConfig();
if (!validation.valid) {
  console.error('Configuration errors:', validation.errors);
}
```

## Best Practices

### For Providers
1. **Error Handling**: Always wrap API calls in try-catch blocks
2. **File Validation**: Check file size and format before processing
3. **Logging**: Use structured logging with relevant metadata
4. **Health Checks**: Implement health check methods for monitoring
5. **Configuration**: Support flexible configuration options

### For Prompts
1. **Version Control**: Always increment version numbers when changing prompts
2. **Variable Validation**: Ensure all required variables are defined
3. **Testing**: Test prompts with various input types and edge cases
4. **Documentation**: Document the purpose and expected behavior
5. **Domain Expertise**: Tailor prompts to specific professional domains

### For Configuration
1. **Environment Separation**: Use different configs for dev/staging/prod
2. **Validation**: Always validate configuration before use
3. **Defaults**: Provide sensible defaults for all options
4. **Security**: Never commit API keys or sensitive data
5. **Monitoring**: Log configuration changes and feature usage

## Common Patterns

### Async Provider Pattern
```typescript
// For APIs that require polling
async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
  const uploadUrl = await this.uploadFile(file);
  const jobId = await this.createJob(uploadUrl);
  const result = await this.pollForCompletion(jobId);
  return this.formatResult(result);
}
```

### Local Processing Pattern
```typescript
// For local tools and subprocess execution
async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
  const tempFile = await this.writeTempFile(file);
  const result = await this.runLocalTool(tempFile);
  await this.cleanup(tempFile);
  return this.formatResult(result);
}
```

### Configuration Factory Pattern
```typescript
// For dynamic configuration selection
class ConfigFactory {
  static create(useCase: string): EnvironmentConfig {
    return ConfigUtils.getRecommendedConfig(useCase);
  }
}
```

## Troubleshooting

### Common Issues

1. **Provider Not Found**: Ensure provider is registered in ProviderRegistry
2. **API Key Missing**: Check environment variables and configuration
3. **File Format Error**: Verify supported formats match your provider
4. **Timeout Errors**: Adjust timeout values for slow providers
5. **Memory Issues**: Check file size limits for local processing

### Debug Mode

Enable detailed logging for troubleshooting:

```bash
DEBUG=audio-ai:* npm run dev
```

### Health Checks

Test provider availability:

```typescript
const provider = ProviderRegistry.createProvider(config);
const isHealthy = await provider.healthCheck();
```

## Contributing

When contributing new examples:

1. **Follow Patterns**: Use existing examples as templates
2. **Add Tests**: Include unit tests for your examples
3. **Document**: Add comprehensive JSDoc comments
4. **Real-World**: Make examples practical and production-ready
5. **Update Docs**: Update this README with your additions

## Support

- Review the [DEVELOPER_GUIDE.md](../DEVELOPER_GUIDE.md) for detailed implementation guidance
- Check existing provider implementations in `src/services/transcription-providers/`
- Examine the prompt system in `src/config/prompts.ts`
- Look at the configuration system in `src/config/index.ts`