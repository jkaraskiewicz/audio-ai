import { Request, Response, NextFunction } from 'express';
import { TranscriptProcessorService } from '../services/TranscriptProcessorService';
import { ProcessTranscriptRequest, ProcessFileRequest } from '../types';
import { logger } from '../utils/logger';

export class TranscriptController {
  constructor(private transcriptProcessorService: TranscriptProcessorService) {}

  processTranscript = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const { transcript } = req.body as ProcessTranscriptRequest;

      logger.info('Processing transcript request', {
        ip: req.ip,
        userAgent: req.get('User-Agent'),
        transcriptLength: transcript?.length,
      });

      const result = await this.transcriptProcessorService.processTranscript(transcript!);

      res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  };

  processFileOrTranscript = async (
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<void> => {
    try {
      const { transcript } = req.body as ProcessFileRequest;
      const file = req.file;

      logger.info('Processing file or transcript request', {
        ip: req.ip,
        userAgent: req.get('User-Agent'),
        hasFile: !!file,
        hasTranscript: !!transcript,
        filename: file?.originalname,
        fileSize: file?.size,
        transcriptLength: transcript?.length,
      });

      // For text transcripts, process synchronously (fast)
      if (transcript) {
        const result = await this.transcriptProcessorService.processFileOrTranscript({
          file,
          transcript,
        });
        res.status(200).json(result);
        return;
      }

      // For files (especially audio), respond immediately and process in background
      if (file) {
        logger.info('Starting background processing for file', {
          filename: file.originalname,
          fileSize: file.size,
        });

        // Respond immediately with success
        res.status(200).json({
          message: 'File received and processing started',
          filename: file.originalname,
          status: 'processing',
          timestamp: new Date().toISOString(),
        });

        // Process in background (fire and forget)
        setImmediate(async () => {
          try {
            logger.info('Background processing started', { filename: file.originalname });
            const result = await this.transcriptProcessorService.processFileOrTranscript({
              file,
              transcript,
            });
            logger.info('Background processing completed successfully', {
              filename: file.originalname,
              savedPath: result.saved_to,
            });
          } catch (backgroundError) {
            logger.error('Background processing failed', {
              filename: file.originalname,
              error: backgroundError instanceof Error ? backgroundError.message : 'Unknown error',
              stack: backgroundError instanceof Error ? backgroundError.stack : undefined,
            });
          }
        });
      }
    } catch (error) {
      next(error);
    }
  };

  healthCheck = (_req: Request, res: Response): void => {
    res.status(200).json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'audio-ai',
    });
  };
}
