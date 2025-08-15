#!/usr/bin/env node

/**
 * Audio-AI Configuration Management CLI
 * 
 * This utility helps developers manage configuration settings,
 * validate configurations, and troubleshoot setup issues.
 * 
 * Usage:
 *   node scripts/config.js [command] [options]
 * 
 * Commands:
 *   validate    - Validate current configuration
 *   show        - Show current configuration summary
 *   test        - Test provider connectivity
 *   init        - Initialize configuration with guided setup
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// ANSI color codes
const colors = {
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m',
  white: '\x1b[37m',
  reset: '\x1b[0m',
  bold: '\x1b[1m'
};

function colorize(text, color) {
  return `${colors[color]}${text}${colors.reset}`;
}

function printHeader(title) {
  console.log('\n' + colorize('='.repeat(60), 'blue'));
  console.log(colorize(`  ${title}`, 'bold'));
  console.log(colorize('='.repeat(60), 'blue') + '\n');
}

function printSection(title) {
  console.log(colorize(`\n▶ ${title}`, 'cyan'));
  console.log(colorize('-'.repeat(title.length + 2), 'cyan'));
}

function printSuccess(message) {
  console.log(colorize(`✓ ${message}`, 'green'));
}

function printWarning(message) {
  console.log(colorize(`⚠ ${message}`, 'yellow'));
}

function printError(message) {
  console.log(colorize(`✗ ${message}`, 'red'));
}

function printInfo(message) {
  console.log(colorize(`ℹ ${message}`, 'blue'));
}

/**
 * Load environment variables from .env file
 */
function loadEnv() {
  const envPath = path.join(__dirname, '..', '.env');
  if (fs.existsSync(envPath)) {
    const envContent = fs.readFileSync(envPath, 'utf-8');
    envContent.split('\n').forEach(line => {
      const [key, value] = line.split('=', 2);
      if (key && value && !process.env[key]) {
        process.env[key] = value.trim();
      }
    });
  }
}

/**
 * Get configuration from the system
 */
async function getConfiguration() {
  try {
    // Import the configuration dynamically
    const configModule = require('../dist/config/index.js');
    const { ConfigManager } = configModule;
    
    const configManager = ConfigManager.getInstance();
    return {
      summary: configManager.getConfigSummary(),
      validation: configManager.validateConfig(),
      environment: configManager.getEnvironment(),
      features: configManager.getFeatureFlags(),
    };
  } catch (error) {
    // Try to get basic configuration without requiring build
    return {
      summary: getBasicConfig(),
      validation: validateBasicConfig(),
      environment: {
        NODE_ENV: process.env.NODE_ENV || 'development',
        USE_CASE: process.env.USE_CASE,
      },
      features: null,
      buildRequired: true,
    };
  }
}

/**
 * Get basic configuration without requiring TypeScript build
 */
function getBasicConfig() {
  return {
    environment: process.env.NODE_ENV || 'development',
    useCase: process.env.USE_CASE,
    port: parseInt(process.env.PORT || '3000', 10),
    hasGeminiKey: !!(process.env.GEMINI_API_KEY && process.env.GEMINI_API_KEY !== 'your_api_key_here'),
    transcriptionProvider: process.env.TRANSCRIPTION_PROVIDER || 'free_web_speech',
    hasTranscriptionKey: !!process.env.TRANSCRIPTION_API_KEY,
  };
}

/**
 * Validate basic configuration
 */
function validateBasicConfig() {
  const errors = [];
  const warnings = [];

  if (!process.env.GEMINI_API_KEY || process.env.GEMINI_API_KEY === 'your_api_key_here') {
    errors.push('GEMINI_API_KEY is not configured');
  }

  const port = parseInt(process.env.PORT || '3000', 10);
  if (isNaN(port) || port < 1 || port > 65535) {
    errors.push('Invalid PORT value');
  }

  const provider = process.env.TRANSCRIPTION_PROVIDER;
  if (provider && provider !== 'free_web_speech' && provider !== 'mock' && !process.env.TRANSCRIPTION_API_KEY) {
    warnings.push(`Transcription provider ${provider} typically requires an API key`);
  }

  return {
    valid: errors.length === 0,
    errors,
    warnings,
  };
}

/**
 * Command: validate
 */
