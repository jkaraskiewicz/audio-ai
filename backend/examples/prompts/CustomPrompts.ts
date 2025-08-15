/**
 * Custom Prompt Examples
 * 
 * This file shows how to customize and extend the prompt system
 * for different use cases and domains.
 */

import { PromptConfig, ContentTypePrompts, PromptEngine } from '../../src/config/prompts';

/**
 * Example: Medical/Healthcare focused prompts
 * For healthcare professionals using voice notes
 */
export const MEDICAL_PROMPTS: ContentTypePrompts = {
  base: {
    name: 'medical_transcript_processor',
    description: 'Medical-focused prompt for processing healthcare voice transcripts',
    version: '1.0.0',
    template: `You are a medical documentation assistant. Process this healthcare voice transcript and create a structured clinical document.

CRITICAL: Follow HIPAA guidelines and medical documentation standards. Always include appropriate disclaimers.

**Include Commentary For:**
- Clinical questions → Evidence-based medical information and references
- Patient cases → Differential diagnosis considerations and treatment options
- Medical procedures → Best practices, contraindications, and safety considerations
- Symptom descriptions → Possible diagnoses and recommended next steps
- Drug interactions → Safety warnings and alternative options

**NO Commentary For:**
- Administrative tasks (scheduling, billing, simple reminders)
- Personal notes unrelated to patient care

Content to process:
---
{{transcript}}
---

**REQUIRED FORMAT** - Return EXACTLY this structure:

---
category: {{category_placeholder}}
filename: {{filename_placeholder}}
commentary_needed: {{commentary_decision}}
document_type: clinical_note
---

# {{title_placeholder}}

## Chief Complaint
{{chief_complaint_placeholder}}

## History of Present Illness
{{hpi_placeholder}}

## Assessment
{{assessment_placeholder}}

## Plan
{{plan_placeholder}}

## Medical Commentary
[INCLUDE THIS SECTION ONLY if commentary_needed: true]
{{medical_commentary_placeholder}}

## Follow-up
{{followup_placeholder}}

**DISCLAIMER:** This documentation is for reference only. Always consult current medical guidelines and use clinical judgment.

**Categories to choose from:** clinical_notes, patient_care, medical_research, continuing_education, case_studies, or create your own.`,
    variables: ['transcript'],
  },

  commentary: {
    question: {
      name: 'medical_question_commentary',
      description: 'Provides evidence-based medical answers',
      version: '1.0.0',
      template: `Provide evidence-based medical information for these clinical questions:

Questions: {{questions}}
Context: {{full_text}}

Include:
1. **Evidence-Based Response**
   - Current medical guidelines and recommendations
   - Clinical research findings and statistics
   - Level of evidence classification

2. **Clinical Considerations**
   - Patient factors to consider
   - Contraindications and precautions
   - Risk-benefit analysis

3. **Additional Resources**
   - Relevant medical guidelines
   - Recent research studies
   - Professional society recommendations

**MEDICAL DISCLAIMER:** This information is for educational purposes only and should not replace professional medical judgment or patient-specific clinical decision making.`,
      variables: ['questions', 'full_text'],
    },

    project: {
      name: 'medical_project_commentary',
      description: 'Analysis for medical research or quality improvement projects',
      version: '1.0.0',
      template: `Analyze this medical/healthcare project proposal:

Project: {{project_text}}

Provide analysis covering:

1. **Clinical Feasibility**
   - Medical validity and safety considerations
   - Regulatory requirements (FDA, IRB, etc.)
   - Evidence base and literature review needs

2. **Implementation Strategy**
   - Healthcare setting requirements
   - Staff training and competency needs
   - Technology and resource requirements

3. **Quality & Safety**
   - Patient safety considerations
   - Quality metrics and outcomes
   - Risk mitigation strategies

4. **Compliance & Ethics**
   - Regulatory compliance requirements
   - Ethical considerations and IRB review
   - Data privacy and HIPAA compliance`,
      variables: ['project_text'],
    },

    technical: {
      name: 'medical_technical_commentary',
      description: 'Technical analysis for medical procedures or technologies',
      version: '1.0.0',
      template: `Provide technical medical analysis for:

Content: {{technical_text}}

Include:

1. **Clinical Mechanism**
   - Physiological basis and mechanisms
   - Anatomical considerations
   - Pharmacological principles (if applicable)

2. **Evidence & Guidelines**
   - Clinical evidence quality and level
   - Professional guidelines and recommendations
   - Comparative effectiveness data

3. **Practical Application**
   - Indications and contraindications
   - Technical specifications and requirements
   - Monitoring and safety protocols

4. **Clinical Integration**
   - Workflow integration considerations
   - Training and competency requirements
   - Quality assurance measures`,
      variables: ['technical_text'],
    },

    problemSolving: {
      name: 'medical_problem_solving_commentary',
      description: 'Clinical problem-solving and diagnostic reasoning',
      version: '1.0.0',
      template: `Analyze this clinical problem or diagnostic challenge:

Problem: {{problem_text}}

Provide systematic analysis:

1. **Clinical Assessment**
   - Differential diagnosis considerations
   - Key clinical findings and significance
   - Risk stratification

2. **Diagnostic Approach**
   - Recommended diagnostic workup
   - Test ordering rationale and sequence
   - Cost-effectiveness considerations

3. **Treatment Considerations**
   - Evidence-based treatment options
   - Risk-benefit analysis
   - Patient-specific factors

4. **Quality Improvement**
   - System-level considerations
   - Process improvement opportunities
   - Prevention strategies`,
      variables: ['problem_text'],
    },

    reflection: {
      name: 'medical_reflection_commentary',
      description: 'Supportive analysis for medical professional reflections',
      version: '1.0.0',
      template: `Provide supportive commentary for this medical professional reflection:

Reflection: {{reflection_text}}

Offer thoughtful insights on:

1. **Professional Development**
   - Learning opportunities and insights
   - Skill development areas
   - Career advancement considerations

2. **Clinical Excellence**
   - Best practice opportunities
   - Quality improvement insights
   - Patient care enhancement

3. **Well-being & Resilience**
   - Professional wellness strategies
   - Stress management and burnout prevention
   - Work-life balance considerations

4. **Continuing Education**
   - Relevant learning resources
   - Professional development opportunities
   - Skill-building recommendations

Maintain supportive, non-judgmental tone while providing actionable professional insights.`,
      variables: ['reflection_text'],
    },
  },
};

