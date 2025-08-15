#!/bin/bash
# 🎵➡️📝 Audio-AI Setup Script
# Simplifies configuration for different transcription scenarios

set -e

echo "🎵 Audio-AI Setup Assistant"
echo "=========================="

# Check if .env exists
if [ -f .env ]; then
    echo "📝 Found existing .env file"
    read -p "Do you want to overwrite it? (y/N): " overwrite
    if [[ ! $overwrite =~ ^[Yy]$ ]]; then
        echo "Using existing .env file"
        exit 0
    fi
fi

echo
echo "Choose your transcription setup:"
echo "1) Use existing Whisper service (like your port 1991 setup)"
echo "2) Start fresh local Whisper service"
echo "3) Use free web speech recognition (no setup required)"
echo

read -p "Enter choice (1-3): " choice

# Create base .env
cat > .env << EOF
# Audio-AI Configuration
# Edit GEMINI_API_KEY with your actual API key

GEMINI_API_KEY=your_api_key_here
BASE_DIRECTORY=processed
MAX_FILE_SIZE=52428800
TRANSCRIPTION_LANGUAGE=en
WHISPER_MODEL=base

EOF

case $choice in
    1)
        echo "📡 Configuring for existing Whisper service..."
        read -p "Enter your Whisper service URL (default: http://host.docker.internal:1991): " whisper_url
        whisper_url=${whisper_url:-http://host.docker.internal:1991}
        
        cat >> .env << EOF
# External Whisper Configuration
TRANSCRIPTION_PROVIDER=openai_whisper_webservice
WHISPER_SERVICE_URL=$whisper_url
EOF
        
        echo "✅ Configured for external Whisper at $whisper_url"
        echo "🚀 Run: docker-compose -f docker-compose.unified.yml up -d"
        ;;
        
    2)
        echo "🐳 Configuring for self-hosted Whisper..."
        cat >> .env << EOF
# Self-hosted Whisper Configuration
TRANSCRIPTION_PROVIDER=docker_whisper
WHISPER_SERVICE_URL=http://whisper-service:8001
EOF
        
        echo "✅ Configured for self-hosted Whisper"
        echo "🚀 Run: docker-compose -f docker-compose.unified.yml --profile whisper-local up -d"
        ;;
        
    3)
        echo "🌐 Configuring for free web speech..."
        cat >> .env << EOF
# Free Web Speech Configuration
TRANSCRIPTION_PROVIDER=free_web_speech
EOF
        
        echo "✅ Configured for free web speech recognition"
        echo "🚀 Run: docker-compose -f docker-compose.unified.yml up -d"
        ;;
        
    *)
        echo "❌ Invalid choice. Please run the script again."
        exit 1
        ;;
esac

echo
echo "📝 Next steps:"
echo "1. Edit .env and add your GEMINI_API_KEY"
echo "2. Run the docker-compose command shown above"
echo "3. Test with: curl http://localhost:3000/health"
echo
echo "📖 For more details, see WHISPER_SETUP_GUIDE.md"