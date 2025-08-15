import express from 'express';
import { ServiceFactory } from './utils/ServiceFactory';
import { validateTranscriptRequest } from './middleware/validation';
import { upload, validateFileOrTranscript, handleMulterError } from './middleware/fileValidation';
import { errorHandler } from './middleware/errorHandler';
import { logger } from './utils/logger';

const app = express();
const serviceFactory = ServiceFactory.getInstance();
const config = serviceFactory.getConfig();
const transcriptController = serviceFactory.createTranscriptController();

// Middleware
app.use(express.json({ limit: '1mb' }));
app.use(express.urlencoded({ extended: true }));

// Add request logging middleware
app.use((req, _res, next) => {
  logger.info(`${req.method} ${req.path}`, { ip: req.ip });
  next();
});

// Health check endpoint
app.get('/', transcriptController.healthCheck);
app.get('/health', transcriptController.healthCheck);

// Legacy transcript-only endpoint (backward compatibility)
app.post('/process', validateTranscriptRequest, transcriptController.processTranscript);

// New unified endpoint that accepts both files and text
app.post(
  '/process-file',
  upload.single('file'),
  handleMulterError,
  validateFileOrTranscript,
  transcriptController.processFileOrTranscript
);

// Error handling middleware (must be last)
app.use(errorHandler);

// Graceful shutdown
process.on('SIGTERM', () => {
  logger.info('SIGTERM received, shutting down gracefully');
  process.exit(0);
});

process.on('SIGINT', () => {
  logger.info('SIGINT received, shutting down gracefully');
  process.exit(0);
});

app.listen(config.port, () => {
  logger.info(`Server is running at http://localhost:${config.port}`);
});

export { app };