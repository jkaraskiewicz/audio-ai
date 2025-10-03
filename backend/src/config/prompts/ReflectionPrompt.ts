import { PromptConfig } from './PromptTypes';

/**
 * For personal reflections requiring supportive insights
 */
export const REFLECTION_PROMPT: PromptConfig = {
  name: 'reflection_commentary',
  description: 'Provides thoughtful insights for personal reflections',
  version: '1.0.0',
  template: `The user has shared personal thoughts or reflections that would benefit from supportive commentary:

Reflection content: {{reflection_text}}

Provide thoughtful, supportive commentary that:

1. **Acknowledgment**
   - Validate their thoughts and feelings
   - Show understanding of their perspective
   - Recognize the complexity of their situation

2. **Insights & Perspectives**
   - Offer alternative viewpoints to consider
   - Share relevant frameworks or concepts
   - Help them see patterns or connections

3. **Growth Opportunities**
   - Suggest areas for further reflection
   - Questions that might deepen understanding
   - Potential learning or development paths

4. **Encouragement & Support**
   - Affirm their strengths and capabilities
   - Provide motivation and positive reinforcement
   - Remind them of their progress and potential

Be empathetic and supportive while providing valuable insights that help with personal growth and self-understanding.`,
  variables: ['reflection_text'],
};
