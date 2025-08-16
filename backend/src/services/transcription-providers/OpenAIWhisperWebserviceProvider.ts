import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult, FileType } from '../../types';
import { logger } from '../../utils/logger';
import { AudioConverter } from '../../utils/audioConverter';

/**
 * OpenAI Whisper ASR Webservice Provider
 * Compatible with onerahmet/openai-whisper-asr-webservice
 * Uses /asr endpoint with multipart form data
 */
export class OpenAIWhisperWebserviceProvider implements AudioTranscriptionProvider {
  private whisperUrl: string;
  private isConfigured = false;

  constructor(whisperUrl?: string) {
    // Allow configuration via environment variable or parameter
    this.whisperUrl = whisperUrl || process.env.WHISPER_SERVICE_URL || 'http://localhost:1991';
    this.checkWhisperAvailability();
  }

  private async checkWhisperAvailability(): Promise<void> {
    try {
      logger.debug('Checking OpenAI Whisper Webservice availability', { url: this.whisperUrl });
      
      // Test with a simple POST to /asr to see if service responds
      const response = await fetch(`${this.whisperUrl}/asr`, {
        method: 'POST',
        timeout: 5000,
      } as any);

      // Expect 422 (validation error) which means service is up but missing audio_file
      this.isConfigured = response.status === 422;
      
      if (this.isConfigured) {
        logger.info('OpenAI Whisper Webservice is available', { url: this.whisperUrl });
      } else {
        logger.warn('OpenAI Whisper Webservice not available', { 
          url: this.whisperUrl,
          status: response.status 
        });
      }
    } catch (error) {
      logger.warn('OpenAI Whisper Webservice check failed', { 
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
          `OpenAI Whisper Webservice not available at ${this.whisperUrl}. ` +
          'Make sure the service is running and accessible.'
        );
      }
    }

    logger.info('Starting OpenAI Whisper Webservice transcription', {
      filename: file.originalname,
      size: file.size,
      whisperUrl: this.whisperUrl,
    });

    try {
      // Check if audio conversion is needed for better Whisper compatibility
      let audioBuffer = file.buffer;
      let audioFilename = file.originalname;
      let audioMimetype = file.mimetype;

      if (AudioConverter.shouldConvert(file.originalname, file.mimetype)) {
        logger.info('Converting audio format for better Whisper compatibility', {
          originalFormat: file.originalname,
          originalMimetype: file.mimetype,
        });

        const converted = await AudioConverter.convertForWhisper(file.buffer, file.originalname);
        audioBuffer = converted.buffer;
        audioFilename = converted.filename;
        audioMimetype = converted.mimetype;

        logger.info('Audio conversion completed', {
          originalSize: file.buffer.length,
          convertedSize: audioBuffer.length,
          convertedFormat: audioFilename,
        });
      }

      // Create form data for the webservice
      const formData = new FormData();
      
      // Create a blob from the (possibly converted) audio buffer
      const audioBlob = new Blob([new Uint8Array(audioBuffer)], { type: audioMimetype });
      formData.append('audio_file', audioBlob, audioFilename);
      
      // Optional parameters that the webservice supports
      formData.append('task', 'transcribe'); // transcribe or translate
      // Don't set language parameter for auto-detection
      formData.append('output', 'txt'); // json or txt

      const response = await fetch(`${this.whisperUrl}/asr`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`OpenAI Whisper Webservice API error: ${response.status} ${errorText}`);
      }

      // Check if response is JSON or plain text
      const contentType = response.headers.get('content-type') || '';
      let transcription: string;
      let language = 'auto-detected';
      
      if (contentType.includes('application/json')) {
        const result = await response.json();
        transcription = result.text || result.transcript || '';
        language = result.language || 'auto-detected';
      } else {
        // Handle plain text response
        transcription = await response.text();
      }
      
      if (!transcription || transcription.trim().length === 0) {
        throw new Error('Empty transcription result from OpenAI Whisper Webservice');
      }

      transcription = transcription.trim();

      logger.info('OpenAI Whisper Webservice transcription completed', {
        filename: file.originalname,
        transcriptionLength: transcription.length,
        language,
      });

      return {
        extractedText: transcription,
        fileType: FileType.AUDIO,
        processingMethod: `openai_whisper_webservice_${language}`,
      };
    } catch (error) {
      logger.error('OpenAI Whisper Webservice transcription failed', {
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
    return 100 * 1024 * 1024; // 100MB - generous for local processing
  }

  getProviderName(): string {
    return `OpenAI Whisper Webservice (${this.whisperUrl})`;
  }

  isReady(): boolean {
    return this.isConfigured;
  }
}