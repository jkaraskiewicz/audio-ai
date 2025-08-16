import { GeneratedContent } from './AIContentGenerator';
import { ProcessingResult } from '../domain/ProcessingResult';
import { logger } from '../utils/logger';
import * as path from 'path';

/**
 * Single Responsibility: Save generated content to file system
 * Uncle Bob Approved: Focused service for file operations
 */
export interface IContentSaver {
  saveGeneratedContent(
    content: GeneratedContent,
    originalResult: ProcessingResult,
    filename?: string
  ): Promise<SaveResult>;
}

export class ContentSaver implements IContentSaver {
  constructor(
    private baseDirectory: string,
    private fileSystemService: IFileSystemService
  ) {}

  async saveGeneratedContent(
    content: GeneratedContent,
    originalResult: ProcessingResult,
    filename?: string
  ): Promise<SaveResult> {
    logger.debug('Starting content save operation', {
      contentLength: content.getContentLength(),
      processingMethod: content.getProcessingMethod(),
      filename,
    });

    const saveOperation = SaveOperation.create(
      content,
      originalResult,
      this.baseDirectory,
      filename
    );

    await this.ensureDirectoryExists(saveOperation.getDirectoryPath());
    await this.fileSystemService.writeFile(
      saveOperation.getFullFilePath(),
      saveOperation.getContentToSave()
    );

    const saveResult = SaveResult.create(
      saveOperation.getFullFilePath(),
      content.getContentLength(),
      originalResult.getProcessingMethod()
    );

    logger.info('Content saved successfully', {
      filePath: saveResult.getFilePath(),
      contentLength: saveResult.getContentLength(),
      processingMethod: saveResult.getProcessingMethod(),
    });

    return saveResult;
  }

  private async ensureDirectoryExists(directoryPath: string): Promise<void> {
    try {
      await this.fileSystemService.ensureDirectory(directoryPath);
    } catch (error) {
      logger.error('Failed to create directory', {
        directoryPath,
        error: error instanceof Error ? error.message : 'Unknown error',
      });
      throw new Error(`Failed to create directory: ${directoryPath}`);
    }
  }
}

export class SaveOperation {
  private constructor(
    private readonly content: GeneratedContent,
    private readonly originalResult: ProcessingResult,
    private readonly baseDirectory: string,
    private readonly filename: string
  ) {}

  static create(
    content: GeneratedContent,
    originalResult: ProcessingResult,
    baseDirectory: string,
    customFilename?: string
  ): SaveOperation {
    const filename = customFilename || SaveOperation.generateFilename(originalResult);
    return new SaveOperation(content, originalResult, baseDirectory, filename);
  }

  getDirectoryPath(): string {
    const category = this.determineCategory();
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');

    return path.join(this.baseDirectory, category, String(year), month);
  }

  getFullFilePath(): string {
    return path.join(this.getDirectoryPath(), this.filename);
  }

  getContentToSave(): string {
    return this.content.getContent();
  }

  private static generateFilename(result: ProcessingResult): string {
    const now = new Date();
    const dateStr = now.toISOString().split('T')[0]; // YYYY-MM-DD
    const sanitizedMethod = result.getProcessingMethod().replace(/[^a-zA-Z0-9]/g, '-');

    return `${dateStr}_${sanitizedMethod}.md`;
  }

  private determineCategory(): string {
    const text = this.originalResult.getExtractedText().toLowerCase();

    if (text.includes('meeting') || text.includes('discussion')) {
      return 'meetings';
    }

    if (this.originalResult.containsActionItems()) {
      return 'tasks';
    }

    return 'notes';
  }
}

export class SaveResult {
  private constructor(
    private readonly filePath: string,
    private readonly contentLength: number,
    private readonly processingMethod: string
  ) {}

  static create(filePath: string, contentLength: number, processingMethod: string): SaveResult {
    return new SaveResult(filePath, contentLength, processingMethod);
  }

  getFilePath(): string {
    return this.filePath;
  }

  getContentLength(): number {
    return this.contentLength;
  }

  getProcessingMethod(): string {
    return this.processingMethod;
  }

  getFileName(): string {
    return path.basename(this.filePath);
  }

  getDirectory(): string {
    return path.dirname(this.filePath);
  }
}

// Interface for file system operations
export interface IFileSystemService {
  writeFile(filePath: string, content: string): Promise<void>;
  ensureDirectory(directoryPath: string): Promise<void>;
  fileExists(filePath: string): Promise<boolean>;
}
