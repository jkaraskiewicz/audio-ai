# 🎙️ Scribely App - Hand Over Notes

## 📖 Overview

**Scribely** is an Android audio recording app built with modern Android development practices. The app features a OnePlus Recorder-inspired interface with real-time waveform visualization, timeline controls, and clean UI design.

## 🎨 UI Customization Made Easy

### Quick Customization Reference

The app is designed for **easy UI customization**. All design constants are centralized in `UIConfig.kt`:

```kotlin
// 🔴 Change main brand color
UIConfig.Colors.ScribelyRed = Color(0xFFE53935)

// 📏 Adjust logo size
UIConfig.Sizing.LogoIconSize = 28.dp

// 📱 Move logo position
UIConfig.LayoutPresets.currentLogoPosition = LogoPosition.TOP_LEFT
```

### 🎨 Key Customization Files

#### 1. **UIConfig.kt** - The Design Control Center
Location: `android/app/src/main/java/com/karaskiewicz/audioai/ui/theme/UIConfig.kt`

**What you can change:**
- **Colors**: Brand colors, recording states, UI accents
- **Spacing**: Padding, margins, component spacing
- **Sizing**: Button sizes, timeline dimensions, logo size
- **Typography**: Letter spacing, text styles
- **Behavior**: Animation durations, timeline settings
- **Layout Presets**: Logo position, controls layout

#### 2. **MainScreen.kt** - UI Components
Location: `android/app/src/main/java/com/karaskiewicz/audioai/ui/screen/MainScreen.kt`

**Look for 🎨 CUSTOMIZABLE comments** throughout the file for quick modification points.

## 🔧 Quick Modification Guide

### Change the Logo
```kotlin
// In ModernLogoText component (MainScreen.kt:744)
Text(
  text = "YourApp", // 🎨 Change app name here
  // ...
)

Box(
  // Logo icon background
  modifier = Modifier.background(
    UIConfig.Colors.ScribelyRed, // 🎨 Change logo color
    RoundedCornerShape(UIConfig.Sizing.LogoIconCornerRadius)
  )
) {
  Text(
    text = "Y", // 🎨 Change logo letter here
    // ...
  )
}
```

### Move UI Elements
```kotlin
// Change logo position
UIConfig.LayoutPresets.currentLogoPosition = LogoPosition.TOP_LEFT // or TOP_RIGHT, TOP_CENTER

// Adjust spacing
UIConfig.Spacing.LogoPadding = 60.dp // More space around logo
UIConfig.Spacing.ControlsBottomPadding = 80.dp // Move controls higher
```

### Change Colors & Styling
```kotlin
// Brand colors
UIConfig.Colors.ScribelyRed = Color(0xFF1976D2)     // Change to blue
UIConfig.Colors.ScribelyGreen = Color(0xFFFF9800)   // Change to orange

// Button sizes
UIConfig.Sizing.MainButtonSize = 80.dp              // Bigger buttons
UIConfig.Sizing.RecordButtonSize = 90.dp            // Bigger record button

// Timeline appearance
UIConfig.Behavior.WaveformBarsCount = 100           // More waveform bars
UIConfig.Sizing.TimelineWidth = 400.dp              // Wider timeline
```

## 🏗️ Architecture Overview

### Clean Architecture Pattern
The app follows **Uncle Bob's Clean Architecture** principles:

```
📁 domain/
  ├── 📁 model/          # Business entities
  ├── 📁 repository/     # Data contracts
  └── 📁 usecase/        # Business logic

📁 data/
  └── 📁 repository/     # Repository implementations

📁 ui/
  ├── 📁 screen/         # Composable screens
  ├── 📁 theme/          # UI configuration
  └── 📁 viewmodel/      # MVVM ViewModels
```

### Key Components

#### 🎛️ MainViewModel
- **State Management**: Uses `StateFlow` for reactive UI updates
- **Recording Control**: Manages MediaRecorder lifecycle
- **Error Handling**: Centralized error and success message handling

#### 🎨 UI Components
- **ModernLogoText**: Configurable logo component
- **TimelineWithWaveform**: Real-time waveform visualization
- **CleanRecordingControls**: OnePlus-style control buttons
- **OnePlusStyleTimeline**: Complete recording interface

#### 🔧 Configuration System
- **UIConfig**: Centralized design constants
- **Theme Integration**: Works with Material You
- **Easy Customization**: Developer-friendly parameter system

## 📱 Key Features

### ✨ OnePlus-Style Interface
- **Timeline View**: Time markers with moving red playhead
- **Waveform Visualization**: Real-time audio waveform display
- **Recording States**: Visual feedback for REC/PAUSED states
- **Modern Controls**: Circular buttons with proper elevation

