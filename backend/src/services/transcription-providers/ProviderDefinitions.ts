import { TranscriptionProvider, TranscriptionProviderConfig } from '../../interfaces/AudioTranscriptionProvider';
import { ProviderRegistryEntry } from './ProviderMetadata';
import { MockTranscriptionProvider } from './MockTranscriptionProvider';
import { GeminiAudioTranscriptionProvider } from './GeminiAudioTranscriptionProvider';
import { HuggingFaceTranscriptionProvider } from './HuggingFaceTranscriptionProvider';
import { LocalWhisperProvider } from './LocalWhisperProvider';
import { FreeWebSpeechProvider } from './FreeWebSpeechProvider';
import { DockerWhisperProvider } from './DockerWhisperProvider';
import { OpenAIWhisperWebserviceProvider } from './OpenAIWhisperWebserviceProvider';
import { logger } from '../../utils/logger';

/**
 * Provider definitions - centralized configuration for all providers
 */
export const PROVIDER_DEFINITIONS = new Map<TranscriptionProvider, ProviderRegistryEntry>([
  [
    TranscriptionProvider.FREE_WEB_SPEECH,
    {
      name: TranscriptionProvider.FREE_WEB_SPEECH,
      description: 'Free web speech recognition (no API key required)',
      constructor: FreeWebSpeechProvider,
      requiresApiKey: false,
      supportsModels: false,
      supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
      maxFileSize: 50 * 1024 * 1024, // 50MB
      isAvailable: true,
      metadata: {
        cost: 'free',
        installation: 'none',
        performance: 'medium',
        accuracy: 'medium',
      },
    },
  ],
  [
    TranscriptionProvider.LOCAL_WHISPER,
    {
      name: TranscriptionProvider.LOCAL_WHISPER,
      description: 'Local OpenAI Whisper (free, requires: pip install openai-whisper)',
      constructor: LocalWhisperProvider,
      requiresApiKey: false,
      supportsModels: true,
      supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4'],
      maxFileSize: 50 * 1024 * 1024, // 50MB
      isAvailable: true,
      metadata: {
        cost: 'free',
        installation: 'pip',
        performance: 'high',
        accuracy: 'high',
      },
    },
  ],
  [
    TranscriptionProvider.HUGGING_FACE,
    {
      name: TranscriptionProvider.HUGGING_FACE,
      description: 'Hugging Face Whisper API (free: 1,000 requests/month)',
      constructor: HuggingFaceTranscriptionProvider,
      requiresApiKey: true,
      supportsModels: true,
      supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a'],
      maxFileSize: 20 * 1024 * 1024, // 20MB for Hugging Face
      isAvailable: true,
      metadata: {
        cost: 'freemium',
        installation: 'api_key',
        performance: 'high',
        accuracy: 'high',
      },
    },
  ],
  [
    TranscriptionProvider.MOCK,
    {
      name: TranscriptionProvider.MOCK,
      description: 'Mock provider for development and testing (free)',
      constructor: MockTranscriptionProvider,
      requiresApiKey: false,
      supportsModels: false,
      supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
      maxFileSize: 50 * 1024 * 1024, // 50MB
      isAvailable: true,
      metadata: {
        cost: 'free',
        installation: 'none',
        performance: 'low',
        accuracy: 'low',
      },
    },
  ],
  [
    TranscriptionProvider.GEMINI_AUDIO,
    {
      name: TranscriptionProvider.GEMINI_AUDIO,
      description: 'Google Gemini AI audio transcription (when available)',
      constructor: GeminiAudioTranscriptionProvider,
      factory: (config: TranscriptionProviderConfig) => {
        if (!config.apiKey) {
          logger.warn('Gemini API key not provided, falling back to mock provider');
          return new MockTranscriptionProvider();
        }
        return new GeminiAudioTranscriptionProvider(config.apiKey, config.model);
      },
      requiresApiKey: true,
      supportsModels: true,
      supportedFormats: ['.mp3', '.wav', '.ogg', '.flac', '.m4a'],
      maxFileSize: 20 * 1024 * 1024, // 20MB estimated for Gemini
      isAvailable: false, // Not actually available yet
      metadata: {
        cost: 'paid',
        installation: 'api_key',
        performance: 'high',
        accuracy: 'high',
      },
    },
  ],
  [
    TranscriptionProvider.DOCKER_WHISPER,
    {
      name: TranscriptionProvider.DOCKER_WHISPER,
      description: 'Docker-based OpenAI Whisper service (free, external or self-hosted)',
      constructor: DockerWhisperProvider,
      factory: (config: TranscriptionProviderConfig) => {
        const whisperUrl = (config as any).whisperServiceUrl || process.env.WHISPER_SERVICE_URL;
        return new DockerWhisperProvider(whisperUrl);
      },
      requiresApiKey: false,
      supportsModels: true,
      supportedFormats: ['.wav', '.mp3', '.ogg', '.flac', '.m4a', '.mp4', '.webm', '.aiff'],
      maxFileSize: 100 * 1024 * 1024, // 100MB
      isAvailable: true,
      metadata: {
        cost: 'free',
        installation: 'docker',
        performance: 'high',
        accuracy: 'high',
      },
    },
  ],
  [
    TranscriptionProvider.OPENAI_WHISPER_WEBSERVICE,
    {
      name: TranscriptionProvider.OPENAI_WHISPER_WEBSERVICE,
      description: 'OpenAI Whisper ASR Webservice (onerahmet/openai-whisper-asr-webservice)',
      constructor: OpenAIWhisperWebserviceProvider,
      factory: (config: TranscriptionProviderConfig) => {
        const whisperUrl = (config as any).whisperServiceUrl || process.env.WHISPER_SERVICE_URL;
        return new OpenAIWhisperWebserviceProvider(whisperUrl);
      },
      requiresApiKey: false,
      supportsModels: true,
      supportedFormats: ['.wav', '.mp3', '.ogg', '.flac', '.m4a', '.mp4', '.webm', '.aiff'],
      maxFileSize: 100 * 1024 * 1024, // 100MB
      isAvailable: true,
      metadata: {
        cost: 'free',
        installation: 'docker',
        performance: 'high',
        accuracy: 'high',
      },
    },
  ],
]);
