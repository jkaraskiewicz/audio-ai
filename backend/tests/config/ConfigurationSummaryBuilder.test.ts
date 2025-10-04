import { ConfigurationSummaryBuilder } from '../../src/config/ConfigurationSummaryBuilder';
import { EnvironmentConfigLoader } from '../../src/config/EnvironmentConfigLoader';
import { FeatureFlagManager } from '../../src/config/FeatureFlagManager';
import { AppConfig, TranscriptionConfig, AIServiceConfig } from '../../src/types';

describe('ConfigurationSummaryBuilder', () => {
  let builder: ConfigurationSummaryBuilder;
  let mockEnvironmentLoader: jest.Mocked<EnvironmentConfigLoader>;
  let mockFeatureFlagManager: jest.Mocked<FeatureFlagManager>;

  beforeEach(() => {
    mockEnvironmentLoader = {
      loadEnvironmentConfiguration: jest.fn(),
    } as any;

    mockFeatureFlagManager = {
      loadFeatureFlags: jest.fn(),
    } as any;

    builder = new ConfigurationSummaryBuilder(mockEnvironmentLoader, mockFeatureFlagManager);
  });

  describe('buildSummary', () => {
    it('should build complete configuration summary', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
        useCase: 'testing',
      } as any);

      mockFeatureFlagManager.loadFeatureFlags.mockReturnValue({
        enableCommentary: true,
        enableHealthChecks: true,
      } as any);

      const appConfig: AppConfig = {
        port: 3000,
        baseDirectory: '/test/dir',
        geminiApiKey: 'test-key',
      } as any;

      const transcriptionConfig: TranscriptionConfig = {
        provider: 'openai_whisper',
        apiKey: 'whisper-key',
        model: 'whisper-1',
        language: 'en',
        maxFileSize: 25,
      } as any;

      const aiConfig: AIServiceConfig = {
        apiKey: 'gemini-key',
        model: 'gemini-2.0-flash-exp',
        maxTokens: 8192,
        temperature: 0.1,
      };

      const summary = builder.buildSummary(appConfig, transcriptionConfig, aiConfig);

      expect(summary.environment).toBe('development');
      expect(summary.useCase).toBe('testing');
      expect(summary.port).toBe(3000);
      expect(summary.baseDirectory).toBe('/test/dir');
      expect(summary.transcription.provider).toBe('openai_whisper');
      expect(summary.transcription.hasApiKey).toBe(true);
      expect(summary.transcription.model).toBe('whisper-1');
      expect(summary.transcription.language).toBe('en');
      expect(summary.transcription.maxFileSize).toBe(25);
      expect(summary.ai.model).toBe('gemini-2.0-flash-exp');
      expect(summary.ai.maxTokens).toBe(8192);
      expect(summary.ai.temperature).toBe(0.1);
      expect(summary.features.enableCommentary).toBe(true);
      expect(summary.features.enableHealthChecks).toBe(true);
    });

    it('should indicate hasApiKey as false when transcription API key is not set', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
      } as any);

      mockFeatureFlagManager.loadFeatureFlags.mockReturnValue({} as any);

      const appConfig: AppConfig = {
        port: 3000,
        baseDirectory: '/test/dir',
        geminiApiKey: 'test-key',
      } as any;

      const transcriptionConfig: TranscriptionConfig = {
        provider: 'free_web_speech',
        apiKey: '',
        model: '',
        language: 'en',
        maxFileSize: 25,
      } as any;

      const aiConfig: AIServiceConfig = {
        apiKey: 'gemini-key',
        model: 'gemini-2.0-flash-exp',
        maxTokens: 8192,
        temperature: 0.1,
      };

      const summary = builder.buildSummary(appConfig, transcriptionConfig, aiConfig);

      expect(summary.transcription.hasApiKey).toBe(false);
    });

    it('should handle errors gracefully', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockImplementation(() => {
        throw new Error('Environment loading failed');
      });

      const appConfig: AppConfig = {} as any;
      const transcriptionConfig: TranscriptionConfig = {} as any;
      const aiConfig: AIServiceConfig = {} as any;

      const summary = builder.buildSummary(appConfig, transcriptionConfig, aiConfig);

      expect(summary.error).toBe('Environment loading failed');
    });

    it('should handle unknown errors gracefully', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockImplementation(() => {
        throw 'String error';
      });

      const appConfig: AppConfig = {} as any;
      const transcriptionConfig: TranscriptionConfig = {} as any;
      const aiConfig: AIServiceConfig = {} as any;

      const summary = builder.buildSummary(appConfig, transcriptionConfig, aiConfig);

      expect(summary.error).toBe('Configuration error');
    });
  });
});
