"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const supertest_1 = __importDefault(require("supertest"));
const express_1 = __importDefault(require("express"));
const TranscriptController_1 = require("../../src/controllers/TranscriptController");
const TranscriptProcessorService_1 = require("../../src/services/TranscriptProcessorService");
// Mock the service
jest.mock('../../src/services/TranscriptProcessorService');
const MockedTranscriptProcessorService = TranscriptProcessorService_1.TranscriptProcessorService;
describe('TranscriptController', () => {
    let app;
    let transcriptController;
    let mockTranscriptProcessorService;
    beforeEach(() => {
        mockTranscriptProcessorService = new MockedTranscriptProcessorService({}, {});
        transcriptController = new TranscriptController_1.TranscriptController(mockTranscriptProcessorService);
        app = (0, express_1.default)();
        app.use(express_1.default.json());
        app.get('/health', transcriptController.healthCheck);
        app.post('/process', transcriptController.processTranscript);
    });
    afterEach(() => {
        jest.clearAllMocks();
    });
    describe('GET /health', () => {
        it('should return health status', () => __awaiter(void 0, void 0, void 0, function* () {
            const response = yield (0, supertest_1.default)(app).get('/health');
            expect(response.status).toBe(200);
            expect(response.body).toMatchObject({
                status: 'healthy',
                service: 'audio-ai',
            });
            expect(response.body.timestamp).toBeDefined();
        }));
    });
    describe('POST /process', () => {
        it('should process transcript successfully', () => __awaiter(void 0, void 0, void 0, function* () {
            const transcript = 'Build a mobile app';
            const mockResult = {
                result: '# Mobile App\\n\\nContent here',
                saved_to: 'saved_ideas/projects/2025-08-14_mobile-app.md',
                message: 'Idea processed and saved',
            };
            mockTranscriptProcessorService.processTranscript.mockResolvedValue(mockResult);
            const response = yield (0, supertest_1.default)(app)
                .post('/process')
                .send({ transcript })
                .expect(200);
            expect(response.body).toEqual(mockResult);
            expect(mockTranscriptProcessorService.processTranscript).toHaveBeenCalledWith(transcript);
        }));
        it('should handle service errors', () => __awaiter(void 0, void 0, void 0, function* () {
            mockTranscriptProcessorService.processTranscript.mockRejectedValue(new Error('Service error'));
            const response = yield (0, supertest_1.default)(app)
                .post('/process')
                .send({ transcript: 'test' });
            expect(response.status).toBe(500);
        }));
    });
});
