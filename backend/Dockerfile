# Multi-stage production Dockerfile for Audio-AI Backend
# Optimized for GitHub repository builds and Portainer deployment

# Stage 1: Dependencies and Build
FROM node:20-alpine AS builder

# Install system dependencies for building
RUN apk add --no-cache python3 make g++ ffmpeg

WORKDIR /usr/src/app

# Copy package files for dependency installation
COPY package*.json ./

# Install ALL dependencies (including devDependencies for building)
RUN npm ci && npm cache clean --force

# Copy source code and build
COPY . .
RUN npm run build

# Stage 2: Production Runtime
FROM node:20-alpine AS production

# Install runtime dependencies
RUN apk add --no-cache ffmpeg curl

# Create app directory and user for security
WORKDIR /usr/src/app
RUN addgroup -g 1001 -S nodejs
RUN adduser -S audioai -u 1001

# Copy package.json and install only production dependencies
COPY --chown=audioai:nodejs package*.json ./
RUN npm ci --only=production && npm cache clean --force

# Copy built application from builder stage
COPY --from=builder --chown=audioai:nodejs /usr/src/app/dist ./dist

# Create processed directory for file storage with proper permissions
RUN mkdir -p /usr/src/app/processed && chown -R audioai:nodejs /usr/src/app/processed && chmod -R 755 /usr/src/app/processed

# Switch to non-root user
USER audioai

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

# Start the application
CMD ["node", "dist/index.js"]