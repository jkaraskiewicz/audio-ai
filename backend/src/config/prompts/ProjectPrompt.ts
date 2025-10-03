import { PromptConfig } from './PromptTypes';

/**
 * For project ideas requiring feasibility analysis
 */
export const PROJECT_PROMPT: PromptConfig = {
  name: 'project_commentary',
  description: 'Provides feasibility analysis and recommendations for project ideas',
  version: '1.0.0',
  template: `The user has shared a project idea that needs analysis and guidance:

Project concept: {{project_text}}

Provide comprehensive commentary covering:

1. **Feasibility Assessment**
   - Technical feasibility and complexity
   - Resource requirements (time, skills, budget)
   - Realistic timeline expectations

2. **Market Analysis**
   - Similar existing solutions
   - Market opportunity and competition
   - Target audience considerations

3. **Implementation Guidance**
   - Recommended technology stack or approach
   - Key challenges and potential solutions
   - Suggested next steps and milestones

4. **Success Factors**
   - Critical requirements for success
   - Common pitfalls to avoid
   - Success metrics to track

Be encouraging but realistic. Focus on actionable insights that help move the project forward.`,
  variables: ['project_text'],
};
