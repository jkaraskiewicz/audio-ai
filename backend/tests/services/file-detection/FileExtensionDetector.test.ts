import { FileExtensionDetector } from '../../../src/services/file-detection/FileExtensionDetector';
import { FileType } from '../../../src/types';

describe('FileExtensionDetector', () => {
  let detector: FileExtensionDetector;

  beforeEach(() => {
    detector = new FileExtensionDetector();
  });

  describe('extractExtension', () => {
    it('should extract .txt extension', () => {
      const extension = detector.extractExtension('document.txt');
      expect(extension).toBe('.txt');
    });

    it('should extract .md extension', () => {
      const extension = detector.extractExtension('readme.md');
      expect(extension).toBe('.md');
    });

    it('should extract extension with uppercase', () => {
      const extension = detector.extractExtension('FILE.TXT');
      expect(extension).toBe('.txt');
    });

    it('should handle files with no extension', () => {
      const extension = detector.extractExtension('makefile');
      expect(extension).toBe('');
    });

    it('should handle files with multiple dots', () => {
      const extension = detector.extractExtension('archive.tar.gz');
      expect(extension).toBe('.gz');
    });

    it('should handle hidden files with extension', () => {
      const extension = detector.extractExtension('.gitignore');
      expect(extension).toBe('.gitignore');
    });
  });

  describe('detectByExtension', () => {
    describe('Text file detection', () => {
      it('should detect .txt as TEXT', () => {
        const result = detector.detectByExtension('document.txt');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect .md as TEXT', () => {
        const result = detector.detectByExtension('readme.md');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect .markdown as TEXT', () => {
        const result = detector.detectByExtension('notes.markdown');
        expect(result).toBe(FileType.TEXT);
      });

      it('should detect .text as TEXT', () => {
        const result = detector.detectByExtension('file.text');
        expect(result).toBe(FileType.TEXT);
      });

      it('should be case-insensitive for text files', () => {
        const result = detector.detectByExtension('FILE.TXT');
        expect(result).toBe(FileType.TEXT);
      });
    });

    describe('Audio file detection', () => {
      it('should detect .mp3 as AUDIO', () => {
        const result = detector.detectByExtension('song.mp3');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .wav as AUDIO', () => {
        const result = detector.detectByExtension('recording.wav');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .wave as AUDIO', () => {
        const result = detector.detectByExtension('sound.wave');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .aiff as AUDIO', () => {
        const result = detector.detectByExtension('track.aiff');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .aif as AUDIO', () => {
        const result = detector.detectByExtension('audio.aif');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .flac as AUDIO', () => {
        const result = detector.detectByExtension('lossless.flac');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .ogg as AUDIO', () => {
        const result = detector.detectByExtension('voice.ogg');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .m4a as AUDIO', () => {
        const result = detector.detectByExtension('podcast.m4a');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should detect .webm as AUDIO', () => {
        const result = detector.detectByExtension('recording.webm');
        expect(result).toBe(FileType.AUDIO);
      });

      it('should be case-insensitive for audio files', () => {
        const result = detector.detectByExtension('SONG.MP3');
        expect(result).toBe(FileType.AUDIO);
      });
    });

    describe('Binary file detection', () => {
      it('should detect .exe as UNKNOWN', () => {
        const result = detector.detectByExtension('program.exe');
        expect(result).toBe(FileType.UNKNOWN);
      });

      it('should detect .bin as UNKNOWN', () => {
        const result = detector.detectByExtension('data.bin');
        expect(result).toBe(FileType.UNKNOWN);
      });

      it('should detect .dll as UNKNOWN', () => {
        const result = detector.detectByExtension('library.dll');
        expect(result).toBe(FileType.UNKNOWN);
      });

      it('should detect .so as UNKNOWN', () => {
        const result = detector.detectByExtension('lib.so');
        expect(result).toBe(FileType.UNKNOWN);
      });

      it('should detect .dylib as UNKNOWN', () => {
        const result = detector.detectByExtension('framework.dylib');
        expect(result).toBe(FileType.UNKNOWN);
      });
    });

    describe('Unknown extensions', () => {
      it('should return null for .json', () => {
        const result = detector.detectByExtension('data.json');
        expect(result).toBeNull();
      });

      it('should return null for .png', () => {
        const result = detector.detectByExtension('image.png');
        expect(result).toBeNull();
      });

      it('should return null for unknown extension', () => {
        const result = detector.detectByExtension('file.xyz');
        expect(result).toBeNull();
      });

      it('should return null for file with no extension', () => {
        const result = detector.detectByExtension('makefile');
        expect(result).toBeNull();
      });
    });
  });

  describe('getSupportedFormats', () => {
    it('should return text formats', () => {
      const formats = detector.getSupportedFormats();

      expect(formats.text).toContain('.txt');
      expect(formats.text).toContain('.md');
      expect(formats.text).toContain('.markdown');
      expect(formats.text).toContain('.text');
    });

    it('should return audio formats', () => {
      const formats = detector.getSupportedFormats();

      expect(formats.audio).toContain('.mp3');
      expect(formats.audio).toContain('.wav');
      expect(formats.audio).toContain('.m4a');
      expect(formats.audio).toContain('.flac');
      expect(formats.audio).toContain('.ogg');
    });
  });
});
