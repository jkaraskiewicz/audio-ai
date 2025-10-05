module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
    project: './tsconfig.json',
  },
  plugins: ['@typescript-eslint', 'prettier'],
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier',
    'plugin:prettier/recommended',
  ],
  rules: {
    'prettier/prettier': 'error',
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    '@typescript-eslint/explicit-function-return-type': 'error',
    '@typescript-eslint/no-explicit-any': 'error',
    '@typescript-eslint/no-non-null-assertion': 'error',
    '@typescript-eslint/strict-boolean-expressions': 'off', // Too strict, causes many false positives
    'prefer-const': 'error',
    'no-var': 'error',
    'no-console': ['warn', { allow: ['error'] }], // Allow console.error, warn for others (logger.ts uses console)
    'eqeqeq': ['error', 'always'], // Require === and !==
    'curly': ['error', 'all'], // Require braces for all control statements
  },
  env: {
    node: true,
    jest: true,
  },
};