import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';

/**
 * Single Responsibility: Manage feature flag state and queries
 * Uncle Bob Approved: Focused on feature flag logic only
 */
export interface FeatureFlags {
  readonly enableCommentary: boolean;
  readonly enableHealthChecks: boolean;
  readonly enableDetailedLogging: boolean;
  readonly enableRateLimiting: boolean;
  readonly enableMetrics: boolean;
}

export class FeatureFlagManager {
  constructor(private environmentLoader: EnvironmentConfigLoader) {}

  loadFeatureFlags(): FeatureFlags {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();

    return {
      enableCommentary: this.isCommentaryEnabled(),
      enableHealthChecks: this.areHealthChecksEnabled(),
      enableDetailedLogging: this.isDetailedLoggingEnabled(environment.nodeEnvironment),
      enableRateLimiting: this.isRateLimitingEnabled(environment.nodeEnvironment),
      enableMetrics: this.areMetricsEnabled(),
    };
  }

  isFeatureEnabled(featureName: keyof FeatureFlags): boolean {
    const flags = this.loadFeatureFlags();
    return flags[featureName];
  }

  enableFeature(featureName: keyof FeatureFlags): void {
    // For runtime feature flag updates
    process.env[this.getEnvironmentVariableNameForFeature(featureName)] = 'true';
  }

  disableFeature(featureName: keyof FeatureFlags): void {
    // For runtime feature flag updates
    process.env[this.getEnvironmentVariableNameForFeature(featureName)] = 'false';
  }

  private isCommentaryEnabled(): boolean {
    return process.env.ENABLE_COMMENTARY !== 'false';
  }

  private areHealthChecksEnabled(): boolean {
    return process.env.ENABLE_HEALTH_CHECKS !== 'false';
  }

  private isDetailedLoggingEnabled(nodeEnvironment: string): boolean {
    return process.env.ENABLE_DETAILED_LOGGING === 'true' || nodeEnvironment === 'development';
  }

  private isRateLimitingEnabled(nodeEnvironment: string): boolean {
    return process.env.ENABLE_RATE_LIMITING === 'true' || nodeEnvironment === 'production';
  }

  private areMetricsEnabled(): boolean {
    return process.env.ENABLE_METRICS === 'true';
  }

  private getEnvironmentVariableNameForFeature(featureName: keyof FeatureFlags): string {
    const mapping: Record<keyof FeatureFlags, string> = {
      enableCommentary: 'ENABLE_COMMENTARY',
      enableHealthChecks: 'ENABLE_HEALTH_CHECKS',
      enableDetailedLogging: 'ENABLE_DETAILED_LOGGING',
      enableRateLimiting: 'ENABLE_RATE_LIMITING',
      enableMetrics: 'ENABLE_METRICS',
    };

    return mapping[featureName];
  }
}
