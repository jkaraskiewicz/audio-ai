#!/bin/bash

# Audio-AI Production Deployment Helper Script
# This script helps set up the production environment for Portainer deployment

set -e

echo "🚀 Audio-AI Production Deployment Setup"
echo "======================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check if running from correct directory
if [[ ! -f "docker-compose.production.yml" ]]; then
    print_error "docker-compose.production.yml not found. Please run this script from the project root directory."
    exit 1
fi

print_status "Starting production deployment setup..."

# Step 1: Check prerequisites
echo
echo "Step 1: Checking prerequisites..."
print_info "Checking Docker installation..."
if command -v docker &> /dev/null; then
    print_status "Docker is installed"
    docker --version
else
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

print_info "Checking Docker Compose installation..."
if command -v docker-compose &> /dev/null; then
    print_status "Docker Compose is installed"
    docker-compose --version
else
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Step 2: Create production environment file
echo
echo "Step 2: Setting up environment file..."
if [[ ! -f ".env.production" ]]; then
    print_info "Creating .env.production from template..."
    cp .env.production.example .env.production
    print_warning "Please edit .env.production and set your GEMINI_API_KEY and other values"
    print_info "Required variables to set:"
    echo "  - GEMINI_API_KEY (required)"
    echo "  - PROCESSED_FILES_PATH (recommended: /var/lib/audio-ai/processed)"
    echo "  - BACKEND_PORT (if different from 3000)"
else
    print_status ".env.production already exists"
fi

# Step 3: Create processed files directory
echo
echo "Step 3: Creating processed files directory..."
PROCESSED_DIR="${PROCESSED_FILES_PATH:-./processed}"
if [[ ! -d "$PROCESSED_DIR" ]]; then
    print_info "Creating processed files directory: $PROCESSED_DIR"
    mkdir -p "$PROCESSED_DIR"
    print_status "Directory created: $PROCESSED_DIR"
else
    print_status "Processed files directory already exists: $PROCESSED_DIR"
fi

# Step 4: Update GitHub repository reference
echo
echo "Step 4: Checking GitHub repository configuration..."
GITHUB_USERNAME=$(git config user.name 2>/dev/null || echo "your-username")
REPO_NAME=$(basename "$(git rev-parse --show-toplevel)" 2>/dev/null || echo "audio-ai")

print_warning "Please update the GitHub image reference in docker-compose.production.yml:"
print_info "Current: ghcr.io/your-username/audio-ai/backend:latest"
print_info "Should be: ghcr.io/$GITHUB_USERNAME/$REPO_NAME/backend:latest"

# Step 5: Show next steps
echo
echo "🎯 Next Steps for Portainer Deployment:"
echo "======================================="
echo "1. Push your code to GitHub:"
echo "   git add ."
echo "   git commit -m 'Add production deployment configuration'"
echo "   git push origin master"
echo
echo "2. Wait for GitHub Actions to build the Docker image"
echo "   Check: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions"
echo
echo "3. In Portainer:"
echo "   - Create new stack named 'audio-ai-production'"
echo "   - Use Git repository method"
echo "   - Repository: https://github.com/$GITHUB_USERNAME/$REPO_NAME"
echo "   - Compose file: docker-compose.production.yml"
echo "   - Add environment variables from .env.production"
echo
echo "4. Deploy and verify:"
echo "   - Check all services are running"
echo "   - Test health endpoints"
echo "   - Review container logs"
echo
print_status "Production deployment setup complete!"
print_info "See DEPLOYMENT.md for detailed instructions."