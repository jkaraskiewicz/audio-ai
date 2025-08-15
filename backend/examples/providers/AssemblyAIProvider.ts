/**
 * AssemblyAI Audio Transcription Provider Example
 * 
 * This example shows how to implement a real-world provider
 * using AssemblyAI's API for high-quality speech-to-text.
 */

import { AudioTranscriptionProvider } from '../../src/interfaces/AudioTranscriptionProvider';
import { FileProcessingResult } from '../../src/types';
import { logger } from '../../src/utils/logger';

interface AssemblyAIConfig {
  apiKey: string;
  language?: string;
  speakerLabels?: boolean;
  punctuate?: boolean;
  formatText?: boolean;
}

export class AssemblyAIProvider implements AudioTranscriptionProvider {
  private config: AssemblyAIConfig;
  private isConfigured: boolean;
  private baseURL = 'https://api.assemblyai.com/v2';

  constructor(apiKey?: string, model?: string, options?: Record<string, any>) {
    this.config = {
      apiKey: apiKey || '',
      language: options?.language || 'en',
      speakerLabels: options?.speakerLabels || false,
      punctuate: options?.punctuate ?? true,
      formatText: options?.formatText ?? true,
    };

    this.isConfigured = !!apiKey;

    if (this.isConfigured) {
      logger.info('AssemblyAI Provider initialized successfully', {
        language: this.config.language,
        speakerLabels: this.config.speakerLabels,
      });
    } else {
      logger.warn('AssemblyAI Provider not configured - missing API key');
    }
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    if (!this.isReady()) {
      throw new Error('AssemblyAI Provider is not properly configured');
    }

    this.validateFile(file);

    try {
      logger.debug('Starting transcription with AssemblyAI', {
        filename: file.originalname,
        size: file.size,
        mimetype: file.mimetype,
      });

      const startTime = Date.now();

      // Step 1: Upload the file
      const audioUrl = await this.uploadFile(file);

      // Step 2: Create transcription job
      const transcriptId = await this.createTranscription(audioUrl);

      // Step 3: Poll for completion
      const result = await this.waitForCompletion(transcriptId);

      const duration = Date.now() - startTime;

      logger.info('AssemblyAI transcription completed', {
        filename: file.originalname,
        duration: `${duration}ms`,
        textLength: result.text.length,
        confidence: result.confidence,
      });

      return {
        extractedText: result.text,
        processingMethod: 'assemblyai_transcription',
        metadata: {
          provider: this.getProviderName(),
          confidence: result.confidence,
          duration: `${duration}ms`,
          speakerLabels: this.config.speakerLabels,
          language: result.detected_language || this.config.language,
          wordCount: result.words?.length || 0,
        },
      };
    } catch (error) {
      logger.error('AssemblyAI transcription failed', {
        filename: file.originalname,
        error: error instanceof Error ? error.message : 'Unknown error',
      });

      throw new Error(
        `AssemblyAI transcription failed: ${
          error instanceof Error ? error.message : 'Unknown error'
        }`
      );
    }
  }

  private async uploadFile(file: Express.Multer.File): Promise<string> {
    const response = await fetch(`${this.baseURL}/upload`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.config.apiKey}`,
        'Content-Type': 'application/octet-stream',
      },
      body: file.buffer,
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`File upload failed: ${response.status} ${errorText}`);
    }

    const data = await response.json();
    return data.upload_url;
  }

  private async createTranscription(audioUrl: string): Promise<string> {
    const requestData = {
      audio_url: audioUrl,
      language_code: this.config.language,
      speaker_labels: this.config.speakerLabels,
      punctuate: this.config.punctuate,
      format_text: this.config.formatText,
    };

    const response = await fetch(`${this.baseURL}/transcript`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.config.apiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Transcription request failed: ${response.status} ${errorText}`);
    }

    const data = await response.json();
    return data.id;
  }

  private async waitForCompletion(transcriptId: string, maxWaitTime = 300000): Promise<{
    text: string;
    confidence: number;
    detected_language?: string;
    words?: any[];
  }> {
    const startTime = Date.now();
    const pollInterval = 3000; // 3 seconds

    while (Date.now() - startTime < maxWaitTime) {
      const response = await fetch(`${this.baseURL}/transcript/${transcriptId}`, {
        headers: {
          'Authorization': `Bearer ${this.config.apiKey}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to check transcription status: ${response.status}`);
      }

      const data = await response.json();

      if (data.status === 'completed') {
        return {
          text: data.text,
          confidence: data.confidence,
          detected_language: data.language_code,
          words: data.words,
        };
      }

      if (data.status === 'error') {
        throw new Error(`Transcription failed: ${data.error}`);
      }

      // Wait before next poll
      await new Promise(resolve => setTimeout(resolve, pollInterval));
    }

    throw new Error('Transcription timed out');
  }

  private validateFile(file: Express.Multer.File): void {
    if (file.size > this.getMaxFileSize()) {
      throw new Error(
        `File too large: ${file.size} bytes (max: ${this.getMaxFileSize()} bytes)`
      );
    }

    const extension = this.getFileExtension(file.originalname);
    if (!this.getSupportedFormats().includes(extension)) {
      throw new Error(
        `Unsupported file format: ${extension}. Supported formats: ${this.getSupportedFormats().join(', ')}`
      );
    }
  }

  private getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : filename.substring(lastDotIndex).toLowerCase();
  }

  getSupportedFormats(): string[] {
    return [
      '.mp3',
      '.wav',
      '.flac',
      '.ogg',
      '.opus',
      '.m4a',
      '.aac',
      '.wma',
      '.amr',
      '.3gp',
    ];
  }

  getMaxFileSize(): number {
    // AssemblyAI supports up to 2GB files
    return 2 * 1024 * 1024 * 1024;
  }

  getProviderName(): string {
    return 'assemblyai';
  }

  isReady(): boolean {
    return this.isConfigured && !!this.config.apiKey;
  }

  async healthCheck(): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseURL}/transcript`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.config.apiKey}`,
        },
      });
      return response.status !== 401; // Unauthorized means bad API key
    } catch {
      return false;
    }
  }
}

/**
 * Example usage:
 * 
 * // Basic usage
 * const provider = new AssemblyAIProvider('your-api-key');
 * 
 * // With speaker labels and custom language
 * const provider = new AssemblyAIProvider('your-api-key', undefined, {
 *   language: 'es',
 *   speakerLabels: true,
 *   punctuate: true,
 *   formatText: true
 * });
 */