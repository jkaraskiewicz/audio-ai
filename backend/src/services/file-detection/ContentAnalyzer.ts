import { logger } from '../../utils/logger';

/**
 * Analyzes file content to detect text files
 * Follows Single Responsibility Principle
 */
export class ContentAnalyzer {
  /**
   * Checks if file content looks like text based on printable characters
   */
  looksLikeText(buffer: Buffer): boolean {
    // Simple heuristic: check if the first 1024 bytes contain mostly printable characters
    const sample = buffer.subarray(0, Math.min(1024, buffer.length));
    const totalCount = sample.length;

    // If file is very small, be more strict
    if (totalCount < 50) {
      return false;
    }

    let printableCount = 0;

    for (let i = 0; i < sample.length; i++) {
      const byte = sample[i];
      // Consider ASCII printable characters (32-126) and common whitespace (9, 10, 13)
      if ((byte >= 32 && byte <= 126) || byte === 9 || byte === 10 || byte === 13) {
        printableCount++;
      }
    }

    // If more than 80% are printable characters, consider it text
    const printableRatio = printableCount / totalCount;
    const isText = printableRatio > 0.8;

    if (isText) {
      logger.debug('File detected as text by content analysis');
    }

    return isText;
  }

  /**
   * Validates file size is within acceptable limits
   */
  isValidFileSize(fileSize: number, maxSizeMB: number = 50): boolean {
    const maxFileSize = maxSizeMB * 1024 * 1024;
    return fileSize <= maxFileSize;
  }
}
