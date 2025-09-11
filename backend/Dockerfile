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

# Install runtime dependencies including su-exec for user switching
RUN apk add --no-cache ffmpeg curl su-exec

# Create app directory
WORKDIR /usr/src/app

# Copy package.json and install only production dependencies
COPY package*.json ./
RUN npm ci --only=production && npm cache clean --force

# Copy built application from builder stage
COPY --from=builder /usr/src/app/dist ./dist

# Create entrypoint script to handle PUID/PGID
RUN cat > /entrypoint.sh << 'EOF'
#!/bin/sh
set -e

# Default values if not provided
PUID=${PUID:-1001}
PGID=${PGID:-1001}

echo "Setting up user with PUID=$PUID and PGID=$PGID"

# Create group and user with specified IDs
addgroup -g "$PGID" -S appgroup 2>/dev/null || true
adduser -u "$PUID" -G appgroup -S -D -h /usr/src/app appuser 2>/dev/null || true

# Create and set permissions for processed directory
mkdir -p /usr/src/app/processed
chown -R "$PUID:$PGID" /usr/src/app/processed
chmod -R 755 /usr/src/app/processed

# Change ownership of app files to the specified user
chown -R "$PUID:$PGID" /usr/src/app

# Execute the main command as the specified user
exec su-exec "$PUID:$PGID" "$@"
EOF

# Make entrypoint executable
RUN chmod +x /entrypoint.sh

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

# Use entrypoint script
ENTRYPOINT ["/entrypoint.sh"]
CMD ["node", "dist/index.js"]