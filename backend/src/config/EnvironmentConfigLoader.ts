import { config } from 'dotenv';

config();

/**
 * Single Responsibility: Load and parse environment variables
 * Uncle Bob Approved: Focused class with clear naming
 */
export interface EnvironmentConfig {
  readonly nodeEnvironment: string;
  readonly useCase?: string;
  readonly debugMode?: string;
}

export class EnvironmentConfigLoader {
  loadEnvironmentConfiguration(): EnvironmentConfig {
    return {
      nodeEnvironment: process.env.NODE_ENV || 'development',
      useCase: process.env.USE_CASE,
      debugMode: process.env.DEBUG,
    };
  }

  isProductionEnvironment(): boolean {
    return this.loadEnvironmentConfiguration().nodeEnvironment === 'production';
  }

  isDevelopmentEnvironment(): boolean {
    return this.loadEnvironmentConfiguration().nodeEnvironment === 'development';
  }

  isTestEnvironment(): boolean {
    return this.loadEnvironmentConfiguration().nodeEnvironment === 'test';
  }
}
