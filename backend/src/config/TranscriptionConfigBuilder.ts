import { TranscriptionConfig } from '../types';
import { TranscriptionProvider } from '../interfaces/AudioTranscriptionProvider';
import { EnvironmentConfigLoader } from './EnvironmentConfigLoader';

/**
 * Single Responsibility: Build transcription configuration with intelligent defaults
 * Uncle Bob Approved: Focused class with clear business rules
 */
export class TranscriptionConfigBuilder {
  private static readonly DEFAULT_LANGUAGE = 'en';
  private static readonly DEFAULT_MAX_FILE_SIZE_HUGGING_FACE = 100 * 1024 * 1024; // 100MB
  private static readonly DEFAULT_MAX_FILE_SIZE_LOCAL_WHISPER = 500 * 1024 * 1024; // 500MB
  private static readonly DEFAULT_MAX_FILE_SIZE_GEMINI = 50 * 1024 * 1024; // 50MB
  private static readonly DEFAULT_MAX_FILE_SIZE_GENERIC = 25 * 1024 * 1024; // 25MB

  constructor(private environmentLoader: EnvironmentConfigLoader) {}

  buildTranscriptionConfig(): TranscriptionConfig {
    const provider = this.selectDefaultProvider();

    return {
      provider,
      apiKey: this.getProviderApiKey(provider),
      model: this.getProviderModel(provider),
      language: this.getTranscriptionLanguage(),
      maxFileSize: this.getMaximumFileSize(provider),
      whisperServiceUrl: this.getWhisperServiceUrl(),
    };
  }

  private selectDefaultProvider(): TranscriptionProvider {
    const configuredProvider = process.env.TRANSCRIPTION_PROVIDER;
    if (configuredProvider) {
      return configuredProvider as TranscriptionProvider;
    }

    return this.getProviderBasedOnUseCase();
  }

  private getProviderBasedOnUseCase(): TranscriptionProvider {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();

    switch (environment.useCase) {
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
    const environmentModel = process.env.TRANSCRIPTION_MODEL;
    if (environmentModel) {
      return environmentModel;
    }

    return this.getDefaultModelForProvider(provider);
  }

  private getDefaultModelForProvider(provider: TranscriptionProvider): string | undefined {
    const environment = this.environmentLoader.loadEnvironmentConfiguration();
    const isHighAccuracy = environment.useCase === 'high-accuracy';

    switch (provider) {
      case TranscriptionProvider.HUGGING_FACE:
        return isHighAccuracy ? 'openai/whisper-large-v3' : 'openai/whisper-base';
      case TranscriptionProvider.LOCAL_WHISPER:
        return isHighAccuracy ? 'large-v3' : 'base';
      default:
        return undefined;
    }
  }

  private getTranscriptionLanguage(): string {
    return process.env.TRANSCRIPTION_LANGUAGE || TranscriptionConfigBuilder.DEFAULT_LANGUAGE;
  }

  private getMaximumFileSize(provider: TranscriptionProvider): number {
    const configuredSize = process.env.MAX_FILE_SIZE;
    if (configuredSize) {
      return parseInt(configuredSize, 10);
    }

    return this.getDefaultMaxFileSizeForProvider(provider);
  }

  private getDefaultMaxFileSizeForProvider(provider: TranscriptionProvider): number {
    switch (provider) {
      case TranscriptionProvider.HUGGING_FACE:
        return TranscriptionConfigBuilder.DEFAULT_MAX_FILE_SIZE_HUGGING_FACE;
      case TranscriptionProvider.LOCAL_WHISPER:
        return TranscriptionConfigBuilder.DEFAULT_MAX_FILE_SIZE_LOCAL_WHISPER;
      case TranscriptionProvider.GEMINI_AUDIO:
        return TranscriptionConfigBuilder.DEFAULT_MAX_FILE_SIZE_GEMINI;
      default:
        return TranscriptionConfigBuilder.DEFAULT_MAX_FILE_SIZE_GENERIC;
    }
  }

  private getWhisperServiceUrl(): string {
    return process.env.WHISPER_SERVICE_URL || 'http://localhost:8001';
  }
}
