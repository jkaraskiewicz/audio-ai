import request from 'supertest';
import express from 'express';
import { TranscriptController } from '../../src/controllers/TranscriptController';
import { TranscriptProcessorService } from '../../src/services/TranscriptProcessorService';

// Mock the service
jest.mock('../../src/services/TranscriptProcessorService');
const MockedTranscriptProcessorService = TranscriptProcessorService as jest.MockedClass<
  typeof TranscriptProcessorService
>;

describe('TranscriptController', () => {
  let app: express.Application;
  let transcriptController: TranscriptController;
  let mockTranscriptProcessorService: jest.Mocked<TranscriptProcessorService>;

  beforeEach(() => {
    mockTranscriptProcessorService = new MockedTranscriptProcessorService(
      {} as any,
      {} as any,
      {} as any
    ) as jest.Mocked<TranscriptProcessorService>;

    transcriptController = new TranscriptController(mockTranscriptProcessorService);

    app = express();
    app.use(express.json());
    app.get('/health', transcriptController.healthCheck);
    app.post('/process', transcriptController.processTranscript);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /health', () => {
    it('should return health status', async () => {
      const response = await request(app).get('/health');

      expect(response.status).toBe(200);
      expect(response.body).toMatchObject({
        status: 'healthy',
        service: 'audio-ai',
      });
      expect(response.body.timestamp).toBeDefined();
    });
  });

  describe('POST /process', () => {
    it('should process transcript successfully', async () => {
      const transcript = 'Build a mobile app';
      const mockResult = {
        result: '# Mobile App\\n\\nContent here',
        saved_to: 'processed/projects/2025-08-14_mobile-app.md',
        message: 'Idea processed and saved',
      };

      mockTranscriptProcessorService.processTranscript.mockResolvedValue(mockResult);

      const response = await request(app)
        .post('/process')
        .send({ transcript })
        .expect(200);

      expect(response.body).toEqual(mockResult);
      expect(mockTranscriptProcessorService.processTranscript).toHaveBeenCalledWith(transcript);
    });

    it('should handle service errors', async () => {
      mockTranscriptProcessorService.processTranscript.mockRejectedValue(
        new Error('Service error')
      );

      const response = await request(app)
        .post('/process')
        .send({ transcript: 'test' });

      expect(response.status).toBe(500);
    });
  });
});