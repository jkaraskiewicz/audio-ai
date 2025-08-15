"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Jest setup file
const dotenv_1 = require("dotenv");
// Load test environment variables
(0, dotenv_1.config)({ path: '.env.test' });
// Set test timeout
jest.setTimeout(10000);
// Mock console.error for cleaner test output
const originalConsoleError = console.error;
beforeEach(() => {
    console.error = jest.fn();
});
afterEach(() => {
    console.error = originalConsoleError;
});
