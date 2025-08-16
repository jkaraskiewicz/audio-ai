#!/bin/bash

# Audio-AI Docker Stack Startup Script

set -e

echo "🎵➡️📝 Starting Audio-AI Stack"
echo "=============================="

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}➤${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

print_success "Docker is running"

# Check for environment file
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        print_step "Creating .env file from template..."
        cp .env.example .env
        print_warning "Please edit .env file to add your GEMINI_API_KEY"
        echo "Get your free API key from: https://makersuite.google.com/app/apikey"
        echo ""
        read -p "Press Enter to continue once you've added your API key..."
    else
        print_warning "No .env file found. Using default environment variables."
    fi
fi

# Create processed directory if it doesn't exist
print_step "Ensuring processed directory exists..."
mkdir -p ../processed
print_success "Processed directory ready"

# Start the stack
print_step "Starting Audio-AI stack..."
docker-compose up -d --build

echo ""
print_step "Waiting for services to be ready..."

# Wait for services with timeout
timeout=180
counter=0

while [ $counter -lt $timeout ]; do
    if curl -f http://localhost:3000/health >/dev/null 2>&1; then
        break
    fi
    echo -n "."
    sleep 2
    counter=$((counter + 2))
done

echo ""

if [ $counter -ge $timeout ]; then
    print_warning "Services are taking longer than expected to start."
    echo "You can check the logs with: docker-compose logs"
    echo "Or check individual service status:"
    echo "  Whisper:  curl http://localhost:9000/docs"
    echo "  Audio-AI: curl http://localhost:3000/health"
else
    print_success "Audio-AI stack is ready!"
    echo ""
    echo "📋 Service URLs:"
    echo "   Audio-AI Backend: http://localhost:3000"
    echo "   Whisper Service:  http://localhost:9000"
    echo ""
    echo "🧪 Quick Test:"
    echo "   curl -X POST http://localhost:3000/process \\"
    echo "     -H \"Content-Type: application/json\" \\"
    echo "     -d '{\"transcript\":\"Test message\"}'"
    echo ""
    echo "📁 Your processed files will be saved to: ../processed/"
    echo ""
    echo "🛑 To stop the stack: docker-compose down"
fi