import { AIConfigBuilder } from '../../src/config/AIConfigBuilder';
import { EnvironmentConfigLoader } from '../../src/config/EnvironmentConfigLoader';

describe('AIConfigBuilder', () => {
  let builder: AIConfigBuilder;
  let mockEnvironmentLoader: jest.Mocked<EnvironmentConfigLoader>;

  beforeEach(() => {
    mockEnvironmentLoader = {
      loadEnvironmentConfiguration: jest.fn(),
    } as any;

    builder = new AIConfigBuilder(mockEnvironmentLoader);
  });

  describe('buildAIConfig', () => {
    it('should build AI config with production model in production environment', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'production',
        useCase: undefined,
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.apiKey).toBe('test-api-key');
      expect(config.model).toBe('gemini-1.5-pro');
      expect(config.maxTokens).toBe(8192);
      expect(config.temperature).toBe(0.1);
    });

    it('should build AI config with production model in staging environment', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'staging',
        useCase: undefined,
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.model).toBe('gemini-1.5-pro');
    });

    it('should build AI config with flash model in development environment', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
        useCase: undefined,
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.model).toBe('gemini-2.0-flash-exp');
    });

    it('should use high-accuracy model when use case is high-accuracy', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
        useCase: 'high-accuracy',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.model).toBe('gemini-1.5-pro');
    });

    it('should use high-volume model when use case is high-volume', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
        useCase: 'high-volume',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.model).toBe('gemini-1.5-flash');
    });

    it('should use environment variable for maxTokens if provided', () => {
      process.env.GEMINI_MAX_TOKENS = '4096';
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.maxTokens).toBe(4096);
      delete process.env.GEMINI_MAX_TOKENS;
    });

    it('should use environment variable for temperature if provided', () => {
      process.env.GEMINI_TEMPERATURE = '0.5';
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.temperature).toBe(0.5);
      delete process.env.GEMINI_TEMPERATURE;
    });

    it('should use default maxTokens when environment variable is not set', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.maxTokens).toBe(8192);
    });

    it('should use default temperature when environment variable is not set', () => {
      mockEnvironmentLoader.loadEnvironmentConfiguration.mockReturnValue({
        nodeEnvironment: 'development',
      } as any);

      const config = builder.buildAIConfig('test-api-key');

      expect(config.temperature).toBe(0.1);
    });
  });
});
