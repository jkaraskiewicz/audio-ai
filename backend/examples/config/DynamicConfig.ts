/**
 * Dynamic Configuration Management Examples
 * 
 * This file demonstrates advanced configuration patterns
 * for different environments and use cases.
 */

import { TranscriptionProviderConfig, AIServiceConfig } from '../../src/types';
import { TranscriptionProvider } from '../../src/interfaces/AudioTranscriptionProvider';

/**
 * Environment-based configuration
 */
export interface EnvironmentConfig {
  environment: 'development' | 'staging' | 'production';
  transcription: TranscriptionProviderConfig;
  ai: AIServiceConfig;
  features: {
    enableCommentary: boolean;
    enableHealthChecks: boolean;
    enableDetailedLogging: boolean;
    maxFileSize: number;
    rateLimiting: {
      enabled: boolean;
      requestsPerMinute: number;
    };
  };
}

/**
 * Development Configuration
 * Fast models, detailed logging, relaxed limits
 */
export const DEVELOPMENT_CONFIG: EnvironmentConfig = {
  environment: 'development',
  transcription: {
    provider: TranscriptionProvider.MOCK, // Fast mock for development
    apiKey: process.env.MOCK_API_KEY,
    model: 'fast-model',
    language: 'en',
  },
  ai: {
    apiKey: process.env.GEMINI_API_KEY!,
    model: 'gemini-1.5-flash', // Faster model for development
  },
  features: {
    enableCommentary: true,
    enableHealthChecks: true,
    enableDetailedLogging: true,
    maxFileSize: 10 * 1024 * 1024, // 10MB
    rateLimiting: {
      enabled: false, // No rate limiting in dev
      requestsPerMinute: 1000,
    },
  },
};

/**
 * Staging Configuration
 * Production-like but with more logging
 */
export const STAGING_CONFIG: EnvironmentConfig = {
  environment: 'staging',
  transcription: {
    provider: TranscriptionProvider.OPENAI_WHISPER,
    apiKey: process.env.OPENAI_API_KEY,
    model: 'whisper-1',
    language: 'en',
  },
  ai: {
    apiKey: process.env.GEMINI_API_KEY!,
    model: 'gemini-1.5-pro', // More capable model for staging
  },
  features: {
    enableCommentary: true,
    enableHealthChecks: true,
    enableDetailedLogging: true,
    maxFileSize: 25 * 1024 * 1024, // 25MB
    rateLimiting: {
      enabled: true,
      requestsPerMinute: 100,
    },
  },
};

/**
 * Production Configuration
 * Optimized for performance and reliability
 */
export const PRODUCTION_CONFIG: EnvironmentConfig = {
  environment: 'production',
  transcription: {
    provider: TranscriptionProvider.OPENAI_WHISPER,
    apiKey: process.env.OPENAI_API_KEY!,
    model: 'whisper-1',
    language: 'en',
  },
  ai: {
    apiKey: process.env.GEMINI_API_KEY!,
    model: 'gemini-1.5-pro',
  },
  features: {
    enableCommentary: true,
    enableHealthChecks: true,
    enableDetailedLogging: false, // Minimal logging in production
    maxFileSize: 50 * 1024 * 1024, // 50MB
    rateLimiting: {
      enabled: true,
      requestsPerMinute: 60,
    },
  },
};

/**
 * Use case specific configurations
 */

/**
 * High-volume processing configuration
 * Optimized for throughput
 */
export const HIGH_VOLUME_CONFIG: EnvironmentConfig = {
  environment: 'production',
  transcription: {
    provider: TranscriptionProvider.ASSEMBLY_AI, // Hypothetical fast provider
    apiKey: process.env.ASSEMBLY_AI_API_KEY!,
    model: 'nano', // Fastest model
    language: 'en',
  },
  ai: {
    apiKey: process.env.GEMINI_API_KEY!,
    model: 'gemini-1.5-flash', // Faster model
  },
  features: {
    enableCommentary: false, // Disable to increase speed
    enableHealthChecks: false,
    enableDetailedLogging: false,
    maxFileSize: 10 * 1024 * 1024, // Smaller files for speed
    rateLimiting: {
      enabled: true,
      requestsPerMinute: 200,
    },
  },
};

/**
 * High-accuracy configuration
 * Optimized for quality over speed
 */
