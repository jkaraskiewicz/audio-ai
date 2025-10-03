import {
  AudioTranscriptionProvider,
  TranscriptionProvider,
  TranscriptionProviderConfig,
  TranscriptionProviderInfo,
} from '../../interfaces/AudioTranscriptionProvider';
import { PROVIDER_DEFINITIONS } from './ProviderDefinitions';
import { ProviderFactory } from './ProviderFactory';
import { MockTranscriptionProvider } from './MockTranscriptionProvider';
import { ProviderRegistryEntry } from './ProviderMetadata';
import { logger } from '../../utils/logger';

/**
 * Centralized registry for all audio transcription providers
 * Simplified registry focusing on provider lookup and information retrieval
 */
export class ProviderRegistry {
  private static providers = PROVIDER_DEFINITIONS;

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

    return ProviderFactory.create(entry, config);
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