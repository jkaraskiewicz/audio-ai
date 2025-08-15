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
const TranscriptProcessorService_1 = require("../../src/services/TranscriptProcessorService");
const AIService_1 = require("../../src/services/AIService");
const FileService_1 = require("../../src/services/FileService");
// Mock the dependencies
jest.mock('../../src/services/AIService');
jest.mock('../../src/services/FileService');
const MockedAIService = AIService_1.AIService;
const MockedFileService = FileService_1.FileService;
describe('TranscriptProcessorService', () => {
    let transcriptProcessorService;
    let mockAIService;
    let mockFileService;
    beforeEach(() => {
        mockAIService = new MockedAIService({});
        mockFileService = new MockedFileService({});
        transcriptProcessorService = new TranscriptProcessorService_1.TranscriptProcessorService(mockAIService, mockFileService);
    });
    afterEach(() => {
        jest.clearAllMocks();
    });
    describe('processTranscript', () => {
        it('should process transcript successfully', () => __awaiter(void 0, void 0, void 0, function* () {
            const transcript = 'Build a mobile app for expense tracking';
            const aiResult = '---\ncategory: projects\nfilename: expense-app\n---\n\n# Expense App';
            const savedPath = 'processed/projects/2025-08-14_expense-app.md';
            mockAIService.processTranscript.mockResolvedValue(aiResult);
            mockFileService.saveMarkdownFile.mockReturnValue(savedPath);
            const result = yield transcriptProcessorService.processTranscript(transcript);
            expect(mockAIService.processTranscript).toHaveBeenCalledWith(transcript);
            expect(mockFileService.saveMarkdownFile).toHaveBeenCalledWith(aiResult);
            expect(result).toEqual({
                result: aiResult,
                saved_to: savedPath,
                message: `Idea processed and saved to ${savedPath}`,
            });
        }));
        it('should throw error for empty transcript', () => __awaiter(void 0, void 0, void 0, function* () {
            yield expect(transcriptProcessorService.processTranscript('')).rejects.toThrow('Transcript is required and cannot be empty');
            yield expect(transcriptProcessorService.processTranscript('   ')).rejects.toThrow('Transcript is required and cannot be empty');
        }));
        it('should handle AI service errors', () => __awaiter(void 0, void 0, void 0, function* () {
            mockAIService.processTranscript.mockRejectedValue(new Error('AI error'));
            yield expect(transcriptProcessorService.processTranscript('test transcript')).rejects.toThrow('AI error');
        }));
        it('should handle file service errors', () => __awaiter(void 0, void 0, void 0, function* () {
            mockAIService.processTranscript.mockResolvedValue('ai result');
            mockFileService.saveMarkdownFile.mockImplementation(() => {
                throw new Error('File error');
            });
            yield expect(transcriptProcessorService.processTranscript('test transcript')).rejects.toThrow('File error');
        }));
    });
});
