import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult, FileType } from '../../types';
import { logger } from '../../utils/logger';

/**
 * Docker Whisper transcription provider
 * Uses OpenAI Whisper running in a Docker container
 * Completely free, no API keys, isolated environment
 */
export class DockerWhisperProvider implements AudioTranscriptionProvider {
  private whisperUrl: string;
  private isConfigured = false;

  constructor(whisperUrl = 'http://localhost:8001') {
    this.whisperUrl = whisperUrl;
    this.checkWhisperAvailability();
  }

  private async checkWhisperAvailability(): Promise<void> {
    try {
      logger.debug('Checking Docker Whisper availability', { url: this.whisperUrl });
      
      const response = await fetch(`${this.whisperUrl}/health`, {
        method: 'GET',
        timeout: 5000,
      } as any);

      this.isConfigured = response.ok;
      
      if (this.isConfigured) {
        logger.info('Docker Whisper service is available', { url: this.whisperUrl });
      } else {
        logger.warn('Docker Whisper service not available', { 
          url: this.whisperUrl,
          status: response.status 
        });
      }
    } catch (error) {
      logger.warn('Docker Whisper service check failed', { 
        url: this.whisperUrl,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
      this.isConfigured = false;
    }
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    if (!this.isConfigured) {
      // Try to check availability again
      await this.checkWhisperAvailability();
      
      if (!this.isConfigured) {
        throw new Error(
          `Docker Whisper service not available at ${this.whisperUrl}. ` +
          'Start it with: docker run -p 8001:8001 audio-ai-whisper'
        );
      }
    }

    logger.info('Starting Docker Whisper transcription', {
      filename: file.originalname,
      size: file.size,
      whisperUrl: this.whisperUrl,
    });

    try {
      const response = await fetch(`${this.whisperUrl}/transcribe`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/octet-stream',
          'Content-Length': file.size.toString(),
        },
        body: new Uint8Array(file.buffer),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Docker Whisper API error: ${response.status} ${errorText}`);
      }

      const result = await response.json();
      
      if (!result.text || result.text.trim().length === 0) {
        throw new Error('Empty transcription result from Docker Whisper');
      }

      const transcription = result.text.trim();

      logger.info('Docker Whisper transcription completed', {
        filename: file.originalname,
        transcriptionLength: transcription.length,
        language: result.language || 'unknown',
      });

      return {
        extractedText: transcription,
        fileType: FileType.AUDIO,
        processingMethod: `docker_whisper_${result.language || 'unknown'}`,
      };
    } catch (error) {
      logger.error('Docker Whisper transcription failed', {
        filename: file.originalname,
        whisperUrl: this.whisperUrl,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw error;
    }
  }

  getSupportedFormats(): string[] {
    return ['wav', 'mp3', 'ogg', 'flac', 'm4a', 'mp4', 'webm', 'aiff'];
  }

  getMaxFileSize(): number {
    return 100 * 1024 * 1024; // 100MB - generous for local Docker processing
  }

  getProviderName(): string {
    return `Docker Whisper (${this.whisperUrl})`;
  }

  isReady(): boolean {
    return this.isConfigured;
  }
}