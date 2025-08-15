/**
 * Local Whisper Audio Transcription Provider Example
 * 
 * This example shows how to implement a provider that uses
 * a local Whisper installation via Python subprocess.
 * Perfect for privacy-sensitive applications.
 */

import { AudioTranscriptionProvider } from '../../src/interfaces/AudioTranscriptionProvider';
import { FileProcessingResult } from '../../src/types';
import { logger } from '../../src/utils/logger';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';
import * as os from 'os';

interface WhisperLocalConfig {
  model: string;
  language?: string;
  device?: 'cpu' | 'cuda';
  outputFormat: 'json' | 'txt' | 'vtt' | 'srt';
  whisperPath?: string; // Path to whisper executable
}

export class WhisperLocalProvider implements AudioTranscriptionProvider {
  private config: WhisperLocalConfig;
  private isConfigured: boolean;

  constructor(apiKey?: string, model?: string, options?: Record<string, any>) {
    // Note: apiKey parameter is not used for local Whisper but kept for interface compatibility
    this.config = {
      model: model || 'base',
      language: options?.language || 'auto',
      device: options?.device || 'cpu',
      outputFormat: options?.outputFormat || 'json',
      whisperPath: options?.whisperPath || 'whisper', // Assumes whisper is in PATH
    };

    this.isConfigured = true; // Local provider doesn't need external API key

    logger.info('Whisper Local Provider initialized successfully', {
      model: this.config.model,
      language: this.config.language,
      device: this.config.device,
    });
  }

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    if (!this.isReady()) {
      throw new Error('Whisper Local Provider is not properly configured');
    }

    this.validateFile(file);

    // Create temporary file for processing
    const tempDir = os.tmpdir();
    const tempFilePath = path.join(tempDir, `whisper-${Date.now()}-${file.originalname}`);
    
