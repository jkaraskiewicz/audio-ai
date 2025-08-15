import { FileTypeDetectionService } from '../../src/services/FileTypeDetectionService';
import { FileType } from '../../src/types';

describe('FileTypeDetectionService', () => {
  let service: FileTypeDetectionService;

  beforeEach(() => {
    service = new FileTypeDetectionService();
  });

  describe('detectFileType', () => {
    it('should detect text files by MIME type', () => {
      const mockFile = {
        originalname: 'test.txt',
        mimetype: 'text/plain',
        size: 100,
        buffer: Buffer.from('Hello world'),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.TEXT);
    });

    it('should detect markdown files by MIME type', () => {
      const mockFile = {
        originalname: 'test.md',
        mimetype: 'text/markdown',
        size: 100,
        buffer: Buffer.from('# Hello'),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.TEXT);
    });

    it('should detect audio files by MIME type', () => {
      const mockFile = {
        originalname: 'test.mp3',
        mimetype: 'audio/mpeg',
        size: 1000000,
        buffer: Buffer.alloc(1000000),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.AUDIO);
    });

    it('should detect text files by extension when MIME type is unclear', () => {
      const mockFile = {
        originalname: 'test.txt',
        mimetype: 'application/octet-stream',
        size: 100,
        buffer: Buffer.from('Hello world'),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.TEXT);
    });

    it('should detect audio files by extension when MIME type is unclear', () => {
      const mockFile = {
        originalname: 'test.wav',
        mimetype: 'application/octet-stream',
        size: 1000000,
        buffer: Buffer.alloc(1000000),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.AUDIO);
    });

    it('should detect text content by analysis', () => {
      const mockFile = {
        originalname: 'unknown',
        mimetype: 'application/octet-stream',
        size: 100,
        buffer: Buffer.from('This is clearly readable text content with normal characters.'),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.TEXT);
    });

    it('should return unknown for unrecognizable files', () => {
      const mockFile = {
        originalname: 'binary.bin',
        mimetype: 'application/octet-stream',
        size: 100,
        buffer: Buffer.from([0x00, 0x01, 0x02, 0xff, 0xfe, 0xfd]),
      } as Express.Multer.File;

      const result = service.detectFileType(mockFile);
      expect(result).toBe(FileType.UNKNOWN);
    });
  });

  describe('isValidFileSize', () => {
    it('should accept files under 50MB', () => {
      const mockFile = {
        size: 10 * 1024 * 1024, // 10MB
      } as Express.Multer.File;

      const result = service.isValidFileSize(mockFile);
      expect(result).toBe(true);
    });

    it('should reject files over 50MB', () => {
      const mockFile = {
        size: 100 * 1024 * 1024, // 100MB
      } as Express.Multer.File;

      const result = service.isValidFileSize(mockFile);
      expect(result).toBe(false);
    });
  });

  describe('getSupportedFormats', () => {
    it('should return supported formats', () => {
      const formats = service.getSupportedFormats();

      expect(formats.text).toContain('.txt');
      expect(formats.text).toContain('.md');
      expect(formats.audio).toContain('.mp3');
      expect(formats.audio).toContain('.wav');
    });
  });
});