export const HIGH_ACCURACY_CONFIG: EnvironmentConfig = {
  environment: 'production',
  transcription: {
    provider: TranscriptionProvider.ASSEMBLY_AI,
    apiKey: process.env.ASSEMBLY_AI_API_KEY!,
    model: 'best', // Most accurate model
    language: 'auto',
    speakerLabels: true,
    punctuate: true,
  },
  ai: {
    apiKey: process.env.GEMINI_API_KEY!,
    model: 'gemini-1.5-pro', // Most capable model
  },
  features: {
    enableCommentary: true,
    enableHealthChecks: true,
    enableDetailedLogging: true,
    maxFileSize: 100 * 1024 * 1024, // Larger files allowed
    rateLimiting: {
      enabled: true,
      requestsPerMinute: 30, // Lower rate for quality processing
    },
  },
};

/**
 * Privacy-focused configuration
 * Uses only local/private providers
 */
export const PRIVACY_CONFIG: EnvironmentConfig = {
  environment: 'production',
  transcription: {
    provider: TranscriptionProvider.WHISPER_LOCAL, // Hypothetical local provider
    model: 'large-v3',
    language: 'auto',
    device: 'cpu',
  },
  ai: {
    apiKey: process.env.LOCAL_AI_API_KEY || 'local',
    model: 'local-llm', // Local LLM instance
    endpoint: 'http://localhost:8080/v1', // Local AI endpoint
  },
  features: {
    enableCommentary: true,
    enableHealthChecks: true,
    enableDetailedLogging: false, // Minimal logging for privacy
    maxFileSize: 500 * 1024 * 1024, // Large files OK for local processing
    rateLimiting: {
      enabled: false, // No external API limits
      requestsPerMinute: 1000,
    },
  },
};

/**
 * Configuration Manager
 * Handles dynamic configuration selection and validation
 */
export class ConfigurationManager {
  private static instance: ConfigurationManager;
  private currentConfig: EnvironmentConfig;

  private constructor() {
    this.currentConfig = this.loadConfiguration();
  }

  static getInstance(): ConfigurationManager {
    if (!ConfigurationManager.instance) {
      ConfigurationManager.instance = new ConfigurationManager();
    }
    return ConfigurationManager.instance;
  }

  /**
   * Load configuration based on environment and use case
   */
  private loadConfiguration(): EnvironmentConfig {
    const env = process.env.NODE_ENV || 'development';
    const useCase = process.env.USE_CASE; // e.g., 'high-volume', 'high-accuracy', 'privacy'

    // Load use case specific config first
    if (useCase) {
      switch (useCase) {
        case 'high-volume':
          return HIGH_VOLUME_CONFIG;
        case 'high-accuracy':
          return HIGH_ACCURACY_CONFIG;
        case 'privacy':
          return PRIVACY_CONFIG;
      }
    }

    // Fall back to environment-based config
    switch (env) {
      case 'production':
        return PRODUCTION_CONFIG;
      case 'staging':
        return STAGING_CONFIG;
      case 'development':
      default:
        return DEVELOPMENT_CONFIG;
    }
  }

  /**
   * Get current configuration
   */
  getConfig(): EnvironmentConfig {
    return this.currentConfig;
  }

  /**
   * Update configuration at runtime
   */
  updateConfig(updates: Partial<EnvironmentConfig>): void {
    this.currentConfig = { ...this.currentConfig, ...updates };
  }

  /**
   * Get transcription provider config
   */
  getTranscriptionConfig(): TranscriptionProviderConfig {
    return this.currentConfig.transcription;
  }

  /**
   * Get AI service config
   */
  getAIConfig(): AIServiceConfig {
    return this.currentConfig.ai;
  }

  /**
   * Check if feature is enabled
   */
  isFeatureEnabled(feature: keyof EnvironmentConfig['features']): boolean {
    return Boolean(this.currentConfig.features[feature]);
  }

  /**
   * Get feature configuration
   */
  getFeatureConfig(): EnvironmentConfig['features'] {
    return this.currentConfig.features;
  }

  /**
   * Validate configuration
   */
  validateConfig(): { valid: boolean; errors: string[] } {
    const errors: string[] = [];

    // Check required API keys
    if (this.currentConfig.transcription.apiKey === undefined) {
      errors.push('Transcription API key is required');
    }

    if (this.currentConfig.ai.apiKey === undefined) {
      errors.push('AI service API key is required');
    }

    // Check file size limits
    if (this.currentConfig.features.maxFileSize <= 0) {
      errors.push('Max file size must be positive');
    }

    // Check rate limiting
    if (this.currentConfig.features.rateLimiting.enabled && 
        this.currentConfig.features.rateLimiting.requestsPerMinute <= 0) {
      errors.push('Rate limiting requests per minute must be positive');
    }

    return {
      valid: errors.length === 0,
      errors,
    };
  }

