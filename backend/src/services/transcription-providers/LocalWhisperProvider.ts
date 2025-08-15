import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult, FileType } from '../../types';
import { logger } from '../../utils/logger';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

/**
 * Local Whisper transcription provider using OpenAI's whisper Python package
 * Requires: pip install openai-whisper
 * Completely free, runs locally, no API keys needed
 */
export class LocalWhisperProvider implements AudioTranscriptionProvider {
  private isConfigured = false;

  constructor() {
    // Check if whisper is available
    this.checkWhisperAvailability();
  }

  private async checkWhisperAvailability(): Promise<void> {
    try {
      const result = await this.runCommand('whisper', ['--help']);
      this.isConfigured = result.success;
      if (this.isConfigured) {
        logger.info('Local Whisper is available and configured');
      } else {
        logger.warn('Local Whisper not found. Install with: pip install openai-whisper');
      }
    } catch (error) {
      logger.warn('Local Whisper check failed', { error });
      this.isConfigured = false;
    }
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    if (!this.isConfigured) {
      throw new Error('Local Whisper is not installed. Run: pip install openai-whisper');
    }

    logger.info('Starting local Whisper transcription', {
      filename: file.originalname,
      size: file.size,
    });

    // Create temp file
    const tempDir = '/tmp';
    const tempFilePath = path.join(tempDir, `whisper_${Date.now()}_${file.originalname}`);
    const outputDir = path.join(tempDir, `whisper_output_${Date.now()}`);

    try {
      // Write audio file to temp location
      fs.writeFileSync(tempFilePath, file.buffer);
      fs.mkdirSync(outputDir, { recursive: true });

      // Run whisper command
      const args = [
        tempFilePath,
        '--output_dir', outputDir,
        '--output_format', 'txt',
        '--model', 'base', // Use base model for speed
        '--language', 'en'
      ];

      logger.debug('Running whisper command', { args });
      const result = await this.runCommand('whisper', args, 120000); // 2 minute timeout

      if (!result.success) {
        throw new Error(`Whisper command failed: ${result.error}`);
      }

      // Read the transcription result
      const baseFilename = path.parse(file.originalname).name;
      const transcriptPath = path.join(outputDir, `${baseFilename}.txt`);
      
      if (!fs.existsSync(transcriptPath)) {
        throw new Error('Whisper transcription file not found');
      }

      const transcription = fs.readFileSync(transcriptPath, 'utf-8').trim();

      if (!transcription || transcription.length === 0) {
        throw new Error('Empty transcription result from Whisper');
      }

      // Create preview for logging
      const transcriptionPreview = transcription.length > 200 
        ? transcription.substring(0, 200) + '...'
        : transcription;

      logger.info('Local Whisper transcription completed', {
        filename: file.originalname,
        transcriptionLength: transcription.length,
        transcriptionPreview,
      });

      // Log full transcription for debugging
      logger.debug('Local Whisper full transcription result', {
        filename: file.originalname,
        fullTranscription: transcription,
      });

      return {
        extractedText: transcription,
        fileType: FileType.AUDIO,
        processingMethod: 'local_whisper_base',
      };
    } catch (error) {
      logger.error('Local Whisper transcription failed', {
        filename: file.originalname,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw error;
    } finally {
      // Cleanup temp files
      try {
        if (fs.existsSync(tempFilePath)) {
          fs.unlinkSync(tempFilePath);
        }
        if (fs.existsSync(outputDir)) {
          fs.rmSync(outputDir, { recursive: true, force: true });
        }
      } catch (cleanupError) {
        logger.warn('Failed to cleanup temp files', { cleanupError });
      }
    }
  }

  private runCommand(command: string, args: string[], timeout = 60000): Promise<{ success: boolean; output: string; error: string }> {
    return new Promise((resolve) => {
      const child = spawn(command, args);
      let stdout = '';
      let stderr = '';
      let finished = false;

      const timeoutId = setTimeout(() => {
        if (!finished) {
          finished = true;
          child.kill();
          resolve({ success: false, output: stdout, error: `Command timeout after ${timeout}ms` });
        }
      }, timeout);

      child.stdout.on('data', (data) => {
        stdout += data.toString();
      });

      child.stderr.on('data', (data) => {
        stderr += data.toString();
      });

      child.on('close', (code) => {
        if (!finished) {
          finished = true;
          clearTimeout(timeoutId);
          resolve({
            success: code === 0,
            output: stdout,
            error: stderr || (code !== 0 ? `Process exited with code ${code}` : '')
          });
        }
      });

      child.on('error', (error) => {
        if (!finished) {
          finished = true;
          clearTimeout(timeoutId);
          resolve({ success: false, output: stdout, error: error.message });
        }
      });
    });
  }

  getSupportedFormats(): string[] {
    return ['wav', 'mp3', 'ogg', 'flac', 'm4a', 'mp4', 'webm', 'aiff'];
  }

  getMaxFileSize(): number {
    return 100 * 1024 * 1024; // 100MB - generous for local processing
  }

  getProviderName(): string {
    return 'Local Whisper (OpenAI)';
  }

  isReady(): boolean {
    return this.isConfigured;
  }
}