import { ProcessingResult } from '../domain/ProcessingResult';
import { logger } from '../utils/logger';

/**
 * Single Responsibility: Generate AI-enhanced content from processed text
 * Uncle Bob Approved: Focused service for AI interaction
 */
export interface IAIContentGenerator {
  generateStructuredContent(processingResult: ProcessingResult): Promise<GeneratedContent>;
}

export class AIContentGenerator implements IAIContentGenerator {
  constructor(private aiService: IAIService) {}

  async generateStructuredContent(processingResult: ProcessingResult): Promise<GeneratedContent> {
    logger.debug('Starting AI content generation', {
      inputLength: processingResult.getTextLength(),
      processingMethod: processingResult.getProcessingMethod(),
      containsActionItems: processingResult.containsActionItems(),
    });

    const prompt = this.createStructuredPrompt(processingResult);
    const aiResponse = await this.aiService.generateContent(prompt);

    const generatedContent = GeneratedContent.create(
      aiResponse,
      processingResult.getProcessingMethod(),
      processingResult.getFileType()
    );

    logger.info('AI content generation completed', {
      originalLength: processingResult.getTextLength(),
      generatedLength: generatedContent.getContent().length,
      processingMethod: processingResult.getProcessingMethod(),
    });

    return generatedContent;
  }

  private createStructuredPrompt(processingResult: ProcessingResult): string {
    const text = processingResult.getExtractedText();
    const hasActionItems = processingResult.containsActionItems();

    return `
Please analyze and organize the following ${processingResult.getFileType()} content:

"${text}"

Organize it into a structured markdown document with:
1. A clear title
2. Summary section
3. Key ideas or insights
4. Action items (if any)${hasActionItems ? ' - I noticed this content likely contains action items' : ''}
5. Relevant tags

Make it actionable and well-organized. Focus on extracting the most important information.
    `.trim();
  }
}

export class GeneratedContent {
  private constructor(
    private readonly content: string,
    private readonly processingMethod: string,
    private readonly sourceType: string
  ) {}

  static create(content: string, processingMethod: string, sourceType: string): GeneratedContent {
    if (!content || content.trim().length === 0) {
      throw new Error('Generated content cannot be empty');
    }

    return new GeneratedContent(content.trim(), processingMethod, sourceType);
  }

  getContent(): string {
    return this.content;
  }

  getProcessingMethod(): string {
    return this.processingMethod;
  }

  getSourceType(): string {
    return this.sourceType;
  }

  getContentLength(): number {
    return this.content.length;
  }

  createSummary(): ContentSummary {
    return {
      contentLength: this.getContentLength(),
      processingMethod: this.processingMethod,
      sourceType: this.sourceType,
      preview: this.content.substring(0, 200) + (this.content.length > 200 ? '...' : ''),
    };
  }
}

export interface ContentSummary {
  readonly contentLength: number;
  readonly processingMethod: string;
  readonly sourceType: string;
  readonly preview: string;
}

// Interface for AI service dependency
export interface IAIService {
  generateContent(prompt: string): Promise<string>;
}
