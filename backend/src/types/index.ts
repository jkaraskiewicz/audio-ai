export interface ProcessTranscriptRequest {
  transcript?: string;
}

export interface ProcessFileRequest {
  file?: Express.Multer.File;
  transcript?: string;
}

export interface FileProcessingResult {
  extractedText: string;
  fileType: FileType;
  processingMethod: string;
}

export enum FileType {
  TEXT = 'text',
  AUDIO = 'audio',
  UNKNOWN = 'unknown',
}

export interface ProcessTranscriptResponse {
  result: string;
  saved_to: string;
  message: string;
}

export interface ErrorResponse {
  error: string;
}

export interface FrontMatter {
  category: string;
  filename: string;
}

export interface ParsedContent {
  frontMatter: FrontMatter;
  cleanContent: string;
}

export interface AIServiceConfig {
  apiKey: string;
  model: string;
  maxTokens?: number;
  temperature?: number;
  endpoint?: string; // For local AI services
}

export interface FileServiceConfig {
  baseDirectory: string;
  specialCategories: Record<string, string>;
}

export interface TranscriptionConfig {
  provider: import('../interfaces/AudioTranscriptionProvider').TranscriptionProvider;
  apiKey?: string;
  model?: string;
  language?: string;
  maxFileSize?: number;
  speakerLabels?: boolean;
  punctuate?: boolean;
  formatText?: boolean;
  device?: 'cpu' | 'cuda'; // For local providers
  whisperServiceUrl?: string; // For Docker Whisper provider
  [key: string]: unknown;
}

export interface AppConfig {
  port: number;
  geminiApiKey: string;
  baseDirectory: string;
}
