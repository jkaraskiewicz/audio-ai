#!/bin/bash

# Audio-AI Deployment Test Script
# Tests all endpoints to verify the application is working properly

set -e  # Exit on any error

BASE_URL="http://localhost:3000"
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Testing Audio-AI Deployment${NC}"
echo "Base URL: $BASE_URL"
echo

# Test 1: Health Check
echo -e "${YELLOW}1. Testing Health Check...${NC}"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health")
if [ "$response" = "200" ]; then
    echo -e "${GREEN}✅ Health check passed${NC}"
else
    echo -e "${RED}❌ Health check failed (HTTP $response)${NC}"
    exit 1
fi

# Test 2: Root endpoint
echo -e "${YELLOW}2. Testing Root Endpoint...${NC}"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/")
if [ "$response" = "200" ]; then
    echo -e "${GREEN}✅ Root endpoint passed${NC}"
else
    echo -e "${RED}❌ Root endpoint failed (HTTP $response)${NC}"
    exit 1
fi

# Test 3: Process transcript endpoint
echo -e "${YELLOW}3. Testing Text Processing...${NC}"
response=$(curl -s -X POST "$BASE_URL/process" \
    -H "Content-Type: application/json" \
    -d '{"transcript": "This is a test transcript about a new project idea for a mobile app"}' \
    -w "%{http_code}")

# Extract status code (last 3 characters)
status_code="${response: -3}"
if [ "$status_code" = "200" ]; then
    echo -e "${GREEN}✅ Text processing passed${NC}"
else
    echo -e "${RED}❌ Text processing failed (HTTP $status_code)${NC}"
    echo "Response: $response"
fi

# Test 4: File processing endpoint (if test audio exists)
if [ -f "test-audio.m4a" ]; then
    echo -e "${YELLOW}4. Testing Audio File Processing...${NC}"
    response=$(curl -s -X POST "$BASE_URL/process-file" \
        -F "file=@test-audio.m4a" \
        -w "%{http_code}")
    
    status_code="${response: -3}"
    if [ "$status_code" = "200" ]; then
        echo -e "${GREEN}✅ Audio file processing passed${NC}"
    else
        echo -e "${RED}❌ Audio file processing failed (HTTP $status_code)${NC}"
        echo "Response: $response"
    fi
else
    echo -e "${YELLOW}4. Skipping audio test (test-audio.m4a not found)${NC}"
fi

# Test 5: Error handling
echo -e "${YELLOW}5. Testing Error Handling...${NC}"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/nonexistent")
if [ "$response" = "404" ]; then
    echo -e "${GREEN}✅ Error handling passed${NC}"
else
    echo -e "${RED}❌ Error handling unexpected (HTTP $response)${NC}"
fi

echo
echo -e "${GREEN}🎉 Deployment test completed successfully!${NC}"
echo
echo "Available endpoints:"
echo "  GET  $BASE_URL/          - Health check"
echo "  GET  $BASE_URL/health    - Health check"
echo "  POST $BASE_URL/process   - Process text transcript"
echo "  POST $BASE_URL/process-file - Process file (audio/text)"
echo
echo "Example usage:"
echo "  curl -X POST $BASE_URL/process-file -F \"file=@your-audio.mp3\""