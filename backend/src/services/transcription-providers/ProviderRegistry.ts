import {
  AudioTranscriptionProvider,
  TranscriptionProvider,
  TranscriptionProviderConfig,
  TranscriptionProviderInfo,
} from '../../interfaces/AudioTranscriptionProvider';
import { MockTranscriptionProvider } from './MockTranscriptionProvider';
import { GeminiAudioTranscriptionProvider } from './GeminiAudioTranscriptionProvider';
import { HuggingFaceTranscriptionProvider } from './HuggingFaceTranscriptionProvider';
import { LocalWhisperProvider } from './LocalWhisperProvider';
import { FreeWebSpeechProvider } from './FreeWebSpeechProvider';
import { logger } from '../../utils/logger';

/**
 * Provider constructor signature for factory instantiation
 */
type ProviderConstructor = new (...args: any[]) => AudioTranscriptionProvider;

/**
 * Provider factory function signature for custom instantiation logic
 */
type ProviderFactory = (config: TranscriptionProviderConfig) => AudioTranscriptionProvider;

/**
 * Registry entry containing all information needed to create and describe a provider
 */
interface ProviderRegistryEntry {
  name: TranscriptionProvider;
  description: string;
  constructor?: ProviderConstructor;
  factory?: ProviderFactory;
  requiresApiKey: boolean;
  supportsModels: boolean;
  supportedFormats: string[];
  maxFileSize: number;
  isAvailable: boolean;
  metadata: {
    cost: 'free' | 'paid' | 'freemium';
    installation: 'none' | 'api_key' | 'docker' | 'pip';
    performance: 'low' | 'medium' | 'high';
    accuracy: 'low' | 'medium' | 'high';
  };
}

/**
 * Centralized registry for all audio transcription providers
 * Makes it easy to add new providers without modifying the factory
 */
export class ProviderRegistry {
  private static readonly providers = new Map<TranscriptionProvider, ProviderRegistryEntry>([
    [
      TranscriptionProvider.FREE_WEB_SPEECH,
      {
        name: TranscriptionProvider.FREE_WEB_SPEECH,
        description: 'Free web speech recognition (no API key required)',
        constructor: FreeWebSpeechProvider,
        requiresApiKey: false,
        supportsModels: false,
        supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
        maxFileSize: 50 * 1024 * 1024, // 50MB
        isAvailable: true,
        metadata: {
          cost: 'free',
          installation: 'none',
          performance: 'medium',
          accuracy: 'medium',
        },
      },
    ],
    [
      TranscriptionProvider.LOCAL_WHISPER,
      {
        name: TranscriptionProvider.LOCAL_WHISPER,
        description: 'Local OpenAI Whisper (free, requires: pip install openai-whisper)',
        constructor: LocalWhisperProvider,
        requiresApiKey: false,
        supportsModels: true,
        supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4'],
        maxFileSize: 50 * 1024 * 1024, // 50MB
        isAvailable: true,
        metadata: {
          cost: 'free',
          installation: 'pip',
          performance: 'high',
          accuracy: 'high',
        },
      },
    ],
    [
      TranscriptionProvider.HUGGING_FACE,
      {
        name: TranscriptionProvider.HUGGING_FACE,
        description: 'Hugging Face Whisper API (free: 1,000 requests/month)',
        constructor: HuggingFaceTranscriptionProvider,
        requiresApiKey: true,
        supportsModels: true,
        supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a'],
        maxFileSize: 20 * 1024 * 1024, // 20MB for Hugging Face
        isAvailable: true,
        metadata: {
          cost: 'freemium',
          installation: 'api_key',
          performance: 'high',
          accuracy: 'high',
        },
      },
    ],
    [
      TranscriptionProvider.MOCK,
      {
        name: TranscriptionProvider.MOCK,
        description: 'Mock provider for development and testing (free)',
        constructor: MockTranscriptionProvider,
        requiresApiKey: false,
        supportsModels: false,
        supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
        maxFileSize: 50 * 1024 * 1024, // 50MB
        isAvailable: true,
        metadata: {
          cost: 'free',
          installation: 'none',
          performance: 'low',
          accuracy: 'low',
        },
      },
    ],
    [
      TranscriptionProvider.GEMINI_AUDIO,
      {
        name: TranscriptionProvider.GEMINI_AUDIO,
        description: 'Google Gemini AI audio transcription (when available)',
        constructor: GeminiAudioTranscriptionProvider,
        factory: (config: TranscriptionProviderConfig) => {
          if (!config.apiKey) {
            logger.warn('Gemini API key not provided, falling back to mock provider');
            return new MockTranscriptionProvider();
          }
          return new GeminiAudioTranscriptionProvider(config.apiKey, config.model);
        },
        requiresApiKey: true,
        supportsModels: true,
        supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a'],
        maxFileSize: 20 * 1024 * 1024, // 20MB estimated for Gemini
        isAvailable: false, // Not actually available yet
        metadata: {
          cost: 'paid',
          installation: 'api_key',
          performance: 'high',
          accuracy: 'high',
        },
      },
    ],
  ]);

