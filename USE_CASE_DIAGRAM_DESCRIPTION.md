# DevSync - Use Case Diagram Description

## System Overview
DevSync is an intelligent Java code analysis platform that provides comprehensive static code analysis, AI-powered insights, and detailed reporting capabilities. The system combines advanced detection algorithms with machine learning to identify code smells, security vulnerabilities, and maintainability issues in Java projects.

## Primary Actors

### 1. Developer (Primary Actor)
- **Description**: Software developers who upload Java projects for analysis
- **Goals**: Improve code quality, identify issues, get AI-powered recommendations
- **Characteristics**: Technical users familiar with Java development

### 2. System Administrator (Secondary Actor)
- **Description**: Technical personnel managing the DevSync platform
- **Goals**: Monitor system performance, manage user accounts, maintain system health
- **Characteristics**: IT professionals with system administration expertise

### 3. AI Analysis Engine (External System Actor)
- **Description**: Ollama AI service providing intelligent code analysis
- **Goals**: Generate contextual recommendations and insights
- **Characteristics**: External AI service integrated via REST API

## Use Cases

### Authentication & User Management

#### UC-001: User Registration
- **Actor**: Developer
- **Description**: New users create accounts to access DevSync services
- **Preconditions**: User has valid email address
- **Main Flow**:
  1. User navigates to signup page
  2. User enters username, email, and password
  3. System validates input data
  4. System checks for existing email/username
  5. System creates new user account
  6. System sends confirmation message
- **Postconditions**: User account created and stored in database
- **Alternative Flows**:
  - A1: Email already exists - System displays error message
  - A2: Invalid input format - System shows validation errors
- **Business Rules**: 
  - Email must be unique
  - Password must meet security requirements
  - Username must be unique

#### UC-002: User Login
- **Actor**: Developer
- **Description**: Registered users authenticate to access the platform
- **Preconditions**: User has valid account credentials
- **Main Flow**:
  1. User enters email and password
  2. System validates credentials against database
  3. System generates session token
  4. System redirects to dashboard
  5. User gains access to platform features
- **Postconditions**: User authenticated and session established
- **Alternative Flows**:
  - A1: Invalid credentials - System displays error message
  - A2: Account locked - System shows account status message
- **Business Rules**:
  - Session expires after inactivity
  - Failed login attempts are tracked

#### UC-003: User Logout
- **Actor**: Developer
- **Description**: Users securely terminate their session
- **Preconditions**: User is logged in
- **Main Flow**:
  1. User clicks logout button
  2. System invalidates session token
  3. System clears user data from client
  4. System redirects to login page
- **Postconditions**: User session terminated

### Project Analysis

#### UC-004: Upload Java Project
- **Actor**: Developer
- **Description**: Users upload ZIP files containing Java projects for analysis
- **Preconditions**: User is authenticated
- **Main Flow**:
  1. User selects ZIP file containing Java project
  2. System validates file format and size
  3. System extracts ZIP to unique directory
  4. System scans for Java files (.java extension)
  5. System confirms successful upload
  6. System prepares project for analysis
- **Postconditions**: Project files extracted and ready for analysis
- **Alternative Flows**:
  - A1: Invalid file format - System rejects upload
  - A2: File too large - System displays size limit error
  - A3: No Java files found - System warns user
- **Business Rules**:
  - Only ZIP files accepted
  - Maximum file size limit enforced
  - Unique folder naming prevents conflicts

#### UC-005: Perform Code Analysis
- **Actor**: Developer
- **Description**: System analyzes uploaded Java code using multiple detection algorithms
- **Preconditions**: Java project successfully uploaded
- **Main Flow**:
  1. System initiates analysis process
  2. System parses Java files using JavaParser
  3. System runs multiple detectors in parallel:
     - Long Method Detector (cyclomatic complexity analysis)
     - Long Parameter List Detector (parameter count analysis)
     - Magic Number Detector (literal value analysis)
     - Empty Catch Block Detector (exception handling analysis)
     - Long Identifier Detector (naming convention analysis)
     - Code Duplication Detector (similarity analysis)
     - God Class Detector (class complexity analysis)
  4. System aggregates detection results
  5. System categorizes issues by severity (Critical, Warning, Suggestion)
  6. System generates comprehensive analysis report
- **Postconditions**: Analysis complete with categorized issues identified
- **Alternative Flows**:
  - A1: Parse error in Java file - System logs error and continues
  - A2: Analysis timeout - System returns partial results
- **Business Rules**:
  - Multiple detection algorithms run simultaneously
  - Issues categorized by severity levels
  - Analysis continues despite individual file failures

#### UC-006: Generate AI Insights
- **Actor**: AI Analysis Engine
- **Description**: External AI service provides intelligent recommendations based on analysis results
- **Preconditions**: Code analysis completed successfully
- **Main Flow**:
  1. System sends analysis report to Ollama AI service
  2. AI service processes code issues and patterns
  3. AI service generates contextual recommendations
  4. AI service returns structured insights
  5. System appends AI analysis to report
- **Postconditions**: Report enhanced with AI-powered recommendations
- **Alternative Flows**:
  - A1: AI service unavailable - System continues without AI insights
  - A2: AI request timeout - System logs failure and proceeds
  - A3: AI service error - System handles gracefully
- **Business Rules**:
  - AI analysis is optional enhancement
  - System functions without AI service
  - AI failures don't block core functionality

### Report Management

