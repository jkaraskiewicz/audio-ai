import { FileType } from '../types';
import { logger } from '../utils/logger';

export class FileTypeDetectionService {
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

  private readonly textExtensions = ['.txt', '.md', '.markdown', '.text'];
  private readonly audioExtensions = [
    '.mp3',
    '.wav',
    '.wave',
    '.aiff',
    '.aif',
    '.ogg',
    '.flac',
    '.m4a',
    '.mp4',
    '.webm',
  ];

  private readonly binaryExtensions = [
    '.exe',
    '.bin',
    '.dll',
    '.so',
    '.dylib',
    '.app',
    '.zip',
    '.rar',
    '.7z',
    '.tar',
    '.gz',
    '.jpg',
    '.jpeg',
    '.png',
    '.gif',
    '.bmp',
    '.tiff',
    '.pdf',
    '.doc',
    '.docx',
    '.xls',
    '.xlsx',
    '.ppt',
    '.pptx',
    '.dmg',
    '.iso',
    '.img',
  ];

  detectFileType(file: Express.Multer.File): FileType {
    logger.debug('Detecting file type', {
      filename: file.originalname,
      mimetype: file.mimetype,
      size: file.size,
    });

    const extension = this.getFileExtension(file.originalname);

    // First check for known binary extensions that should be rejected
    if (this.isBinaryExtension(extension)) {
      logger.debug('File detected as binary by extension', { extension });
      return FileType.UNKNOWN;
    }

    // Second, check by MIME type
    if (this.textMimeTypes.includes(file.mimetype)) {
      logger.debug('File detected as text by MIME type', { mimetype: file.mimetype });
      return FileType.TEXT;
    }

    if (this.audioMimeTypes.includes(file.mimetype)) {
      logger.debug('File detected as audio by MIME type', { mimetype: file.mimetype });
      return FileType.AUDIO;
    }

    // If MIME type is not conclusive, check by file extension
    if (this.isTextExtension(extension)) {
      logger.debug('File detected as text by extension', { extension });
      return FileType.TEXT;
    }

    if (this.isAudioExtension(extension)) {
      logger.debug('File detected as audio by extension', { extension });
      return FileType.AUDIO;
    }

    // If still unclear, check if it's a generic text file
    if (file.mimetype.startsWith('text/')) {
      logger.debug('File detected as text by generic MIME type', { mimetype: file.mimetype });
      return FileType.TEXT;
    }

    // Try to detect by content (for cases where MIME type is wrong)
    if (this.looksLikeTextContent(file.buffer)) {
      logger.debug('File detected as text by content analysis');
      return FileType.TEXT;
    }

    logger.warn('Unable to determine file type, marking as unknown', {
      filename: file.originalname,
      mimetype: file.mimetype,
    });

    return FileType.UNKNOWN;
  }

  private getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : filename.substring(lastDotIndex).toLowerCase();
  }

  private looksLikeTextContent(buffer: Buffer): boolean {
    // Simple heuristic: check if the first 1024 bytes contain mostly printable characters
    const sample = buffer.subarray(0, Math.min(1024, buffer.length));
    let printableCount = 0;
    const totalCount = sample.length;

    // If file is very small, be more strict
    if (totalCount < 50) {
      return false;
    }

    for (let i = 0; i < sample.length; i++) {
      const byte = sample[i];
      // Consider ASCII printable characters (32-126) and common whitespace (9, 10, 13)
      if ((byte >= 32 && byte <= 126) || byte === 9 || byte === 10 || byte === 13) {
        printableCount++;
      }
    }

    // If more than 80% are printable characters, consider it text
    const printableRatio = printableCount / totalCount;
    return printableRatio > 0.8;
  }

  isValidFileSize(file: Express.Multer.File): boolean {
    const maxFileSize = 50 * 1024 * 1024; // 50MB
    return file.size <= maxFileSize;
  }

  getSupportedFormats(): { text: string[]; audio: string[] } {
    return {
      text: this.textExtensions,
      audio: this.audioExtensions,
    };
  }

  private isTextExtension(extension: string): boolean {
    return this.textExtensions.includes(extension);
  }

  private isAudioExtension(extension: string): boolean {
    return this.audioExtensions.includes(extension);
  }

  private isBinaryExtension(extension: string): boolean {
    return this.binaryExtensions.includes(extension);
  }
}
