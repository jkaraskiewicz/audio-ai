import { PromptConfig } from './PromptTypes';

/**
 * Prompt template engine
 * Handles variable substitution in prompt templates
 */
export class PromptEngine {
  /**
   * Process a prompt template with variables
   */
  static processTemplate(template: string, variables: Record<string, any>): string {
    let processed = template;

    // Replace all variables in the format {{variable_name}}
    Object.entries(variables).forEach(([key, value]) => {
      const placeholder = `{{${key}}}`;
      // Convert arrays to formatted strings if needed
      const stringValue = Array.isArray(value) ? value.join('\n- ') : String(value);
      processed = processed.replace(new RegExp(placeholder, 'g'), stringValue);
    });

    return processed;
  }

  /**
   * Get a prompt with variables filled in
   */
  static getPrompt(promptConfig: PromptConfig, variables: Record<string, any>): string {
    // Validate that all required variables are provided
    const missingVars = promptConfig.variables.filter((varName) => !(varName in variables));
    if (missingVars.length > 0) {
      throw new Error(
        `Missing required variables for prompt '${promptConfig.name}': ${missingVars.join(', ')}`
      );
    }

    return this.processTemplate(promptConfig.template, variables);
  }
}
