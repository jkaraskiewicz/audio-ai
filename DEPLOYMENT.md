# 🚀 Audio-AI Portainer Deployment Guide

This guide covers deploying the Audio-AI backend using Portainer with GitHub repository-based Docker images.

## 📋 Prerequisites

- Portainer CE/EE installed and running
- Access to a Docker host (local or remote)
- GitHub repository with the Audio-AI codebase
- Gemini API key for AI processing

## 🏗️ Architecture Overview

```
GitHub Repository → GitHub Actions → GitHub Container Registry → Portainer → Docker Host
```

The deployment uses:
- **GitHub Actions** for automated Docker image building
- **GitHub Container Registry (GHCR)** for image storage
- **Portainer** for container orchestration and management
- **Docker Compose** for multi-service deployment

## 🔧 Setup Instructions

### Step 1: Prepare GitHub Repository

1. **Push code to GitHub repository**
   ```bash
   git push origin master
   ```

2. **Enable GitHub Actions**
   - The workflow file is located at `.github/workflows/docker-build.yml`
   - It will automatically build Docker images on code changes

3. **Enable GitHub Container Registry**
   - Go to your repository settings
   - Navigate to "Packages" section
   - Ensure GHCR is enabled for your repository

### Step 2: Configure GitHub Container Registry

1. **Update image reference in docker-compose.production.yml**
   ```yaml
   # Replace 'your-username/audio-ai' with your actual GitHub repository
   image: ghcr.io/YOUR_GITHUB_USERNAME/audio-ai/backend:latest
   ```

2. **Set repository visibility (if needed)**
   - For private repositories, configure access tokens in Portainer

### Step 3: Portainer Stack Deployment

1. **Access Portainer Dashboard**
   - Navigate to your Portainer instance
   - Go to "Stacks" section

2. **Create New Stack**
   - Click "Add stack"
   - Name: `audio-ai-production`
   - Method: Choose "Git repository"

3. **Configure Git Repository**
   ```
   Repository URL: https://github.com/YOUR_USERNAME/audio-ai
   Reference: refs/heads/master
   Compose Path: docker-compose.production.yml
   ```

4. **Set Environment Variables**
   Add these environment variables in Portainer:
   ```
   GEMINI_API_KEY=your_gemini_api_key_here
   BACKEND_PORT=3000
   TRANSCRIPTION_PROVIDER=openai_whisper_webservice
   WHISPER_SERVICE_URL=http://whisper:9000
   MAX_FILE_SIZE_MB=100
   PROCESSED_FILES_PATH=/var/lib/audio-ai/processed
   LOG_LEVEL=info
   TZ=UTC
   ```

5. **Deploy Stack**
   - Click "Deploy the stack"
   - Portainer will pull the Docker images and start services

### Step 4: Verify Deployment

1. **Check Service Status**
   - All services should show as "running"
   - Green health indicators for both `whisper` and `audio-ai` services

2. **Test Health Endpoints**
   ```bash
   # Test Whisper service
   curl http://localhost:9000/docs
   
   # Test Audio-AI backend
   curl http://localhost:3000/health
   ```

3. **Review Logs**
   - Check container logs in Portainer for any errors
   - Look for successful startup messages

## 🔄 Automatic Updates

### GitHub Actions Workflow

The deployment includes automatic image building:

- **On push to master**: Builds and publishes `latest` tag
- **On release**: Builds and publishes version tags
- **Multi-platform**: Supports both AMD64 and ARM64 architectures

### Portainer Webhooks (Optional)

1. **Enable Webhook for Stack**
   ```bash
   # In Portainer, go to Stack settings
   # Enable "Re-deploy from git repository" webhook
   # Copy the webhook URL
   ```

2. **Configure GitHub Webhook**
   - Go to GitHub repository settings
   - Add webhook with Portainer webhook URL
   - Set to trigger on push events

## 📁 File Structure

```
audio-ai/
├── backend/
│   ├── Dockerfile                     # Production Docker build
│   ├── src/                          # Application source code
│   └── package.json                  # Dependencies
├── .github/
│   └── workflows/
│       └── docker-build.yml          # GitHub Actions workflow
├── docker-compose.production.yml      # Production compose file
├── .env.production.example            # Environment template
└── DEPLOYMENT.md                      # This file
```

## 🔒 Security Best Practices

1. **Use Secrets for API Keys**
   - Store `GEMINI_API_KEY` in Portainer secrets
   - Reference secrets in stack configuration

2. **Network Isolation**
   - Services communicate through internal Docker network
   - Only necessary ports exposed to host

3. **Non-root User**
   - Backend container runs as non-root user `audioai`
   - Proper file permissions and ownership

4. **Resource Limits**
   - Memory and CPU limits configured in compose file
   - Prevents resource exhaustion

## 🐛 Troubleshooting

### Common Issues

1. **Image Pull Errors**
   ```
   Solution: Check GitHub repository visibility and Portainer registry auth
   ```

2. **Health Check Failures**
   ```
   Solution: Verify service ports and network connectivity
   Check logs for startup errors
   ```

3. **File Permission Errors**
   ```
   Solution: Ensure PROCESSED_FILES_PATH has correct permissions
   chown -R 1001:1001 /var/lib/audio-ai/processed
   ```

4. **Environment Variable Issues**
   ```
   Solution: Verify all required environment variables are set
   Check .env.production.example for reference
   ```

### Logs and Monitoring

1. **Container Logs**
   ```bash
   # View logs in Portainer or via Docker CLI
   docker logs audio-ai-backend-prod
   docker logs audio-ai-whisper-prod
   ```

2. **Health Status**
   ```bash
   # Check container health
   docker ps --format "table {{.Names}}\t{{.Status}}"
   ```

## 🚀 Production Considerations

1. **Backup Strategy**
   - Regular backups of processed files directory
   - Database backups (if added in future)

2. **Monitoring**
   - Set up log aggregation (ELK stack, etc.)
   - Monitor container resource usage
   - Set up alerting for service failures

3. **Scaling**
   - Multiple backend replicas behind load balancer
   - Shared storage for processed files
   - Redis for session management (if needed)

4. **Updates**
   - Test updates in staging environment first
   - Use rolling deployments for zero downtime
   - Keep rollback strategy ready

## 📞 Support

For deployment issues:
1. Check container logs in Portainer
2. Verify environment variables
3. Test network connectivity between services
4. Review GitHub Actions build logs

---

*This deployment guide ensures a production-ready setup with automatic updates and proper security practices.*