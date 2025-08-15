import { FileProcessingResult } from '../types';
import { TranscriptionProviderFactory } from './transcription-providers/TranscriptionProviderFactory';
import { getTranscriptionConfig } from '../config';
import { logger } from '../utils/logger';
import {
  AudioTranscriptionProvider,
  TranscriptionProviderConfig,
} from '../interfaces/AudioTranscriptionProvider';

export class AudioTranscriptionService {
  private provider: AudioTranscriptionProvider;

  constructor(providerConfig?: TranscriptionProviderConfig) {
    const config = providerConfig || getTranscriptionConfig();
    this.provider = TranscriptionProviderFactory.createProvider(config);
  }

  async transcribeAudioFile(file: Express.Multer.File): Promise<FileProcessingResult> {
    try {
      logger.debug('Starting audio transcription', {
        filename: file.originalname,
        size: file.size,
        mimetype: file.mimetype,
        provider: this.provider.getProviderName(),
      });

      // Validate audio file against provider capabilities
      this.validateAudioFile(file);

      if (!this.provider.isReady()) {
        throw new Error(
          `Audio transcription provider "${this.provider.getProviderName()}" is not properly configured`
        );
      }

      const result = await this.provider.transcribe(file);

      // Log transcription result with preview for debugging
      const transcriptionPreview = result.extractedText.length > 200 
        ? result.extractedText.substring(0, 200) + '...'
        : result.extractedText;

      logger.info('Successfully transcribed audio file', {
        filename: file.originalname,
        transcriptionLength: result.extractedText.length,
        audioSize: file.size,
        provider: this.provider.getProviderName(),
        transcriptionPreview,
      });

      // Log full transcription text for debugging (at debug level)
      logger.debug('Full transcription result', {
        filename: file.originalname,
        provider: this.provider.getProviderName(),
        fullTranscription: result.extractedText,
      });

      return result;
    } catch (error) {
      logger.error('Failed to transcribe audio file', {
        filename: file.originalname,
        provider: this.provider.getProviderName(),
        error: error instanceof Error ? error.message : 'Unknown error',
      });

      throw new Error(
        `Failed to transcribe audio file: ${error instanceof Error ? error.message : 'Unknown error'}`
      );
    }
  }

  private validateAudioFile(file: Express.Multer.File): void {
    const maxFileSize = this.provider.getMaxFileSize();
    const supportedFormats = this.provider.getSupportedFormats();

    if (file.size > maxFileSize) {
      throw new Error(
        `Audio file is too large (${Math.round(file.size / 1024 / 1024)}MB). Maximum size for ${this.provider.getProviderName()} is ${Math.round(maxFileSize / 1024 / 1024)}MB`
      );
    }

    const extension = this.getFileExtension(file.originalname).replace('.', '');
    if (!supportedFormats.includes(extension)) {
      throw new Error(
        `Unsupported audio format: ${extension}. ${this.provider.getProviderName()} supports: ${supportedFormats.join(', ')}`
      );
    }

    if (file.size < 1024) {
      // Less than 1KB is suspicious for an audio file
      throw new Error('Audio file appears to be too small or corrupted');
    }
  }

  private getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : filename.substring(lastDotIndex).toLowerCase();
  }

  getSupportedFormats(): string[] {
    return this.provider.getSupportedFormats();
  }

  getMaxFileSize(): number {
    return this.provider.getMaxFileSize();
  }

  getProviderName(): string {
    return this.provider.getProviderName();
  }
}