/**
 * Example: Business/Corporate focused prompts
 * For business professionals and executives
 */
export const BUSINESS_PROMPTS: ContentTypePrompts = {
  base: {
    name: 'business_transcript_processor',
    description: 'Business-focused prompt for processing corporate voice transcripts',
    version: '1.0.0',
    template: `You are an executive assistant and business analyst. Process this business voice transcript and create a structured business document.

**Include Commentary For:**
- Strategic questions → Market analysis, competitive insights, strategic recommendations
- Project proposals → Feasibility analysis, ROI projections, risk assessment
- Problem-solving → Root cause analysis, solution frameworks, implementation plans
- Market analysis → Competitive intelligence, trend analysis, opportunity assessment
- Financial discussions → Financial modeling insights, valuation considerations

**NO Commentary For:**
- Simple scheduling or administrative tasks
- Basic meeting notes without strategic content
- Personal reminders unrelated to business

Content to process:
---
{{transcript}}
---

**REQUIRED FORMAT** - Return EXACTLY this structure:

---
category: {{category_placeholder}}
filename: {{filename_placeholder}}
commentary_needed: {{commentary_decision}}
business_priority: {{priority_level}}
---

# {{title_placeholder}}

## Executive Summary
{{executive_summary_placeholder}}

## Key Points
{{key_points_placeholder}}

## Strategic Implications
{{strategic_implications_placeholder}}

## Action Items
{{action_items_placeholder}}

## Business Commentary
[INCLUDE THIS SECTION ONLY if commentary_needed: true]
{{business_commentary_placeholder}}

## Next Steps
{{next_steps_placeholder}}

**Categories to choose from:** strategy, operations, finance, marketing, hr, sales, product, or create your own.`,
    variables: ['transcript'],
  },

  commentary: {
    question: {
      name: 'business_question_commentary',
      description: 'Strategic business analysis and recommendations',
      version: '1.0.0',
      template: `Provide strategic business analysis for these questions:

Questions: {{questions}}
Context: {{full_text}}

Include:

1. **Strategic Analysis**
   - Market context and competitive landscape
   - Industry trends and implications
   - Strategic options and trade-offs

2. **Financial Considerations**
   - Cost-benefit analysis framework
   - Revenue and profitability implications
   - ROI and financial modeling insights

3. **Implementation Strategy**
   - Execution roadmap and timeline
   - Resource requirements and dependencies
   - Risk mitigation strategies

4. **Success Metrics**
   - KPIs and measurement framework
   - Benchmarking and performance targets
   - Monitoring and adjustment mechanisms`,
      variables: ['questions', 'full_text'],
    },

    project: {
      name: 'business_project_commentary',
      description: 'Business project feasibility and strategic analysis',
      version: '1.0.0',
      template: `Analyze this business project or initiative:

Project: {{project_text}}

Provide comprehensive business analysis:

1. **Market Opportunity**
   - Market size and growth potential
   - Customer needs and pain points
   - Competitive landscape analysis

2. **Business Model**
   - Revenue model and monetization
   - Cost structure and economics
   - Scalability and growth potential

3. **Execution Strategy**
   - Go-to-market strategy
   - Operational requirements
   - Technology and infrastructure needs

4. **Financial Projections**
   - Investment requirements and funding
   - Revenue and profitability timeline
   - Break-even and ROI analysis`,
      variables: ['project_text'],
    },

    technical: {
      name: 'business_technical_commentary',
      description: 'Technical business analysis and recommendations',
      version: '1.0.0',
      template: `Provide business-focused technical analysis:

Content: {{technical_text}}

Include:

1. **Technology Assessment**
   - Technical feasibility and complexity
   - Technology stack recommendations
   - Integration and compatibility considerations

2. **Business Impact**
   - Operational efficiency gains
   - Cost reduction opportunities
   - Revenue enhancement potential

3. **Implementation Roadmap**
   - Phased implementation approach
   - Resource and skill requirements
   - Timeline and milestone planning

4. **Risk Management**
   - Technical risks and mitigation
   - Business continuity considerations
   - Change management requirements`,
      variables: ['technical_text'],
    },

    problemSolving: {
      name: 'business_problem_solving_commentary',
      description: 'Systematic business problem analysis and solutions',
      version: '1.0.0',
      template: `Analyze this business problem or challenge:

Problem: {{problem_text}}

Provide systematic business analysis:

1. **Root Cause Analysis**
   - Problem definition and scope
   - Underlying causes and factors
   - Impact assessment

2. **Solution Framework**
   - Multiple solution alternatives
   - Cost-benefit analysis of options
   - Risk assessment for each approach

3. **Implementation Plan**
   - Detailed action plan and timeline
   - Resource allocation and ownership
   - Success metrics and monitoring

4. **Change Management**
   - Stakeholder impact and communication
   - Training and capability building
   - Organizational change requirements`,
      variables: ['problem_text'],
    },

    reflection: {
      name: 'business_reflection_commentary',
      description: 'Professional development and leadership insights',
      version: '1.0.0',
      template: `Provide professional development insights for this business reflection:

Reflection: {{reflection_text}}

Offer strategic professional guidance:

1. **Leadership Development**
   - Leadership skills and competencies
   - Executive presence and influence
   - Decision-making and judgment

2. **Strategic Thinking**
   - Strategic perspective and vision
   - Industry knowledge and expertise
   - Innovation and creative thinking

3. **Professional Growth**
   - Career advancement opportunities
   - Skill development priorities
   - Network and relationship building

4. **Business Impact**
   - Value creation opportunities
   - Performance optimization
   - Organizational contribution`,
      variables: ['reflection_text'],
    },
  },
};

