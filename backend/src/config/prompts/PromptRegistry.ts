import { ContentTypePrompts } from './PromptTypes';
import { BASE_PROMPT } from './BasePrompt';
import { QUESTION_PROMPT } from './QuestionPrompt';
import { PROJECT_PROMPT } from './ProjectPrompt';
import { TECHNICAL_PROMPT } from './TechnicalPrompt';
import { PROBLEM_SOLVING_PROMPT } from './ProblemSolvingPrompt';
import { REFLECTION_PROMPT } from './ReflectionPrompt';
import { SHOPPING_PROMPT } from './ShoppingPrompt';
import { TRAVEL_PROMPT } from './TravelPrompt';
import { FINANCE_PROMPT } from './FinancePrompt';
import { PromptEngine } from './PromptEngine';

/**
 * Main prompt configuration registry
 * Centralized access to all prompts
 */
export const PROMPTS: ContentTypePrompts = {
  base: BASE_PROMPT,
  commentary: {
    question: QUESTION_PROMPT,
    project: PROJECT_PROMPT,
    technical: TECHNICAL_PROMPT,
    problemSolving: PROBLEM_SOLVING_PROMPT,
    reflection: REFLECTION_PROMPT,
    shopping: SHOPPING_PROMPT,
    travel: TRAVEL_PROMPT,
    finance: FINANCE_PROMPT,
  },
};

/**
 * Convenience functions for getting prompts
 */
export const getBasePrompt = (transcript: string): string => {
  return PromptEngine.getPrompt(PROMPTS.base, { transcript });
};

export const getCommentaryPrompt = (
  type: keyof typeof PROMPTS.commentary,
  variables: Record<string, any>
): string => {
  const promptConfig = PROMPTS.commentary[type];
  if (!promptConfig) {
    throw new Error(`Unknown commentary type: ${type}`);
  }
  return PromptEngine.getPrompt(promptConfig, variables);
};