  /**
   * Create a provider instance using the registry
   */
  static createProvider(config: TranscriptionProviderConfig): AudioTranscriptionProvider {
    logger.debug('Creating transcription provider from registry', { provider: config.provider });

    const entry = this.providers.get(config.provider);
    if (!entry) {
      logger.warn(`Unknown transcription provider: ${config.provider}, using mock provider`);
      return new MockTranscriptionProvider();
    }

    try {
      if (entry.factory) {
        return entry.factory(config);
      } else if (entry.constructor) {
        // Use constructor with appropriate parameters
        if (entry.requiresApiKey && config.apiKey) {
          if (entry.supportsModels && config.model) {
            return new entry.constructor(config.apiKey, config.model);
          }
          return new entry.constructor(config.apiKey);
        }
        return new entry.constructor();
      }

      logger.error(`Provider ${config.provider} has no constructor or factory`);
      return new MockTranscriptionProvider();
    } catch (error) {
      logger.error(`Failed to create provider ${config.provider}`, error);
      return new MockTranscriptionProvider();
    }
  }

  /**
   * Get all available providers
   */
  static getAvailableProviders(): TranscriptionProvider[] {
    return Array.from(this.providers.keys()).filter(
      (provider) => this.providers.get(provider)?.isAvailable
    );
  }

  /**
   * Get all providers (including unavailable ones)
   */
  static getAllProviders(): TranscriptionProvider[] {
    return Array.from(this.providers.keys());
  }

  /**
   * Get detailed information about a provider
   */
  static getProviderInfo(provider: TranscriptionProvider): TranscriptionProviderInfo | null {
    const entry = this.providers.get(provider);
    if (!entry) return null;

    return {
      name: entry.name,
      description: entry.description,
      requiresApiKey: entry.requiresApiKey,
      supportsModels: entry.supportsModels,
      supportedFormats: entry.supportedFormats,
      maxFileSize: entry.maxFileSize,
      isAvailable: entry.isAvailable,
      metadata: entry.metadata,
    };
  }

  /**
   * Get all provider information for documentation/UI
   */
  static getAllProviderInfo(): TranscriptionProviderInfo[] {
    return Array.from(this.providers.values()).map((entry) => ({
      name: entry.name,
      description: entry.description,
      requiresApiKey: entry.requiresApiKey,
      supportsModels: entry.supportsModels,
      supportedFormats: entry.supportedFormats,
      maxFileSize: entry.maxFileSize,
      isAvailable: entry.isAvailable,
      metadata: entry.metadata,
    }));
  }

  /**
   * Register a new provider (for external extensions)
   */
  static registerProvider(entry: ProviderRegistryEntry): void {
    logger.info(`Registering new transcription provider: ${entry.name}`);
    this.providers.set(entry.name, entry);
  }

  /**
   * Get provider description (backwards compatibility)
   */
  static getProviderDescription(provider: TranscriptionProvider): string {
    return this.providers.get(provider)?.description || 'Unknown provider';
  }
}