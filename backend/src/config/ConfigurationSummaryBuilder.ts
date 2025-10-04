import { AppConfig, TranscriptionConfig, AIServiceConfig } from '../types';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';
import { FeatureFlagManager } from './FeatureFlagManager';

/**
 * Builds configuration summary for logging and debugging
 * Follows Single Responsibility Principle
 */
export class ConfigurationSummaryBuilder {
  constructor(
    private environmentLoader: EnvironmentConfigLoader,
    private featureFlagManager: FeatureFlagManager
  ) {}

  buildSummary(
    appConfig: AppConfig,
    transcriptionConfig: TranscriptionConfig,
    aiConfig: AIServiceConfig
  ): Record<string, any> {
    try {
      const environment = this.environmentLoader.loadEnvironmentConfiguration();
      const features = this.featureFlagManager.loadFeatureFlags();

      return {
        environment: environment.nodeEnvironment,
        useCase: environment.useCase,
        port: appConfig.port,
        baseDirectory: appConfig.baseDirectory,
        transcription: {
          provider: transcriptionConfig.provider,
          hasApiKey: !!transcriptionConfig.apiKey,
          model: transcriptionConfig.model,
          language: transcriptionConfig.language,
          maxFileSize: transcriptionConfig.maxFileSize,
        },
        ai: {
          model: aiConfig.model,
          maxTokens: aiConfig.maxTokens,
          temperature: aiConfig.temperature,
        },
        features,
      };
    } catch (error) {
      return {
        error: error instanceof Error ? error.message : 'Configuration error',
      };
    }
  }
}
