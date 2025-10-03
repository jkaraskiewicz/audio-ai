/**
 * Base prompt configuration interface
 */
export interface PromptConfig {
  name: string;
  description: string;
  version: string;
  template: string;
  variables: string[];
}

/**
 * Content type specific prompt configurations
 */
export interface ContentTypePrompts {
  base: PromptConfig;
  commentary: {
    question: PromptConfig;
    project: PromptConfig;
    technical: PromptConfig;
    problemSolving: PromptConfig;
    reflection: PromptConfig;
    shopping: PromptConfig;
    travel: PromptConfig;
    finance: PromptConfig;
  };
}
