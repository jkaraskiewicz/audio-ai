import { Request, Response, NextFunction } from 'express';
import { ProcessTranscriptRequest } from '../types';
import { logger } from '../utils/logger';

export const validateTranscriptRequest = (
  req: Request,
  res: Response,
  next: NextFunction
): void => {
  const { transcript } = req.body as ProcessTranscriptRequest;

  if (!transcript) {
    logger.warn('Validation failed: transcript is required');
    res.status(400).json({ error: 'Transcript is required' });
    return;
  }

  if (typeof transcript !== 'string') {
    logger.warn('Validation failed: transcript must be a string');
    res.status(400).json({ error: 'Transcript must be a string' });
    return;
  }

  if (transcript.trim().length === 0) {
    logger.warn('Validation failed: transcript cannot be empty');
    res.status(400).json({ error: 'Transcript cannot be empty' });
    return;
  }

  if (transcript.length > 10000) {
    logger.warn('Validation failed: transcript too long', { length: transcript.length });
    res.status(400).json({ error: 'Transcript is too long (max 10,000 characters)' });
    return;
  }

  next();
};
