#!/bin/bash

# Audio-AI Setup Script
# This script helps you quickly set up Audio-AI with different configurations

set -e

echo "🎵➡️📝 Audio-AI Setup"
echo "===================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_step() {
    echo -e "${BLUE}➤${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

print_success "Docker and Docker Compose are installed"

# Setup configuration
print_step "Setting up configuration..."

if [ ! -f .env ]; then
    print_step "Creating .env file from template..."
    cp .env.example .env
    print_warning "Please edit .env file to add your GEMINI_API_KEY"
    echo "You can get a free API key from: https://makersuite.google.com/app/apikey"
else
    print_success ".env file already exists"
fi

# Create processed directory
print_step "Creating processed files directory..."
mkdir -p processed
print_success "Created ./processed directory for your transcribed files"

# Setup selection
echo ""
echo "Choose your setup:"
echo "1) 🆕 Full setup with local Whisper service (recommended for new users)"
echo "2) 🔗 Connect to existing Whisper service"
echo "3) ☁️  Use cloud services (Hugging Face, etc.)"
echo "4) 🧪 Development setup with testing provider"
echo ""

read -p "Enter your choice (1-4): " choice

case $choice in
    1)
        print_step "Setting up full Audio-AI with local Whisper service..."
        
        print_step "Starting services..."
        docker-compose --profile full up -d --build
        
        print_success "Full setup complete!"
        echo ""
        echo "📋 Service URLs:"
        echo "   Audio-AI Backend: http://localhost:3000"
        echo "   Whisper Service:  http://localhost:9000"
        echo "   Health Check:     curl http://localhost:3000/health"
        echo ""
        echo "📁 Your processed files will be saved to: ./processed/"
        ;;
        
    2)
        print_step "Setting up Audio-AI for external Whisper service..."
        
        read -p "Enter your Whisper service URL (e.g., http://localhost:1991): " whisper_url
        
        print_step "Updating configuration..."
        # Update .env file
        if grep -q "WHISPER_SERVICE_URL=" .env; then
            sed -i.bak "s|WHISPER_SERVICE_URL=.*|WHISPER_SERVICE_URL=$whisper_url|" .env
        else
            echo "WHISPER_SERVICE_URL=$whisper_url" >> .env
        fi
        
        print_step "Starting Audio-AI service..."
        docker-compose up audio-ai -d --build
        
        print_success "External Whisper setup complete!"
        echo ""
        echo "📋 Service URLs:"
        echo "   Audio-AI Backend: http://localhost:3000"
        echo "   External Whisper: $whisper_url"
        echo "   Health Check:     curl http://localhost:3000/health"
        echo ""
        echo "📁 Your processed files will be saved to: ./processed/"
        ;;
        
    3)
        print_step "Setting up Audio-AI for cloud services..."
        
        echo "Choose cloud provider:"
        echo "1) Hugging Face"
        echo "2) Google Gemini Audio"
        
        read -p "Enter choice (1-2): " cloud_choice
        
        case $cloud_choice in
            1)
                read -p "Enter your Hugging Face API token: " hf_token
                # Update .env file
                sed -i.bak "s|TRANSCRIPTION_PROVIDER=.*|TRANSCRIPTION_PROVIDER=huggingface|" .env
                if grep -q "HUGGINGFACE_API_TOKEN=" .env; then
                    sed -i.bak "s|HUGGINGFACE_API_TOKEN=.*|HUGGINGFACE_API_TOKEN=$hf_token|" .env
                else
                    echo "HUGGINGFACE_API_TOKEN=$hf_token" >> .env
                fi
                ;;
            2)
                sed -i.bak "s|TRANSCRIPTION_PROVIDER=.*|TRANSCRIPTION_PROVIDER=gemini_audio|" .env
                ;;
        esac
        
        print_step "Starting Audio-AI service..."
        docker-compose up audio-ai -d --build
        
        print_success "Cloud service setup complete!"
        echo ""
        echo "📋 Service URLs:"
        echo "   Audio-AI Backend: http://localhost:3000"
        echo "   Health Check:     curl http://localhost:3000/health"
        ;;
        
    4)
        print_step "Setting up development environment..."
        
        # Set free provider for development
        sed -i.bak "s|TRANSCRIPTION_PROVIDER=.*|TRANSCRIPTION_PROVIDER=free_web_speech|" .env
        
        print_step "Starting development services..."
        docker-compose --profile dev up -d --build
        
        print_success "Development setup complete!"
        echo ""
        echo "📋 Service URLs:"
        echo "   Audio-AI Dev:     http://localhost:3001"
        echo "   Health Check:     curl http://localhost:3001/health"
        echo ""
        echo "🔧 Development features:"
        echo "   - Hot reload enabled"
        echo "   - Mock transcription provider"
        echo "   - Debug logging"
        ;;
        
    *)
        print_error "Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""
print_step "Testing setup..."

# Wait for services to start
sleep 10

# Test health endpoint
if curl -f http://localhost:3000/health >/dev/null 2>&1; then
    print_success "Audio-AI is running and healthy!"
else
    if [ "$choice" = "4" ]; then
        if curl -f http://localhost:3001/health >/dev/null 2>&1; then
            print_success "Audio-AI development server is running and healthy!"
        else
            print_warning "Service might still be starting. Check with: docker-compose logs"
        fi
    else
        print_warning "Service might still be starting. Check with: docker-compose logs"
    fi
fi

echo ""
echo "🎉 Setup complete! You can now:"
echo "   • Upload audio files: curl -X POST http://localhost:3000/process-file -F \"file=@audio.mp3\""
echo "   • Process text: curl -X POST http://localhost:3000/process -H \"Content-Type: application/json\" -d '{\"transcript\":\"Your text here\"}'"
echo "   • Check your processed files in: ./processed/"
echo ""
echo "📖 For more information, see README.md and DEVELOPER.md"