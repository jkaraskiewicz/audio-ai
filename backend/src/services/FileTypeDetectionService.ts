import { FileType } from '../types';
import { logger } from '../utils/logger';
import { MimeTypeDetector } from './file-detection/MimeTypeDetector';
import { FileExtensionDetector } from './file-detection/FileExtensionDetector';
import { ContentAnalyzer } from './file-detection/ContentAnalyzer';

/**
 * Orchestrates file type detection using multiple strategies
 * Follows Strategy Pattern and delegates to specialized detectors
 */
export class FileTypeDetectionService {
  private readonly mimeTypeDetector: MimeTypeDetector;
  private readonly extensionDetector: FileExtensionDetector;
  private readonly contentAnalyzer: ContentAnalyzer;

  constructor() {
    this.mimeTypeDetector = new MimeTypeDetector();
    this.extensionDetector = new FileExtensionDetector();
    this.contentAnalyzer = new ContentAnalyzer();
  }

  detectFileType(file: Express.Multer.File): FileType {
    logger.debug('Detecting file type', {
      filename: file.originalname,
      mimetype: file.mimetype,
      size: file.size,
    });

    // First check for known binary extensions that should be rejected
    const binaryCheck = this.extensionDetector.detectByExtension(file.originalname);
    if (binaryCheck === FileType.UNKNOWN) {
      return FileType.UNKNOWN;
    }

    // Second, check by MIME type
    const mimeTypeResult = this.mimeTypeDetector.detectByMimeType(file.mimetype);
    if (mimeTypeResult !== null) {
      return mimeTypeResult;
    }

    // If MIME type is not conclusive, check by file extension
    const extensionResult = this.extensionDetector.detectByExtension(file.originalname);
    if (extensionResult !== null && extensionResult !== FileType.UNKNOWN) {
      return extensionResult;
    }

    // Try to detect by content (for cases where MIME type is wrong)
    if (this.contentAnalyzer.looksLikeText(file.buffer)) {
      return FileType.TEXT;
    }

    logger.warn('Unable to determine file type, marking as unknown', {
      filename: file.originalname,
      mimetype: file.mimetype,
    });

    return FileType.UNKNOWN;
  }

  isValidFileSize(file: Express.Multer.File): boolean {
    return this.contentAnalyzer.isValidFileSize(file.size);
  }

  getSupportedFormats(): { text: string[]; audio: string[] } {
    return this.extensionDetector.getSupportedFormats();
  }
}
