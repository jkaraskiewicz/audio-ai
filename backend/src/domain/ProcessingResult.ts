import { FileType } from '../types';
import { TranscriptText } from './TranscriptText';

/**
 * Value Object: Represents file processing result with behavior
 * Uncle Bob Approved: Rich domain object that encapsulates processing logic
 */
export class ProcessingResult {
  private constructor(
    private readonly extractedText: TranscriptText,
    private readonly fileType: FileType,
    private readonly processingMethod: string
  ) {}

  static create(
    extractedText: string,
    fileType: FileType,
    processingMethod: string
  ): ProcessingResult {
    const transcriptText = TranscriptText.create(extractedText);
    return new ProcessingResult(transcriptText, fileType, processingMethod);
  }

  getExtractedText(): string {
    return this.extractedText.getText();
  }

  getFileType(): FileType {
    return this.fileType;
  }

  getProcessingMethod(): string {
    return this.processingMethod;
  }

  getTextLength(): number {
    return this.extractedText.getLength();
  }

  createTextPreview(maxLength?: number): string {
    return this.extractedText.createPreview(maxLength);
  }

  isSuccessful(): boolean {
    return !this.extractedText.isEmpty();
  }

  isAudioProcessing(): boolean {
    return this.fileType === FileType.AUDIO;
  }

  isTextProcessing(): boolean {
    return this.fileType === FileType.TEXT;
  }

  getWordCount(): number {
    return this.extractedText.getWordCount();
  }

  containsActionItems(): boolean {
    return this.extractedText.isLikelyActionItem();
  }

  createProcessingSummary(): ProcessingSummary {
    return {
      textLength: this.getTextLength(),
      wordCount: this.getWordCount(),
      fileType: this.fileType,
      processingMethod: this.processingMethod,
      hasActionItems: this.containsActionItems(),
      preview: this.createTextPreview(),
    };
  }

  getProviderFromMethod(): string {
    // Extract provider name from processing method like "openai_whisper_webservice_auto-detected"
    const parts = this.processingMethod.split('_');
    return parts.slice(0, -1).join('_'); // Remove last part (usually language)
  }

  wasProcessedByProvider(providerName: string): boolean {
    return this.processingMethod.includes(providerName);
  }
}

export interface ProcessingSummary {
  readonly textLength: number;
  readonly wordCount: number;
  readonly fileType: FileType;
  readonly processingMethod: string;
  readonly hasActionItems: boolean;
  readonly preview: string;
}
