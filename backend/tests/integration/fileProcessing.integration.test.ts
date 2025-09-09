import request from 'supertest';
import { app } from '../../src/index';

describe('File Processing Integration Tests', () => {
  describe('POST /process-file', () => {
    it('should handle text file upload with async processing', async () => {
      const textContent = 'I want to build a mobile app for tracking expenses and budgeting.';

      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from(textContent), 'expense-tracker-idea.txt')
        .expect(200);

      // File uploads now return async response
      expect(response.body).toHaveProperty('message');
      expect(response.body).toHaveProperty('filename');
      expect(response.body).toHaveProperty('status');
      expect(response.body).toHaveProperty('timestamp');
      expect(response.body.message).toBe('File received and processing started');
      expect(response.body.status).toBe('processing');
      expect(response.body.filename).toBe('expense-tracker-idea.txt');
    }, 30000);

    it('should handle markdown file upload with async processing', async () => {
      const markdownContent = `# Project Ideas

## Mobile App Ideas
- Expense tracker
- Habit tracker
- Note-taking app

## Next Steps
- Research existing solutions
- Create wireframes`;

      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from(markdownContent), 'project-ideas.md')
        .expect(200);

      // File uploads now return async response
      expect(response.body).toHaveProperty('message');
      expect(response.body).toHaveProperty('filename');
      expect(response.body).toHaveProperty('status');
      expect(response.body).toHaveProperty('timestamp');
      expect(response.body.message).toBe('File received and processing started');
      expect(response.body.status).toBe('processing');
      expect(response.body.filename).toBe('project-ideas.md');
    }, 30000);

    it('should handle transcript text without file', async () => {
      const transcript = 'I need to organize my study schedule for next semester.';

      const response = await request(app)
        .post('/process-file')
        .field('transcript', transcript)
        .expect(200);

      expect(response.body).toHaveProperty('result');
      expect(response.body).toHaveProperty('saved_to');
      expect(response.body.message).toContain('direct text input');
    }, 30000);

    it('should reject requests with both file and transcript', async () => {
      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from('test content'), 'test.txt')
        .field('transcript', 'some transcript')
        .expect(400);

      expect(response.body.error).toContain('either a file OR transcript');
    });

    it('should reject requests with neither file nor transcript', async () => {
      const response = await request(app)
        .post('/process-file')
        .field('transcript', '') // Send empty transcript to trigger multipart processing
        .expect(400);

      expect(response.body.error).toContain('Either a file or transcript text is required');
      expect(response.body).toHaveProperty('supportedFormats');
    });

    it('should reject empty files', async () => {
      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from(''), 'empty.txt')
        .expect(400);

      expect(response.body.error).toContain('cannot be empty');
    });

    it('should reject files without extensions', async () => {
      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from('test content'), 'noextension')
        .expect(400);

      expect(response.body.error).toContain('valid extension');
    });

    it('should handle reasonably sized files with async processing', async () => {
      // Create a reasonable sized file that should work
      const content = 'This is a test document for file processing. '.repeat(100); // Much smaller, more realistic

      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from(content), 'test-document.txt')
        .expect(200); // Should work for reasonable size

      // File uploads now return async response
      expect(response.body).toHaveProperty('message');
      expect(response.body).toHaveProperty('filename');
      expect(response.body).toHaveProperty('status');
      expect(response.body.message).toBe('File received and processing started');
      expect(response.body.filename).toBe('test-document.txt');
    }, 30000);
  });

  describe('Error Handling', () => {
    it('should handle malformed multipart data', async () => {
      const response = await request(app)
        .post('/process-file')
        .send('invalid data')
        .expect(400);

      expect(response.body).toHaveProperty('error');
    });

    it('should accept unsupported file types for async processing', async () => {
      // With async processing, unsupported files are accepted and processed in background
      // Errors occur during background processing, not at upload time
      const response = await request(app)
        .post('/process-file')
        .attach('file', Buffer.from('binary data'), 'test.exe')
        .expect(200);

      // File uploads now return async response even for unsupported types
      expect(response.body).toHaveProperty('message');
      expect(response.body).toHaveProperty('filename');
      expect(response.body).toHaveProperty('status');
      expect(response.body.message).toBe('File received and processing started');
      expect(response.body.filename).toBe('test.exe');
      // The error will be logged in background processing, not returned to client
    });
  });
});