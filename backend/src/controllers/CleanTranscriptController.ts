import { Request, Response, NextFunction } from 'express';
import { TranscriptProcessorService } from '../services/TranscriptProcessorService';
import { TranscriptText } from '../domain/TranscriptText';
import { AudioFile } from '../domain/AudioFile';
import { logger } from '../utils/logger';

/**
 * Clean Controller following Uncle Bob principles
 * - Thin coordinator that delegates to services
 * - No business logic in controller
 * - Single responsibility: HTTP request/response coordination
 * - Short methods (< 20 lines)
 */
export class CleanTranscriptController {
  constructor(private transcriptProcessorService: TranscriptProcessorService) {}

  processTranscript = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const transcriptRequest = this.createTranscriptRequest(req);
      const result = await this.transcriptProcessorService.processTranscript(
        transcriptRequest.getText()
      );
      this.sendJsonResponse(res, result);
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
      const fileOrTranscriptRequest = this.createFileOrTranscriptRequest(req);
      const legacyRequest = this.convertToLegacyRequest(fileOrTranscriptRequest, req);
      const result = await this.transcriptProcessorService.processFileOrTranscript(legacyRequest);
      this.sendJsonResponse(res, result);
    } catch (error) {
      next(error);
    }
  };

  checkHealth = (_req: Request, res: Response): void => {
    const healthStatus = this.createHealthStatus();
    this.sendJsonResponse(res, healthStatus);
  };

  private createTranscriptRequest(req: Request): TranscriptText {
    const { transcript } = req.body;
    this.logTranscriptRequest(req, transcript);
    return TranscriptText.create(transcript);
  }

  private createFileOrTranscriptRequest(req: Request): FileOrTranscriptRequest {
    const { transcript } = req.body;
    const file = req.file;

    this.logFileOrTranscriptRequest(req, file, transcript);

    return {
      audioFile: file ? AudioFile.fromMulterFile(file) : undefined,
      transcriptText: transcript ? TranscriptText.create(transcript) : undefined,
    };
  }

  private createHealthStatus(): HealthStatus {
    return {
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'audio-ai',
    };
  }

  private sendJsonResponse(res: Response, data: any): void {
    res.status(200).json(data);
  }

  private logTranscriptRequest(req: Request, transcript?: string): void {
    logger.info('Processing transcript request', {
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      transcriptLength: transcript?.length,
    });
  }

  private convertToLegacyRequest(
    fileOrTranscriptRequest: FileOrTranscriptRequest,
    req: Request
  ): any {
    // Adapter pattern to convert clean domain objects back to legacy format
    const { transcript } = req.body;
    const file = req.file;

    return {
      file,
      transcript,
    };
  }

  private logFileOrTranscriptRequest(
    req: Request,
    file?: Express.Multer.File,
    transcript?: string
  ): void {
    logger.info('Processing file or transcript request', {
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      hasFile: !!file,
      hasTranscript: !!transcript,
      filename: file?.originalname,
      fileSize: file?.size,
      transcriptLength: transcript?.length,
    });
  }
}

// Clean interfaces for the controller's use
interface FileOrTranscriptRequest {
  readonly audioFile?: AudioFile;
  readonly transcriptText?: TranscriptText;
}

interface HealthStatus {
  readonly status: string;
  readonly timestamp: string;
  readonly service: string;
}
