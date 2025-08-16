/**
 * Value Object: Represents an audio file with validation and metadata
 * Uncle Bob Approved: Encapsulates file validation and metadata extraction
 */
export class AudioFile {
  private static readonly SUPPORTED_FORMATS = [
    'audio/mpeg',
    'audio/mp3',
    'audio/wav',
    'audio/ogg',
    'audio/flac',
    'audio/mp4',
    'audio/m4a',
    'audio/webm',
    'audio/aiff',
  ];

  private static readonly MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

  private constructor(
    private readonly originalName: string,
    private readonly mimeType: string,
    private readonly sizeInBytes: number,
    private readonly buffer: Buffer
  ) {
    this.validateAudioFile();
  }

  static fromMulterFile(file: Express.Multer.File): AudioFile {
    return new AudioFile(file.originalname, file.mimetype, file.size, file.buffer);
  }

  getOriginalName(): string {
    return this.originalName;
  }

  getMimeType(): string {
    return this.mimeType;
  }

  getSizeInBytes(): number {
    return this.sizeInBytes;
  }

  getBuffer(): Buffer {
    return this.buffer;
  }

  getFileExtension(): string {
    const lastDotIndex = this.originalName.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : this.originalName.substring(lastDotIndex);
  }

  getSizeInMB(): number {
    return this.sizeInBytes / (1024 * 1024);
  }

  isValidFormat(): boolean {
    return AudioFile.SUPPORTED_FORMATS.includes(this.mimeType);
  }

  isWithinSizeLimit(): boolean {
    return this.sizeInBytes <= AudioFile.MAX_FILE_SIZE;
  }

  isEmpty(): boolean {
    return this.sizeInBytes === 0;
  }

  createMetadata(): AudioFileMetadata {
    return {
      originalName: this.originalName,
      mimeType: this.mimeType,
      sizeInBytes: this.sizeInBytes,
      sizeInMB: this.getSizeInMB(),
      extension: this.getFileExtension(),
      isValidFormat: this.isValidFormat(),
      isWithinSizeLimit: this.isWithinSizeLimit(),
    };
  }

  generateUniqueFilename(): string {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const extension = this.getFileExtension();
    const baseName = this.originalName.replace(extension, '');

    return `${baseName}-${timestamp}${extension}`;
  }

  private validateAudioFile(): void {
    if (this.isEmpty()) {
      throw new Error('Audio file cannot be empty');
    }

    if (!this.isValidFormat()) {
      throw new Error(
        `Unsupported audio format: ${this.mimeType}. Supported formats: ${AudioFile.SUPPORTED_FORMATS.join(', ')}`
      );
    }

    if (!this.isWithinSizeLimit()) {
      throw new Error(
        `File size ${this.getSizeInMB().toFixed(2)}MB exceeds maximum allowed size of ${AudioFile.MAX_FILE_SIZE / (1024 * 1024)}MB`
      );
    }
  }
}

export interface AudioFileMetadata {
  readonly originalName: string;
  readonly mimeType: string;
  readonly sizeInBytes: number;
  readonly sizeInMB: number;
  readonly extension: string;
  readonly isValidFormat: boolean;
  readonly isWithinSizeLimit: boolean;
}
