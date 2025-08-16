import { AppConfig, AIServiceConfig, TranscriptionConfig } from '../types';
import { TranscriptionProvider } from '../interfaces/AudioTranscriptionProvider';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';
import { FeatureFlagManager } from './FeatureFlagManager';

/**
 * Single Responsibility: Validate configuration objects and provide feedback
 * Uncle Bob Approved: Focused validation logic with clear error reporting
 */
export interface ValidationResult {
  readonly isValid: boolean;
  readonly errors: ReadonlyArray<string>;
  readonly warnings: ReadonlyArray<string>;
}

export class ConfigurationValidator {
  private static readonly MIN_AI_TOKENS = 100;
  private static readonly MAX_AI_TOKENS = 32768;
  private static readonly MIN_AI_TEMPERATURE = 0;
  private static readonly MAX_AI_TEMPERATURE = 2;

  constructor(
    private environmentLoader: EnvironmentConfigLoader,
    private featureFlagManager: FeatureFlagManager
  ) {}

  validateCompleteConfiguration(
    appConfig: AppConfig,
    transcriptionConfig: TranscriptionConfig,
    aiConfig: AIServiceConfig
  ): ValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];

    this.validateApplicationConfig(appConfig, errors);
    this.validateTranscriptionConfig(transcriptionConfig, errors);
    this.validateAIConfig(aiConfig, warnings);
    this.validateEnvironmentSpecificSettings(warnings);

    return {
      isValid: errors.length === 0,
      errors: Object.freeze([...errors]),
      warnings: Object.freeze([...warnings]),
    };
  }

  private validateApplicationConfig(appConfig: AppConfig, errors: string[]): void {
    if (!appConfig.geminiApiKey) {
      errors.push('Gemini API key is required');
    }

    if (!appConfig.baseDirectory) {
      errors.push('Base directory must be specified');
    }
  }

  private validateTranscriptionConfig(
    transcriptionConfig: TranscriptionConfig,
    errors: string[]
  ): void {
    if (this.requiresApiKeyButMissingIt(transcriptionConfig)) {
      errors.push(`API key required for transcription provider: ${transcriptionConfig.provider}`);
    }
  }

  private requiresApiKeyButMissingIt(config: TranscriptionConfig): boolean {
    const providersRequiringApiKey = [
      TranscriptionProvider.HUGGING_FACE,
      TranscriptionProvider.GEMINI_AUDIO,
    ];

    const providerRequiresApiKey = providersRequiringApiKey.includes(config.provider);
    return providerRequiresApiKey && !config.apiKey;
  }

  private validateAIConfig(aiConfig: AIServiceConfig, warnings: string[]): void {
    if (aiConfig.maxTokens && this.isInvalidTokenCount(aiConfig.maxTokens)) {
      warnings.push(
        `AI max tokens should be between ${ConfigurationValidator.MIN_AI_TOKENS} and ${ConfigurationValidator.MAX_AI_TOKENS}`
      );
    }

    if (aiConfig.temperature && this.isInvalidTemperature(aiConfig.temperature)) {
      warnings.push(
        `AI temperature should be between ${ConfigurationValidator.MIN_AI_TEMPERATURE} and ${ConfigurationValidator.MAX_AI_TEMPERATURE}`
      );
    }
  }

  private isInvalidTokenCount(tokens: number): boolean {
    return (
      tokens < ConfigurationValidator.MIN_AI_TOKENS || tokens > ConfigurationValidator.MAX_AI_TOKENS
    );
  }

  private isInvalidTemperature(temperature: number): boolean {
    return (
      temperature < ConfigurationValidator.MIN_AI_TEMPERATURE ||
      temperature > ConfigurationValidator.MAX_AI_TEMPERATURE
    );
  }

  private validateEnvironmentSpecificSettings(warnings: string[]): void {
    if (this.environmentLoader.isProductionEnvironment()) {
      this.validateProductionSettings(warnings);
    }
  }

  private validateProductionSettings(warnings: string[]): void {
    if (this.featureFlagManager.isFeatureEnabled('enableDetailedLogging')) {
      warnings.push(
        'Detailed logging is enabled in production - consider disabling for performance'
      );
    }

    if (!this.featureFlagManager.isFeatureEnabled('enableRateLimiting')) {
      warnings.push('Rate limiting is disabled in production - consider enabling for security');
    }
  }
}
