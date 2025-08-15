"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const fs_1 = __importDefault(require("fs"));
const FileService_1 = require("../../src/services/FileService");
// Mock fs module
jest.mock('fs');
const mockFs = fs_1.default;
describe('FileService', () => {
    let fileService;
    const testConfig = {
        baseDirectory: 'test_ideas',
        specialCategories: { daily: 'daily/tasks' },
    };
    beforeEach(() => {
        fileService = new FileService_1.FileService(testConfig);
        jest.clearAllMocks();
    });
    describe('parseFrontMatter', () => {
        it('should parse valid frontmatter correctly', () => {
            const content = `---
category: projects
filename: test-project
---

# Test Project

## Summary
A test project.`;
            const result = fileService.parseFrontMatter(content);
            expect(result.frontMatter.category).toBe('projects');
            expect(result.frontMatter.filename).toBe('test-project');
            expect(result.cleanContent).toBe('# Test Project\n\n## Summary\nA test project.');
        });
        it('should handle missing frontmatter', () => {
            const content = '# Test Project\n\nA project without frontmatter.';
            const result = fileService.parseFrontMatter(content);
            expect(result.frontMatter.category).toBe('notes');
            expect(result.frontMatter.filename).toBe('untitled-note');
            expect(result.cleanContent).toBe(content);
        });
        it('should handle malformed frontmatter', () => {
            const content = `---
invalid: frontmatter
---

# Test Project`;
            const result = fileService.parseFrontMatter(content);
            expect(result.frontMatter.category).toBe('notes');
            expect(result.frontMatter.filename).toBe('untitled-note');
        });
    });
    describe('saveMarkdownFile', () => {
        beforeEach(() => {
            mockFs.existsSync.mockReturnValue(true);
            mockFs.mkdirSync.mockImplementation();
            mockFs.writeFileSync.mockImplementation();
        });
        it('should save file successfully with valid content', () => {
            const content = `---
category: projects
filename: test-app
---

# Test App

Content here.`;
            const result = fileService.saveMarkdownFile(content);
            expect(mockFs.writeFileSync).toHaveBeenCalledWith(expect.stringContaining('test-app.md'), '# Test App\n\nContent here.', 'utf8');
            expect(result).toMatch(/projects.*test-app\.md$/);
        });
        it('should create directory if it does not exist', () => {
            mockFs.existsSync.mockReturnValue(false);
            const content = `---
category: travel
filename: japan-trip
---

# Japan Trip`;
            fileService.saveMarkdownFile(content);
            expect(mockFs.mkdirSync).toHaveBeenCalledWith(expect.stringContaining('travel'), { recursive: true });
        });
        it('should handle special categories', () => {
            const content = `---
category: daily
filename: grocery-list
---

# Grocery List`;
            const result = fileService.saveMarkdownFile(content);
            expect(result).toMatch(/daily\/tasks.*grocery-list\.md$/);
        });
    });
});
