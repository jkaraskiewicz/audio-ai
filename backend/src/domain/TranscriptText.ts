/**
 * Value Object: Represents transcript text with built-in validation and behavior
 * Uncle Bob Approved: Rich domain object with behavior, not anemic data structure
 */
export class TranscriptText {
  private static readonly MAX_LENGTH = 10_000;
  private static readonly MIN_LENGTH = 1;

  private constructor(private readonly text: string) {
    this.validateTranscript();
  }

  static create(text: string | undefined): TranscriptText {
    if (!text) {
      throw new Error('Transcript text cannot be empty or undefined');
    }

    return new TranscriptText(text.trim());
  }

  getText(): string {
    return this.text;
  }

  getLength(): number {
    return this.text.length;
  }

  isEmpty(): boolean {
    return this.text.length === 0;
  }

  isWithinLengthLimits(): boolean {
    return (
      this.getLength() >= TranscriptText.MIN_LENGTH && this.getLength() <= TranscriptText.MAX_LENGTH
    );
  }

  createPreview(maxLength: number = 200): string {
    if (this.text.length <= maxLength) {
      return this.text;
    }

    return this.text.substring(0, maxLength) + '...';
  }

  getWordCount(): number {
    return this.text.split(/\s+/).filter((word) => word.length > 0).length;
  }

  containsKeywords(keywords: string[]): boolean {
    const lowercaseText = this.text.toLowerCase();
    return keywords.some((keyword) => lowercaseText.includes(keyword.toLowerCase()));
  }

  extractSentences(): string[] {
    return this.text
      .split(/[.!?]+/)
      .map((sentence) => sentence.trim())
      .filter((sentence) => sentence.length > 0);
  }

  isLikelyActionItem(): boolean {
    const actionIndicators = [
      'todo',
      'action',
      'need to',
      'should',
      'must',
      'follow up',
      'deadline',
      'by friday',
      'next week',
    ];

    return this.containsKeywords(actionIndicators);
  }

  private validateTranscript(): void {
    if (this.isEmpty()) {
      throw new Error('Transcript text cannot be empty');
    }

    if (!this.isWithinLengthLimits()) {
      throw new Error(
        `Transcript length ${this.getLength()} exceeds maximum allowed length of ${TranscriptText.MAX_LENGTH}`
      );
    }
  }
}
