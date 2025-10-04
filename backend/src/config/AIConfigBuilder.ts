import { AIServiceConfig } from '../types';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';

/**
 * Builds AI service configuration
 * Follows Single Responsibility Principle
 */
export class AIConfigBuilder {
  constructor(private environmentLoader: EnvironmentConfigLoader) {}

  buildAIConfig(geminiApiKey: string): AIServiceConfig {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();

    return {
      apiKey: geminiApiKey,
      model: this.selectModel(environment.nodeEnvironment, environment.useCase),
      maxTokens: this.getMaxTokens(),
      temperature: this.getTemperature(),
    };
  }

  private selectModel(nodeEnvironment: string, useCase?: string): string {
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
}
