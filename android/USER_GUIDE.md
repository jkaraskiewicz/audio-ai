# Audio AI Android App - User Guide

> **Transform voice recordings and shared content into organized documents with one tap**

## 📱 What is Audio AI?

Audio AI is an Android share target app that seamlessly integrates with your existing apps to process voice recordings, text messages, and files using AI. Simply share content from any app, and Audio AI will:

- **Transcribe audio** to text using advanced speech recognition
- **Extract insights** with AI-powered analysis  
- **Organize content** into structured markdown documents
- **Save to your server** for easy access and search

## 🚀 Quick Start (2 minutes)

### 1. Install the App
```bash
# Method 1: Install from APK file
adb install app-debug.apk

# Method 2: Build and install from source
cd android
./gradlew installDebug
```

### 2. Configure Server Connection
1. **Open Audio AI app**
2. **Tap "Settings"** in the main screen
3. **Enter Server URL**: e.g., `http://192.168.1.100:3000`
4. **Test Connection** - should show "✅ Connection successful"

### 3. Start Sharing
1. **Open any app** (WhatsApp, Voice Recorder, Files, etc.)
2. **Select content** to share (audio, text, files)
3. **Tap "Share" button**
4. **Choose "Audio AI"** from the share menu
5. **Wait for processing** - you'll see success notification

## 🎯 Supported Content Types

### 📢 Audio Files
**Sources**: Voice Recorder, WhatsApp voice messages, Telegram audio, downloaded podcasts, meeting recordings

**Supported Formats**: MP3, WAV, M4A, OGG, FLAC, WebM, AIFF

**What happens**: Audio is transcribed to text, then AI analyzes for key insights, action items, and organizing into structured documents

### 📝 Text Content  
**Sources**: Messages, notes, emails, copied text, documents

**Supported Types**: Plain text, messaging app content, clipboard text

**What happens**: Text is directly processed by AI for categorization, summary generation, and action item extraction

### 📁 Documents
**Sources**: File managers, cloud storage apps, document viewers

**Supported Types**: .txt, .md files

**What happens**: File contents are extracted and processed for insights and organization

## 💡 Usage Examples

### Example 1: Meeting Recording
```
1. Record meeting in Voice Recorder app
2. Tap "Share" → "Audio AI" 
3. AI generates:
   - Meeting summary
   - Action items with ownership
   - Key decisions and follow-ups
   - Organized in processed/meetings/
```

### Example 2: WhatsApp Voice Message
```
1. Long-press voice message in WhatsApp
2. Tap "Share" → "Audio AI"
3. AI creates:
   - Transcription of conversation
   - Important points and context
   - Categorized by topic/person
```

### Example 3: Brainstorming Session
```
1. Voice record ideas in any recording app
2. Share to Audio AI
3. Get organized output:
   - Summary of concepts discussed
   - Categorized ideas by theme
   - Action items for next steps
   - Tags for easy searching
```

## ⚙️ App Configuration

### Server Settings

**Server URL Format**: 
- Local network: `http://192.168.1.100:3000`
- Remote server: `https://your-domain.com`
- Localhost (testing): `http://10.0.2.2:3000` (Android emulator)

**Test Connection**: Always test after entering URL to verify:
- ✅ Server is reachable
- ✅ API endpoints are working  
- ✅ Authentication is configured

### Network Requirements

**Permissions**: App requires internet permission (automatically granted)

**Connectivity**: Works on WiFi, mobile data, VPN connections

**Timeout Settings**: 
- Connection: 30 seconds
- Upload: 60 seconds (adjustable for large files)

## 📊 Understanding Output

### Generated File Structure
```
processed/
├── meetings/2025/01/
│   ├── weekly-standup-jan-15.md
│   └── project-review-jan-20.md
├── personal/2025/01/
│   ├── weekend-plans-ideas.md
│   └── health-goals-discussion.md
└── projects/2025/01/
    ├── app-feature-brainstorm.md
    └── client-feedback-analysis.md
```

### Document Format
```markdown
# [AI-Generated Title Based on Content]

## Summary
Brief overview of the main topics and themes discussed...

## Ideas
- Key concepts and creative thoughts
- Brainstorming results and suggestions
- Plans and proposals mentioned

## Action Items  
- [ ] Specific tasks with clear next steps
- [ ] Assignments and deadlines
- [ ] Follow-up items and reminders

## Tags
topic1, person-names, project-id, meeting-type
```

