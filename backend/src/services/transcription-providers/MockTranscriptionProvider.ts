import { AudioTranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';
import { FileProcessingResult, FileType } from '../../types';
import { logger } from '../../utils/logger';

/**
 * Mock transcription provider for development and testing
 * Generates realistic-looking transcriptions based on file metadata
 */
export class MockTranscriptionProvider implements AudioTranscriptionProvider {
  private isConfigured = true;

  async transcribe(file: Express.Multer.File): Promise<FileProcessingResult> {
    logger.info('Using mock transcription provider', {
      filename: file.originalname,
      size: file.size,
    });

    // Simulate processing time
    await this.delay(500 + Math.random() * 1000);

    // Generate mock transcription based on filename or create generic content
    const transcription = this.generateMockTranscription(file.originalname, file.size);

    logger.info('Mock transcription completed', {
      filename: file.originalname,
      transcriptionLength: transcription.length,
    });

    return {
      extractedText: transcription,
      fileType: FileType.AUDIO,
      processingMethod: 'mock_transcription',
    };
  }

  getSupportedFormats(): string[] {
    return ['mp3', 'wav', 'ogg', 'flac', 'm4a', 'mp4', 'webm', 'aiff'];
  }

  getMaxFileSize(): number {
    return 100 * 1024 * 1024; // 100MB - generous for mock
  }

  getProviderName(): string {
    return 'Mock Transcription Provider';
  }

  isReady(): boolean {
    return this.isConfigured;
  }

  private generateMockTranscription(filename: string, fileSize: number): string {
    const baseTemplates = [
      "I have an idea for a new mobile application that could help people organize their daily tasks more efficiently.",
      "Let me share some thoughts about a project I've been considering for improving team productivity at work.",
      "I want to document some ideas about creating a better user experience for online shopping platforms.",
      "Here are my thoughts on developing a learning management system for educational institutions.",
      "I'd like to explore the possibility of building a health and fitness tracking application.",
      "Let me record some ideas about creating a social platform for local community engagement.",
      "I'm thinking about developing a tool that could help small businesses manage their finances better.",
      "Here's an idea for a travel planning application that could make trip organization much easier.",
      "I want to brainstorm some features for a food delivery service that focuses on local restaurants.",
      "Let me share thoughts about creating a platform for freelancers to manage their projects and clients.",
    ];

    // Choose template based on filename hash for consistency
    const hash = this.simpleHash(filename);
    const template = baseTemplates[hash % baseTemplates.length];

    // Add some variations based on file size
    let elaboration = '';
    if (fileSize > 1024 * 1024) { // > 1MB, assume longer recording
      elaboration += ' The main features should include user authentication, data synchronization, and an intuitive dashboard. I also think we should consider mobile-first design and ensure the application works well offline. We might want to integrate with popular third-party services and provide comprehensive analytics for users.';
    } else {
      elaboration += ' Key features would include a clean interface and essential functionality that users need most.';
    }

    // Add some realistic filler
    const fillers = [
      ' I think this could really solve a common problem that many people face.',
      ' This idea came to me while I was thinking about current market gaps.',
      ' I believe there\'s a real opportunity here if we execute it well.',
      ' The timing seems right for this kind of solution.',
      ' This could potentially help a lot of people in their daily lives.',
    ];

    const filler = fillers[hash % fillers.length];

    return template + elaboration + filler;
  }

  private simpleHash(str: string): number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32-bit integer
    }
    return Math.abs(hash);
  }

  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}