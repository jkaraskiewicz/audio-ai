# PROGRAMMING_V2.md Guidelines Summary

This project follows strict professional software engineering guidelines from PROGRAMMING_V2.md. Key rules:

## Critical Rules

### File Size Limit
- **Maximum 150 lines per file** (excluding imports/package declarations)
- This is a guideline that can be broken with good reason
- Exceptions: config files, test files, data files, complex algorithms

### Function Size Limit
- **Prefer functions with 50 lines or less**
- Each function should have ONE responsibility
- **Cyclomatic complexity limit**: Max 5 decision points per method

### Class Size
- **Prefer classes with 1-5 methods**
- Large classes indicate poor separation of concerns

### Immutability
- **ALWAYS prefer immutable data structures**
- TypeScript: `const` over `let`
- Kotlin: `val` over `var`
- Do NOT use mutability for performance/simplicity

### Null Safety
- **Eliminate nullable types as quickly as possible**
- **Don't smuggle nullables across codebase**
- TypeScript: Use `T` over `T | null`
- Kotlin: Avoid `?` types when possible

### Avoid Nesting
- **No nested if statements** (max 2 levels in exceptional cases)
- **No nested try/catch blocks**
- **No nested loops**
- **Prefer**: Early returns, guard clauses, functional approaches

### Functional Programming
- **ALWAYS prefer functional approaches**
- Use `map`, `filter`, `reduce`, `flatMap` over loops
- Chain operations into pipelines
- Pure functions when possible
- **Performance is NOT an excuse** - readability wins

### Design Patterns
- **Actively seek opportunities to apply design patterns**
- Prefer patterns over procedural code
- Common patterns: Strategy, Factory, Command, State, Repository, Observer

### Dependencies
- **ALWAYS use NEWEST versions** of packages
- Check online for latest versions
- Auto-upgrade dependencies
- Inform user after upgrade

### Testing
- **Write unit tests for ALL public methods**
- **CRITICAL**: Tests must be GREEN after every change
- Do NOT accept broken tests
- Verify work constantly

### Linting
- **Always set up linters automatically**
- Use **very strict** configurations
- Backend: ESLint + Prettier
- Android: ktlint

### Error Handling
- Balance between Result types and Exceptions
- Use Result/Option when error contains domain information
- Use Exceptions for truly unexpected cases
- Kotlin: Prefer `runCatching` and `Result<T>`
- TypeScript: Use try/catch for async operations

### Abstraction Layers
- **Every file must be part of an abstraction layer**
- **Never mix abstraction layers** in same class
- Example: Don't mix UI and API layers

### Documentation
- Good naming and code structure > Comments
- Only comment when it provides meaningful addition
- Self-documenting code is preferred
- Don't comment obvious code

## Code Smells to Fix

| Code Smell | Solution |
|------------|----------|
| Long if-else chains | Strategy, State, or Command pattern |
| Type checking (instanceof/typeof) | Polymorphism or Visitor pattern |
| Hardcoded dependencies | Dependency Injection |
| Duplicated code | Template Method or Strategy |
| Complex conditionals | Specification pattern |
| Object creation scattered | Factory or Builder |
| Switch/when statements | Strategy or State |
| Files > 150 lines | Split into smaller focused files |
| Functions > 50 lines | Extract methods, apply SRP |
| Nested structures | Early returns, functional approaches |

## Refactoring Philosophy
- **Refactor very aggressively** - aim for high code quality
- When encountering violations: explain issues, propose plan, get approval, refactor comprehensively
- Don't follow existing poor style - propose improvements

## Version Control
- Use **Conventional Commits** format
- Auto-commit after changes (can be disabled)
- Work on current branch (don't create feature branches)