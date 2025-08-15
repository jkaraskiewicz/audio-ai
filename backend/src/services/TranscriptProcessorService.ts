import { AIService } from './AIService';
import { FileService } from './FileService';
import { FileProcessorService } from './FileProcessorService';
import { ProcessTranscriptResponse, ProcessFileRequest } from '../types';
import { logger } from '../utils/logger';

export class TranscriptProcessorService {
  constructor(
    private aiService: AIService,
    private fileService: FileService,
    private fileProcessorService: FileProcessorService
  ) {}

  async processTranscript(transcript: string): Promise<ProcessTranscriptResponse> {
    if (!transcript || transcript.trim().length === 0) {
      throw new Error('Transcript is required and cannot be empty');
    }

    logger.info('Starting transcript processing', { transcriptLength: transcript.length });

    try {
      // Step 1: Process with AI
      const structuredData = await this.aiService.processTranscript(transcript);

      // Step 2: Save to file
      const savedPath = this.fileService.saveMarkdownFile(structuredData);

      logger.info('Transcript processing completed successfully', { savedPath });

      return {
        result: structuredData,
        saved_to: savedPath,
        message: `Idea processed and saved to ${savedPath}`,
      };
    } catch (error) {
      logger.error('Transcript processing failed', error);
      throw error;
    }
  }

  async processFileOrTranscript(request: ProcessFileRequest): Promise<ProcessTranscriptResponse> {
    const { file, transcript } = request;

    logger.info('Starting file or transcript processing', {
      hasFile: !!file,
      hasTranscript: !!transcript,
      filename: file?.originalname,
      fileSize: file?.size,
    });

    try {
      let textToProcess: string;
      let processingSource: string;

      if (file) {
        // Step 1: Extract text from file
        const fileResult = await this.fileProcessorService.processFile(file);
        textToProcess = fileResult.extractedText;
        processingSource = `${fileResult.processingMethod} from file: ${file.originalname}`;

        logger.info('File processed successfully, extracted text for AI processing', {
          filename: file.originalname,
          fileType: fileResult.fileType,
          method: fileResult.processingMethod,
          extractedLength: textToProcess.length,
        });
      } else if (transcript) {
        // Use provided transcript directly
        textToProcess = transcript.trim();
        processingSource = 'direct text input';
      } else {
        throw new Error('Either file or transcript must be provided');
      }

      // Step 2: Process with AI (same as before)
      const structuredData = await this.aiService.processTranscript(textToProcess);

      // Step 3: Save to file
      const savedPath = this.fileService.saveMarkdownFile(structuredData);

      logger.info('File or transcript processing completed successfully', {
        savedPath,
        processingSource,
      });

      return {
        result: structuredData,
        saved_to: savedPath,
        message: `Idea processed (${processingSource}) and saved to ${savedPath}`,
      };
    } catch (error) {
      logger.error('File or transcript processing failed', error);
      throw error;
    }
  }
}
