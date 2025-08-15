import { Request, Response, NextFunction } from 'express';
import multer from 'multer';
import { ProcessFileRequest } from '../types';
import { logger } from '../utils/logger';

// Configure multer for memory storage
const storage = multer.memoryStorage();

const fileFilter = (
  _req: Request,
  _file: Express.Multer.File,
  cb: multer.FileFilterCallback
): void => {
  // Allow all file types - we'll validate them in our service
  cb(null, true);
};

export const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: 50 * 1024 * 1024, // 50MB max file size
    files: 1, // Only allow 1 file at a time
  },
});

export const validateFileOrTranscript = (req: Request, res: Response, next: NextFunction): void => {
  const { transcript } = req.body as ProcessFileRequest;
  const file = req.file;
  const hasValidTranscript =
    transcript && typeof transcript === 'string' && transcript.trim().length > 0;

  logger.debug('Validating file or transcript request', {
    hasFile: !!file,
    hasTranscript: !!hasValidTranscript,
    filename: file?.originalname,
    fileSize: file?.size,
  });

  // Must have either a file or transcript, but not both
  if (!file && !transcript) {
    logger.warn('Validation failed: neither file nor transcript provided');
    res.status(400).json({
      error: 'Either a file or transcript text is required',
      supportedFormats: {
        text: ['.txt', '.md', '.markdown'],
        audio: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
      },
    });
    return;
  }

  // Check that either file or transcript is provided (but not both)
  if (!file && !hasValidTranscript) {
    logger.warn('Validation failed: neither file nor valid transcript provided');
    res.status(400).json({
      error: 'Either a file or transcript text is required',
      supportedFormats: {
        text: ['.txt', '.md', '.markdown'],
        audio: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
      },
    });
    return;
  }

  if (file && hasValidTranscript) {
    logger.warn('Validation failed: both file and transcript provided');
    res.status(400).json({
      error: 'Please provide either a file OR transcript text, not both',
    });
    return;
  }

  // If transcript is provided, validate it
  if (transcript) {
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

    if (transcript.length > 50000) {
      logger.warn('Validation failed: transcript too long', { length: transcript.length });
      res.status(400).json({ error: 'Transcript is too long (max 50,000 characters)' });
      return;
    }
  }

  // If file is provided, basic validation
  if (file) {
    if (!file.originalname) {
      logger.warn('Validation failed: file has no name');
      res.status(400).json({ error: 'File must have a valid filename' });
      return;
    }

    if (file.size === 0) {
      logger.warn('Validation failed: empty file');
      res.status(400).json({ error: 'File cannot be empty' });
      return;
    }

    // Check file extension exists
    const hasExtension = file.originalname.lastIndexOf('.') > 0;
    if (!hasExtension) {
      logger.warn('Validation failed: file has no extension', { filename: file.originalname });
      res.status(400).json({
        error: 'File must have a valid extension',
        supportedFormats: {
          text: ['.txt', '.md', '.markdown'],
          audio: ['.mp3', '.wav', '.ogg', '.flac', '.m4a', '.mp4', '.webm'],
        },
      });
      return;
    }
  }

  next();
};

// Error handler for multer errors
export const handleMulterError = (
  error: unknown,
  _req: Request,
  res: Response,
  next: NextFunction
): void => {
  if (error instanceof multer.MulterError) {
    logger.warn('Multer error occurred', { error: error.message, code: error.code });

    switch (error.code) {
      case 'LIMIT_FILE_SIZE':
        res.status(400).json({
          error: 'File is too large. Maximum size is 50MB',
          maxSize: '50MB',
        });
        return;

      case 'LIMIT_FILE_COUNT':
        res.status(400).json({
          error: 'Too many files. Only 1 file is allowed',
        });
        return;

      case 'LIMIT_UNEXPECTED_FILE':
        res.status(400).json({
          error: 'Unexpected file field. Use "file" as the field name',
        });
        return;

      default:
        res.status(400).json({
          error: `File upload error: ${error.message}`,
        });
        return;
    }
  }

  // Pass other errors to the global error handler
  next(error);
};
