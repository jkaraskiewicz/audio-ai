import { AppConfig, AIServiceConfig, TranscriptionConfig } from '../types';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';
import { FeatureFlagManager, FeatureFlags } from './FeatureFlagManager';
import { ApplicationConfigBuilder } from './ApplicationConfigBuilder';
import { TranscriptionConfigBuilder } from './TranscriptionConfigBuilder';
import { AIConfigBuilder } from './AIConfigBuilder';
import {
  ConfigurationSummaryBuilder,
  ConfigurationSummary,
  ConfigurationSummaryError,
} from './ConfigurationSummaryBuilder';
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
    private aiConfigBuilder: AIConfigBuilder,
    private summaryBuilder: ConfigurationSummaryBuilder,
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
    const aiConfigBuilder = new AIConfigBuilder(environmentLoader);
    const summaryBuilder = new ConfigurationSummaryBuilder(environmentLoader, featureFlagManager);
    const configurationValidator = new ConfigurationValidator(
      environmentLoader,
      featureFlagManager
    );

    return new ConfigurationService(
      environmentLoader,
      featureFlagManager,
      applicationConfigBuilder,
      transcriptionConfigBuilder,
      aiConfigBuilder,
      summaryBuilder,
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
    return this.aiConfigBuilder.buildAIConfig(geminiApiKey);
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

  isFeatureEnabled(featureName: keyof FeatureFlags): boolean {
    return this.featureFlagManager.isFeatureEnabled(featureName);
  }

  getConfigurationSummary(): ConfigurationSummary | ConfigurationSummaryError {
    const appConfig = this.getApplicationConfig();
    const transcriptionConfig = this.getTranscriptionConfig();
    const aiConfig = this.getAIConfig();

    return this.summaryBuilder.buildSummary(appConfig, transcriptionConfig, aiConfig);
  }

  private logInitialization(): void {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();
    const features = this.featureFlagManager.loadFeatureFlags();

    logger.info('Configuration initialized', {
      environment: environment.nodeEnvironment,
      features,
    });
  }
}

// Backward compatibility exports
export const getConfig = (): AppConfig => ConfigurationService.getInstance().getApplicationConfig();

export const getTranscriptionConfig = (): TranscriptionConfig =>
  ConfigurationService.getInstance().getTranscriptionConfig();

export const getAIConfig = (): AIServiceConfig => ConfigurationService.getInstance().getAIConfig();