async function validateCommand() {
  printHeader('Configuration Validation');
  
  const config = await getConfiguration();
  
  if (config.buildRequired) {
    printWarning('TypeScript build not found. Running basic validation only.');
    printInfo('Run "npm run build" for comprehensive validation.');
  }

  printSection('Validation Results');
  
  if (config.validation.valid) {
    printSuccess('Configuration is valid!');
  } else {
    printError('Configuration has issues:');
    config.validation.errors.forEach(error => {
      console.log(`  ${colorize('•', 'red')} ${error}`);
    });
  }

  if (config.validation.warnings && config.validation.warnings.length > 0) {
    printSection('Warnings');
    config.validation.warnings.forEach(warning => {
      console.log(`  ${colorize('•', 'yellow')} ${warning}`);
    });
  }

  return config.validation.valid;
}

/**
 * Command: show
 */
async function showCommand() {
  printHeader('Current Configuration');
  
  const config = await getConfiguration();
  
  if (config.buildRequired) {
    printWarning('Showing basic configuration only (TypeScript build required for full details)');
    showBasicConfig();
    return;
  }

  printSection('Environment');
  console.log(`Environment: ${colorize(config.summary.environment, 'green')}`);
  if (config.summary.useCase) {
    console.log(`Use Case: ${colorize(config.summary.useCase, 'green')}`);
  }
  console.log(`Port: ${config.summary.port}`);
  console.log(`Base Directory: ${config.summary.baseDirectory}`);

  printSection('AI Configuration');
  console.log(`Model: ${config.summary.ai.model}`);
  console.log(`Max Tokens: ${config.summary.ai.maxTokens}`);
  console.log(`Temperature: ${config.summary.ai.temperature}`);

  printSection('Transcription Configuration');
  console.log(`Provider: ${colorize(config.summary.transcription.provider, 'cyan')}`);
  console.log(`Has API Key: ${config.summary.transcription.hasApiKey ? colorize('Yes', 'green') : colorize('No', 'red')}`);
  if (config.summary.transcription.model) {
    console.log(`Model: ${config.summary.transcription.model}`);
  }
  console.log(`Language: ${config.summary.transcription.language}`);
  console.log(`Max File Size: ${formatBytes(config.summary.transcription.maxFileSize)}`);

  printSection('Feature Flags');
  Object.entries(config.summary.features).forEach(([feature, enabled]) => {
    const status = enabled ? colorize('Enabled', 'green') : colorize('Disabled', 'red');
    console.log(`${feature}: ${status}`);
  });
}

/**
 * Show basic configuration
 */
function showBasicConfig() {
  const config = getBasicConfig();
  
  printSection('Basic Configuration');
  console.log(`Environment: ${colorize(config.environment, 'green')}`);
  if (config.useCase) {
    console.log(`Use Case: ${colorize(config.useCase, 'green')}`);
  }
  console.log(`Port: ${config.port}`);
  console.log(`Gemini API Key: ${config.hasGeminiKey ? colorize('Configured', 'green') : colorize('Missing', 'red')}`);
  console.log(`Transcription Provider: ${colorize(config.transcriptionProvider, 'cyan')}`);
  console.log(`Transcription API Key: ${config.hasTranscriptionKey ? colorize('Configured', 'green') : colorize('Not Set', 'yellow')}`);
}

/**
 * Command: test
 */
async function testCommand() {
  printHeader('Provider Connectivity Test');
  
  printWarning('This feature requires the application to be built and running.');
  printInfo('To test providers:');
  console.log('1. Run: npm run build');
  console.log('2. Run: npm run dev');
  console.log('3. Test with: curl -X POST http://localhost:3000/process -d \'{"transcript":"test"}\'');
}

/**
 * Command: init
 */