## 🔧 Troubleshooting

### Connection Issues

**❌ "Connection Failed"**
```
Solutions:
1. Check server URL format (include http://)
2. Verify server is running (test in browser)
3. Ensure same network (WiFi) or proper port forwarding
4. Check firewall settings on server
```

**❌ "Timeout Error"**  
```
Solutions:
1. Check internet connection stability
2. Try smaller file sizes first
3. Verify server isn't overloaded
4. Restart app and try again
```

### Processing Issues

**❌ "Processing Failed"**
```
Possible causes:
1. File format not supported
2. File too large for current provider
3. Server transcription service down
4. Invalid audio/text content

Solutions:
1. Try different file format (MP3 recommended)
2. Check file size limits in server logs
3. Verify transcription provider is working
4. Test with simple audio first
```

**❌ "No Response"**
```
Solutions:
1. Check server logs for errors
2. Verify Gemini API key is configured
3. Test server directly with curl
4. Restart backend service
```

### App Issues

**❌ App Crashes on Share**
```
Solutions:
1. Check Android logs: adb logcat | grep AudioAI
2. Clear app data and reconfigure
3. Update to latest APK version
4. Report issue with crash logs
```

**❌ Share Menu Doesn't Show Audio AI**
```
Solutions:
1. Reinstall app to refresh system registration
2. Clear default app settings for sharing
3. Try sharing different content type
4. Restart Android device
```

## 🎯 Best Practices

### For Best Results

1. **Clear Audio**: Record in quiet environments when possible
2. **Reasonable Length**: 1-30 minute recordings work best
3. **Good Quality**: Higher bitrate audio = better transcription
4. **Structured Speech**: Speak clearly with pauses between topics

### Organizing Content

1. **Consistent Naming**: Use descriptive filenames for uploaded content
2. **Regular Processing**: Don't let recordings pile up - process regularly  
3. **Review Generated Content**: Check AI output and edit if needed
4. **Use Tags**: Take advantage of generated tags for searching

### Privacy & Security

1. **Local Network**: Keep server on local network when possible
2. **HTTPS**: Use HTTPS for remote server connections
3. **Audio Cleanup**: Original audio files are not permanently stored
4. **Review Content**: Check generated documents before sharing

## 📈 Advanced Usage

### Batch Processing
```
1. Share multiple files from file manager
2. Audio AI processes each individually  
3. Results saved with timestamps
4. Check server logs for processing status
```

### Custom Categories
```
Backend can be configured for custom categories:
- meetings → /processed/meetings/
- personal → /processed/personal/
- projects → /processed/projects/
```

### Integration with Other Apps
```
Audio AI works with any app that supports Android sharing:
- Voice recorders (default, Smart Recorder, etc.)
- Messaging (WhatsApp, Telegram, Signal)  
- Cloud storage (Google Drive, Dropbox)
- Email (Gmail, Outlook)
- Document viewers (Adobe, Office)
```

## 🆘 Getting Help

### Diagnostic Information

When reporting issues, include:

```bash
# App version
Settings → About → Version

# Server connection test result  
Settings → Test Connection → Copy result

# Android version
Settings → System → About Phone

# Sample server log
curl http://your-server:3000/health
```

### Common Solutions Summary

| Problem | Quick Fix |
|---------|-----------|
| Can't connect | Check server URL format |
| Share menu missing | Reinstall app |
| Processing slow | Check file size/server load |
| No transcription | Verify transcription provider |
| App crashes | Clear data, reinstall |
| Wrong category | Check server AI configuration |

---

## 🌟 Tips for Maximum Productivity

1. **Record Thoughts Immediately**: Use voice recording for quick idea capture
2. **Process Daily**: Make it part of your daily routine
3. **Review & Edit**: AI is smart but human review adds value
4. **Search & Reference**: Use generated tags to find related content
5. **Share Results**: Generated markdown works great in team docs

**Audio AI helps you capture, process, and organize information effortlessly. Start with simple voice recordings and discover how AI can transform your note-taking workflow!**