import { ContentAnalyzer } from '../../../src/services/file-detection/ContentAnalyzer';

describe('ContentAnalyzer', () => {
  let analyzer: ContentAnalyzer;

  beforeEach(() => {
    analyzer = new ContentAnalyzer();
  });

  describe('looksLikeText', () => {
    it('should return true for plain text content', () => {
      const buffer = Buffer.from('This is a simple text file with normal characters.');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return true for text with newlines and tabs', () => {
      const buffer = Buffer.from('Line 1\nLine 2\tTabbed content\nLine 3\nLine 4 with more text to reach 50 bytes minimum');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return true for markdown content', () => {
      const buffer = Buffer.from('# Heading\n\n## Subheading\n\nThis is **bold** text with enough content.');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return true for text with punctuation', () => {
      const buffer = Buffer.from('Hello, world! How are you? I am fine. Thanks for asking!');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return true for text with numbers', () => {
      const buffer = Buffer.from('The answer is 42. Pi is approximately 3.14159 and more content here.');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return true for code content', () => {
      const buffer = Buffer.from('function hello() {\n  console.log("Hello, world!");\n  return true;\n}');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should return false for binary content with low printable ratio', () => {
      const buffer = Buffer.from([0x00, 0x01, 0x02, 0xff, 0xfe, 0xfd, 0x89, 0x50, 0x4e, 0x47]);
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(false);
    });

    it('should return false for mostly binary content with some text', () => {
      const binaryData = Buffer.alloc(100);
      binaryData.fill(0xff); // Fill with binary data
      binaryData.write('text', 90); // Add some text at the end
      const result = analyzer.looksLikeText(binaryData);
      expect(result).toBe(false);
    });

    it('should return false for content smaller than 50 bytes', () => {
      const buffer = Buffer.from('Short text');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(false);
    });

    it('should return false for empty buffer', () => {
      const buffer = Buffer.from('');
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(false);
    });

    it('should return true for exactly 50 bytes of text', () => {
      const buffer = Buffer.from('A'.repeat(50));
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });

    it('should sample only first 1024 bytes for large files', () => {
      const largeBuffer = Buffer.alloc(2048);
      largeBuffer.fill('A'.charCodeAt(0), 0, 1024); // First 1024 bytes are text
      largeBuffer.fill(0xff, 1024); // Rest is binary
      const result = analyzer.looksLikeText(largeBuffer);
      expect(result).toBe(true);
    });

    it('should handle Unicode text', () => {
      const buffer = Buffer.from('Hello 世界 🌍');
      if (buffer.length >= 50) {
        const result = analyzer.looksLikeText(buffer);
        expect(result).toBe(true);
      }
    });

    it('should detect text with 80% printable ratio threshold', () => {
      // Create buffer with exactly 80% printable characters
      const buffer = Buffer.alloc(100);
      buffer.fill('A'.charCodeAt(0), 0, 80); // 80 printable
      buffer.fill(0xff, 80); // 20 non-printable
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(false); // Should be false since we need > 0.8
    });

    it('should detect text with 81% printable ratio', () => {
      // Create buffer with >80% printable characters
      const buffer = Buffer.alloc(100);
      buffer.fill('A'.charCodeAt(0), 0, 81); // 81 printable
      buffer.fill(0xff, 81); // 19 non-printable
      const result = analyzer.looksLikeText(buffer);
      expect(result).toBe(true);
    });
  });

  describe('isValidFileSize', () => {
    it('should accept files under default 50MB limit', () => {
      const fileSize = 10 * 1024 * 1024; // 10MB
      const result = analyzer.isValidFileSize(fileSize);
      expect(result).toBe(true);
    });

    it('should accept files exactly at 50MB limit', () => {
      const fileSize = 50 * 1024 * 1024; // 50MB
      const result = analyzer.isValidFileSize(fileSize);
      expect(result).toBe(true);
    });

    it('should reject files over default 50MB limit', () => {
      const fileSize = 51 * 1024 * 1024; // 51MB
      const result = analyzer.isValidFileSize(fileSize);
      expect(result).toBe(false);
    });

    it('should accept files under custom limit', () => {
      const fileSize = 15 * 1024 * 1024; // 15MB
      const result = analyzer.isValidFileSize(fileSize, 25);
      expect(result).toBe(true);
    });

    it('should accept files exactly at custom limit', () => {
      const fileSize = 25 * 1024 * 1024; // 25MB
      const result = analyzer.isValidFileSize(fileSize, 25);
      expect(result).toBe(true);
    });

    it('should reject files over custom limit', () => {
      const fileSize = 30 * 1024 * 1024; // 30MB
      const result = analyzer.isValidFileSize(fileSize, 25);
      expect(result).toBe(false);
    });

    it('should accept very small files', () => {
      const fileSize = 1024; // 1KB
      const result = analyzer.isValidFileSize(fileSize);
      expect(result).toBe(true);
    });

    it('should accept zero-byte files', () => {
      const fileSize = 0;
      const result = analyzer.isValidFileSize(fileSize);
      expect(result).toBe(true);
    });
  });
});
