import { config } from 'dotenv';
import { AppConfig, TranscriptionConfig, AIServiceConfig } from '../types';
import { TranscriptionProvider } from '../interfaces/AudioTranscriptionProvider';
import { logger } from '../utils/logger';

config();

/**
 * Environment configuration interface
 */
interface EnvironmentConfig {
  NODE_ENV: string;
  USE_CASE?: string;
  DEBUG?: string;
}

/**
 * Feature flags configuration
 */
interface FeatureFlags {
  enableCommentary: boolean;
  enableHealthChecks: boolean;
  enableDetailedLogging: boolean;
  enableRateLimiting: boolean;
  enableMetrics: boolean;
}

/**
 * Enhanced configuration manager with developer-friendly features
 */
export class ConfigManager {
  private static instance: ConfigManager;
  private environment: EnvironmentConfig;
  private featureFlags: FeatureFlags;

  private constructor() {
    this.environment = this.loadEnvironmentConfig();
    this.featureFlags = this.loadFeatureFlags();

    logger.info('Configuration initialized', {
      environment: this.environment.NODE_ENV,
      useCase: this.environment.USE_CASE,
      features: this.featureFlags,
    });
  }

  static getInstance(): ConfigManager {
    if (!ConfigManager.instance) {
      ConfigManager.instance = new ConfigManager();
    }
    return ConfigManager.instance;
  }

  private loadEnvironmentConfig(): EnvironmentConfig {
    return {
      NODE_ENV: process.env.NODE_ENV || 'development',
      USE_CASE: process.env.USE_CASE,
      DEBUG: process.env.DEBUG,
    };
  }

  private loadFeatureFlags(): FeatureFlags {
    return {
      enableCommentary: process.env.ENABLE_COMMENTARY !== 'false',
      enableHealthChecks: process.env.ENABLE_HEALTH_CHECKS !== 'false',
      enableDetailedLogging:
        process.env.ENABLE_DETAILED_LOGGING === 'true' ||
        this.environment.NODE_ENV === 'development',
      enableRateLimiting:
        process.env.ENABLE_RATE_LIMITING === 'true' || this.environment.NODE_ENV === 'production',
      enableMetrics: process.env.ENABLE_METRICS === 'true',
    };
  }

  /**
   * Get application configuration
   */
  getAppConfig(): AppConfig {
    const geminiApiKey = process.env.GEMINI_API_KEY;

    if (!geminiApiKey || geminiApiKey === 'your_api_key_here') {
      throw new Error('GEMINI_API_KEY not configured. Please add your API key to .env file');
    }

    const port = parseInt(process.env.PORT || '3000', 10);
    if (isNaN(port) || port < 1 || port > 65535) {
      throw new Error(
        `Invalid PORT value: ${process.env.PORT}. Must be a number between 1 and 65535.`
      );
    }

    return {
      port,
      geminiApiKey,
      baseDirectory: process.env.BASE_DIRECTORY || 'saved_ideas',
    };
  }

  /**
   * Get AI service configuration with environment-based defaults
   */
  getAIConfig(): AIServiceConfig {
    const geminiApiKey = process.env.GEMINI_API_KEY;

    if (!geminiApiKey || geminiApiKey === 'your_api_key_here') {
      throw new Error('GEMINI_API_KEY not configured');
    }

    // Environment-based model selection
    let defaultModel: string;
    switch (this.environment.NODE_ENV) {
      case 'production':
        defaultModel = 'gemini-1.5-pro'; // Most capable for production
        break;
      case 'staging':
        defaultModel = 'gemini-1.5-pro'; // Production-like
        break;
      case 'development':
      default:
        defaultModel = 'gemini-2.0-flash-exp'; // Fast for development
        break;
    }

    // Use case specific overrides
    if (this.environment.USE_CASE === 'high-accuracy') {
      defaultModel = 'gemini-1.5-pro';
    } else if (this.environment.USE_CASE === 'high-volume') {
      defaultModel = 'gemini-1.5-flash';
    }

    return {
      apiKey: geminiApiKey,
      model: process.env.GEMINI_MODEL || defaultModel,
      maxTokens: parseInt(process.env.GEMINI_MAX_TOKENS || '8192', 10),
      temperature: parseFloat(process.env.GEMINI_TEMPERATURE || '0.1'),
    };
  }

  /**
   * Get transcription configuration with intelligent defaults
   */
  getTranscriptionConfig(): TranscriptionConfig {
    const provider = this.getDefaultTranscriptionProvider();

    return {
      provider,
      apiKey: this.getProviderApiKey(provider),
      model: this.getProviderModel(provider),
      language: process.env.TRANSCRIPTION_LANGUAGE || 'en',
      maxFileSize: parseInt(
        process.env.MAX_FILE_SIZE || this.getDefaultMaxFileSize(provider).toString(),
        10
      ),
    };
  }

  private getDefaultTranscriptionProvider(): TranscriptionProvider {
    // Environment-based provider selection
    if (process.env.TRANSCRIPTION_PROVIDER) {
      return process.env.TRANSCRIPTION_PROVIDER as TranscriptionProvider;
    }

    // Use case specific defaults
    switch (this.environment.USE_CASE) {
      case 'privacy':
        return TranscriptionProvider.LOCAL_WHISPER || TranscriptionProvider.FREE_WEB_SPEECH;
      case 'high-accuracy':
        return TranscriptionProvider.HUGGING_FACE || TranscriptionProvider.LOCAL_WHISPER;
      case 'high-volume':
        return TranscriptionProvider.FREE_WEB_SPEECH;
      default:
        return TranscriptionProvider.FREE_WEB_SPEECH;
    }
  }

