# Docker image with Whisper for transcription services
FROM python:3.9-slim

# Install system dependencies
RUN apt-get update && apt-get install -y \
    ffmpeg \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install OpenAI Whisper
RUN pip install --no-cache-dir openai-whisper

# Create app directory
WORKDIR /app

# Create a simple whisper service script
COPY whisper-service.py .

# Expose port for the whisper service
EXPOSE 8001

# Run the whisper service
CMD ["python", "whisper-service.py"]