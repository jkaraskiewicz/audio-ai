/**
 * DEPRECATED: This file is maintained for backward compatibility only.
 * Use imports from './prompts/' subdirectory instead.
 * 
 * This file will be removed in a future version.
 */

// Re-export types and core functionality
export type { PromptConfig, ContentTypePrompts } from './prompts/PromptTypes';
export { PROMPTS } from './prompts/PromptRegistry';
export { PromptEngine as BasePromptEngine } from './prompts/PromptEngine';
export { PromptUtils } from './prompts/PromptUtils';

// Backward compatible PromptEngine class
import { PromptEngine as BaseEngine } from './prompts/PromptEngine';
import { getBasePrompt as getBase, getCommentaryPrompt as getCommentary } from './prompts/PromptRegistry';

export class PromptEngine extends BaseEngine {
  static getBasePrompt(transcript: string): string {
    return getBase(transcript);
  }

  static getCommentaryPrompt(
    type: 'question' | 'project' | 'technical' | 'problemSolving' | 'reflection' | 'shopping' | 'travel' | 'finance',
    variables: Record<string, any>
  ): string {
    return getCommentary(type, variables);
  }
}