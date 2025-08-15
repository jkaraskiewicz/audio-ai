import { Request, Response, NextFunction } from 'express';
import { validateTranscriptRequest } from '../../src/middleware/validation';

describe('validateTranscriptRequest', () => {
  let mockRequest: Partial<Request>;
  let mockResponse: Partial<Response>;
  let mockNext: NextFunction;

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

    validateTranscriptRequest(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockNext).toHaveBeenCalled();
    expect(mockResponse.status).not.toHaveBeenCalled();
  });

  it('should return 400 for missing transcript', () => {
    mockRequest.body = {};

    validateTranscriptRequest(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(400);
    expect(mockResponse.json).toHaveBeenCalledWith({
      error: 'Transcript is required',
    });
    expect(mockNext).not.toHaveBeenCalled();
  });

  it('should return 400 for non-string transcript', () => {
    mockRequest.body = { transcript: 123 };

    validateTranscriptRequest(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(400);
    expect(mockResponse.json).toHaveBeenCalledWith({
      error: 'Transcript must be a string',
    });
  });

  it('should return 400 for empty transcript', () => {
    mockRequest.body = { transcript: '   ' };

    validateTranscriptRequest(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(400);
    expect(mockResponse.json).toHaveBeenCalledWith({
      error: 'Transcript cannot be empty',
    });
  });

  it('should return 400 for transcript too long', () => {
    mockRequest.body = { transcript: 'a'.repeat(10001) };

    validateTranscriptRequest(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(400);
    expect(mockResponse.json).toHaveBeenCalledWith({
      error: 'Transcript is too long (max 10,000 characters)',
    });
  });
});