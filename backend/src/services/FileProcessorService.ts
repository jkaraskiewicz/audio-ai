import { FileTypeDetectionService } from './FileTypeDetectionService';
import { TextFileProcessorService } from './TextFileProcessorService';
import { AudioTranscriptionService } from './AudioTranscriptionService';
import { FileProcessingResult, FileType } from '../types';
import { logger } from '../utils/logger';

export class FileProcessorService {
  constructor(
    private fileTypeDetectionService: FileTypeDetectionService,
    private textFileProcessorService: TextFileProcessorService,
    private audioTranscriptionService: AudioTranscriptionService
  ) {}

  async processFile(file: Express.Multer.File): Promise<FileProcessingResult> {
    try {
      logger.info('Starting file processing', {
        filename: file.originalname,
        size: file.size,
        mimetype: file.mimetype,
      });

      // Step 1: Validate file size
      if (!this.fileTypeDetectionService.isValidFileSize(file)) {
        throw new Error('File size exceeds maximum allowed limit (50MB)');
      }

      // Step 2: Detect file type
      const fileType = this.fileTypeDetectionService.detectFileType(file);

      // Step 3: Process based on file type
      let result: FileProcessingResult;

      switch (fileType) {
        case FileType.TEXT:
          result = await this.textFileProcessorService.processTextFile(file);
          break;

        case FileType.AUDIO:
          result = await this.audioTranscriptionService.transcribeAudioFile(file);
          break;

        case FileType.UNKNOWN:
        default:
          throw new Error(
            `Unsupported file type. Supported formats: ${this.getSupportedFormatsString()}`
          );
      }

      // Step 4: Final validation
      if (!result.extractedText || result.extractedText.trim().length === 0) {
        throw new Error('No usable content could be extracted from the file');
      }

      logger.info('File processing completed successfully', {
        filename: file.originalname,
        fileType: result.fileType,
        processingMethod: result.processingMethod,
        extractedTextLength: result.extractedText.length,
      });

      return result;
    } catch (error) {
      logger.error('File processing failed', {
        filename: file.originalname,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw error;
    }
  }

  getSupportedFormats(): { text: string[]; audio: string[] } {
    return this.fileTypeDetectionService.getSupportedFormats();
  }

  private getSupportedFormatsString(): string {
    const formats = this.getSupportedFormats();
    return `Text: ${formats.text.join(', ')}; Audio: ${formats.audio.join(', ')}`;
  }

  getMaxFileSize(): number {
    return 50 * 1024 * 1024; // 50MB
  }

  validateFileBeforeProcessing(file: Express.Multer.File): { isValid: boolean; error?: string } {
    try {
      // Check file size
      if (!this.fileTypeDetectionService.isValidFileSize(file)) {
        return {
          isValid: false,
          error: 'File size exceeds maximum allowed limit (50MB)',
        };
      }

      // Check if file type is supported
      const fileType = this.fileTypeDetectionService.detectFileType(file);
      if (fileType === FileType.UNKNOWN) {
        return {
          isValid: false,
          error: `Unsupported file type. Supported formats: ${this.getSupportedFormatsString()}`,
        };
      }

      // Additional validation for audio files (OpenAI Whisper has 25MB limit)
      if (fileType === FileType.AUDIO && file.size > 25 * 1024 * 1024) {
        return {
          isValid: false,
          error: 'Audio files must be smaller than 25MB for transcription',
        };
      }

      return { isValid: true };
    } catch (error) {
      return {
        isValid: false,
        error: error instanceof Error ? error.message : 'File validation failed',
      };
    }
  }
}
