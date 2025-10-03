import { PromptConfig } from './PromptTypes';

/**
 * For financial topics and investment discussions
 */
export const FINANCE_PROMPT: PromptConfig = {
  name: 'finance_commentary',
  description: 'Provides financial guidance with current market context',
  version: '1.0.0',
  template: `The user is discussing financial topics that need practical guidance and market context:

Financial content: {{finance_text}}

Provide comprehensive financial commentary including:

1. **Market Context & Current Conditions**
   - Current market trends and recent performance for mentioned assets
   - General market sentiment and key factors affecting prices
   - Recent news or events impacting the financial instruments discussed
   - Historical context and typical price ranges

2. **Investment Analysis**
   - Risk assessment and considerations
   - Diversification implications
   - Time horizon considerations
   - Tax implications to be aware of

3. **Practical Guidance**
   - Reputable platforms and brokers for execution
   - Cost considerations (fees, commissions, expense ratios)
   - How to research further before making decisions
   - Dollar-cost averaging and timing strategies

4. **Risk Management & Education**
   - Important disclaimers about investment risks
   - Suggested educational resources
   - When to consult with financial professionals
   - Portfolio allocation considerations

IMPORTANT: Always include appropriate disclaimers about investment risks and the importance of doing personal research. Focus on education rather than specific investment recommendations.`,
  variables: ['finance_text'],
};
