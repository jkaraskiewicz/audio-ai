# Suggested Development Commands

## Backend Commands (TypeScript/Node.js)

### Building & Running
```bash
cd backend && npm run build          # Compile TypeScript to JavaScript
cd backend && npm run dev            # Development server with hot reload
cd backend && npm start              # Production server
```

### Testing
```bash
cd backend && npm test               # Run all tests
cd backend && npm run test:watch     # Run tests in watch mode
cd backend && npm run test:coverage  # Run tests with coverage report
```

### Code Quality
```bash
cd backend && npm run lint           # Lint TypeScript files
cd backend && npm run lint:fix       # Auto-fix linting issues
cd backend && npm run format         # Format code with Prettier
cd backend && npm run typecheck      # TypeScript type checking
```

### Configuration
```bash
cd backend && npm run config         # Interactive configuration
cd backend && npm run config:validate # Validate configuration
cd backend && npm run config:show    # Show current configuration
cd backend && npm run config:test    # Test configuration
```

## Android Commands (Kotlin)

### Building
```bash
cd android && ./gradlew assembleDebug        # Build debug APK
cd android && ./gradlew assembleRelease      # Build release APK
cd android && ./gradlew installDebug         # Install debug APK on device
cd android && ./gradlew clean                # Clean build artifacts
```

### Testing
```bash
cd android && ./gradlew test                 # Run unit tests
cd android && ./gradlew connectedAndroidTest # Run instrumented tests (requires device)
```

### Code Quality
```bash
cd android && ./gradlew ktlintCheck          # Lint Kotlin code
cd android && ./gradlew ktlintFormat         # Auto-format Kotlin code
```

### Combined (Build + Lint)
```bash
cd android && ./gradlew ktlintFormat && ./gradlew ktlintCheck && ./gradlew assembleDebug
```

## Docker Commands

### Development
```bash
docker-compose up -d                         # Start all services (backend + Whisper)
docker-compose --profile dev up -d           # Start with development hot reload
docker-compose --profile full up -d          # Full stack including local Whisper
docker-compose logs -f audio-ai              # View backend logs
docker-compose logs -f whisper               # View Whisper logs
docker-compose down                          # Stop all services
```

### Production (GitHub Container Registry)
```bash
docker-compose -f docker-compose.production.yml up -d        # Start production
docker-compose -f docker-compose.production.yml down         # Stop production
docker-compose -f docker-compose.production.yml pull         # Pull latest images
```

## Git Commands (macOS/Darwin)

```bash
git status                           # Check repository status
git add .                            # Stage all changes
git commit -m "feat: description"    # Commit with conventional commits format
git push                             # Push to remote
git log --oneline -10                # View last 10 commits
```

## Testing the System

### Health Check
```bash
curl http://localhost:3000/health
```

### Process Audio File
```bash
curl -X POST http://localhost:3000/process-file \
  -F "file=@sample-audio.mp3"
```

### Process Text
```bash
curl -X POST http://localhost:3000/process-text \
  -H "Content-Type: application/json" \
  -d '{"text":"Meeting notes: discuss project timeline"}'
```

## Task Completion Workflow

When completing a task, run in this order:

### For Backend Changes:
1. `cd backend && npm run typecheck`  # Verify types
2. `cd backend && npm run lint`       # Check linting
3. `cd backend && npm test`           # Run tests
4. `cd backend && npm run build`      # Verify build succeeds

### For Android Changes:
1. `cd android && ./gradlew ktlintFormat`     # Format code
2. `cd android && ./gradlew ktlintCheck`      # Check lint
3. `cd android && ./gradlew test`             # Run tests
4. `cd android && ./gradlew assembleDebug`    # Verify build succeeds

### For Both:
After all checks pass, commit changes with conventional commits format:
```bash
git add .
git commit -m "type: description"  # type = feat|fix|refactor|test|docs|chore
git push
```