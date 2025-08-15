import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult, FileType } from '../../types';
import { logger } from '../../utils/logger';

/**
 * Hugging Face Whisper transcription provider
 * Uses Hugging Face's free inference API with OpenAI Whisper model
 * Free tier: 1,000 requests/month per model
 */
export class HuggingFaceTranscriptionProvider implements AudioTranscriptionProvider {
  private apiKey?: string;
  private model: string;
  private isConfigured: boolean;
  private readonly baseUrl = 'https://api-inference.huggingface.co/models';

  constructor(apiKey?: string, model = 'openai/whisper-large-v3') {
    this.apiKey = apiKey;
    this.model = model;
    this.isConfigured = true; // HF has free tier without API key
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    logger.info('Starting Hugging Face Whisper transcription', {
      filename: file.originalname,
      size: file.size,
      model: this.model,
    });

    try {
      const headers: Record<string, string> = {
        'Content-Type': 'application/octet-stream',
      };

      if (this.apiKey) {
        headers['Authorization'] = `Bearer ${this.apiKey}`;
      }

      const response = await fetch(`${this.baseUrl}/${this.model}`, {
        method: 'POST',
        headers,
        body: new Uint8Array(file.buffer),
      });

      if (!response.ok) {
        const errorText = await response.text();
        logger.error('Hugging Face API error', {
          status: response.status,
          statusText: response.statusText,
          error: errorText,
        });

        if (response.status === 503) {
          throw new Error(
            'Hugging Face model is loading. Please try again in a few minutes.'
          );
        }

        if (response.status === 429) {
          throw new Error(
            'Hugging Face rate limit exceeded. Consider using an API key for higher limits.'
          );
        }

        if (response.status === 401) {
          throw new Error(
            'Hugging Face authentication failed. Free tier may be limited. Consider setting TRANSCRIPTION_API_KEY environment variable with your HF token.'
          );
        }

        throw new Error(`Hugging Face API error: ${response.status} ${response.statusText}`);
      }

      const result = await response.json();
      
      if (!result || !result.text) {
        throw new Error('No transcription text received from Hugging Face');
      }

      const transcription = result.text.trim();

      logger.info('Hugging Face transcription completed', {
        filename: file.originalname,
        transcriptionLength: transcription.length,
        model: this.model,
      });

      return {
        extractedText: transcription,
        fileType: FileType.AUDIO,
        processingMethod: `huggingface_${this.model.replace('/', '_')}`,
      };
    } catch (error) {
      logger.error('Hugging Face transcription failed', {
        filename: file.originalname,
        model: this.model,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw error;
    }
  }

  getSupportedFormats(): string[] {
    return ['wav', 'mp3', 'ogg', 'flac', 'm4a', 'webm'];
  }

  getMaxFileSize(): number {
    return 30 * 1024 * 1024; // 30MB - HF inference API limit
  }

  getProviderName(): string {
    return `Hugging Face Whisper (${this.model})`;
  }

  isReady(): boolean {
    return this.isConfigured;
  }
}