### 🎯 User Experience
- **Permission Handling**: Audio recording permission management
- **State Persistence**: Recording state survives configuration changes
- **Visual Feedback**: Color changes during recording (gray background)
- **Error Handling**: User-friendly error and success messages

### 🔄 Recording Flow
1. **Idle State**: Shows main record button
2. **Recording State**: Timeline interface with REC indicator
3. **Paused State**: Timeline interface with PAUSED indicator
4. **Processing State**: Loading indicator while processing

## 🛠️ Development Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- Kotlin 1.8+
- Compile SDK 34
- Min SDK 26

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Check code style
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

### Key Dependencies
- **Jetpack Compose**: Modern declarative UI
- **Material You**: Material Design 3
- **ViewModel**: MVVM architecture
- **Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management

## 🔍 Common Customization Tasks

### 1. **Change Button Layout**
```kotlin
// In UIConfig.kt
object LayoutPresets {
    val currentControlsLayout = ControlsLayout.THREE_BUTTONS // Add third button
}
```

### 2. **Modify Timeline Behavior**
```kotlin
// In UIConfig.kt
object Behavior {
    val TimelineWindowDuration = 15000L  // 15-second window instead of 10
    val WaveformBarsCount = 120         // More detailed waveform
}
```

### 3. **Add New Color Themes**
```kotlin
// In UIConfig.kt
object Colors {
    // Dark theme variant
    val DarkScribelyRed = Color(0xFFFF5252)
    val DarkBackground = Color(0xFF121212)
    
    // Blue theme variant
    val BlueThemePrimary = Color(0xFF2196F3)
    val BlueThemeSecondary = Color(0xFF03DAC6)
}
```

### 4. **Customize Animations**
```kotlin
// In UIConfig.kt
object Behavior {
    val ButtonAnimationDuration = 300     // Slower button animations
    val WaveformAnimationDuration = 100   // Faster waveform updates
}
```

## 📂 File Structure Quick Reference

```
android/app/src/main/java/com/karaskiewicz/audioai/
├── 🎨 ui/
│   ├── screen/MainScreen.kt           # Main UI components
│   ├── screen/SettingsScreen.kt       # Settings page
│   ├── screen/ShareScreen.kt          # Share functionality
│   ├── theme/UIConfig.kt              # 🔧 Design constants (MODIFY THIS!)
│   ├── theme/Theme.kt                 # Material Theme setup
│   ├── theme/Color.kt                 # Base color definitions
│   └── viewmodel/MainViewModel.kt     # Business logic
├── 🏗️ domain/
│   ├── model/RecordingState.kt        # Business entities
│   └── usecase/RecordingUseCase.kt    # Recording business logic
└── 📁 data/
    └── repository/                    # Data layer implementations
```

## 🚀 Next Steps for Development

### Immediate Tasks
1. **Test Customizations**: Try changing colors/sizes in `UIConfig.kt`
2. **Review Components**: Understand the modular component structure
3. **Explore Features**: Test recording, pausing, and UI states

### Potential Enhancements
1. **Save Functionality**: Implement actual save/cancel recording actions
2. **Audio Quality**: Add quality settings to recording
3. **Export Options**: Multiple audio format support
4. **Themes**: Multiple color theme presets
5. **Animations**: Enhanced UI animations and transitions

### Code Quality Maintenance
- **Follow SOLID principles** already established
- **Use existing patterns** for consistency
- **Update UIConfig** instead of hardcoding values
- **Add tests** for new features
- **Run linting** before commits

## 💡 Tips for Developers

### 🔧 Quick Testing
1. **Change logo color**: Modify `UIConfig.Colors.ScribelyRed`
2. **Move logo**: Change `UIConfig.LayoutPresets.currentLogoPosition`
3. **Resize buttons**: Adjust `UIConfig.Sizing.MainButtonSize`

### 🎨 Design Philosophy
- **Configuration over hardcoding**: Use `UIConfig` constants
- **Component modularity**: Pass customizable parameters
- **Clean separation**: UI logic separate from business logic
- **Developer experience**: Clear comments and documentation

### 🔍 Finding Code
- Look for **🎨 CUSTOMIZABLE** comments for quick modification points
- Use Android Studio's **"Find in Files"** to search for specific UI elements
- Check `UIConfig.kt` first for any design-related changes
- Follow the import statements to understand component relationships

---

**Happy Coding! 🎉**

The app is now ready for easy customization and further development. All the groundwork has been laid for quick UI modifications and clean code maintenance.