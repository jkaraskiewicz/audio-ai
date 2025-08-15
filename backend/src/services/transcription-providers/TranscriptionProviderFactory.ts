import {
  AudioTranscriptionProvider,
  TranscriptionProvider,
  TranscriptionProviderConfig,
  TranscriptionProviderInfo,
} from '../../interfaces/AudioTranscriptionProvider';
import { ProviderRegistry } from './ProviderRegistry';

/**
 * Factory for creating audio transcription providers
 * Uses the ProviderRegistry for extensible provider management
 * 
 * @deprecated Use ProviderRegistry.createProvider() directly for new code
 * This class is maintained for backwards compatibility
 */
export class TranscriptionProviderFactory {
  /**
   * Create a provider instance
   */
  static createProvider(config: TranscriptionProviderConfig): AudioTranscriptionProvider {
    return ProviderRegistry.createProvider(config);
  }

  /**
   * Get all available providers
   */
  static getAvailableProviders(): TranscriptionProvider[] {
    return ProviderRegistry.getAvailableProviders();
  }

  /**
   * Get provider description
   */
  static getProviderDescription(provider: TranscriptionProvider): string {
    return ProviderRegistry.getProviderDescription(provider);
  }

  /**
   * Get detailed provider information
   */
  static getProviderInfo(provider: TranscriptionProvider): TranscriptionProviderInfo | null {
    return ProviderRegistry.getProviderInfo(provider);
  }

  /**
   * Get all provider information
   */
  static getAllProviderInfo(): TranscriptionProviderInfo[] {
    return ProviderRegistry.getAllProviderInfo();
  }
}