import request from 'supertest';
import { app } from '../../src/index';

describe('App Integration Tests', () => {
  describe('Health Check', () => {
    it('should return health status', async () => {
      const response = await request(app).get('/health');

      expect(response.status).toBe(200);
      expect(response.body).toMatchObject({
        status: 'healthy',
        service: 'audio-ai',
      });
    });
  });

  describe('Process Endpoint', () => {
    it('should validate request body', async () => {
      const response = await request(app).post('/process').send({});

      expect(response.status).toBe(400);
      expect(response.body.error).toContain('required');
    });

    it('should validate transcript length', async () => {
      const response = await request(app)
        .post('/process')
        .send({ transcript: 'a'.repeat(10001) });

      expect(response.status).toBe(400);
      expect(response.body.error).toContain('too long');
    });

    it('should handle empty transcript', async () => {
      const response = await request(app).post('/process').send({ transcript: '   ' });

      expect(response.status).toBe(400);
      expect(response.body.error).toContain('empty');
    });
  });
});