/**
 * Prompt Engine Extensions
 * Utility functions for working with custom prompts
 */
export class CustomPromptEngine extends PromptEngine {
  /**
   * Get medical-focused prompt
   */
  static getMedicalPrompt(transcript: string): string {
    return this.getPrompt(MEDICAL_PROMPTS.base, { transcript });
  }

  /**
   * Get business-focused prompt
   */
  static getBusinessPrompt(transcript: string): string {
    return this.getPrompt(BUSINESS_PROMPTS.base, { transcript });
  }

  /**
   * Get domain-specific commentary prompt
   */
  static getDomainCommentaryPrompt(
    domain: 'medical' | 'business',
    type: 'question' | 'project' | 'technical' | 'problemSolving' | 'reflection',
    variables: Record<string, any>
  ): string {
    const prompts = domain === 'medical' ? MEDICAL_PROMPTS : BUSINESS_PROMPTS;
    const promptConfig = prompts.commentary[type];
    return this.getPrompt(promptConfig, variables);
  }

  /**
   * Create a custom prompt for a specific use case
   */
  static createCustomPrompt(config: {
    domain: string;
    template: string;
    variables: Record<string, any>;
  }): string {
    return this.processTemplate(config.template, config.variables);
  }
}

/**
 * Example usage:
 * 
 * // Use medical prompts
 * const medicalPrompt = CustomPromptEngine.getMedicalPrompt(transcript);
 * 
 * // Use business prompts
 * const businessPrompt = CustomPromptEngine.getBusinessPrompt(transcript);
 * 
 * // Get domain-specific commentary
 * const commentary = CustomPromptEngine.getDomainCommentaryPrompt(
 *   'medical',
 *   'question',
 *   { questions: 'What are the treatment options?', full_text: transcript }
 * );
 * 
 * // Create completely custom prompt
 * const customPrompt = CustomPromptEngine.createCustomPrompt({
 *   domain: 'legal',
 *   template: 'Analyze this legal document: {{document}}',
 *   variables: { document: documentText }
 * });
 */