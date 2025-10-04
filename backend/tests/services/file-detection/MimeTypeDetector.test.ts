import { MimeTypeDetector } from '../../../src/services/file-detection/MimeTypeDetector';
import { FileType } from '../../../src/types';

describe('MimeTypeDetector', () => {
  let detector: MimeTypeDetector;

  beforeEach(() => {
    detector = new MimeTypeDetector();
  });

  describe('detectByMimeType', () => {
    describe('Text detection', () => {
      it('should detect text/plain as TEXT', () => {
        const result = detector.detectByMimeType('text/plain');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect text/markdown as TEXT', () => {
        const result = detector.detectByMimeType('text/markdown');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect text/x-markdown as TEXT', () => {
        const result = detector.detectByMimeType('text/x-markdown');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect application/x-markdown as TEXT', () => {
        const result = detector.detectByMimeType('application/x-markdown');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect generic text/* MIME types as TEXT', () => {
        const result = detector.detectByMimeType('text/html');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect text/csv as TEXT', () => {
        const result = detector.detectByMimeType('text/csv');
        expect(result).toBe(FileType.TEXT);
      });
    });

    describe('Audio detection', () => {
      it('should detect audio/mpeg as AUDIO', () => {
        const result = detector.detectByMimeType('audio/mpeg');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/mp3 as AUDIO', () => {
        const result = detector.detectByMimeType('audio/mp3');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/wav as AUDIO', () => {
        const result = detector.detectByMimeType('audio/wav');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/x-wav as AUDIO', () => {
        const result = detector.detectByMimeType('audio/x-wav');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/wave as AUDIO', () => {
        const result = detector.detectByMimeType('audio/wave');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/aiff as AUDIO', () => {
        const result = detector.detectByMimeType('audio/aiff');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/x-aiff as AUDIO', () => {
        const result = detector.detectByMimeType('audio/x-aiff');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/flac as AUDIO', () => {
        const result = detector.detectByMimeType('audio/flac');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/ogg as AUDIO', () => {
        const result = detector.detectByMimeType('audio/ogg');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/m4a as AUDIO', () => {
        const result = detector.detectByMimeType('audio/m4a');
        expect(result).toBe(FileType.AUDIO);
      });


      it('should detect audio/mp4 as AUDIO', () => {
        const result = detector.detectByMimeType('audio/mp4');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect audio/webm as AUDIO', () => {
        const result = detector.detectByMimeType('audio/webm');
        expect(result).toBe(FileType.AUDIO);
      });
    });

    describe('Unknown MIME types', () => {
      it('should return null for application/octet-stream', () => {
        const result = detector.detectByMimeType('application/octet-stream');
        expect(result).toBeNull();
      });

      it('should return null for application/json', () => {
        const result = detector.detectByMimeType('application/json');
        expect(result).toBeNull();
      });

      it('should return null for image/png', () => {
        const result = detector.detectByMimeType('image/png');
        expect(result).toBeNull();
      });

      it('should return null for video/mp4', () => {
        const result = detector.detectByMimeType('video/mp4');
        expect(result).toBeNull();
      });

      it('should return null for unknown MIME type', () => {
        const result = detector.detectByMimeType('application/custom');
        expect(result).toBeNull();
      });
    });
  });
});
