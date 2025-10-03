import { PROMPTS } from './PromptRegistry';

/**
 * Prompt utilities for common operations
 */
export class PromptUtils {
  /**
   * Get information about all available prompts
   */
  static getPromptInfo(): Array<{
    name: string;
    type: 'base' | 'commentary';
    description: string;
    version: string;
    variables: string[];
  }> {
    const info: Array<{
      name: string;
      type: 'base' | 'commentary';
      description: string;
      version: string;
      variables: string[];
    }> = [
      {
        name: PROMPTS.base.name,
        type: 'base',
        description: PROMPTS.base.description,
        version: PROMPTS.base.version,
        variables: PROMPTS.base.variables,
      },
    ];

    // Add commentary prompts
    Object.entries(PROMPTS.commentary).forEach(([_key, prompt]) => {
      info.push({
        name: prompt.name,
        type: 'commentary',
        description: prompt.description,
        version: prompt.version,
        variables: prompt.variables,
      });
    });

    return info;
  }

  /**
   * Validate a prompt template for common issues
   */
  static validatePrompt(template: string): { valid: boolean; issues: string[] } {
    const issues: string[] = [];

    // Check for unmatched brackets
    const openBrackets = (template.match(/{{/g) || []).length;
    const closeBrackets = (template.match(/}}/g) || []).length;
    if (openBrackets !== closeBrackets) {
      issues.push('Unmatched template brackets {{}}');
    }

    // Check for empty variables
    const emptyVars = template.match(/{{\s*}}/g);
    if (emptyVars) {
      issues.push('Empty variable placeholders found');
    }

    // Check for reasonable length
    if (template.length < 50) {
      issues.push('Template seems too short to be useful');
    }

    if (template.length > 10000) {
      issues.push('Template is very long and may hit token limits');
    }

    return {
      valid: issues.length === 0,
      issues,
    };
  }
}
