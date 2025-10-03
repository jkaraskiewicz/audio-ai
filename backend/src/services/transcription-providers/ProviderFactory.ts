import { AudioTranscriptionProvider, TranscriptionProviderConfig } from '../../interfaces/AudioTranscriptionProvider';
import { ProviderRegistryEntry } from './ProviderMetadata';
import { MockTranscriptionProvider } from './MockTranscriptionProvider';
import { logger } from '../../utils/logger';

/**
 * Factory for creating provider instances
 */
export class ProviderFactory {
  /**
   * Create a provider instance from registry entry
   */
  static create(
    entry: ProviderRegistryEntry,
    config: TranscriptionProviderConfig
  ): AudioTranscriptionProvider {
    try {
      if (entry.factory) {
        return entry.factory(config);
      }

      if (entry.constructor) {
        return this.createFromConstructor(entry, config);
      }

      logger.error(`Provider ${config.provider} has no constructor or factory`);
      return new MockTranscriptionProvider();
    } catch (error) {
      logger.error(`Failed to create provider ${config.provider}`, error);
      return new MockTranscriptionProvider();
    }
  }

  /**
   * Create provider using constructor with appropriate parameters
   */
  private static createFromConstructor(
    entry: ProviderRegistryEntry,
    config: TranscriptionProviderConfig
  ): AudioTranscriptionProvider {
    if (!entry.constructor) {
      throw new Error('No constructor available');
    }

    if (entry.requiresApiKey && config.apiKey) {
      if (entry.supportsModels && config.model) {
        return new entry.constructor(config.apiKey, config.model);
      }
      return new entry.constructor(config.apiKey);
    }

    return new entry.constructor();
  }
}
