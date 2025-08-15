# Audio AI - Android Share Target App

An Android app that acts as a share target for text and files, sending them to your Audio AI backend for processing.

**Package**: `com.karaskiewicz.audioai`

## Features

- **Share Target**: Appears in Android's share menu for text and files
- **Text Processing**: Share selected text from any app
- **File Processing**: Share files from file managers or other apps
- **Configurable Backend**: Set your backend server URL in settings
- **Connection Testing**: Test connection to your backend server

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0)
- Your Audio AI backend running and accessible

### Build Instructions

1. Open the `android` directory in Android Studio
2. Sync Gradle files
3. Build and run on device/emulator

### Configuration

1. Install the app on your Android device
2. Open the app and go to Settings
3. Set your backend server URL (e.g., `http://192.168.1.100:3000`)
4. Test the connection to verify it works
5. Now you can share text and files from other apps!

## Usage

### Sharing Text
1. Select text in any app (browser, notes, etc.)
2. Tap the Share button
3. Choose "Audio AI Share" from the list
4. The text will be sent to your backend for processing

### Sharing Files
1. Select a file in a file manager
2. Tap Share
3. Choose "Audio AI Share" from the list
4. The file will be sent to your backend for processing

## Supported File Types

The app accepts all file types (`*/*`) but your backend determines which files can actually be processed. Refer to your backend documentation for supported formats.

## Network Configuration

Make sure your Android device can reach your backend server:

- If running locally, use your computer's local IP address
- If using Docker, ensure ports are properly exposed
- If using cloud hosting, use the public URL

Example URLs:
- Local development: `http://192.168.1.100:3000`
- Cloud hosting: `https://your-domain.com`
- Docker with port mapping: `http://your-ip:3000`

## Troubleshooting

### Connection Issues
- Verify the server URL is correct
- Check that your backend is running
- Ensure your device and server are on the same network (for local development)
- Test the connection using the "Test Connection" button in settings

### App Not Appearing in Share Menu
- Make sure you've installed the app properly
- Restart the sharing app if needed
- Check that you're sharing supported content types

## Technical Details

- **Language**: Kotlin
- **Architecture**: MVVM pattern with Repository
- **Networking**: Retrofit + OkHttp
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)