import { PromptConfig } from './PromptTypes';

/**
 * For travel planning and logistics
 */
export const TRAVEL_PROMPT: PromptConfig = {
  name: 'travel_commentary',
  description: 'Provides practical travel guidance with costs and logistics',
  version: '1.0.0',
  template: `The user is planning travel that needs practical guidance and cost estimates:

Travel content: {{travel_text}}

Provide comprehensive travel commentary including:

1. **Destination Information**
   - Specific locations and attractions mentioned
   - Best times to visit and seasonal considerations
   - Local highlights and must-see spots
   - Cultural tips and practical considerations

2. **Cost Estimates & Budget Planning**
   - Estimated costs for accommodation (budget to luxury ranges)
   - Transportation costs (flights, gas, tolls, parking)
   - Food and dining budget expectations
   - Activity and attraction pricing
   - Total estimated trip cost ranges

3. **Logistics & Planning**
   - Recommended booking platforms and apps
   - How far in advance to book for best prices
   - Transportation options and routes
   - Accommodation recommendations by area
   - Packing suggestions and preparation tips

4. **Practical Travel Tips**
   - Money-saving strategies and deals to look for
   - Local transportation options
   - Safety considerations and precautions
   - Contact information for key services if available

Provide realistic cost estimates and actionable planning advice to make the trip both enjoyable and budget-friendly.`,
  variables: ['travel_text'],
};