    try {
      logger.debug('Starting transcription with Whisper Local', {
        filename: file.originalname,
        size: file.size,
        model: this.config.model,
        tempFile: tempFilePath,
      });

      const startTime = Date.now();

      // Write file buffer to temporary file
      await fs.promises.writeFile(tempFilePath, file.buffer);

      // Run Whisper transcription
      const result = await this.runWhisper(tempFilePath);

      const duration = Date.now() - startTime;

      logger.info('Whisper Local transcription completed', {
        filename: file.originalname,
        duration: `${duration}ms`,
        textLength: result.text.length,
        model: this.config.model,
      });

      return {
        extractedText: result.text,
        processingMethod: 'whisper_local_transcription',
        metadata: {
          provider: this.getProviderName(),
          model: this.config.model,
          language: result.detected_language || this.config.language,
          duration: `${duration}ms`,
          device: this.config.device,
          segments: result.segments?.length || 0,
        },
      };
    } catch (error) {
      logger.error('Whisper Local transcription failed', {
        filename: file.originalname,
        error: error instanceof Error ? error.message : 'Unknown error',
      });

      throw new Error(
        `Whisper Local transcription failed: ${
          error instanceof Error ? error.message : 'Unknown error'
        }`
      );
    } finally {
      // Clean up temporary file
      try {
        await fs.promises.unlink(tempFilePath);
      } catch (error) {
        logger.warn('Failed to clean up temporary file', { tempFilePath, error });
      }
    }
  }

  private async runWhisper(filePath: string): Promise<{
    text: string;
    detected_language?: string;
    segments?: any[];
  }> {
    return new Promise((resolve, reject) => {
      const args = [
        filePath,
        '--model', this.config.model,
        '--output_format', this.config.outputFormat,
        '--device', this.config.device,
      ];

      // Add language if specified and not auto
      if (this.config.language && this.config.language !== 'auto') {
        args.push('--language', this.config.language);
      }

      // Add output directory to control where files are saved
      const outputDir = path.dirname(filePath);
      args.push('--output_dir', outputDir);

      logger.debug('Running Whisper command', { 
        command: this.config.whisperPath,
        args: args.filter(arg => arg !== filePath) // Don't log full file path
      });

      const whisperProcess = spawn(this.config.whisperPath!, args);

      let stdout = '';
      let stderr = '';

      whisperProcess.stdout?.on('data', (data) => {
        stdout += data.toString();
      });

      whisperProcess.stderr?.on('data', (data) => {
        stderr += data.toString();
      });

      whisperProcess.on('close', async (code) => {
        if (code !== 0) {
          reject(new Error(`Whisper process failed with code ${code}: ${stderr}`));
          return;
        }

        try {
          // Read the output file based on format
          const result = await this.parseWhisperOutput(filePath, outputDir);
          resolve(result);
        } catch (error) {
          reject(error);
        }
      });

      whisperProcess.on('error', (error) => {
        reject(new Error(`Failed to spawn Whisper process: ${error.message}`));
      });

      // Set a timeout to prevent hanging
      setTimeout(() => {
        whisperProcess.kill();
        reject(new Error('Whisper process timed out'));
      }, 300000); // 5 minutes timeout
    });
  }

  private async parseWhisperOutput(originalPath: string, outputDir: string): Promise<{
    text: string;
    detected_language?: string;
    segments?: any[];
  }> {
    const baseName = path.basename(originalPath, path.extname(originalPath));
    const outputFile = path.join(outputDir, `${baseName}.${this.config.outputFormat}`);

    try {
      const content = await fs.promises.readFile(outputFile, 'utf-8');

      if (this.config.outputFormat === 'json') {
        const data = JSON.parse(content);
        return {
          text: data.text || '',
          detected_language: data.language,
          segments: data.segments,
        };
      } else {
        // For txt, vtt, srt formats, return as plain text
        return {
          text: content.trim(),
        };
      }
    } catch (error) {
      throw new Error(`Failed to read Whisper output file: ${error}`);
    } finally {
      // Clean up output file
      try {
        await fs.promises.unlink(outputFile);
      } catch (error) {
        logger.warn('Failed to clean up Whisper output file', { outputFile, error });
      }
    }
  }

  private validateFile(file: Express.Multer.File): void {
    if (file.size > this.getMaxFileSize()) {
      throw new Error(
        `File too large: ${file.size} bytes (max: ${this.getMaxFileSize()} bytes)`
      );
    }

    const extension = this.getFileExtension(file.originalname);
    if (!this.getSupportedFormats().includes(extension)) {
      throw new Error(
        `Unsupported file format: ${extension}. Supported formats: ${this.getSupportedFormats().join(', ')}`
      );
    }
  }

  private getFileExtension(filename: string): string {
    const lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex === -1 ? '' : filename.substring(lastDotIndex).toLowerCase();
  }

  getSupportedFormats(): string[] {
    return [
      '.mp3',
      '.wav',
      '.flac',
      '.ogg',
      '.opus',
      '.m4a',
      '.aac',
      '.wma',
      '.mp4',
      '.avi',
      '.mov',
      '.mkv',
    ];
  }

  getMaxFileSize(): number {
    // Local processing can handle larger files, limited by available memory
    return 500 * 1024 * 1024; // 500MB
  }

  getProviderName(): string {
    return 'whisper_local';
  }

  isReady(): boolean {
    return this.isConfigured;
  }

  async healthCheck(): Promise<boolean> {
    try {
      // Test if whisper command is available
      return new Promise((resolve) => {
        const testProcess = spawn(this.config.whisperPath!, ['--help']);
        
        testProcess.on('close', (code) => {
          resolve(code === 0);
        });

        testProcess.on('error', () => {
          resolve(false);
        });

        // Timeout after 5 seconds
        setTimeout(() => {
          testProcess.kill();
          resolve(false);
        }, 5000);
      });
    } catch {
      return false;
    }
  }

  /**
   * Get available Whisper models
   */
  getAvailableModels(): string[] {
    return ['tiny', 'base', 'small', 'medium', 'large', 'large-v2', 'large-v3'];
  }

  /**
   * Get model information and resource requirements
   */
  getModelInfo(model: string): {
    size: string;
    vramRequired: string;
    speed: string;
    accuracy: string;
  } {
    const modelInfo: Record<string, any> = {
      tiny: { size: '37 MB', vramRequired: '1 GB', speed: 'fastest', accuracy: 'lowest' },
      base: { size: '142 MB', vramRequired: '1 GB', speed: 'fast', accuracy: 'low' },
      small: { size: '461 MB', vramRequired: '2 GB', speed: 'medium', accuracy: 'medium' },
      medium: { size: '1.5 GB', vramRequired: '5 GB', speed: 'slow', accuracy: 'good' },
      large: { size: '2.9 GB', vramRequired: '10 GB', speed: 'slowest', accuracy: 'best' },
      'large-v2': { size: '2.9 GB', vramRequired: '10 GB', speed: 'slowest', accuracy: 'best' },
      'large-v3': { size: '2.9 GB', vramRequired: '10 GB', speed: 'slowest', accuracy: 'best' },
    };

    return modelInfo[model] || modelInfo.base;
  }
}

/**
 * Example usage:
 * 
 * // Basic usage with default base model
 * const provider = new WhisperLocalProvider();
 * 
 * // With specific model and language
 * const provider = new WhisperLocalProvider(undefined, 'large-v3', {
 *   language: 'en',
 *   device: 'cuda',
 *   outputFormat: 'json'
 * });
 * 
 * // Custom Whisper installation path
 * const provider = new WhisperLocalProvider(undefined, 'medium', {
 *   whisperPath: '/path/to/whisper'
 * });
 */