  private getProviderApiKey(provider: TranscriptionProvider): string | undefined {
    switch (provider) {
      case TranscriptionProvider.HUGGING_FACE:
        return process.env.HUGGING_FACE_API_KEY;
      case TranscriptionProvider.GEMINI_AUDIO:
        return process.env.GEMINI_API_KEY;
      default:
        return process.env.TRANSCRIPTION_API_KEY;
    }
  }

  private getProviderModel(provider: TranscriptionProvider): string | undefined {
    // Return environment-specific or use-case specific models
    const envModel = process.env.TRANSCRIPTION_MODEL;
    if (envModel) return envModel;

    // Provider-specific defaults based on use case
    switch (provider) {
      case TranscriptionProvider.HUGGING_FACE:
        return this.environment.USE_CASE === 'high-accuracy'
          ? 'openai/whisper-large-v3'
          : 'openai/whisper-base';
      case TranscriptionProvider.LOCAL_WHISPER:
        return this.environment.USE_CASE === 'high-accuracy' ? 'large-v3' : 'base';
      default:
        return undefined;
    }
  }

  private getDefaultMaxFileSize(provider: TranscriptionProvider): number {
    switch (provider) {
      case TranscriptionProvider.HUGGING_FACE:
        return 100 * 1024 * 1024; // 100MB
      case TranscriptionProvider.LOCAL_WHISPER:
        return 500 * 1024 * 1024; // 500MB
      case TranscriptionProvider.GEMINI_AUDIO:
        return 50 * 1024 * 1024; // 50MB
      default:
        return 25 * 1024 * 1024; // 25MB
    }
  }

  /**
   * Get feature flags
   */
  getFeatureFlags(): FeatureFlags {
    return { ...this.featureFlags };
  }

  /**
   * Check if a feature is enabled
   */
  isFeatureEnabled(feature: keyof FeatureFlags): boolean {
    return this.featureFlags[feature];
  }

  /**
   * Get environment information
   */
  getEnvironment(): EnvironmentConfig {
    return { ...this.environment };
  }

  /**
   * Update feature flag at runtime (for testing/debugging)
   */
  updateFeatureFlag(feature: keyof FeatureFlags, enabled: boolean): void {
    this.featureFlags[feature] = enabled;
    logger.info(`Feature flag updated: ${feature} = ${enabled}`);
  }

  /**
   * Validate configuration
   */
  validateConfig(): { valid: boolean; errors: string[]; warnings: string[] } {
    const errors: string[] = [];
    const warnings: string[] = [];

    try {
      // Validate app config
      const appConfig = this.getAppConfig();
      if (!appConfig.geminiApiKey) {
        errors.push('Gemini API key is required');
      }

      // Validate transcription config
      const transcriptionConfig = this.getTranscriptionConfig();
      if (
        transcriptionConfig.provider !== TranscriptionProvider.FREE_WEB_SPEECH &&
        transcriptionConfig.provider !== TranscriptionProvider.MOCK &&
        transcriptionConfig.provider !== TranscriptionProvider.WEB_SPEECH_API &&
        !transcriptionConfig.apiKey
      ) {
        errors.push(`API key required for transcription provider: ${transcriptionConfig.provider}`);
      }

      // Validate AI config
      const aiConfig = this.getAIConfig();
      if (aiConfig.maxTokens && (aiConfig.maxTokens < 100 || aiConfig.maxTokens > 32768)) {
        warnings.push('AI max tokens should be between 100 and 32768');
      }

      if (aiConfig.temperature && (aiConfig.temperature < 0 || aiConfig.temperature > 2)) {
        warnings.push('AI temperature should be between 0 and 2');
      }

      // Environment-specific validations
      if (this.environment.NODE_ENV === 'production') {
        if (this.featureFlags.enableDetailedLogging) {
          warnings.push(
            'Detailed logging is enabled in production - consider disabling for performance'
          );
        }

        if (!this.featureFlags.enableRateLimiting) {
          warnings.push('Rate limiting is disabled in production - consider enabling for security');
        }
      }
    } catch (error) {
      errors.push(
        `Configuration validation failed: ${error instanceof Error ? error.message : 'Unknown error'}`
      );
    }

    return {
      valid: errors.length === 0,
      errors,
      warnings,
    };
  }

  /**
   * Get configuration summary for debugging
   */
  getConfigSummary(): Record<string, any> {
    try {
      const appConfig = this.getAppConfig();
      const transcriptionConfig = this.getTranscriptionConfig();
      const aiConfig = this.getAIConfig();

      return {
        environment: this.environment.NODE_ENV,
        useCase: this.environment.USE_CASE,
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
        features: this.featureFlags,
      };
    } catch (error) {
      return {
        error: error instanceof Error ? error.message : 'Configuration error',
      };
    }
  }
}

// Backward compatibility exports
export const getConfig = (): AppConfig => ConfigManager.getInstance().getAppConfig();
export const getTranscriptionConfig = (): TranscriptionConfig =>
  ConfigManager.getInstance().getTranscriptionConfig();
export const getAIConfig = (): AIServiceConfig => ConfigManager.getInstance().getAIConfig();

// Enhanced exports (ConfigManager already exported above)

export const AI_CONFIG = {
  model: 'gemini-2.0-flash-exp',
} as const;

export const FILE_CONFIG = {
  specialCategories: {
    daily: 'daily/tasks',
  },
} as const;
