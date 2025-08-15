import { AIService } from '../services/AIService';
import { FileService } from '../services/FileService';
import { TranscriptProcessorService } from '../services/TranscriptProcessorService';
import { FileProcessorService } from '../services/FileProcessorService';
import { FileTypeDetectionService } from '../services/FileTypeDetectionService';
import { TextFileProcessorService } from '../services/TextFileProcessorService';
import { AudioTranscriptionService } from '../services/AudioTranscriptionService';
import { TranscriptController } from '../controllers/TranscriptController';
import { getConfig, getTranscriptionConfig, AI_CONFIG, FILE_CONFIG } from '../config';
import { AppConfig } from '../types';

export class ServiceFactory {
  private static instance: ServiceFactory;
  private config: AppConfig;

  private constructor() {
    this.config = getConfig();
  }

  static getInstance(): ServiceFactory {
    if (!ServiceFactory.instance) {
      ServiceFactory.instance = new ServiceFactory();
    }
    return ServiceFactory.instance;
  }

  createAIService(): AIService {
    return new AIService({
      apiKey: this.config.geminiApiKey,
      model: AI_CONFIG.model,
    });
  }

  createFileService(): FileService {
    return new FileService({
      baseDirectory: this.config.baseDirectory,
      specialCategories: FILE_CONFIG.specialCategories,
    });
  }

  createFileTypeDetectionService(): FileTypeDetectionService {
    return new FileTypeDetectionService();
  }

  createTextFileProcessorService(): TextFileProcessorService {
    return new TextFileProcessorService();
  }

  createAudioTranscriptionService(): AudioTranscriptionService {
    const transcriptionConfig = getTranscriptionConfig();
    return new AudioTranscriptionService(transcriptionConfig);
  }

  createFileProcessorService(): FileProcessorService {
    const fileTypeDetectionService = this.createFileTypeDetectionService();
    const textFileProcessorService = this.createTextFileProcessorService();
    const audioTranscriptionService = this.createAudioTranscriptionService();

    return new FileProcessorService(
      fileTypeDetectionService,
      textFileProcessorService,
      audioTranscriptionService
    );
  }

  createTranscriptProcessorService(): TranscriptProcessorService {
    const aiService = this.createAIService();
    const fileService = this.createFileService();
    const fileProcessorService = this.createFileProcessorService();
    return new TranscriptProcessorService(aiService, fileService, fileProcessorService);
  }

  createTranscriptController(): TranscriptController {
    const transcriptProcessorService = this.createTranscriptProcessorService();
    return new TranscriptController(transcriptProcessorService);
  }

  getConfig(): AppConfig {
    return this.config;
  }
}
