#!/bin/bash
# 🧪 Audio-AI Test Script
# Verifies your transcription setup is working

set -e

echo "🧪 Testing Audio-AI Setup"
echo "========================"

# Function to test endpoint
test_endpoint() {
    local url=$1
    local description=$2
    
    echo -n "Testing $description... "
    if curl -s --max-time 5 "$url" > /dev/null 2>&1; then
        echo "✅"
        return 0
    else
        echo "❌"
        return 1
    fi
}

# Function to test JSON endpoint
test_json_endpoint() {
    local url=$1
    local description=$2
    
    echo -n "Testing $description... "
    response=$(curl -s --max-time 10 "$url" 2>/dev/null || echo "failed")
    
    if [[ $response == *"healthy"* ]] || [[ $response == *"status"* ]]; then
        echo "✅"
        return 0
    else
        echo "❌ ($response)"
        return 1
    fi
}

echo "🔍 Checking services..."

# Test Audio-AI health
if test_json_endpoint "http://localhost:3000/health" "Audio-AI health"; then
    audio_ai_healthy=true
else
    audio_ai_healthy=false
fi

# Test local Whisper (if running)
if test_json_endpoint "http://localhost:8001/health" "Local Whisper health"; then
    local_whisper_healthy=true
else
    local_whisper_healthy=false
fi

# Check if external Whisper is configured
if [ -f .env ] && grep -q "host.docker.internal:1991" .env; then
    echo -n "Testing external Whisper (port 1991)... "
    if curl -s --max-time 5 "http://localhost:1991/asr" -X POST > /dev/null 2>&1; then
        echo "✅"
        external_whisper_healthy=true
    else
        echo "❌"
        external_whisper_healthy=false
    fi
else
    external_whisper_healthy=false
fi

echo
echo "📊 Service Status:"
echo "==================="
echo "Audio-AI (port 3000): $([ "$audio_ai_healthy" = true ] && echo "✅ Running" || echo "❌ Not running")"
echo "Local Whisper (port 8001): $([ "$local_whisper_healthy" = true ] && echo "✅ Running" || echo "❌ Not running")"
echo "External Whisper (port 1991): $([ "$external_whisper_healthy" = true ] && echo "✅ Running" || echo "❌ Not running")"

echo
if [ "$audio_ai_healthy" = true ]; then
    echo "🎉 Audio-AI is running!"
    
    # Test transcript processing
    echo -n "Testing transcript processing... "
    response=$(curl -s -X POST http://localhost:3000/process-transcript \
        -H "Content-Type: application/json" \
        -d '{"transcript":"Test transcription from setup script"}' 2>/dev/null || echo "failed")
    
    if [[ $response == *"result"* ]] || [[ $response == *"saved"* ]]; then
        echo "✅"
        echo "✅ Transcript processing works!"
    else
        echo "❌"
        echo "⚠️  Transcript processing may have issues"
    fi
    
    # Test file upload endpoint
    echo -n "Testing file upload endpoint... "
    response=$(curl -s -X POST http://localhost:3000/process-file \
        -F "file=" 2>/dev/null || echo "failed")
    
    if [[ $response == *"empty"* ]] || [[ $response == *"required"* ]]; then
        echo "✅"
        echo "✅ File upload endpoint works!"
    else
        echo "❌"
        echo "⚠️  File upload endpoint may have issues"
    fi
    
else
    echo "❌ Audio-AI is not running"
    echo
    echo "🔧 Troubleshooting:"
    echo "- Check if containers are running: docker-compose ps"
    echo "- Check logs: docker-compose logs audio-ai"
    echo "- Verify .env configuration"
    exit 1
fi

echo
echo "📝 Configuration Summary:"
echo "========================="
if [ -f .env ]; then
    echo "📄 .env file found:"
    grep -E "^(TRANSCRIPTION_PROVIDER|WHISPER_SERVICE_URL|GEMINI_API_KEY)" .env 2>/dev/null | sed 's/GEMINI_API_KEY=.*/GEMINI_API_KEY=***/' || echo "No transcription config found in .env"
else
    echo "⚠️  No .env file found - using defaults"
fi

echo
echo "🎯 Ready to use!"
echo "Upload audio: curl -X POST http://localhost:3000/process-file -F \"file=@your-audio.mp3\""
echo "Check logs: docker-compose logs audio-ai"