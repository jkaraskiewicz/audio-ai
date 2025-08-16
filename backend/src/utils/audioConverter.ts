import { exec } from 'child_process';
import { promisify } from 'util';
import { tmpdir } from 'os';
import { join } from 'path';
import { writeFileSync, readFileSync, unlinkSync } from 'fs';
import { logger } from './logger';

const execAsync = promisify(exec);

/**
 * Audio conversion utility for better Whisper compatibility
 * Uncle Bob Approved: Single responsibility for audio format conversion
 */
export class AudioConverter {
  private static readonly SUPPORTED_INPUT_FORMATS = [
    'm4a', 'aac', 'wav', 'flac', 'ogg', 'mp3', 'wma'
  ];

  private static readonly OPTIMAL_OUTPUT_FORMAT = 'mp3';
  private static readonly OPTIMAL_SAMPLE_RATE = 16000;
  private static readonly OPTIMAL_CHANNELS = 1; // mono

  /**
   * Check if audio format conversion might be beneficial for Whisper
   */
  static shouldConvert(filename: string, mimetype?: string): boolean {
    const extension = this.getFileExtension(filename);
    
    // Convert m4a, aac, and other formats that may cause issues
    const problematicFormats = ['m4a', 'aac', 'wma', 'flac'];
    return problematicFormats.includes(extension.toLowerCase());
  }

  /**
   * Convert audio file to optimal format for Whisper processing
   */
  static async convertForWhisper(
    fileBuffer: Buffer, 
    originalFilename: string
  ): Promise<{ buffer: Buffer; filename: string; mimetype: string }> {
    const inputExtension = this.getFileExtension(originalFilename);
    
    if (!this.SUPPORTED_INPUT_FORMATS.includes(inputExtension.toLowerCase())) {
      throw new Error(`Unsupported audio format: ${inputExtension}`);
    }

    const tempDir = tmpdir();
    const inputPath = join(tempDir, `input_${Date.now()}.${inputExtension}`);
    const outputPath = join(tempDir, `output_${Date.now()}.${this.OPTIMAL_OUTPUT_FORMAT}`);

    try {
      logger.debug('Starting audio conversion for Whisper compatibility', {
        originalFormat: inputExtension,
        targetFormat: this.OPTIMAL_OUTPUT_FORMAT,
        sampleRate: this.OPTIMAL_SAMPLE_RATE,
        channels: this.OPTIMAL_CHANNELS,
      });

      // Write input buffer to temporary file
      writeFileSync(inputPath, fileBuffer);

      // Convert using ffmpeg with optimal settings for Whisper
      const ffmpegCommand = [
        'ffmpeg',
        '-i', `"${inputPath}"`,
        '-ar', this.OPTIMAL_SAMPLE_RATE.toString(), // Sample rate
        '-ac', this.OPTIMAL_CHANNELS.toString(),     // Mono
        '-acodec', 'mp3',                            // MP3 codec
        '-ab', '128k',                               // Bitrate
        '-f', 'mp3',                                 // Force MP3 format
        '-y',                                        // Overwrite output
        `"${outputPath}"`
      ].join(' ');

      logger.debug('Executing ffmpeg conversion', { command: ffmpegCommand });

      const { stdout, stderr } = await execAsync(ffmpegCommand);
      
      if (stderr && stderr.includes('Error')) {
        throw new Error(`FFmpeg conversion error: ${stderr}`);
      }

      // Read converted file
      const convertedBuffer = readFileSync(outputPath);
      const convertedFilename = originalFilename.replace(
        `.${inputExtension}`, 
        `.${this.OPTIMAL_OUTPUT_FORMAT}`
      );

      logger.info('Audio conversion completed successfully', {
        originalSize: fileBuffer.length,
        convertedSize: convertedBuffer.length,
        originalFormat: inputExtension,
        convertedFormat: this.OPTIMAL_OUTPUT_FORMAT,
      });

      return {
        buffer: convertedBuffer,
        filename: convertedFilename,
        mimetype: 'audio/mpeg',
      };

    } catch (error) {
      logger.error('Audio conversion failed', {
        originalFormat: inputExtension,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw new Error(`Audio conversion failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      // Clean up temporary files
      try {
        unlinkSync(inputPath);
        unlinkSync(outputPath);
      } catch (cleanupError) {
        logger.warn('Failed to cleanup temporary files', {
          inputPath,
          outputPath,
          error: cleanupError instanceof Error ? cleanupError.message : 'Unknown error',
        });
      }
    }
  }

  /**
   * Get file extension from filename
   */
  private static getFileExtension(filename: string): string {
    const lastDot = filename.lastIndexOf('.');
    return lastDot !== -1 ? filename.substring(lastDot + 1) : '';
  }

  /**
   * Check if ffmpeg is available in the system
   */
  static async isFFmpegAvailable(): Promise<boolean> {
    try {
      await execAsync('ffmpeg -version');
      return true;
    } catch {
      return false;
    }
  }
}