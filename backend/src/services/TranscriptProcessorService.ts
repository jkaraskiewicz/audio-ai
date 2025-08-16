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

    // Create preview for logging
    const transcriptPreview =
      transcript.length > 300 ? transcript.substring(0, 300) + '...' : transcript;

    logger.info('Starting transcript processing', {
      transcriptLength: transcript.length,
      transcriptPreview,
    });

    // Log full transcript for debugging
    logger.debug('Full transcript for AI processing', {
      fullTranscript: transcript,
    });

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

        // Create preview for logging
        const textPreview =
          textToProcess.length > 300 ? textToProcess.substring(0, 300) + '...' : textToProcess;

        logger.info('File processed successfully, extracted text for AI processing', {
          filename: file.originalname,
          fileType: fileResult.fileType,
          method: fileResult.processingMethod,
          extractedLength: textToProcess.length,
          textPreview,
        });

        // Log full extracted text for debugging
        logger.debug('Full extracted text ready for AI processing', {
          filename: file.originalname,
          method: fileResult.processingMethod,
          fullText: textToProcess,
        });
      } else if (transcript) {
        // Use provided transcript directly
        textToProcess = transcript.trim();
        processingSource = 'direct text input';

        // Log direct transcript input
        const textPreview =
          textToProcess.length > 300 ? textToProcess.substring(0, 300) + '...' : textToProcess;

        logger.info('Processing direct transcript input', {
          transcriptLength: textToProcess.length,
          textPreview,
        });

        logger.debug('Full direct transcript for AI processing', {
          fullText: textToProcess,
        });
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
