import { Request, Response, NextFunction } from 'express';
import { logger } from '../utils/logger';

export const errorHandler = (
  error: Error,
  _req: Request,
  res: Response,
  _next: NextFunction
): void => {
  logger.error('Unhandled error in request', error);

  const isDevelopment = process.env.NODE_ENV !== 'production';

  // Default error response
  let statusCode = 500;
  let message = 'Internal server error';

  // Handle specific error types
  if (error.message.includes('GEMINI_API_KEY not configured')) {
    statusCode = 500;
    message = 'Server configuration error';
  } else if (error.message.includes('AI service failed')) {
    statusCode = 503;
    message = 'AI service is currently unavailable';
  } else if (error.message.includes('File service failed')) {
    statusCode = 500;
    message = 'File system error';
  } else if (error.message.includes('Transcript is required')) {
    statusCode = 400;
    message = error.message;
  } else if (error.message.includes('Unsupported file type')) {
    statusCode = 400;
    message = error.message;
  } else if (error.message.includes('File size exceeds maximum')) {
    statusCode = 400;
    message = error.message;
  } else if (error.message.includes('No usable content could be extracted')) {
    statusCode = 400;
    message = error.message;
  }

  res.status(statusCode).json({
    error: message,
    ...(isDevelopment && { details: error.message, stack: error.stack }),
  });
};
