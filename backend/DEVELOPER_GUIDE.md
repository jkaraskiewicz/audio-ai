# Developer Guide: Extending Audio-AI

This guide helps engineers extend the Audio-AI system by adding new audio transcription providers and modifying AI prompts.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Adding New Audio Transcription Providers](#adding-new-audio-transcription-providers)
3. [Modifying AI Prompts](#modifying-ai-prompts)
4. [Configuration Management](#configuration-management)
5. [Testing Extensions](#testing-extensions)
6. [Best Practices](#best-practices)
7. [Examples](#examples)

## Architecture Overview

The Audio-AI system is built with extensibility in mind, using several key design patterns:

### Key Components

```
src/
├── interfaces/AudioTranscriptionProvider.ts    # Provider interface
├── services/transcription-providers/
│   ├── ProviderRegistry.ts                     # Extensible provider registry
│   ├── TranscriptionProviderFactory.ts        # Factory (backwards compatibility)
│   └── [Provider implementations]
├── config/prompts.ts                           # Centralized prompt management
└── services/AIService.ts                       # AI processing service
```

### Design Patterns Used

- **Registry Pattern**: `ProviderRegistry` for managing audio providers
- **Factory Pattern**: `TranscriptionProviderFactory` for creating providers
- **Template Pattern**: `PromptEngine` for managing AI prompts
- **Interface Segregation**: Clean interfaces for extensions

## Adding New Audio Transcription Providers

### Step 1: Understand the Interface

All audio providers must implement the `AudioTranscriptionProvider` interface:

```typescript
export interface AudioTranscriptionProvider {
  transcribe(file: Express.Multer.File): Promise<FileProcessingResult>;
  getSupportedFormats(): string[];
  getMaxFileSize(): number;
  getProviderName(): string;
  isReady(): boolean;
}
```

### Step 2: Create Your Provider

Create a new file in `src/services/transcription-providers/`:

```typescript
// src/services/transcription-providers/YourNewProvider.ts
import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult } from '../../types';
import { logger } from '../../utils/logger';

export class YourNewProvider implements AudioTranscriptionProvider {
  private apiKey?: string;
  private isConfigured: boolean;

  constructor(apiKey?: string) {
    this.apiKey = apiKey;
    this.isConfigured = !!apiKey;
    
    if (this.isConfigured) {
      logger.info('YourNewProvider initialized successfully');
    } else {
      logger.warn('YourNewProvider not configured - missing API key');
    }
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    if (!this.isReady()) {
      throw new Error('YourNewProvider is not properly configured');
    }

    try {
      logger.debug('Starting transcription with YourNewProvider', {
        filename: file.originalname,
        size: file.size,
      });

      // Your transcription logic here
      // Example: Call your API, process the file, etc.
      const transcriptionText = await this.performTranscription(file);

      return {
        extractedText: transcriptionText,
        processingMethod: 'your_new_provider_transcription',
        metadata: {
          provider: this.getProviderName(),
          duration: 'unknown', // Add if available
          language: 'auto-detected', // Add if available
        },
      };
    } catch (error) {
      logger.error('YourNewProvider transcription failed', error);
      throw new Error(`Transcription failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  }

  private async performTranscription(file: Express.Multer.File): Promise<string> {
    // Implement your actual transcription logic here
    // This is where you'd call your API, use a library, etc.
    
    // Example implementation:
    // const formData = new FormData();
    // formData.append('audio', file.buffer, file.originalname);
    // const response = await fetch('your-api-endpoint', {
    //   method: 'POST',
    //   headers: { 'Authorization': `Bearer ${this.apiKey}` },
    //   body: formData
    // });
    // const result = await response.json();
    // return result.text;
    
    throw new Error('performTranscription not implemented');
  }

  getSupportedFormats(): string[] {
    return ['.mp3', '.wav', '.ogg', '.flac', '.m4a'];
  }

  getMaxFileSize(): number {
    return 25 * 1024 * 1024; // 25MB
  }

  getProviderName(): string {
    return 'your_new_provider';
  }

  isReady(): boolean {
    return this.isConfigured;
  }
}
```

### Step 3: Add to Provider Registry

Update the enum in `src/interfaces/AudioTranscriptionProvider.ts`:

```typescript
export enum TranscriptionProvider {
  // ... existing providers
  YOUR_NEW_PROVIDER = 'your_new_provider',
}
```

Register your provider in `src/services/transcription-providers/ProviderRegistry.ts`:

```typescript
// Add this to the providers Map in ProviderRegistry.ts
[
  TranscriptionProvider.YOUR_NEW_PROVIDER,
  {
    name: TranscriptionProvider.YOUR_NEW_PROVIDER,
    description: 'Your New Provider description (pricing info, requirements)',
    constructor: YourNewProvider,
    requiresApiKey: true,
    supportsModels: false,
    supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a'],
    maxFileSize: 25 * 1024 * 1024, // 25MB
    isAvailable: true,
    metadata: {
      cost: 'paid', // or 'free' or 'freemium'
      installation: 'api_key', // or 'none', 'docker', 'pip'
      performance: 'high', // or 'low', 'medium'
      accuracy: 'high', // or 'low', 'medium'
    },
  },
],
```

### Step 4: Update Configuration

Add your provider to the config in `src/config/index.ts`:

```typescript
// Add to the getTranscriptionConfig function
case 'your_new_provider':
  return {
    provider: TranscriptionProvider.YOUR_NEW_PROVIDER,
    apiKey: process.env.YOUR_NEW_PROVIDER_API_KEY,
  };
```

### Step 5: Environment Variables

Update your `.env` file:

```bash
# Your New Provider Configuration
YOUR_NEW_PROVIDER_API_KEY=your_api_key_here
```

## Modifying AI Prompts

The prompt system is centralized in `src/config/prompts.ts` for easy modification.

### Understanding the Prompt System

The system has two levels of prompts:

1. **Base Prompt**: Main prompt for processing transcripts
2. **Commentary Prompts**: Specialized prompts for different content types

### Modifying the Base Prompt

The base prompt controls the overall structure and decision-making. Edit `PROMPTS.base.template`:

```typescript
// In src/config/prompts.ts
export const PROMPTS: ContentTypePrompts = {
  base: {
    name: 'base_transcript_processor',
    description: 'Main prompt for processing voice transcripts into structured markdown',
    version: '2.1.0', // Increment version when making changes
    template: `Your modified base prompt here...
    
    Content to process:
    ---
    {{transcript}}
    ---
    
    // Your instructions...`,
    variables: ['transcript'],
  },
  // ... rest of prompts
};
```

### Adding New Commentary Types

To add a new commentary type (e.g., for financial content):

```typescript
// In src/config/prompts.ts, add to PROMPTS.commentary:
financial: {
  name: 'financial_commentary',
  description: 'Provides financial analysis and advice',
  version: '1.0.0',
  template: `The user has shared financial content that needs expert analysis:

Financial content: {{financial_text}}

Provide comprehensive financial commentary covering:

1. **Financial Analysis**
   - Key financial metrics and indicators
   - Risk assessment and factors
   - Market context and trends

2. **Recommendations**
   - Actionable financial advice
   - Risk mitigation strategies
   - Optimization opportunities

3. **Next Steps**
   - Immediate actions to take
   - Long-term planning considerations
   - Resources for further learning

Be objective and provide educational information. Do not provide specific investment advice.`,
  variables: ['financial_text'],
},
```

### Using the Prompt Engine

The `PromptEngine` class provides utilities for working with prompts:

```typescript
import { PromptEngine, PROMPTS } from '../config/prompts';

// Get a processed prompt
const prompt = PromptEngine.getPrompt(PROMPTS.base, { 
  transcript: 'Your transcript here' 
});

// Get commentary prompt
const commentaryPrompt = PromptEngine.getCommentaryPrompt('financial', {
  financial_text: 'Financial content here'
});

// Validate prompts
const validation = PromptUtils.validatePrompt(prompt);
if (!validation.valid) {
  console.warn('Prompt issues:', validation.issues);
}
```

### Prompt Best Practices

1. **Use Clear Instructions**: Be specific about what you want the AI to do
2. **Include Examples**: Show the AI the desired format
3. **Version Your Changes**: Always increment version numbers
4. **Test Thoroughly**: Test prompts with various input types
5. **Document Variables**: List all required variables in the config

## Configuration Management

### Environment-Based Configuration

The system supports different configurations per environment:

```typescript
// src/config/index.ts
export const getAIConfig = (): AIServiceConfig => {
  const environment = process.env.NODE_ENV || 'development';
  
  switch (environment) {
    case 'production':
      return {
        apiKey: process.env.GEMINI_API_KEY!,
        model: 'gemini-1.5-pro', // More capable model for production
      };
    case 'development':
      return {
        apiKey: process.env.GEMINI_API_KEY!,
        model: 'gemini-1.5-flash', // Faster model for development
      };
    default:
      return {
        apiKey: process.env.GEMINI_API_KEY!,
        model: 'gemini-1.5-flash',
      };
  }
};
```

### Dynamic Configuration

You can create configuration files for easy runtime changes:

```json
// config/prompts.json (optional override file)
{
  "base": {
    "template": "Your custom base prompt...",
    "version": "2.1.0-custom"
  }
}
```

## Testing Extensions

### Testing New Providers

Create tests for your provider:

```typescript
// tests/services/transcription-providers/YourNewProvider.test.ts
import { YourNewProvider } from '../../../src/services/transcription-providers/YourNewProvider';

describe('YourNewProvider', () => {
  let provider: YourNewProvider;

  beforeEach(() => {
    provider = new YourNewProvider('test-api-key');
  });

  it('should be properly configured with API key', () => {
    expect(provider.isReady()).toBe(true);
    expect(provider.getProviderName()).toBe('your_new_provider');
  });

  it('should support expected audio formats', () => {
    const formats = provider.getSupportedFormats();
    expect(formats).toContain('.mp3');
    expect(formats).toContain('.wav');
  });

  it('should have reasonable file size limit', () => {
    const maxSize = provider.getMaxFileSize();
    expect(maxSize).toBeGreaterThan(0);
    expect(maxSize).toBeLessThanOrEqual(50 * 1024 * 1024); // 50MB
  });

  // Add integration tests for actual transcription
});
```

### Testing Prompt Changes

Create tests for modified prompts:

```typescript
// tests/config/prompts.test.ts
import { PromptEngine, PROMPTS } from '../../src/config/prompts';

describe('Prompt System', () => {
  it('should generate valid base prompts', () => {
    const prompt = PromptEngine.getBasePrompt('test transcript');
    expect(prompt).toContain('test transcript');
    expect(prompt).toContain('commentary_needed:');
  });

  it('should validate prompt templates', () => {
    const validation = PromptUtils.validatePrompt(PROMPTS.base.template);
    expect(validation.valid).toBe(true);
  });
});
```

### Integration Testing

Test the complete flow:

```bash
# Test with different providers
curl -X POST http://localhost:3000/process \
  -H "Content-Type: application/json" \
  -d '{"transcript": "Test content for your new provider"}'
```

## Best Practices

### Code Organization

1. **One Provider Per File**: Keep each provider in its own file
2. **Descriptive Names**: Use clear, descriptive class and method names
3. **Consistent Error Handling**: Follow the established error handling patterns
4. **Logging**: Add appropriate logging for debugging and monitoring

### Provider Development

1. **Graceful Degradation**: Handle API failures gracefully
2. **Timeout Handling**: Implement reasonable timeouts for external calls
3. **File Format Validation**: Validate audio files before processing
4. **Rate Limiting**: Respect API rate limits
5. **Security**: Never log API keys or sensitive data

### Prompt Engineering

1. **Iterative Development**: Test and refine prompts incrementally
2. **Clear Instructions**: Use specific, actionable language
3. **Format Consistency**: Maintain consistent output formats
4. **Edge Case Handling**: Consider how prompts handle unusual inputs
5. **Performance**: Optimize prompt length vs. effectiveness

## Examples

### Complete Provider Example

See `src/services/transcription-providers/FreeWebSpeechProvider.ts` for a complete, working example of a provider implementation.

### Prompt Modification Example

To modify the base prompt to include sentiment analysis:

```typescript
// In src/config/prompts.ts
template: `You are an expert productivity assistant. Process this voice transcript and create a structured markdown document.

// ... existing content ...

## Sentiment Analysis
[Analyze the emotional tone: positive, negative, neutral, mixed]

// ... rest of template ...`
```

### Custom Configuration Example

```typescript
// Custom provider configuration
const customConfig: TranscriptionProviderConfig = {
  provider: TranscriptionProvider.YOUR_NEW_PROVIDER,
  apiKey: process.env.CUSTOM_API_KEY,
  model: 'advanced-model',
  language: 'en-US',
  customParam: 'custom-value',
};

const provider = ProviderRegistry.createProvider(customConfig);
```

## Getting Help

1. **Check Existing Providers**: Look at implemented providers for patterns
2. **Review Tests**: Examine test files for usage examples  
3. **Check Logs**: Enable debug logging to troubleshoot issues
4. **Documentation**: Refer to provider API documentation
5. **Community**: Check issues and discussions in the project repository

## Contributing Back

When you create useful extensions:

1. **Write Tests**: Include comprehensive tests
2. **Add Documentation**: Update this guide and add JSDoc comments
3. **Follow Conventions**: Use established naming and structure patterns
4. **Consider Others**: Make extensions configurable and reusable

Your contributions help make the Audio-AI system more powerful for everyone!