#### UC-007: View Analysis Results
- **Actor**: Developer
- **Description**: Users review detailed analysis results and metrics
- **Preconditions**: Analysis completed successfully
- **Main Flow**:
  1. System displays analysis summary dashboard
  2. User views issue count by severity:
     - Critical Issues (🔴)
     - Warnings (🟡)
     - Suggestions (🟠)
  3. User can drill down into specific issue categories
  4. System shows file-level issue distribution
  5. User reviews AI-generated recommendations
- **Postconditions**: User informed of code quality status
- **Alternative Flows**:
  - A1: No issues found - System displays clean code message
  - A2: Analysis incomplete - System shows partial results
- **Business Rules**:
  - Results categorized by severity
  - Visual indicators for issue types
  - Summary metrics prominently displayed

#### UC-008: Download Detailed Report
- **Actor**: Developer
- **Description**: Users access comprehensive text-based analysis reports
- **Preconditions**: Analysis report generated
- **Main Flow**:
  1. User clicks "Show Report" button
  2. System retrieves report from file system
  3. System validates user access permissions
  4. System displays formatted report in modal
  5. User reviews detailed findings and recommendations
- **Postconditions**: User has access to complete analysis details
- **Alternative Flows**:
  - A1: Report file missing - System displays error message
  - A2: Access denied - System shows permission error
- **Business Rules**:
  - Users can only access their own reports
  - Reports include both automated and AI analysis
  - Reports formatted for readability

### History & Tracking

#### UC-009: View Analysis History
- **Actor**: Developer
- **Description**: Users review their previous analysis sessions
- **Preconditions**: User has performed previous analyses
- **Main Flow**:
  1. User opens history panel
  2. System retrieves user's analysis history from database
  3. System displays chronological list of analyses:
     - Project name
     - Analysis date
     - Issue counts by severity
     - Report access links
  4. User can select specific historical analysis
  5. System provides access to historical reports
- **Postconditions**: User can track analysis trends over time
- **Alternative Flows**:
  - A1: No history available - System shows empty state
  - A2: Database error - System displays error message
- **Business Rules**:
  - History sorted by most recent first
  - Each entry includes summary metrics
  - Historical data persisted indefinitely

#### UC-010: Access Historical Reports
- **Actor**: Developer
- **Description**: Users retrieve and view reports from previous analyses
- **Preconditions**: Historical analysis exists
- **Main Flow**:
  1. User selects historical analysis from history list
  2. System validates user ownership of report
  3. System retrieves historical report file
  4. System displays report content
  5. User reviews historical analysis results
- **Postconditions**: User can compare current and historical analysis
- **Alternative Flows**:
  - A1: Report file deleted - System shows unavailable message
  - A2: Access denied - System enforces ownership rules
- **Business Rules**:
  - Users can only access their own historical reports
  - Report files preserved for historical access
  - Access control enforced at file level

### System Administration

#### UC-011: Monitor System Health
- **Actor**: System Administrator
- **Description**: Administrators monitor platform performance and usage
- **Preconditions**: Administrator has system access
- **Main Flow**:
  1. Administrator accesses system monitoring dashboard
  2. System displays performance metrics
  3. Administrator reviews user activity logs
  4. System shows resource utilization statistics
  5. Administrator identifies potential issues
- **Postconditions**: System health status assessed
- **Alternative Flows**:
  - A1: Performance issues detected - Administrator takes corrective action
- **Business Rules**:
  - Only administrators can access system metrics
  - Monitoring data updated in real-time

#### UC-012: Manage User Accounts
- **Actor**: System Administrator
- **Description**: Administrators manage user accounts and permissions
- **Preconditions**: Administrator has user management privileges
- **Main Flow**:
  1. Administrator accesses user management interface
  2. System displays user account list
  3. Administrator can view user details
  4. Administrator can modify account status
  5. System updates user permissions
- **Postconditions**: User accounts managed according to policies
- **Alternative Flows**:
  - A1: Account suspension - Administrator disables user access
- **Business Rules**:
  - User data privacy maintained
  - Account changes logged for audit

## System Boundaries

### Included in System:
- User authentication and session management
- File upload and extraction capabilities
- Multi-algorithm code analysis engine
- AI integration for enhanced insights
- Report generation and storage
- Analysis history tracking
- Web-based user interface

### External Dependencies:
- Ollama AI service for intelligent analysis
- File system for report storage
- Database for user and history data
- JavaParser library for code parsing

## Non-Functional Requirements

### Performance:
- Analysis completion within 2 minutes for typical projects
- Support for concurrent user sessions
- Efficient memory usage during large project analysis

### Security:
- Secure user authentication
- File access control and isolation
- Data privacy protection
- Session management security

### Reliability:
- Graceful handling of analysis failures
- System continues operation if AI service unavailable
- Data persistence and backup capabilities

### Usability:
- Intuitive web interface
- Real-time analysis progress feedback
- Clear visualization of analysis results
- Responsive design for various devices

## Integration Points

### Frontend-Backend Integration:
- RESTful API communication
- File upload via multipart/form-data
- Real-time status updates
- Session-based authentication

### AI Service Integration:
- HTTP-based communication with Ollama
- Timeout handling and error recovery
- Optional enhancement (system works without AI)

### Database Integration:
- User account management
- Analysis history persistence
- Report metadata storage

This comprehensive use case description provides a detailed view of the DevSync system's functionality, user interactions, and system boundaries, serving as a foundation for system design and development planning.