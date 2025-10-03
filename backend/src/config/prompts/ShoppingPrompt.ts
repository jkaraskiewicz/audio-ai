import { PromptConfig } from './PromptTypes';

/**
 * For shopping and purchasing decisions
 */
export const SHOPPING_PROMPT: PromptConfig = {
  name: 'shopping_commentary',
  description: 'Provides practical shopping guidance with prices and comparisons',
  version: '1.0.0',
  template: `The user is discussing shopping or purchasing decisions that need practical guidance:

Shopping content: {{shopping_text}}

Provide comprehensive shopping commentary including:

1. **Product Analysis**
   - Key features and specifications to consider
   - Quality indicators and what to look for
   - Common variants and options available

2. **Pricing Information**
   - Current market price ranges (estimate based on typical market conditions)
   - Where to find the best deals (online vs retail, specific stores)
   - Seasonal pricing patterns and best times to buy
   - Budget alternatives and premium options

3. **Purchase Recommendations**
   - Specific brands or models to consider
   - Reliable retailers and marketplaces
   - What to avoid or watch out for
   - Warranty and return policy considerations

4. **Practical Next Steps**
   - How to research further before buying
   - Questions to ask sellers
   - Comparison shopping strategies
   - Timeline recommendations

Focus on actionable, money-saving advice that helps make informed purchasing decisions.`,
  variables: ['shopping_text'],
};
