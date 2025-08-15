/**
 * Centralized prompt management system
 * Makes it easy for engineers to modify and extend LLM prompts
 */

/**
 * Base prompt configuration interface
 */
export interface PromptConfig {
  name: string;
  description: string;
  version: string;
  template: string;
  variables: string[];
}

/**
 * Content type specific prompt configurations
 */
export interface ContentTypePrompts {
  base: PromptConfig;
  commentary: {
    question: PromptConfig;
    project: PromptConfig;
    technical: PromptConfig;
    problemSolving: PromptConfig;
    reflection: PromptConfig;
  };
}

/**
 * Main prompt configuration
 * Modify these prompts to change AI behavior
 */
export const PROMPTS: ContentTypePrompts = {
  /**
   * Base prompt for all transcript processing
   * This is the main prompt that structures the output
   */
  base: {
    name: 'base_transcript_processor',
    description: 'Main prompt for processing voice transcripts into structured markdown',
    version: '2.0.0',
    template: `You are an expert productivity assistant. Process this voice transcript and create a structured markdown document.

CRITICAL: You must decide whether this content would benefit from AI commentary/analysis. Use your expert judgment:

**Include Commentary For:**
- Pure questions → Provide comprehensive, accurate answers
- Mixed content with embedded questions → Answer questions contextually within broader analysis
- Project ideas → Feasibility analysis, recommendations, market insights
- Problem-solving → Solution approaches, best practices, troubleshooting steps
- Technical content → Explanations, best practices, learning resources
- Complex thoughts requiring insights → Thoughtful analysis and perspectives

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

## Tags
{{tags_placeholder}}

**Categories to choose from:** projects, daily, personal, work, notes, questions, learning, technical, problem-solving, travel, finance, health, or create your own.

Return ONLY the markdown content with NO additional explanations.`,
    variables: ['transcript'],
  },

  /**
   * Commentary-specific prompts
   * These are used when the base prompt determines commentary is needed
   */
  commentary: {
    /**
     * For pure questions that need comprehensive answers
     */
    question: {
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
    },

    /**
     * For project ideas requiring feasibility analysis
     */
    project: {
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
    },

    /**
     * For technical content requiring explanations
     */
    technical: {
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
    },

    /**
     * For problem-solving scenarios
     */
    problemSolving: {
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
    },

    /**
     * For personal reflections requiring supportive insights
     */
    reflection: {
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
    },
  },
};

/**
 * Prompt template engine
 * Handles variable substitution in prompt templates
 */
export class PromptEngine {
  /**
   * Process a prompt template with variables
   */
  static processTemplate(template: string, variables: Record<string, any>): string {
    let processed = template;

    // Replace all variables in the format {{variable_name}}
    Object.entries(variables).forEach(([key, value]) => {
      const placeholder = `{{${key}}}`;
      // Convert arrays to formatted strings if needed
      const stringValue = Array.isArray(value) ? value.join('\n- ') : String(value);
      processed = processed.replace(new RegExp(placeholder, 'g'), stringValue);
    });

    return processed;
  }

  /**
   * Get a prompt with variables filled in
   */
  static getPrompt(promptConfig: PromptConfig, variables: Record<string, any>): string {
    // Validate that all required variables are provided
    const missingVars = promptConfig.variables.filter((varName) => !(varName in variables));
    if (missingVars.length > 0) {
      throw new Error(
        `Missing required variables for prompt '${promptConfig.name}': ${missingVars.join(', ')}`
      );
    }

    return this.processTemplate(promptConfig.template, variables);
  }

  /**
   * Get the base transcript processing prompt
   */
  static getBasePrompt(transcript: string): string {
    return this.getPrompt(PROMPTS.base, { transcript });
  }

  /**
   * Get a commentary prompt for specific content type
   */
  static getCommentaryPrompt(
    type: keyof typeof PROMPTS.commentary,
    variables: Record<string, any>
  ): string {
    const promptConfig = PROMPTS.commentary[type];
    if (!promptConfig) {
      throw new Error(`Unknown commentary type: ${type}`);
    }

    return this.getPrompt(promptConfig, variables);
  }
}

/**
 * Prompt utilities for common operations
 */
export class PromptUtils {
  /**
   * Get information about all available prompts
   */
  static getPromptInfo(): Array<{
    name: string;
    type: 'base' | 'commentary';
    description: string;
    version: string;
    variables: string[];
  }> {
    const info: Array<{
      name: string;
      type: 'base' | 'commentary';
      description: string;
      version: string;
      variables: string[];
    }> = [
      {
        name: PROMPTS.base.name,
        type: 'base',
        description: PROMPTS.base.description,
        version: PROMPTS.base.version,
        variables: PROMPTS.base.variables,
      },
    ];

    // Add commentary prompts
    Object.entries(PROMPTS.commentary).forEach(([_key, prompt]) => {
      info.push({
        name: prompt.name,
        type: 'commentary',
        description: prompt.description,
        version: prompt.version,
        variables: prompt.variables,
      });
    });

    return info;
  }

  /**
   * Validate a prompt template for common issues
   */
  static validatePrompt(template: string): { valid: boolean; issues: string[] } {
    const issues: string[] = [];

    // Check for unmatched brackets
    const openBrackets = (template.match(/{{/g) || []).length;
    const closeBrackets = (template.match(/}}/g) || []).length;
    if (openBrackets !== closeBrackets) {
      issues.push('Unmatched template brackets {{}}');
    }

    // Check for empty variables
    const emptyVars = template.match(/{{\s*}}/g);
    if (emptyVars) {
      issues.push('Empty variable placeholders found');
    }

    // Check for reasonable length
    if (template.length < 50) {
      issues.push('Template seems too short to be useful');
    }

    if (template.length > 10000) {
      issues.push('Template is very long and may hit token limits');
    }

    return {
      valid: issues.length === 0,
      issues,
    };
  }
}