async function initCommand() {
  printHeader('Configuration Initialization');
  
  const readline = require('readline');
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });

  function question(prompt) {
    return new Promise(resolve => {
      rl.question(prompt, resolve);
    });
  }

  try {
    printInfo('This wizard will help you set up your Audio-AI configuration.');
    console.log('Press Ctrl+C at any time to cancel.\n');

    // Check for existing .env file
    const envPath = path.join(__dirname, '..', '.env');
    if (fs.existsSync(envPath)) {
      const overwrite = await question(colorize('A .env file already exists. Overwrite it? (y/N): ', 'yellow'));
      if (overwrite.toLowerCase() !== 'y') {
        console.log('Configuration initialization cancelled.');
        rl.close();
        return;
      }
    }

    // Environment
    const environment = await question('Environment (development/staging/production) [development]: ') || 'development';
    
    // Use case
    console.log('\nUse cases:');
    console.log('  high-volume   - Optimized for throughput');
    console.log('  high-accuracy - Optimized for quality');
    console.log('  privacy       - Uses local processing only');
    const useCase = await question('Use case (leave empty for default): ');

    // Gemini API Key
    const geminiKey = await question('\nGoogle Gemini API Key (required): ');
    if (!geminiKey) {
      printError('Gemini API Key is required. Exiting.');
      rl.close();
      return;
    }

    // Transcription Provider
    console.log('\nAvailable transcription providers:');
    console.log('  free_web_speech  - Free browser-based (default)');
    console.log('  openai_whisper   - OpenAI Whisper API (requires API key)');
    console.log('  assembly_ai      - AssemblyAI (requires API key)');
    console.log('  mock            - Mock provider for testing');
    const provider = await question('Transcription provider [free_web_speech]: ') || 'free_web_speech';

    let transcriptionKey = '';
    if (provider !== 'free_web_speech' && provider !== 'mock') {
      transcriptionKey = await question(`${provider.toUpperCase()} API Key: `);
    }

    // Port
    const port = await question('Server port [3000]: ') || '3000';

    // Base directory
    const baseDir = await question('Base directory for saved files [processed]: ') || 'processed';

    // Generate .env content
    let envContent = `# Audio-AI Configuration
# Generated by configuration wizard

# Environment
NODE_ENV=${environment}
${useCase ? `USE_CASE=${useCase}\n` : ''}
# Server
PORT=${port}
BASE_DIRECTORY=${baseDir}

# AI Configuration
GEMINI_API_KEY=${geminiKey}

# Transcription Configuration
TRANSCRIPTION_PROVIDER=${provider}
${transcriptionKey ? `TRANSCRIPTION_API_KEY=${transcriptionKey}\n` : ''}
# Feature Flags
ENABLE_COMMENTARY=true
ENABLE_HEALTH_CHECKS=true
ENABLE_DETAILED_LOGGING=${environment === 'development' ? 'true' : 'false'}
ENABLE_RATE_LIMITING=${environment === 'production' ? 'true' : 'false'}
`;

    // Write .env file
    fs.writeFileSync(envPath, envContent);
    
    printSuccess(`Configuration saved to ${envPath}`);
    printInfo('You can now start the application with: npm run dev');
    
    rl.close();

  } catch (error) {
    printError(`Configuration setup failed: ${error.message}`);
    rl.close();
  }
}

/**
 * Format bytes as human-readable text
 */
function formatBytes(bytes) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * Main function
 */
async function main() {
  loadEnv();
  
  const args = process.argv.slice(2);
  const command = args[0] || 'show';

  switch (command.toLowerCase()) {
    case 'validate':
    case 'check':
      const isValid = await validateCommand();
      process.exit(isValid ? 0 : 1);
      break;

    case 'show':
    case 'display':
      await showCommand();
      break;

    case 'test':
    case 'check-connectivity':
      await testCommand();
      break;

    case 'init':
    case 'setup':
      await initCommand();
      break;

    case 'help':
    case '--help':
    case '-h':
      printHeader('Audio-AI Configuration CLI');
      console.log('Usage: node scripts/config.js [command]\n');
      console.log('Commands:');
      console.log('  validate    - Validate current configuration');
      console.log('  show        - Show current configuration summary');
      console.log('  test        - Test provider connectivity');
      console.log('  init        - Initialize configuration with guided setup');
      console.log('  help        - Show this help message\n');
      
      console.log('Environment Variables:');
      console.log('  NODE_ENV                 - Environment (development/staging/production)');
      console.log('  USE_CASE                - Use case (high-volume/high-accuracy/privacy)');
      console.log('  GEMINI_API_KEY          - Google Gemini API key (required)');
      console.log('  TRANSCRIPTION_PROVIDER   - Transcription provider');
      console.log('  TRANSCRIPTION_API_KEY    - Provider-specific API key');
      console.log('  PORT                     - Server port (default: 3000)');
      console.log('  BASE_DIRECTORY           - Directory for saved files');
      break;

    default:
      printError(`Unknown command: ${command}`);
      printInfo('Run "node scripts/config.js help" for usage information.');
      process.exit(1);
  }
}

// Run the CLI
if (require.main === module) {
  main().catch(error => {
    printError(`Unexpected error: ${error.message}`);
    process.exit(1);
  });
}

module.exports = {
  validateCommand,
  showCommand,
  testCommand,
  initCommand,
};