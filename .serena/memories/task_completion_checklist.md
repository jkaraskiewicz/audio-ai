# Task Completion Checklist

When completing any development task, follow these steps to ensure quality:

## For Backend (TypeScript) Changes

### 1. Type Checking
```bash
cd backend && npm run typecheck
```
- Ensures no TypeScript type errors
- Must pass before proceeding

### 2. Linting
```bash
cd backend && npm run lint
```
- Checks code style and best practices
- If issues found, run: `npm run lint:fix`
- All issues must be resolved

### 3. Testing
```bash
cd backend && npm test
```
- Runs all Jest tests
- **ALL TESTS MUST BE GREEN**
- If tests fail, fix them before proceeding
- Add new tests for new features

### 4. Build Verification
```bash
cd backend && npm run build
```
- Compiles TypeScript to JavaScript
- Verifies production build works
- Must complete successfully

### 5. Format (Optional but Recommended)
```bash
cd backend && npm run format
```
- Auto-formats code with Prettier
- Ensures consistent code style

## For Android (Kotlin) Changes

### 1. Auto-Format
```bash
cd android && ./gradlew ktlintFormat
```
- Auto-fixes Kotlin formatting issues
- Should always run first

### 2. Lint Check
```bash
cd android && ./gradlew ktlintCheck
```
- Verifies Kotlin code style
- Must pass with no violations

### 3. Unit Tests
```bash
cd android && ./gradlew test
```
- Runs all unit tests
- **ALL TESTS MUST BE GREEN**
- If tests fail, fix them before proceeding
- Add new tests for new features

### 4. Build Verification
```bash
cd android && ./gradlew assembleDebug
```
- Builds debug APK
- Verifies compilation succeeds
- Must complete successfully

### Quick Combined Check
```bash
cd android && ./gradlew ktlintFormat && ./gradlew ktlintCheck && ./gradlew assembleDebug
```

## For Docker/Infrastructure Changes

### 1. Validate Docker Compose
```bash
docker-compose config
```
- Validates docker-compose.yml syntax
- Must show valid configuration

### 2. Build and Start
```bash
docker-compose up -d --build
```
- Builds and starts containers
- Check logs: `docker-compose logs -f`

### 3. Health Check
```bash
curl http://localhost:3000/health
```
- Verifies backend service is running
- Should return healthy status

## Git Commit Process

After all checks pass:

### 1. Stage Changes
```bash
git add .
```

### 2. Commit with Conventional Format
```bash
git commit -m "type: description"
```

**Commit types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring (no behavior change)
- `test:` - Adding or updating tests
- `docs:` - Documentation changes
- `chore:` - Maintenance tasks
- `style:` - Code style changes (formatting, no logic change)

**Examples:**
- `feat: add audio format conversion for Whisper`
- `fix: resolve null pointer in RecordingUseCase`
- `refactor: split ProviderRegistry into smaller classes`
- `test: add unit tests for AIService`

### 3. Push to Remote
```bash
git push
```

## Critical Rules

### Testing
- **NEVER commit code with failing tests**
- **ALWAYS verify tests pass after changes**
- Tests must be GREEN ✅

### Linting
- **ALWAYS run linter before committing**
- Auto-fix issues when possible
- Resolve all violations

### Build
- **ALWAYS verify build succeeds**
- Both TypeScript compilation and Android build must work

### Type Safety
- **NO TypeScript `any` types** (use `unknown` if needed)
- **NO Kotlin nullable types** unless absolutely necessary
- Type checking must pass

## Pre-Commit Quick Check

**Backend:**
```bash
cd backend && npm run typecheck && npm run lint && npm test && npm run build
```

**Android:**
```bash
cd android && ./gradlew ktlintFormat && ./gradlew ktlintCheck && ./gradlew test && ./gradlew assembleDebug
```

If both pass, you're ready to commit! ✅