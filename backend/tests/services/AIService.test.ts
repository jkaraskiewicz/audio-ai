import { AIService } from '../../src/services/AIService';
import { GoogleGenerativeAI } from '@google/generative-ai';

// Mock the GoogleGenerativeAI
jest.mock('@google/generative-ai');
const mockGoogleGenerativeAI = GoogleGenerativeAI as jest.MockedClass<typeof GoogleGenerativeAI>;

describe('AIService', () => {
  let aiService: AIService;
  let mockGenAI: jest.Mocked<GoogleGenerativeAI>;
  let mockModel: any;
  let mockResponse: any;

  beforeEach(() => {
    mockResponse = {
      text: jest.fn().mockReturnValue('---\ncategory: projects\nfilename: test-project\ncommentary_needed: false\n---\n\n# Test Project\n\n## Summary\nA test project.\n\n## Ideas\n- **Expense Tracking**: Simple expense tracking app\n\n## Action Items\n- [ ] Research expense tracking features\n\n## Tags\nfinance, app, mobile'),
    };

    mockModel = {
      generateContent: jest.fn().mockResolvedValue({
        response: Promise.resolve(mockResponse),
      }),
    };

    mockGenAI = {
      getGenerativeModel: jest.fn().mockReturnValue(mockModel),
    } as any;

    mockGoogleGenerativeAI.mockImplementation(() => mockGenAI);

    aiService = new AIService({
      apiKey: 'test-api-key',
      model: 'gemini-2.0-flash-exp',
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('processTranscript', () => {
    it('should process transcript successfully', async () => {
      const transcript = 'I want to build a mobile app for tracking expenses.';

      const result = await aiService.processTranscript(transcript);

      expect(mockGenAI.getGenerativeModel).toHaveBeenCalledWith({
        model: 'gemini-2.0-flash-exp',
      });
      expect(mockModel.generateContent).toHaveBeenCalledWith(
        expect.stringContaining(transcript)
      );
      expect(result).toContain('# Test Project');
    });

    it('should handle AI service errors', async () => {
      mockModel.generateContent.mockRejectedValue(new Error('AI service error'));

      const transcript = 'Test transcript';

      await expect(aiService.processTranscript(transcript)).rejects.toThrow(
        'AI service failed to process transcript'
      );
    });

    it('should strip markdown code blocks', async () => {
      mockResponse.text.mockReturnValue('```markdown\n# Test\n```');

      const result = await aiService.processTranscript('test');

      expect(result).toBe('# Test');
    });

    it('should include all required sections in enhanced prompt', async () => {
      await aiService.processTranscript('test transcript');

      const prompt = mockModel.generateContent.mock.calls[0][0];
      expect(prompt).toContain('test transcript');
      expect(prompt).toContain('commentary_needed: {{commentary_decision}}');
      expect(prompt).toContain('category:');
      expect(prompt).toContain('filename:');
      expect(prompt).toContain('Summary');
      expect(prompt).toContain('Ideas');
      expect(prompt).toContain('Action Items');
      expect(prompt).toContain('AI Commentary');
      expect(prompt).toContain('Tags');
    });

    it('should handle response with commentary needed', async () => {
      mockResponse.text.mockReturnValue('---\ncategory: questions\nfilename: test-questions\ncommentary_needed: true\n---\n\n# Test Questions\n\n## Summary\nSome questions.\n\n## Ideas\n- **Question**: Who invented the telephone?\n\n## Action Items\n- [ ] Research telephone invention\n\n## AI Commentary\nAlexander Graham Bell invented the telephone in 1876.\n\n## Tags\nhistory, invention');

      const result = await aiService.processTranscript('Who invented the telephone?');

      expect(result).toContain('AI Commentary');
      expect(result).toContain('Alexander Graham Bell');
    });

    it('should remove commentary section when not needed', async () => {
      mockResponse.text.mockReturnValue('---\ncategory: daily\nfilename: grocery-task\ncommentary_needed: false\n---\n\n# Grocery Task\n\n## Summary\nBuy groceries.\n\n## Ideas\n- **Shopping**: Buy milk and bread\n\n## Action Items\n- [ ] Go to store\n\n## AI Commentary\nThis should be removed.\n\n## Tags\nshopping, daily');

      const result = await aiService.processTranscript('Buy milk and bread');

      expect(result).not.toContain('AI Commentary');
      expect(result).not.toContain('This should be removed');
      expect(result).toContain('## Tags');
    });
  });
});