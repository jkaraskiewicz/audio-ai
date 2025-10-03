import { PromptConfig } from './PromptTypes';

/**
 * Base prompt for all transcript processing
 * This is the main prompt that structures the output
 */
export const BASE_PROMPT: PromptConfig = {
  name: 'base_transcript_processor',
  description: 'Main prompt for processing voice transcripts into structured markdown',
  version: '2.0.0',
  template: `You are an expert productivity assistant. Process this voice transcript and create a structured markdown document.

CRITICAL: You must decide whether this content would benefit from AI commentary/analysis. Use your expert judgment:

**Include Commentary For:**
- Pure questions → Provide comprehensive, accurate answers with practical details
- Mixed content with embedded questions → Answer questions contextually within broader analysis
- Project ideas → Feasibility analysis, recommendations, market insights
- Problem-solving → Solution approaches, best practices, troubleshooting steps
- Technical content → Explanations, best practices, learning resources
- Complex thoughts requiring insights → Thoughtful analysis and perspectives
- Shopping/purchasing → Include current price ranges, best places to buy, product comparisons
- Travel planning → Include estimated costs, specific locations, practical logistics
- Financial topics → Include current market data, price trends, practical investment guidance
- Entertainment/activities → Include pricing, locations, booking information

**NO Commentary For:**
- Simple tasks/reminders (e.g., "buy groceries", "call mom")
- Basic notes without questions or analysis needs
- Short factual statements

Content to process:
---
{{transcript}}
---

**REQUIRED FORMAT** - Return EXACTLY this structure:

---
category: {{category_placeholder}}
filename: {{filename_placeholder}}
commentary_needed: {{commentary_decision}}
---

# {{title_placeholder}}

## Summary
{{summary_placeholder}}

## Ideas
{{ideas_placeholder}}

## Action Items
{{action_items_placeholder}}

## AI Commentary
[INCLUDE THIS SECTION ONLY if commentary_needed: true]
{{commentary_placeholder}}

**IMPORTANT: When providing commentary, be practical and specific:**
- For shopping topics: Include realistic price ranges, specific stores/websites, product comparisons
- For travel planning: Include cost estimates, specific locations, booking advice, logistics
- For financial discussions: Include current market context, price trends, practical investment guidance
- For entertainment/activities: Include pricing information, locations, booking details
- Always aim to provide actionable, real-world information rather than generic advice

## Tags
{{tags_placeholder}}

**Categories to choose from:** projects, daily, personal, work, notes, questions, learning, technical, problem-solving, travel, finance, health, shopping, investment, entertainment, research, or create your own.

Return ONLY the markdown content with NO additional explanations.`,
  variables: ['transcript'],
};
