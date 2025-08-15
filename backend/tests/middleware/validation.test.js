"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const validation_1 = require("../../src/middleware/validation");
describe('validateTranscriptRequest', () => {
    let mockRequest;
    let mockResponse;
    let mockNext;
    beforeEach(() => {
        mockRequest = {};
        mockResponse = {
            status: jest.fn().mockReturnThis(),
            json: jest.fn().mockReturnThis(),
        };
        mockNext = jest.fn();
    });
    afterEach(() => {
        jest.clearAllMocks();
    });
    it('should call next() for valid transcript', () => {
        mockRequest.body = { transcript: 'Valid transcript content' };
        (0, validation_1.validateTranscriptRequest)(mockRequest, mockResponse, mockNext);
        expect(mockNext).toHaveBeenCalled();
        expect(mockResponse.status).not.toHaveBeenCalled();
    });
    it('should return 400 for missing transcript', () => {
        mockRequest.body = {};
        (0, validation_1.validateTranscriptRequest)(mockRequest, mockResponse, mockNext);
        expect(mockResponse.status).toHaveBeenCalledWith(400);
        expect(mockResponse.json).toHaveBeenCalledWith({
            error: 'Transcript is required',
        });
        expect(mockNext).not.toHaveBeenCalled();
    });
    it('should return 400 for non-string transcript', () => {
        mockRequest.body = { transcript: 123 };
        (0, validation_1.validateTranscriptRequest)(mockRequest, mockResponse, mockNext);
        expect(mockResponse.status).toHaveBeenCalledWith(400);
        expect(mockResponse.json).toHaveBeenCalledWith({
            error: 'Transcript must be a string',
        });
    });
    it('should return 400 for empty transcript', () => {
        mockRequest.body = { transcript: '   ' };
        (0, validation_1.validateTranscriptRequest)(mockRequest, mockResponse, mockNext);
        expect(mockResponse.status).toHaveBeenCalledWith(400);
        expect(mockResponse.json).toHaveBeenCalledWith({
            error: 'Transcript cannot be empty',
        });
    });
    it('should return 400 for transcript too long', () => {
        mockRequest.body = { transcript: 'a'.repeat(10001) };
        (0, validation_1.validateTranscriptRequest)(mockRequest, mockResponse, mockNext);
        expect(mockResponse.status).toHaveBeenCalledWith(400);
        expect(mockResponse.json).toHaveBeenCalledWith({
            error: 'Transcript is too long (max 10,000 characters)',
        });
    });
});
