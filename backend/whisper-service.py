#!/usr/bin/env python3
"""
Simple HTTP service that wraps OpenAI Whisper for transcription
Runs in Docker container to keep environment clean
"""

import whisper
import tempfile
import os
import json
from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse as urlparse

# Load Whisper model once at module level
model_name = os.environ.get('WHISPER_MODEL', 'base')
print(f"Loading Whisper model: {model_name}")
whisper_model = whisper.load_model(model_name)
print(f"Whisper model '{model_name}' loaded successfully")

class WhisperHandler(BaseHTTPRequestHandler):
    
    def do_POST(self):
        if self.path == '/transcribe':
            self.handle_transcribe()
        else:
            self.send_error(404)
    
    def do_GET(self):
        if self.path == '/health':
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            response = {
                "status": "healthy",
                "service": "whisper-transcription",
                "model": model_name
            }
            self.wfile.write(json.dumps(response).encode())
        else:
            self.send_error(404)
    
    def handle_transcribe(self):
        try:
            # Get content length
            content_length = int(self.headers.get('Content-Length', 0))
            if content_length == 0:
                self.send_error(400, "No content provided")
                return
            
            # Read audio data
            audio_data = self.rfile.read(content_length)
            
            # Save to temporary file
            with tempfile.NamedTemporaryFile(delete=False, suffix='.audio') as temp_file:
                temp_file.write(audio_data)
                temp_path = temp_file.name
            
            try:
                # Transcribe with Whisper
                print(f"Transcribing audio file: {temp_path}")
                result = whisper_model.transcribe(temp_path)
                
                # Send response
                response = {
                    "text": result["text"].strip(),
                    "language": result.get("language", "unknown")
                }
                
                self.send_response(200)
                self.send_header('Content-Type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps(response).encode())
                
                print(f"Transcription completed: {len(result['text'])} characters")
                
            finally:
                # Clean up temp file
                if os.path.exists(temp_path):
                    os.unlink(temp_path)
                    
        except Exception as e:
            print(f"Transcription error: {e}")
            self.send_error(500, f"Transcription failed: {str(e)}")
    
    def log_message(self, format, *args):
        # Custom logging
        print(f"[{self.address_string()}] {format % args}")

def run_server():
    port = int(os.environ.get('PORT', 8001))
    print(f"Starting Whisper service on port {port}...")
    
    # Create handler class with model loaded
    handler_class = WhisperHandler
    
    server = HTTPServer(('0.0.0.0', port), handler_class)
    print(f"Whisper service ready at http://0.0.0.0:{port}")
    
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down Whisper service...")
        server.shutdown()

if __name__ == '__main__':
    run_server()