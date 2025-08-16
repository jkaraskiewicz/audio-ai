import { AudioFile } from '../domain/AudioFile';
import { ProcessingResult } from '../domain/ProcessingResult';
import { FileType } from '../types';
import { logger } from '../utils/logger';

/**
 * Single Responsibility: Process files and extract text content
 * Uncle Bob Approved: Focused service with clear interface
 */
export interface IFileProcessor {
  processAudioFile(audioFile: AudioFile): Promise<ProcessingResult>;
}

export class FileProcessor implements IFileProcessor {
  constructor(private audioTranscriptionService: IAudioTranscriptionService) {}

  async processAudioFile(audioFile: AudioFile): Promise<ProcessingResult> {
    logger.debug('Starting audio file processing', {
      filename: audioFile.getOriginalName(),
      size: audioFile.getSizeInBytes(),
      mimeType: audioFile.getMimeType(),
    });

    const multerFile = this.convertToMulterFile(audioFile);
    const transcriptionResult =
      await this.audioTranscriptionService.transcribeAudioFile(multerFile);

    const processingResult = ProcessingResult.create(
      transcriptionResult.extractedText,
      transcriptionResult.fileType,
      transcriptionResult.processingMethod
    );

    logger.info('Audio file processing completed', {
      filename: audioFile.getOriginalName(),
      processingMethod: processingResult.getProcessingMethod(),
      textLength: processingResult.getTextLength(),
      provider: processingResult.getProviderFromMethod(),
    });

    return processingResult;
  }

  private convertToMulterFile(audioFile: AudioFile): Express.Multer.File {
    // Convert our domain object back to Multer format for legacy compatibility
    return {
      originalname: audioFile.getOriginalName(),
      mimetype: audioFile.getMimeType(),
      size: audioFile.getSizeInBytes(),
      buffer: audioFile.getBuffer(),
      fieldname: 'file',
      encoding: '7bit',
      filename: audioFile.getOriginalName(),
      path: '',
      destination: '',
      stream: {} as any,
    };
  }
}

// Define interface for dependency injection
export interface IAudioTranscriptionService {
  transcribeAudioFile(file: Express.Multer.File): Promise<{
    extractedText: string;
    fileType: FileType;
    processingMethod: string;
  }>;
}
