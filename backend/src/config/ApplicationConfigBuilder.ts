import { AppConfig } from '../types';

/**
 * Single Responsibility: Build and validate application configuration
 * Uncle Bob Approved: Focused on app config creation with clear validation
 */
export class ApplicationConfigBuilder {
  private static readonly DEFAULT_PORT = 3000;
  private static readonly MIN_PORT = 1;
  private static readonly MAX_PORT = 65535;
  private static readonly PLACEHOLDER_API_KEY = 'your_api_key_here';

  buildApplicationConfig(): AppConfig {
    const geminiApiKey = this.getGeminiApiKey();
    const port = this.getValidatedPort();
    const baseDirectory = this.getBaseDirectory();

    return {
      port,
      geminiApiKey,
      baseDirectory,
    };
  }

  private getGeminiApiKey(): string {
    const apiKey = process.env.GEMINI_API_KEY;

    if (!apiKey || apiKey === ApplicationConfigBuilder.PLACEHOLDER_API_KEY) {
      throw new Error('GEMINI_API_KEY not configured. Please add your API key to .env file');
    }

    return apiKey;
  }

  private getValidatedPort(): number {
    const portString = process.env.PORT || ApplicationConfigBuilder.DEFAULT_PORT.toString();
    const port = parseInt(portString, 10);

    if (this.isInvalidPort(port)) {
      throw new Error(
        `Invalid PORT value: ${portString}. Must be a number between ${ApplicationConfigBuilder.MIN_PORT} and ${ApplicationConfigBuilder.MAX_PORT}.`
      );
    }

    return port;
  }

  private isInvalidPort(port: number): boolean {
    return (
      isNaN(port) ||
      port < ApplicationConfigBuilder.MIN_PORT ||
      port > ApplicationConfigBuilder.MAX_PORT
    );
  }

  private getBaseDirectory(): string {
    return process.env.BASE_DIRECTORY || 'processed';
  }
}
