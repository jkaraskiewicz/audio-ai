/**
 * Clean Configuration Module
 * Uncle Bob Approved: Simple, focused exports with proper separation of concerns
 */

// Export the new clean configuration service
export { ConfigurationService } from './ConfigurationService';

// Export focused configuration classes for advanced use cases
export { EnvironmentConfigLoader } from './EnvironmentConfigLoader';
export { FeatureFlagManager } from './FeatureFlagManager';
export { ApplicationConfigBuilder } from './ApplicationConfigBuilder';
export { TranscriptionConfigBuilder } from './TranscriptionConfigBuilder';
export { ConfigurationValidator, type ValidationResult } from './ConfigurationValidator';

// Backward compatibility exports (delegate to new clean implementation)
export { getConfig, getTranscriptionConfig, getAIConfig } from './ConfigurationService';

// Keep legacy constants for backward compatibility
export const AI_CONFIG = {
  model: 'gemini-2.0-flash-exp',
} as const;

export const FILE_CONFIG = {
  specialCategories: {
    daily: 'daily/tasks',
  },
} as const;
