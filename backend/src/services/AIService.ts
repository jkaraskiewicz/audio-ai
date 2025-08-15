import { GoogleGenerativeAI } from '@google/generative-ai';
import { AIServiceConfig } from '../types';
import { logger } from '../utils/logger';
import { PromptEngine } from '../config/prompts';

interface LLMMetadata {
  category: string;
  filename: string;
  commentary_needed: boolean;
}

export class AIService {
  private genAI: GoogleGenerativeAI;
  private model: string;

  constructor(config: AIServiceConfig) {
    this.genAI = new GoogleGenerativeAI(config.apiKey);
    this.model = config.model;
  }

  async processTranscript(transcript: string): Promise<string> {
    try {
      logger.debug('Processing transcript with AI service', {
        transcriptLength: transcript.length,
      });

      const model = this.genAI.getGenerativeModel({ model: this.model });
      const prompt = this.buildEnhancedPrompt(transcript);

      const result = await model.generateContent(prompt);
      const response = await result.response;
      let text = response.text();

      // Parse LLM metadata and process accordingly
      const metadata = this.parseMetadata(text);
      text = this.processLLMResponse(text, metadata);

      logger.debug('AI service response received', {
        responseLength: text.length,
        commentaryNeeded: metadata?.commentary_needed,
      });

      return this.stripMarkdown(text);
    } catch (error) {
      logger.error('Failed to process transcript with AI service', error);
      throw new Error('AI service failed to process transcript');
    }
  }

  private buildEnhancedPrompt(transcript: string): string {
    return PromptEngine.getBasePrompt(transcript);
  }

  private parseMetadata(response: string): LLMMetadata | null {
    try {
      // Extract metadata from YAML frontmatter
      const yamlMatch = response.match(/^---\s*\n([\s\S]*?)\n---/);
      if (!yamlMatch) {
        logger.warn('No metadata found in LLM response');
        return null;
      }

      const yamlContent = yamlMatch[1];
      const metadata: Partial<LLMMetadata> = {};

      // Parse each field
      const categoryMatch = yamlContent.match(/category:\s*(.+)/);
      const filenameMatch = yamlContent.match(/filename:\s*(.+)/);
      const commentaryMatch = yamlContent.match(/commentary_needed:\s*(true|false)/);

      if (categoryMatch) metadata.category = categoryMatch[1].trim();
      if (filenameMatch) metadata.filename = filenameMatch[1].trim();
      if (commentaryMatch) metadata.commentary_needed = commentaryMatch[1] === 'true';

      return metadata as LLMMetadata;
    } catch (error) {
      logger.error('Failed to parse LLM metadata', error);
      return null;
    }
  }

  private processLLMResponse(response: string, metadata: LLMMetadata | null): string {
    if (!metadata) {
      return response; // Return as-is if metadata parsing failed
    }

    // If commentary is not needed, remove any AI Commentary section
    if (!metadata.commentary_needed) {
      return this.removeCommentarySection(response);
    }

    // Commentary is included by LLM if needed, return as-is
    return response;
  }

  private removeCommentarySection(content: string): string {
    // Remove ## AI Commentary section and everything until next ## section or end
    return content.replace(/## AI Commentary[\s\S]*?(?=## |$)/g, '').trim();
  }

  private stripMarkdown(text: string): string {
    return text.replace(/```markdown\n|```\n|```/g, '').trim();
  }
}
