import { PromptConfig } from './PromptTypes';

/**
 * For pure questions that need comprehensive answers
 */
export const QUESTION_PROMPT: PromptConfig = {
  name: 'question_commentary',
  description: 'Generates comprehensive answers for pure questions',
  version: '1.0.0',
  template: `The user has asked questions that need thoughtful, accurate answers with helpful context:

Questions to address:
{{questions}}

Full context: {{full_text}}

Please provide answers that are:
- Accurate and well-researched
- Include relevant context and background information
- Mention uncertainty if you're not completely sure
- Provide additional insights that might be helpful
- Use clear, accessible language

Focus on being educational and comprehensive while remaining concise.`,
  variables: ['questions', 'full_text'],
};