  /**
   * Load configuration from file
   */
  static loadFromFile(filePath: string): EnvironmentConfig {
    try {
      const fs = require('fs');
      const configData = JSON.parse(fs.readFileSync(filePath, 'utf-8'));
      return configData;
    } catch (error) {
      throw new Error(`Failed to load configuration from ${filePath}: ${error}`);
    }
  }

  /**
   * Save configuration to file
   */
  saveToFile(filePath: string): void {
    try {
      const fs = require('fs');
      fs.writeFileSync(filePath, JSON.stringify(this.currentConfig, null, 2));
    } catch (error) {
      throw new Error(`Failed to save configuration to ${filePath}: ${error}`);
    }
  }
}

/**
 * Configuration utilities
 */
export class ConfigUtils {
  /**
   * Create a custom configuration
   */
  static createCustomConfig(baseConfig: EnvironmentConfig, overrides: Partial<EnvironmentConfig>): EnvironmentConfig {
    return {
      ...baseConfig,
      ...overrides,
      features: {
        ...baseConfig.features,
        ...overrides.features,
      },
    };
  }

  /**
   * Compare two configurations
   */
  static compareConfigs(config1: EnvironmentConfig, config2: EnvironmentConfig): {
    differences: string[];
    identical: boolean;
  } {
    const differences: string[] = [];

    // Compare environments
    if (config1.environment !== config2.environment) {
      differences.push(`Environment: ${config1.environment} vs ${config2.environment}`);
    }

    // Compare transcription providers
    if (config1.transcription.provider !== config2.transcription.provider) {
      differences.push(`Transcription provider: ${config1.transcription.provider} vs ${config2.transcription.provider}`);
    }

    // Compare AI models
    if (config1.ai.model !== config2.ai.model) {
      differences.push(`AI model: ${config1.ai.model} vs ${config2.ai.model}`);
    }

    // Compare features
    Object.keys(config1.features).forEach(key => {
      const feature = key as keyof EnvironmentConfig['features'];
      if (JSON.stringify(config1.features[feature]) !== JSON.stringify(config2.features[feature])) {
        differences.push(`Feature ${feature}: ${JSON.stringify(config1.features[feature])} vs ${JSON.stringify(config2.features[feature])}`);
      }
    });

    return {
      differences,
      identical: differences.length === 0,
    };
  }

  /**
   * Get recommended configuration for use case
   */
  static getRecommendedConfig(useCase: 'speed' | 'accuracy' | 'privacy' | 'cost'): EnvironmentConfig {
    switch (useCase) {
      case 'speed':
        return HIGH_VOLUME_CONFIG;
      case 'accuracy':
        return HIGH_ACCURACY_CONFIG;
      case 'privacy':
        return PRIVACY_CONFIG;
      case 'cost':
        return {
          ...DEVELOPMENT_CONFIG,
          transcription: {
            ...DEVELOPMENT_CONFIG.transcription,
            provider: TranscriptionProvider.MOCK, // Free mock provider
          },
          features: {
            ...DEVELOPMENT_CONFIG.features,
            enableCommentary: false, // Reduce AI usage
          },
        };
      default:
        return PRODUCTION_CONFIG;
    }
  }
}

/**
 * Example usage:
 * 
 * // Get current configuration
 * const configManager = ConfigurationManager.getInstance();
 * const config = configManager.getConfig();
 * 
 * // Check if commentary is enabled
 * if (configManager.isFeatureEnabled('enableCommentary')) {
 *   // Process with commentary
 * }
 * 
 * // Update configuration at runtime
 * configManager.updateConfig({
 *   features: {
 *     ...configManager.getFeatureConfig(),
 *     enableDetailedLogging: true,
 *   },
 * });
 * 
 * // Create custom configuration
 * const customConfig = ConfigUtils.createCustomConfig(PRODUCTION_CONFIG, {
 *   transcription: {
 *     ...PRODUCTION_CONFIG.transcription,
 *     model: 'custom-model',
 *   },
 * });
 * 
 * // Load from environment variable or use case
 * process.env.USE_CASE = 'high-accuracy';
 * const manager = ConfigurationManager.getInstance();
 */