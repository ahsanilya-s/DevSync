# DevSync - Java Code Analysis Tool

## Overview
DevSync is a comprehensive Java code analysis tool that detects code smells, security issues, and maintainability problems in Java projects.

## Features
- **Missing Default Detection**: Identifies switch statements without default cases
- **Empty Catch Detection**: Finds empty catch blocks with risk assessment
- **Long Method Detection**: Detects methods that are too long or complex
- **Parameter List Analysis**: Identifies methods with too many parameters
- **Magic Number Detection**: Finds hardcoded numeric literals
- **Identifier Analysis**: Detects overly long or unclear identifiers

## Quick Start
1. Upload your Java project (ZIP file)
2. View analysis results with severity levels
3. Get actionable suggestions for improvements

## API Endpoints
- `POST /api/upload` - Upload and analyze project
- `GET /api/history` - View analysis history
- `GET /api/admin/stats` - Admin statistics

## Severity Levels
- 🔴 **Critical**: Immediate attention required
- 🟡 **High**: Should be addressed soon
- 🟠 **Medium**: Consider improving
- ⚪ **Low**: Minor improvements

## Configuration
Customize analysis thresholds in `AnalysisConfig.java`:
- Method length limits
- Parameter count limits
- Severity thresholds
- Excluded patterns

## Technology Stack
- **Backend**: Spring Boot 3.5.6, Java 21
- **Frontend**: React, Vite, TailwindCSS
- **Parser**: JavaParser 3.25.9
- **Database**: MySQL 8.0