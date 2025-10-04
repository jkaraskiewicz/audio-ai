import { FileType } from '../../types';
import { logger } from '../../utils/logger';

/**
 * Detects file type based on MIME type
 * Follows Single Responsibility Principle
 */
export class MimeTypeDetector {
  private readonly textMimeTypes = [
    'text/plain',
    'text/markdown',
    'text/x-markdown',
    'application/x-markdown',
  ];

  private readonly audioMimeTypes = [
    'audio/mpeg',
    'audio/mp3',
    'audio/wav',
    'audio/wave',
    'audio/x-wav',
    'audio/aiff',
    'audio/x-aiff',
    'audio/ogg',
    'audio/flac',
    'audio/x-flac',
    'audio/mp4',
    'audio/m4a',
    'audio/webm',
  ];

  detectByMimeType(mimetype: string): FileType | null {
    if (this.textMimeTypes.includes(mimetype)) {
      logger.debug('File detected as text by MIME type', { mimetype });
      return FileType.TEXT;
    }

    if (this.audioMimeTypes.includes(mimetype)) {
      logger.debug('File detected as audio by MIME type', { mimetype });
      return FileType.AUDIO;
    }

    // Check for generic text MIME type
    if (mimetype.startsWith('text/')) {
      logger.debug('File detected as text by generic MIME type', { mimetype });
      return FileType.TEXT;
    }

    return null;
  }

  isTextMimeType(mimetype: string): boolean {
    return this.textMimeTypes.includes(mimetype) || mimetype.startsWith('text/');
  }

  isAudioMimeType(mimetype: string): boolean {
    return this.audioMimeTypes.includes(mimetype);
  }
}
