import { FileProcessingResult } from '../types';

export interface AudioTranscriptionProvider {
  /**
   * Transcribe an audio file to text
   * @param file The audio file to transcribe
   * @returns Promise containing the transcription result
   */
  transcribe(file: Express.Multer.File): Promise<FileProcessingResult>;

  /**
   * Get the supported audio formats for this provider
   * @returns Array of supported file extensions
   */
  getSupportedFormats(): string[];

  /**
   * Get the maximum file size supported by this provider
   * @returns Maximum file size in bytes
   */
  getMaxFileSize(): number;

  /**
   * Get the name/identifier of this provider
   * @returns Provider name
   */
  getProviderName(): string;

  /**
   * Check if the provider is properly configured and ready to use
   * @returns True if the provider is ready
   */
  isReady(): boolean;
}

export enum TranscriptionProvider {
  WEB_SPEECH_API = 'web_speech_api',
  GEMINI_AUDIO = 'gemini_audio',
  HUGGING_FACE = 'hugging_face',
  LOCAL_WHISPER = 'local_whisper',
  FREE_WEB_SPEECH = 'free_web_speech',
  MOCK = 'mock', // For testing
}

export interface TranscriptionProviderConfig {
  provider: TranscriptionProvider;
  apiKey?: string;
  model?: string;
  language?: string;
  [key: string]: unknown; // Allow additional provider-specific config
}

export interface TranscriptionProviderInfo {
  name: TranscriptionProvider;
  description: string;
  requiresApiKey: boolean;
  supportsModels: boolean;
  supportedFormats: string[];
  maxFileSize: number;
  isAvailable: boolean;
  metadata: {
    cost: 'free' | 'paid' | 'freemium';
    installation: 'none' | 'api_key' | 'docker' | 'pip';
    performance: 'low' | 'medium' | 'high';
    accuracy: 'low' | 'medium' | 'high';
  };
}
