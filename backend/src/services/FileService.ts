import fs from 'fs';
import path from 'path';
import { ParsedContent, FileServiceConfig } from '../types';
import { logger } from '../utils/logger';

export class FileService {
  private config: FileServiceConfig;

  constructor(config: FileServiceConfig) {
    this.config = config;
  }

  saveMarkdownFile(content: string): string {
    try {
      logger.debug('Saving markdown file', { contentLength: content.length });

      const { frontMatter, cleanContent } = this.parseFrontMatter(content);
      const dirPath = this.createDirectoryStructure(frontMatter.category);
      const filename = this.generateSemanticFilename(frontMatter.filename);
      const filepath = path.join(dirPath, filename);

      fs.writeFileSync(filepath, cleanContent, 'utf8');

      logger.info('Markdown file saved successfully', { filepath });

      return filepath;
    } catch (error) {
      logger.error('Failed to save markdown file', error);
      throw new Error('File service failed to save markdown file');
    }
  }

  parseFrontMatter(content: string): ParsedContent {
    const frontMatterMatch = content.match(/^---\n([\s\S]*?)\n---\n([\s\S]*)$/);

    if (!frontMatterMatch) {
      logger.warn('No frontmatter found in content, using defaults');
      return {
        frontMatter: { category: 'notes', filename: 'untitled-note' },
        cleanContent: content,
      };
    }

    const frontMatterText = frontMatterMatch[1];
    const cleanContent = frontMatterMatch[2];

    const categoryMatch = frontMatterText.match(/category:\s*(.+)/);
    const filenameMatch = frontMatterText.match(/filename:\s*(.+)/);

    return {
      frontMatter: {
        category: categoryMatch ? categoryMatch[1].trim() : 'notes',
        filename: filenameMatch ? filenameMatch[1].trim() : 'untitled-note',
      },
      cleanContent: cleanContent.trim(),
    };
  }

  private createDirectoryStructure(category: string): string {
    const dirStructure = this.config.specialCategories[category] || category;
    const dirPath = path.join(this.config.baseDirectory, dirStructure);

    if (!fs.existsSync(dirPath)) {
      fs.mkdirSync(dirPath, { recursive: true });
      logger.debug('Created directory structure', { dirPath });
    }

    return dirPath;
  }

  private generateSemanticFilename(filename: string): string {
    const timestamp = new Date().toISOString().split('T')[0];
    const sanitizedFilename = filename
      .toLowerCase()
      .replace(/[^a-z0-9-]/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '');

    return `${timestamp}_${sanitizedFilename}.md`;
  }
}
