import { AppConfig, AIServiceConfig, TranscriptionConfig } from '../types';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';
import { FeatureFlagManager } from './FeatureFlagManager';
import { ApplicationConfigBuilder } from './ApplicationConfigBuilder';
import { TranscriptionConfigBuilder } from './TranscriptionConfigBuilder';
import { ConfigurationValidator, ValidationResult } from './ConfigurationValidator';
import { logger } from '../utils/logger';

/**
 * Single Responsibility: Coordinate configuration loading and provide unified access
 * Uncle Bob Approved: Thin coordinator following proper dependency injection
 */
export class ConfigurationService {
  private static instance: ConfigurationService;

  constructor(
    private environmentLoader: EnvironmentConfigLoader,
    private featureFlagManager: FeatureFlagManager,
    private applicationConfigBuilder: ApplicationConfigBuilder,
    private transcriptionConfigBuilder: TranscriptionConfigBuilder,
    private configurationValidator: ConfigurationValidator
  ) {
    this.logInitialization();
  }

  static getInstance(): ConfigurationService {
    if (!ConfigurationService.instance) {
      ConfigurationService.instance = ConfigurationService.createInstance();
    }
    return ConfigurationService.instance;
  }

  private static createInstance(): ConfigurationService {
    const environmentLoader = new EnvironmentConfigLoader();
    const featureFlagManager = new FeatureFlagManager(environmentLoader);
    const applicationConfigBuilder = new ApplicationConfigBuilder();
    const transcriptionConfigBuilder = new TranscriptionConfigBuilder(environmentLoader);
    const configurationValidator = new ConfigurationValidator(
      environmentLoader,
      featureFlagManager
    );

    return new ConfigurationService(
      environmentLoader,
      featureFlagManager,
      applicationConfigBuilder,
      transcriptionConfigBuilder,
      configurationValidator
    );
  }

  getApplicationConfig(): AppConfig {
    return this.applicationConfigBuilder.buildApplicationConfig();
  }

  getTranscriptionConfig(): TranscriptionConfig {
    return this.transcriptionConfigBuilder.buildTranscriptionConfig();
  }

  getAIConfig(): AIServiceConfig {
    const geminiApiKey = this.getApplicationConfig().geminiApiKey;
    const environment = this.environmentLoader.loadEnvironmentConfiguration();

    return {
      apiKey: geminiApiKey,
      model: this.selectModelForEnvironment(environment.nodeEnvironment, environment.useCase),
      maxTokens: this.getMaxTokens(),
      temperature: this.getTemperature(),
    };
  }

  validateConfiguration(): ValidationResult {
    try {
      const appConfig = this.getApplicationConfig();
      const transcriptionConfig = this.getTranscriptionConfig();
      const aiConfig = this.getAIConfig();

      return this.configurationValidator.validateCompleteConfiguration(
        appConfig,
        transcriptionConfig,
        aiConfig
      );
    } catch (error) {
      return {
        isValid: false,
        errors: [
          `Configuration validation failed: ${error instanceof Error ? error.message : 'Unknown error'}`,
        ],
        warnings: [],
      };
    }
  }

  isFeatureEnabled(featureName: string): boolean {
    return this.featureFlagManager.isFeatureEnabled(featureName as any);
  }

  getConfigurationSummary(): Record<string, any> {
    try {
      const appConfig = this.getApplicationConfig();
      const transcriptionConfig = this.getTranscriptionConfig();
      const aiConfig = this.getAIConfig();
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

  private selectModelForEnvironment(nodeEnvironment: string, useCase?: string): string {
    // Use case specific overrides
    if (useCase === 'high-accuracy') {
      return 'gemini-1.5-pro';
    }

    if (useCase === 'high-volume') {
      return 'gemini-1.5-flash';
    }

    // Environment-based model selection
    switch (nodeEnvironment) {
      case 'production':
      case 'staging':
        return 'gemini-1.5-pro';
      case 'development':
      default:
        return 'gemini-2.0-flash-exp';
    }
  }

  private getMaxTokens(): number {
    const envValue = process.env.GEMINI_MAX_TOKENS;
    return envValue ? parseInt(envValue, 10) : 8192;
  }

  private getTemperature(): number {
    const envValue = process.env.GEMINI_TEMPERATURE;
    return envValue ? parseFloat(envValue) : 0.1;
  }

  private logInitialization(): void {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();
    const features = this.featureFlagManager.loadFeatureFlags();

    logger.info('Configuration initialized', {
      environment: environment.nodeEnvironment,
      useCase: environment.useCase,
      features,
    });
  }
}

// Backward compatibility exports
export const getConfig = (): AppConfig => ConfigurationService.getInstance().getApplicationConfig();

export const getTranscriptionConfig = (): TranscriptionConfig =>
  ConfigurationService.getInstance().getTranscriptionConfig();

export const getAIConfig = (): AIServiceConfig => ConfigurationService.getInstance().getAIConfig();
