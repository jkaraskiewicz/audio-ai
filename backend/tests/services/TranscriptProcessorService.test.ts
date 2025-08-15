import { TranscriptProcessorService } from '../../src/services/TranscriptProcessorService';
import { AIService } from '../../src/services/AIService';
import { FileService } from '../../src/services/FileService';
import { FileProcessorService } from '../../src/services/FileProcessorService';

// Mock the dependencies
jest.mock('../../src/services/AIService');
jest.mock('../../src/services/FileService');
jest.mock('../../src/services/FileProcessorService');

const MockedAIService = AIService as jest.MockedClass<typeof AIService>;
const MockedFileService = FileService as jest.MockedClass<typeof FileService>;
const MockedFileProcessorService = FileProcessorService as jest.MockedClass<typeof FileProcessorService>;

describe('TranscriptProcessorService', () => {
  let transcriptProcessorService: TranscriptProcessorService;
  let mockAIService: jest.Mocked<AIService>;
  let mockFileService: jest.Mocked<FileService>;
  let mockFileProcessorService: jest.Mocked<FileProcessorService>;

  beforeEach(() => {
    mockAIService = new MockedAIService({} as any) as jest.Mocked<AIService>;
    mockFileService = new MockedFileService({} as any) as jest.Mocked<FileService>;
    mockFileProcessorService = new MockedFileProcessorService({} as any, {} as any, {} as any) as jest.Mocked<FileProcessorService>;
    
    transcriptProcessorService = new TranscriptProcessorService(
      mockAIService,
      mockFileService,
      mockFileProcessorService
    );
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('processTranscript', () => {
    it('should process transcript successfully', async () => {
      const transcript = 'Build a mobile app for expense tracking';
      const aiResult = '---\ncategory: projects\nfilename: expense-app\n---\n\n# Expense App';
      const savedPath = 'saved_ideas/projects/2025-08-14_expense-app.md';

      mockAIService.processTranscript.mockResolvedValue(aiResult);
      mockFileService.saveMarkdownFile.mockReturnValue(savedPath);

      const result = await transcriptProcessorService.processTranscript(transcript);

      expect(mockAIService.processTranscript).toHaveBeenCalledWith(transcript);
      expect(mockFileService.saveMarkdownFile).toHaveBeenCalledWith(aiResult);
      expect(result).toEqual({
        result: aiResult,
        saved_to: savedPath,
        message: `Idea processed and saved to ${savedPath}`,
      });
    });

    it('should throw error for empty transcript', async () => {
      await expect(transcriptProcessorService.processTranscript('')).rejects.toThrow(
        'Transcript is required and cannot be empty'
      );
      await expect(transcriptProcessorService.processTranscript('   ')).rejects.toThrow(
        'Transcript is required and cannot be empty'
      );
    });

    it('should handle AI service errors', async () => {
      mockAIService.processTranscript.mockRejectedValue(new Error('AI error'));

      await expect(
        transcriptProcessorService.processTranscript('test transcript')
      ).rejects.toThrow('AI error');
    });

    it('should handle file service errors', async () => {
      mockAIService.processTranscript.mockResolvedValue('ai result');
      mockFileService.saveMarkdownFile.mockImplementation(() => {
        throw new Error('File error');
      });

      await expect(
        transcriptProcessorService.processTranscript('test transcript')
      ).rejects.toThrow('File error');
    });
  });
});