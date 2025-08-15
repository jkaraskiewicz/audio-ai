import { FileProcessingResult, FileType } from '../types';
import { logger } from '../utils/logger';

export class TextFileProcessorService {
  async processTextFile(file: Express.Multer.File): Promise<FileProcessingResult> {
    try {
      logger.debug('Processing text file', {
        filename: file.originalname,
        size: file.size,
        mimetype: file.mimetype,
      });

      // Convert buffer to string with proper encoding detection
      const extractedText = this.extractTextFromBuffer(file.buffer);

      // Validate that we extracted meaningful text
      if (!extractedText || extractedText.trim().length === 0) {
        throw new Error('No readable text content found in file');
      }

      // Check if the text looks like natural language
      if (!this.isNaturalLanguageText(extractedText)) {
        logger.warn('File content may not be natural language text', {
          filename: file.originalname,
          contentPreview: extractedText.substring(0, 100),
        });
      }

      logger.info('Successfully processed text file', {
        filename: file.originalname,
        extractedLength: extractedText.length,
      });

      return {
        extractedText: extractedText.trim(),
        fileType: FileType.TEXT,
        processingMethod: 'direct_text_extraction',
      };
    } catch (error) {
      logger.error('Failed to process text file', {
        filename: file.originalname,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw new Error(
        `Failed to process text file: ${error instanceof Error ? error.message : 'Unknown error'}`
      );
    }
  }

  private extractTextFromBuffer(buffer: Buffer): string {
    // Try UTF-8 first
    try {
      const utf8Text = buffer.toString('utf8');
      if (this.isValidUTF8(utf8Text)) {
        return utf8Text;
      }
    } catch (error) {
      logger.debug('UTF-8 decoding failed, trying other encodings');
    }

    // Fallback to Latin1 (covers most Western languages)
    try {
      const latin1Text = buffer.toString('latin1');
      return latin1Text;
    } catch (error) {
      logger.debug('Latin1 decoding failed, using ASCII');
    }

    // Final fallback to ASCII
    return buffer.toString('ascii');
  }

  private isValidUTF8(text: string): boolean {
    // Check for replacement characters that indicate invalid UTF-8
    return !text.includes('\uFFFD');
  }

  private isNaturalLanguageText(text: string): boolean {
    // Simple heuristics to check if text looks like natural language
    const trimmedText = text.trim();

    // Check minimum length
    if (trimmedText.length < 10) {
      return false;
    }

    // Check for reasonable word count
    const words = trimmedText.split(/\s+/);
    if (words.length < 3) {
      return false;
    }

    // Check for reasonable character distribution (letters vs other characters)
    const letterCount = (trimmedText.match(/[a-zA-Z]/g) || []).length;
    const letterRatio = letterCount / trimmedText.length;

    // Should have at least 40% letters for natural language
    if (letterRatio < 0.4) {
      return false;
    }

    // Check for excessive special characters (might indicate code or data files)
    const specialCharCount = (trimmedText.match(/[{}[\]()=<>|&;:@#$%^*]/g) || []).length;
    const specialCharRatio = specialCharCount / trimmedText.length;

    // If more than 20% special characters, probably not natural language
    if (specialCharRatio > 0.2) {
      return false;
    }

    return true;
  }

  getSupportedExtensions(): string[] {
    return ['.txt', '.md', '.markdown', '.text'];
  }

  getSupportedMimeTypes(): string[] {
    return ['text/plain', 'text/markdown', 'text/x-markdown', 'application/x-markdown'];
  }
}
