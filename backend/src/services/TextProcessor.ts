import { TranscriptText } from '../domain/TranscriptText';
import { ProcessingResult } from '../domain/ProcessingResult';
import { FileType } from '../types';
import { logger } from '../utils/logger';

/**
 * Single Responsibility: Process text content for AI analysis
 * Uncle Bob Approved: Focused service with single purpose
 */
export interface ITextProcessor {
  processTranscriptText(transcriptText: TranscriptText): Promise<ProcessingResult>;
}

export class TextProcessor implements ITextProcessor {
  async processTranscriptText(transcriptText: TranscriptText): Promise<ProcessingResult> {
    logger.debug('Starting text processing', {
      textLength: transcriptText.getLength(),
      wordCount: transcriptText.getWordCount(),
      hasActionItems: transcriptText.isLikelyActionItem(),
    });

    // Create processing result for text input
    const processingResult = ProcessingResult.create(
      transcriptText.getText(),
      FileType.TEXT,
      'direct_text_input'
    );

    logger.info('Text processing completed', {
      textLength: processingResult.getTextLength(),
      wordCount: processingResult.getWordCount(),
      containsActionItems: processingResult.containsActionItems(),
    });

    return processingResult;
  }
}
