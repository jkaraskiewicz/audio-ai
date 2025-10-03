import { PromptConfig } from './PromptTypes';

/**
 * For technical content requiring explanations
 */
export const TECHNICAL_PROMPT: PromptConfig = {
  name: 'technical_commentary',
  description: 'Provides technical explanations and best practices',
  version: '1.0.0',
  template: `The user has shared technical content that would benefit from expert commentary:

Technical content: {{technical_text}}

Provide helpful commentary including:

1. **Concept Explanation**
   - Clear explanation of technical concepts
   - How different pieces fit together
   - Context and background information

2. **Best Practices**
   - Industry standards and conventions
   - Performance considerations
   - Security and reliability aspects

3. **Implementation Guidance**
   - Practical implementation tips
   - Common patterns and approaches
   - Tools and resources to consider

4. **Learning Path**
   - Suggested next steps for deeper understanding
   - Related concepts to explore
   - Useful resources and documentation

Keep explanations clear and practical, suitable for someone looking to understand or implement these concepts.`,
  variables: ['technical_text'],
};
