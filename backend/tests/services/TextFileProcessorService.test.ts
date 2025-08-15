import { TextFileProcessorService } from '../../src/services/TextFileProcessorService';
import { FileType } from '../../src/types';

describe('TextFileProcessorService', () => {
  let service: TextFileProcessorService;

  beforeEach(() => {
    service = new TextFileProcessorService();
  });

  describe('processTextFile', () => {
    it('should process a text file successfully', async () => {
      const content = 'This is a sample text file with some content about productivity.';
      const mockFile = {
        originalname: 'test.txt',
        mimetype: 'text/plain',
        size: content.length,
        buffer: Buffer.from(content),
      } as Express.Multer.File;

      const result = await service.processTextFile(mockFile);

      expect(result.extractedText).toBe(content);
      expect(result.fileType).toBe(FileType.TEXT);
      expect(result.processingMethod).toBe('direct_text_extraction');
    });

    it('should process a markdown file successfully', async () => {
      const content = '# Project Ideas\n\nHere are some ideas for new projects:\n\n- Mobile app\n- Web service';
      const mockFile = {
        originalname: 'notes.md',
        mimetype: 'text/markdown',
        size: content.length,
        buffer: Buffer.from(content),
      } as Express.Multer.File;

      const result = await service.processTextFile(mockFile);

      expect(result.extractedText).toBe(content);
      expect(result.fileType).toBe(FileType.TEXT);
      expect(result.processingMethod).toBe('direct_text_extraction');
    });

    it('should handle UTF-8 encoded files', async () => {
      const content = 'Text with émojis 🚀 and special characters: café, naïve, résumé';
      const mockFile = {
        originalname: 'unicode.txt',
        mimetype: 'text/plain',
        size: Buffer.byteLength(content, 'utf8'),
        buffer: Buffer.from(content, 'utf8'),
      } as Express.Multer.File;

      const result = await service.processTextFile(mockFile);

      expect(result.extractedText).toBe(content);
    });

    it('should trim whitespace from extracted text', async () => {
      const content = '   \n\n  Some text with extra whitespace  \n\n   ';
      const expectedText = 'Some text with extra whitespace';
      const mockFile = {
        originalname: 'whitespace.txt',
        mimetype: 'text/plain',
        size: content.length,
        buffer: Buffer.from(content),
      } as Express.Multer.File;

      const result = await service.processTextFile(mockFile);

      expect(result.extractedText).toBe(expectedText);
    });

    it('should throw error for empty files', async () => {
      const mockFile = {
        originalname: 'empty.txt',
        mimetype: 'text/plain',
        size: 0,
        buffer: Buffer.from(''),
      } as Express.Multer.File;

      await expect(service.processTextFile(mockFile)).rejects.toThrow(
        'No readable text content found in file'
      );
    });

    it('should throw error for files with only whitespace', async () => {
      const content = '   \n\n\t   \n   ';
      const mockFile = {
        originalname: 'whitespace-only.txt',
        mimetype: 'text/plain',
        size: content.length,
        buffer: Buffer.from(content),
      } as Express.Multer.File;

      await expect(service.processTextFile(mockFile)).rejects.toThrow(
        'No readable text content found in file'
      );
    });
  });

  describe('getSupportedExtensions', () => {
    it('should return supported text extensions', () => {
      const extensions = service.getSupportedExtensions();

      expect(extensions).toContain('.txt');
      expect(extensions).toContain('.md');
      expect(extensions).toContain('.markdown');
      expect(extensions).toContain('.text');
    });
  });

  describe('getSupportedMimeTypes', () => {
    it('should return supported MIME types', () => {
      const mimeTypes = service.getSupportedMimeTypes();

      expect(mimeTypes).toContain('text/plain');
      expect(mimeTypes).toContain('text/markdown');
    });
  });
});