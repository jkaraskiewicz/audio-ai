import { PromptConfig } from './PromptTypes';

/**
 * For problem-solving scenarios
 */
export const PROBLEM_SOLVING_PROMPT: PromptConfig = {
  name: 'problem_solving_commentary',
  description: 'Provides analysis and solutions for problems or challenges',
  version: '1.0.0',
  template: `The user has described a problem or challenge that needs analysis and solutions:

Problem description: {{problem_text}}

Provide comprehensive problem-solving commentary:

1. **Problem Analysis**
   - Root cause identification
   - Key factors and constraints
   - Problem scope and impact

2. **Solution Approaches**
   - Multiple solution options
   - Pros and cons of each approach
   - Risk assessment and mitigation

3. **Implementation Plan**
   - Step-by-step action plan
   - Resource requirements
   - Timeline and priorities

4. **Prevention & Learning**
   - How to prevent similar issues
   - Lessons learned and best practices
   - Monitoring and early warning signs

Focus on practical, actionable advice that can be implemented immediately.`,
  variables: ['problem_text'],
};
