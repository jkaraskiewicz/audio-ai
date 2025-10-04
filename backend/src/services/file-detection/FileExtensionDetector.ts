import { FileType } from '../../types';
import { logger } from '../../utils/logger';

/**
 * Detects file type based on file extension
 * Follows Single Responsibility Principle
 */
export class FileExtensionDetector {
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

  extractExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : filename.substring(lastDotIndex).toLowerCase();
  }

  detectByExtension(filename: string): FileType | null {
    const extension = this.extractExtension(filename);

    if (this.isBinaryExtension(extension)) {
      logger.debug('File detected as binary by extension', { extension });
      return FileType.UNKNOWN;
    }

    if (this.isTextExtension(extension)) {
      logger.debug('File detected as text by extension', { extension });
      return FileType.TEXT;
    }

    if (this.isAudioExtension(extension)) {
      logger.debug('File detected as audio by extension', { extension });
      return FileType.AUDIO;
    }

    return null;
  }

  isTextExtension(extension: string): boolean {
    return this.textExtensions.includes(extension);
  }

  isAudioExtension(extension: string): boolean {
    return this.audioExtensions.includes(extension);
  }

  isBinaryExtension(extension: string): boolean {
    return this.binaryExtensions.includes(extension);
  }

  getSupportedFormats(): { text: string[]; audio: string[] } {
    return {
      text: this.textExtensions,
      audio: this.audioExtensions,
    };
  }
}
