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
Object.defineProperty(exports, "__esModule", { value: true });
const AIService_1 = require("../../src/services/AIService");
const generative_ai_1 = require("@google/generative-ai");
// Mock the GoogleGenerativeAI
jest.mock('@google/generative-ai');
const mockGoogleGenerativeAI = generative_ai_1.GoogleGenerativeAI;
describe('AIService', () => {
    let aiService;
    let mockGenAI;
    let mockModel;
    let mockResponse;
    beforeEach(() => {
        mockResponse = {
            text: jest.fn().mockReturnValue('---\ncategory: projects\nfilename: test-project\n---\n\n# Test Project\n\n## Summary\nA test project.'),
        };
        mockModel = {
            generateContent: jest.fn().mockResolvedValue({
                response: Promise.resolve(mockResponse),
            }),
        };
        mockGenAI = {
            getGenerativeModel: jest.fn().mockReturnValue(mockModel),
        };
        mockGoogleGenerativeAI.mockImplementation(() => mockGenAI);
        aiService = new AIService_1.AIService({
            apiKey: 'test-api-key',
            model: 'gemini-2.0-flash-exp',
        });
    });
    afterEach(() => {
        jest.clearAllMocks();
    });
    describe('processTranscript', () => {
        it('should process transcript successfully', () => __awaiter(void 0, void 0, void 0, function* () {
            const transcript = 'I want to build a mobile app for tracking expenses.';
            const result = yield aiService.processTranscript(transcript);
            expect(mockGenAI.getGenerativeModel).toHaveBeenCalledWith({
                model: 'gemini-2.0-flash-exp',
            });
            expect(mockModel.generateContent).toHaveBeenCalledWith(expect.stringContaining(transcript));
            expect(result).toContain('# Test Project');
        }));
        it('should handle AI service errors', () => __awaiter(void 0, void 0, void 0, function* () {
            mockModel.generateContent.mockRejectedValue(new Error('AI service error'));
            const transcript = 'Test transcript';
            yield expect(aiService.processTranscript(transcript)).rejects.toThrow('AI service failed to process transcript');
        }));
        it('should strip markdown code blocks', () => __awaiter(void 0, void 0, void 0, function* () {
            mockResponse.text.mockReturnValue('```markdown\n# Test\n```');
            const result = yield aiService.processTranscript('test');
            expect(result).toBe('# Test');
        }));
        it('should include all required sections in prompt', () => __awaiter(void 0, void 0, void 0, function* () {
            yield aiService.processTranscript('test transcript');
            const prompt = mockModel.generateContent.mock.calls[0][0];
            expect(prompt).toContain('test transcript');
            expect(prompt).toContain('category:');
            expect(prompt).toContain('filename:');
            expect(prompt).toContain('Summary');
            expect(prompt).toContain('Ideas');
            expect(prompt).toContain('Action Items');
            expect(prompt).toContain('Tags');
        }));
    });
});
