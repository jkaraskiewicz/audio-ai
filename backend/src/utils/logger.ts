type LogLevel = 'info' | 'warn' | 'error' | 'debug';

class Logger {
  private isDevelopment = process.env.NODE_ENV !== 'production';

  private formatMessage(level: LogLevel, message: string, meta?: unknown): string {
    const timestamp = new Date().toISOString();
    const formattedMeta = meta ? ` ${JSON.stringify(meta)}` : '';
    return `[${timestamp}] ${level.toUpperCase()}: ${message}${formattedMeta}`;
  }

  info(message: string, meta?: unknown): void {
    console.log(this.formatMessage('info', message, meta));
  }

  warn(message: string, meta?: unknown): void {
    console.warn(this.formatMessage('warn', message, meta));
  }

  error(message: string, error?: Error | unknown): void {
    const errorInfo =
      error instanceof Error ? { message: error.message, stack: error.stack } : error;
    console.error(this.formatMessage('error', message, errorInfo));
  }

  debug(message: string, meta?: unknown): void {
    if (this.isDevelopment) {
      console.debug(this.formatMessage('debug', message, meta));
    }
  }
}

export const logger = new Logger();
