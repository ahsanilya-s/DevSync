# DevSync System - Class Diagram Documentation

## Overview
This document provides a detailed description of all classes in the DevSync code analysis system, including their attributes, operations, and relationships. This serves as a textual representation of the complete class diagram for the system.

---

## 1. MODEL LAYER (Entity Classes)

### 1.1 User
**Package:** com.devsync.model  
**Type:** Entity Class  
**Purpose:** Represents a user account in the system

**Attributes:**
- id: Long (Primary Key, Auto-generated)
- username: String
- email: String
- password: String (encrypted)
- createdAt: LocalDateTime

**Operations:**
- User()
- User(username: String, email: String, password: String)
- getId(): Long
- setId(id: Long): void
- getUsername(): String
- setUsername(username: String): void
- getEmail(): String
- setEmail(email: String): void
- getPassword(): String
- setPassword(password: String): void
- getCreatedAt(): LocalDateTime
- setCreatedAt(createdAt: LocalDateTime): void
- onCreate(): void (lifecycle callback)

**Relationships:**
- One-to-One with UserSettings
- One-to-Many with AnalysisHistory

---

### 1.2 UserSettings
**Package:** com.devsync.model  
**Type:** Entity Class  
**Purpose:** Stores user-specific configuration for code analysis detectors

**Attributes:**
- id: Long (Primary Key)
- userId: String (Foreign Key)
- longMethodEnabled: Boolean = true
- maxMethodLength: Integer = 50
- maxMethodComplexity: Integer = 10
- longParameterEnabled: Boolean = true
- maxParameterCount: Integer = 5
- longIdentifierEnabled: Boolean = true
- maxIdentifierLength: Integer = 30
- minIdentifierLength: Integer = 3
- magicNumberEnabled: Boolean = true
- magicNumberThreshold: Integer = 3
- missingDefaultEnabled: Boolean = true
- emptyCatchEnabled: Boolean = true
- complexConditionalEnabled: Boolean = true
- maxConditionalOperators: Integer = 4
- maxNestingDepth: Integer = 3
- longStatementEnabled: Boolean = true
- maxStatementTokens: Integer = 40
- maxStatementChars: Integer = 250
- maxMethodChainLength: Integer = 5
- brokenModularizationEnabled: Boolean = true
- maxResponsibilities: Integer = 3
- minCohesionIndex: Double = 0.4
- maxCouplingCount: Integer = 6
- deficientEncapsulationEnabled: Boolean = true
- unnecessaryAbstractionEnabled: Boolean = true
- maxAbstractionUsage: Integer = 1
- memoryLeakEnabled: Boolean = true
- aiProvider: String = "ollama"
- aiApiKey: String
- aiModel: String = "deepseek-coder:latest"
- aiEnabled: Boolean = true

**Operations:**
- UserSettings()
- UserSettings(userId: String)
- Getters and setters for all attributes (50+ methods)

**Relationships:**
- Many-to-One with User

---

### 1.3 AnalysisHistory
**Package:** com.devsync.model  
**Type:** Entity Class  
**Purpose:** Records historical analysis results for tracking

**Attributes:**
- id: Long (Primary Key)
- userId: String (Foreign Key)
- projectName: String
- reportPath: String
- analysisDate: LocalDateTime
- totalIssues: Integer
- criticalIssues: Integer
- warnings: Integer
- suggestions: Integer
- projectPath: String
- totalLOC: Integer
- grade: String
- issueDensity: Double

**Operations:**
- AnalysisHistory()
- AnalysisHistory(userId: String, projectName: String, reportPath: String, totalIssues: Integer, criticalIssues: Integer, warnings: Integer, suggestions: Integer)
- AnalysisHistory(userId: String, projectName: String, reportPath: String, totalIssues: Integer, criticalIssues: Integer, warnings: Integer, suggestions: Integer, totalLOC: Integer, grade: String, issueDensity: Double)
- Getters and setters for all attributes

**Relationships:**
- Many-to-One with User

---

### 1.4 CommitAnalysis
**Package:** com.devsync.model  
**Type:** Entity Class  
**Purpose:** Stores analysis results for specific Git commits

**Attributes:**
- id: Long (Primary Key)
- userId: String
- repoOwner: String
- repoName: String
- commitSha: String
- commitMessage: String
- commitDate: LocalDateTime
- analysisDate: LocalDateTime
- totalIssues: Integer
- criticalIssues: Integer
- warnings: Integer
- suggestions: Integer
- reportPath: String

**Operations:**
- CommitAnalysis()
- Getters and setters for all attributes

**Relationships:**
- Many-to-One with User

---

### 1.5 AdminSettings
**Package:** com.devsync.model  
**Type:** Entity Class  
**Purpose:** System-wide administrative configuration

**Attributes:**
- id: Long (Primary Key)
- maintenanceMode: Boolean
- maxFileSize: Integer
- allowedFileTypes: String
- aiAnalysisEnabled: Boolean

**Operations:**
- AdminSettings()
- Getters and setters for all attributes

---

### 1.6 CodeIssue
**Package:** com.devsync.model  
**Type:** Data Transfer Object  
**Purpose:** Represents a single code quality issue

**Attributes:**
- type: String
- file: String
- line: int
- severity: String
- message: String
- suggestion: String
- detailedReason: String
- thresholdDetails: LongMethodThresholdDetails
- thresholdDetailsJson: String

**Operations:**
- CodeIssue()
- CodeIssue(type: String, file: String, line: int, severity: String, message: String, suggestion: String)
- Getters and setters for all attributes

---

## 2. REPOSITORY LAYER (Data Access)

### 2.1 UserRepository
**Package:** com.devsync.repository  
**Type:** Interface (extends JpaRepository)  
**Purpose:** Database operations for User entity

**Operations:**
- findByEmail(email: String): User
- All inherited JpaRepository methods (save, findById, findAll, delete, etc.)

---

### 2.2 UserSettingsRepository
**Package:** com.devsync.repository  
**Type:** Interface (extends JpaRepository)  
**Purpose:** Database operations for UserSettings entity

**Operations:**
- findByUserId(userId: String): Optional<UserSettings>
- All inherited JpaRepository methods

---

### 2.3 AnalysisHistoryRepository
**Package:** com.devsync.repository  
**Type:** Interface (extends JpaRepository)  
**Purpose:** Database operations for AnalysisHistory entity

**Operations:**
- findByUserIdOrderByAnalysisDateDesc(userId: String): List<AnalysisHistory>
- All inherited JpaRepository methods

---

### 2.4 CommitAnalysisRepository
**Package:** com.devsync.repository  
**Type:** Interface (extends JpaRepository)  
**Purpose:** Database operations for CommitAnalysis entity

**Operations:**
- All inherited JpaRepository methods

---

### 2.5 AdminSettingsRepository
**Package:** com.devsync.repository  
**Type:** Interface (extends JpaRepository)  
**Purpose:** Database operations for AdminSettings entity

**Operations:**
- All inherited JpaRepository methods

---

## 3. ANALYZER LAYER (Core Analysis Engine)

### 3.1 CodeAnalysisEngine
**Package:** com.devsync.analyzer  
**Type:** Component Class  
**Purpose:** Central orchestrator for code analysis process

**Attributes:**
- logger: Logger (static)
- detectors: Map<String, Object>
- enabledDetectors: Map<String, Boolean>
- maxMethodLength: Integer
- maxParameterCount: Integer
- maxIdentifierLength: Integer

**Operations:**
- CodeAnalysisEngine()
- configureFromSettings(settings: UserSettings): void
- initializeDetectors(): void
- analyzeProject(projectPath: String): Map<String, Object>
- analyzeFile(cu: CompilationUnit, fileName: String, detectorCounts: Map<String, Integer>): List<String>
- updateSeverityCounts(issues: List<String>, counts: Map<String, Integer>): void
- generateSummary(counts: Map<String, Integer>, fileCount: int): String

**Relationships:**
- Uses all 12 Detector classes
- Uses JavaFileCollector
- Uses LOCCounter
- Configured by UserSettings

---

### 3.2 JavaFileCollector
**Package:** com.devsync.analyzer  
**Type:** Utility Class  
**Purpose:** Recursively collects Java source files from project directory

**Attributes:**
- None

**Operations:**
- collectJavaFiles(directoryPath: String): List<File>
- collectRecursively(folder: File, javaFiles: List<File>): void

**Relationships:**
- Used by CodeAnalysisEngine

---

### 3.3 LOCCounter
**Package:** com.devsync.analyzer  
**Type:** Utility Class  
**Purpose:** Counts lines of code excluding comments and blank lines

**Attributes:**
- None (all methods are static)

**Operations:**
- countLinesOfCode(cu: CompilationUnit): int (static)
- countPhysicalLines(file: File): int (static)

**Relationships:**
- Used by CodeAnalysisEngine

---

## 4. DETECTOR LAYER (Code Smell Detection)

### 4.1 LongMethodDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects methods that are too long or complex

**Attributes:**
- baseLineThreshold: int = 35
- criticalLineThreshold: int = 50
- MAX_CYCLOMATIC_COMPLEXITY: int = 10 (static final)
- MAX_COGNITIVE_COMPLEXITY: int = 15 (static final)
- MAX_NESTING_DEPTH: int = 4 (static final)
- METHOD_TYPE_WEIGHTS: Map<String, Double> (static final)

**Operations:**
- setMaxLength(maxLength: int): void
- setMaxComplexity(maxComplexity: int): void
- detect(cu: CompilationUnit): List<String>
- calculateScore(m: MethodInfo): double
- determineMethodType(m: MethodInfo): String
- getSeverity(s: double): String
- generateAnalysis(m: MethodInfo): String
- generateSuggestions(m: MethodInfo): String
- generateDetailedReason(m: MethodInfo): String

**Inner Classes:**
- MethodInfo: Stores method metrics
- MethodAnalyzer: AST visitor for method analysis
- ComplexityCalculator: Calculates cyclomatic complexity
- CognitiveComplexityCalculator: Calculates cognitive complexity
- NestingCalculator: Calculates nesting depth

---

### 4.2 LongParameterListDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects methods with too many parameters

**Attributes:**
- maxParameters: int

**Operations:**
- setMaxParameters(maxParameters: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.3 LongIdentifierDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects identifiers that are too long or too short

**Attributes:**
- maxLength: int
- minLength: int

**Operations:**
- setMaxLength(maxLength: int): void
- setMinLength(minLength: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.4 MagicNumberDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects hardcoded numeric literals

**Attributes:**
- threshold: int

**Operations:**
- setThreshold(threshold: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.5 MissingDefaultDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects switch statements without default case

**Attributes:**
- None

**Operations:**
- detect(cu: CompilationUnit): List<String>

---

### 4.6 EmptyCatchDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects empty catch blocks

**Attributes:**
- None

**Operations:**
- detect(cu: CompilationUnit): List<String>

---

### 4.7 ComplexConditionalDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects overly complex conditional expressions

**Attributes:**
- maxOperators: int
- maxNestingDepth: int

**Operations:**
- setMaxOperators(maxOperators: int): void
- setMaxNestingDepth(maxNestingDepth: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.8 LongStatementDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects statements that are too long

**Attributes:**
- maxTokens: int
- maxChars: int
- maxChainLength: int

**Operations:**
- setMaxTokens(maxTokens: int): void
- setMaxChars(maxChars: int): void
- setMaxChainLength(maxChainLength: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.9 BrokenModularizationDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects classes with poor cohesion and high coupling

**Attributes:**
- maxResponsibilities: int
- minCohesion: double
- maxCoupling: int

**Operations:**
- setMaxResponsibilities(maxResponsibilities: int): void
- setMinCohesion(minCohesion: double): void
- setMaxCoupling(maxCoupling: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.10 DeficientEncapsulationDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects public fields that should be private

**Attributes:**
- None

**Operations:**
- detect(cu: CompilationUnit): List<String>

---

### 4.11 UnnecessaryAbstractionDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects interfaces/abstract classes with minimal usage

**Attributes:**
- maxUsage: int

**Operations:**
- setMaxUsage(maxUsage: int): void
- detect(cu: CompilationUnit): List<String>

---

### 4.12 MemoryLeakDetector
**Package:** com.devsync.detectors  
**Type:** Detector Class  
**Purpose:** Detects potential memory leak patterns

**Attributes:**
- None

**Operations:**
- detect(cu: CompilationUnit): List<String>

---

## 5. CONTROLLER LAYER (REST API Endpoints)

### 5.1 CodeAnalysisController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Handles file upload and code analysis requests

**Attributes:**
- aiAssistantService: AIAssistantService (autowired)
- analysisHistoryRepository: AnalysisHistoryRepository (autowired)
- userSettingsRepository: UserSettingsRepository (autowired)
- adminSettingsService: AdminSettingsService (autowired)

**Operations:**
- getUploadInfo(): ResponseEntity<String> (GET /api/upload)
- getReport(reportPath: String, userId: String): ResponseEntity<String> (GET /api/upload/report)
- getUserHistory(userId: String): ResponseEntity<List<AnalysisHistory>> (GET /api/upload/history)
- fixExistingCounts(): ResponseEntity<String> (POST /api/upload/fix-counts)
- handleFileUpload(file: MultipartFile, userId: String): ResponseEntity<String> (POST /api/upload)
- testVisualEndpoint(): ResponseEntity<String> (GET /api/upload/visual)
- generateVisualReport(file: MultipartFile): ResponseEntity<Map<String, Object>> (POST /api/upload/visual)
- countIssuesInReport(content: String, emoji: String): int

**Relationships:**
- Uses CodeAnalysisEngine
- Uses ReportGenerator
- Uses ZipExtractor
- Uses FolderNamingUtil
- Uses AIAssistantService
- Uses AdminSettingsService

---

### 5.2 AuthController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Handles user authentication and registration

**Attributes:**
- userService: UserService (autowired)

**Operations:**
- signup(user: User): ResponseEntity<?> (POST /api/auth/signup)
- login(user: User): ResponseEntity<?> (POST /api/auth/login)
- getAllUsers(): ResponseEntity<?> (GET /api/auth/users)
- getUserProfile(userId: String): ResponseEntity<?> (GET /api/auth/profile/{userId})

**Inner Classes:**
- LoginResponse: DTO for login response

**Relationships:**
- Uses UserService

---

### 5.3 SettingsController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Manages user settings CRUD operations

**Attributes:**
- userSettingsRepository: UserSettingsRepository (autowired)

**Operations:**
- getSettings(userId: String): ResponseEntity<UserSettings> (GET /api/settings)
- saveSettings(settings: UserSettings): ResponseEntity<UserSettings> (POST /api/settings)
- updateSettings(userId: String, settings: UserSettings): ResponseEntity<UserSettings> (PUT /api/settings/{userId})

---

### 5.4 HistoryController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Retrieves analysis history for users

**Attributes:**
- analysisHistoryRepository: AnalysisHistoryRepository (autowired)

**Operations:**
- getHistory(userId: String): ResponseEntity<List<AnalysisHistory>> (GET /api/history)
- deleteHistory(id: Long): ResponseEntity<Void> (DELETE /api/history/{id})

---

### 5.5 AdminController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Administrative functions and system management

**Attributes:**
- adminSettingsService: AdminSettingsService (autowired)

**Operations:**
- getSettings(): ResponseEntity<AdminSettings> (GET /api/admin/settings)
- updateSettings(settings: AdminSettings): ResponseEntity<AdminSettings> (PUT /api/admin/settings)
- getSystemStats(): ResponseEntity<Map<String, Object>> (GET /api/admin/stats)

---

### 5.6 AdminAuthController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Admin authentication

**Attributes:**
- None

**Operations:**
- adminLogin(credentials: Map<String, String>): ResponseEntity<?> (POST /api/admin/login)

---

### 5.7 GitHubController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** GitHub repository integration

**Attributes:**
- commitAnalysisRepository: CommitAnalysisRepository (autowired)

**Operations:**
- analyzeRepository(repoUrl: String, userId: String): ResponseEntity<?> (POST /api/github/analyze)
- getCommitHistory(userId: String): ResponseEntity<List<CommitAnalysis>> (GET /api/github/commits)

---

### 5.8 DetailedReportController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Provides detailed analysis reports

**Attributes:**
- detailedReportService: DetailedReportService (autowired)

**Operations:**
- getDetailedReport(reportPath: String): ResponseEntity<Map<String, Object>> (GET /api/report/detailed)

---

### 5.9 FileViewController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** Serves source code files for viewing

**Attributes:**
- None

**Operations:**
- getFileContent(filePath: String): ResponseEntity<String> (GET /api/files/view)

---

### 5.10 AIRefactorController
**Package:** com.devsync.controller  
**Type:** REST Controller  
**Purpose:** AI-powered code refactoring suggestions

**Attributes:**
- aiAssistantService: AIAssistantService (autowired)

**Operations:**
- getRefactoringSuggestions(code: String, issueType: String): ResponseEntity<String> (POST /api/ai/refactor)

---

## 6. SERVICE LAYER (Business Logic)

### 6.1 UserService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** User management business logic

**Attributes:**
- userRepository: UserRepository (autowired)
- passwordEncoder: BCryptPasswordEncoder

**Operations:**
- registerUser(user: User): User
- login(email: String, password: String): User
- getAllUsers(): List<User>
- getUserById(userId: Long): User

**Relationships:**
- Uses UserRepository
- Uses BCryptPasswordEncoder

---

### 6.2 AIAssistantService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** AI-powered code analysis and suggestions

**Attributes:**
- httpClient: HttpClient
- objectMapper: ObjectMapper

**Operations:**
- AIAssistantService()
- analyzeWithAI(reportContent: String, settings: UserSettings): String
- analyzeWithOllama(reportContent: String, settings: UserSettings): String
- analyzeWithOpenAI(reportContent: String, settings: UserSettings): String
- analyzeWithAnthropic(reportContent: String, settings: UserSettings): String
- createPrompt(reportContent: String): String
- isOllamaAvailable(): boolean
- generateFallbackAnalysis(reportContent: String): String

**Relationships:**
- Uses UserSettings
- Integrates with external AI APIs (Ollama, OpenAI, Anthropic)

---

### 6.3 AdminSettingsService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** Administrative settings management

**Attributes:**
- adminSettingsRepository: AdminSettingsRepository (autowired)

**Operations:**
- getSettings(): AdminSettings
- updateSettings(settings: AdminSettings): AdminSettings
- isMaintenanceMode(): boolean
- getMaxFileSize(): int
- getAllowedFileTypes(): String[]
- isAiAnalysisEnabled(): boolean

**Relationships:**
- Uses AdminSettingsRepository

---

### 6.4 DetailedReportService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** Generates detailed analysis reports with metrics

**Attributes:**
- None

**Operations:**
- generateDetailedReport(reportPath: String): Map<String, Object>
- parseReportContent(content: String): Map<String, Object>
- extractMetrics(content: String): Map<String, Integer>

---

### 6.5 HighlightMapperService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** Maps code issues to source code highlighting

**Attributes:**
- None

**Operations:**
- mapIssuesToHighlights(issues: List<CodeIssue>): Map<String, List<Highlight>>
- createHighlight(issue: CodeIssue): Highlight

---

### 6.6 OllamaService
**Package:** com.devsync.services  
**Type:** Service Class  
**Purpose:** Direct integration with Ollama AI service

**Attributes:**
- httpClient: HttpClient
- baseUrl: String = "http://localhost:11434"

**Operations:**
- isAvailable(): boolean
- chat(prompt: String, model: String): String
- listModels(): List<String>

---

## 7. REPORT GENERATION LAYER

### 7.1 ReportGenerator
**Package:** com.devsync.reports  
**Type:** Utility Class  
**Purpose:** Generates comprehensive analysis reports

**Attributes:**
- None

**Operations:**
- generateTextReport(issues: List<String>, outputDir: String): String (static)
- generateComprehensiveReport(analysisResults: Map<String, Object>): String
- deduplicateIssues(issues: List<String>): List<String>
- extractDeduplicationKey(issue: String): String
- calculateSeverityCounts(issues: List<String>): Map<String, Integer>
- extractSeverity(issue: String): String
- compareIssuesBySeverity(issue1: String, issue2: String): int
- getSeverityPriority(issue: String): int
- generateTypeBreakdown(issues: List<String>): Map<String, Integer>
- generateFileBreakdown(issues: List<String>): Map<String, Map<String, Integer>>
- appendAIAnalysis(reportPath: String, aiAnalysis: String): void (static)
- readReportContent(reportPath: String): String (static)

**Relationships:**
- Uses GradingSystem

---

### 7.2 GradingSystem
**Package:** com.devsync.grading  
**Type:** Utility Class  
**Purpose:** Calculates code quality grades based on issue density

**Attributes:**
- EXCELLENT_THRESHOLD: double = 0.5 (static final)
- GOOD_THRESHOLD: double = 2.0 (static final)
- ACCEPTABLE_THRESHOLD: double = 5.0 (static final)
- POOR_THRESHOLD: double = 10.0 (static final)
- CRITICAL_WEIGHT: double = 10.0 (static final)
- HIGH_WEIGHT: double = 5.0 (static final)
- MEDIUM_WEIGHT: double = 2.0 (static final)
- LOW_WEIGHT: double = 0.5 (static final)

**Operations:**
- calculateGrade(severityCounts: Map<String, Integer>, totalLOC: int): GradeResult (static)
- calculateBaseScore(issueDensity: double): double (static)
- applyPenalties(baseScore: double, critical: int, high: int, totalLOC: int, density: double): double (static)
- mapScoreToGrade(score: double): String (static)
- getQualityLevel(grade: String): String (static)
- getRecommendation(grade: String, critical: int, high: int, density: double): String (static)
- generateGradingReport(result: GradeResult): String (static)

**Inner Classes:**
- GradeResult: Contains grading results and metrics

---

## 8. VISUAL DIAGRAM GENERATION LAYER

### 8.1 PlantUMLGenerator
**Package:** com.devsync.visual  
**Type:** Utility Class  
**Purpose:** Generates PlantUML diagrams from code structure

**Attributes:**
- None

**Operations:**
- generatePlantUMLText(analysisResults: Map<String, Object>): String
- generateDiagramPNG(plantUMLText: String): byte[]
- saveDiagramToFile(plantUMLText: String, outputPath: String): void
- groupClassesByPackage(classes: Map<String, ClassInfo>): Map<String, List<ClassInfo>>
- generateClassDefinition(uml: StringBuilder, classInfo: ClassInfo): void
- generateRelationship(uml: StringBuilder, dep: DependencyInfo, classes: Map<String, ClassInfo>): void
- getPlantUMLArrow(type: DependencyType): String
- isProjectPackage(packageName: String, projectPackages: Set<String>): boolean
- getExternalClasses(dependencies: List<DependencyInfo>, classes: Map<String, ClassInfo>): Set<String>
- isImportantExternalClass(className: String): boolean
- shouldIncludeDependency(dep: DependencyInfo, classes: Map<String, ClassInfo>): boolean
- getSimpleClassName(fullClassName: String): String

**Relationships:**
- Uses ClassInfo
- Uses DependencyInfo

---

### 8.2 VisualDependencyAnalyzer
**Package:** com.devsync.visual  
**Type:** Analyzer Class  
**Purpose:** Analyzes project structure for diagram generation

**Attributes:**
- None

**Operations:**
- analyzeProject(projectPath: String): Map<String, Object>
- analyzeClass(cu: CompilationUnit): ClassInfo
- extractDependencies(cu: CompilationUnit): List<DependencyInfo>

**Relationships:**
- Produces ClassInfo and DependencyInfo objects

---

### 8.3 VisualReportGenerator
**Package:** com.devsync.visual  
**Type:** Generator Class  
**Purpose:** Orchestrates visual report generation

**Attributes:**
- None

**Operations:**
- generateVisualReport(projectPath: String): Map<String, Object>

**Relationships:**
- Uses VisualDependencyAnalyzer
- Uses PlantUMLGenerator

---

### 8.4 ClassInfo
**Package:** com.devsync.visual  
**Type:** Data Class  
**Purpose:** Stores class metadata for diagram generation

**Attributes:**
- className: String
- packageName: String
- isInterface: boolean
- isAbstract: boolean
- linesOfCode: int
- complexity: int
- methods: List<String>
- fields: List<String>

**Operations:**
- ClassInfo()
- Getters and setters for all attributes

---

### 8.5 DependencyInfo
**Package:** com.devsync.visual  
**Type:** Data Class  
**Purpose:** Represents relationships between classes

**Attributes:**
- fromClass: String
- toClass: String
- type: DependencyType (enum)
- description: String

**Operations:**
- DependencyInfo()
- Getters and setters for all attributes

**Enums:**
- DependencyType: EXTENDS, IMPLEMENTS, USES, COMPOSITION, AGGREGATION

---

## 9. CONFIGURATION LAYER

### 9.1 AnalysisConfig
**Package:** com.devsync.config  
**Type:** Configuration Class  
**Purpose:** Centralized configuration for analysis parameters

**Attributes:**
- SEVERITY_THRESHOLDS: Map<String, Double> (static final)
- EXCLUDED_PATTERNS: Set<String> (static final)
- DEFAULT_MAX_METHOD_LENGTH: int = 50 (static final)
- DEFAULT_MAX_METHOD_COMPLEXITY: int = 10 (static final)
- DEFAULT_MAX_PARAMETER_COUNT: int = 5 (static final)
- DEFAULT_MAX_IDENTIFIER_LENGTH: int = 30 (static final)
- DEFAULT_MIN_IDENTIFIER_LENGTH: int = 3 (static final)
- DEFAULT_MAGIC_NUMBER_THRESHOLD: int = 3 (static final)
- DEFAULT_MAX_CONDITIONAL_OPERATORS: int = 4 (static final)
- DEFAULT_MAX_NESTING_DEPTH: int = 3 (static final)
- DEFAULT_MAX_STATEMENT_TOKENS: int = 40 (static final)
- DEFAULT_MAX_STATEMENT_CHARS: int = 250 (static final)
- DEFAULT_MAX_METHOD_CHAIN_LENGTH: int = 5 (static final)
- DEFAULT_MAX_RESPONSIBILITIES: int = 3 (static final)
- DEFAULT_MIN_COHESION_INDEX: double = 0.4 (static final)
- DEFAULT_MAX_COUPLING_COUNT: int = 6 (static final)
- DEFAULT_MAX_ABSTRACTION_USAGE: int = 1 (static final)

**Operations:**
- isDetectorEnabled(detectorName: String, settings: UserSettings): boolean (static)
- getMaxMethodLength(settings: UserSettings): int (static)
- getMaxMethodComplexity(settings: UserSettings): int (static)
- getMaxParameterCount(settings: UserSettings): int (static)
- getMaxIdentifierLength(settings: UserSettings): int (static)
- getMinIdentifierLength(settings: UserSettings): int (static)
- getMagicNumberThreshold(settings: UserSettings): int (static)
- getMaxConditionalOperators(settings: UserSettings): int (static)
- getMaxNestingDepth(settings: UserSettings): int (static)
- getMaxStatementTokens(settings: UserSettings): int (static)
- getMaxStatementChars(settings: UserSettings): int (static)
- getMaxMethodChainLength(settings: UserSettings): int (static)
- getMaxResponsibilities(settings: UserSettings): int (static)
- getMinCohesionIndex(settings: UserSettings): double (static)
- getMaxCouplingCount(settings: UserSettings): int (static)
- getMaxAbstractionUsage(settings: UserSettings): int (static)
- shouldExclude(path: String): boolean (static)

---

### 9.2 SecurityConfig
**Package:** com.devsync.config  
**Type:** Configuration Class  
**Purpose:** Spring Security configuration

**Attributes:**
- None

**Operations:**
- securityFilterChain(http: HttpSecurity): SecurityFilterChain
- passwordEncoder(): BCryptPasswordEncoder

---

### 9.3 CorsConfig
**Package:** com.devsync.config  
**Type:** Configuration Class  
**Purpose:** Cross-Origin Resource Sharing configuration

**Attributes:**
- None

**Operations:**
- corsConfigurer(): WebMvcConfigurer

---

## 10. UTILITY LAYER

### 10.1 ZipExtractor
**Package:** com.devsync.utils  
**Type:** Utility Class  
**Purpose:** Extracts ZIP archives securely

**Attributes:**
- None

**Operations:**
- extractZip(zipInputStream: InputStream, destDir: String): void (static)
- extractFile(zipIn: ZipInputStream, filePath: String): void (static)

---

### 10.2 FolderNamingUtil
**Package:** com.devsync.utils  
**Type:** Utility Class  
**Purpose:** Generates unique folder names for uploads

**Attributes:**
- None

**Operations:**
- generateUniqueFolderName(originalFileName: String, baseDir: String): String (static)

---

### 10.3 ReportValidator
**Package:** com.devsync.utils  
**Type:** Utility Class  
**Purpose:** Validates report format and content

**Attributes:**
- None

**Operations:**
- validateReport(reportPath: String): boolean (static)
- checkReportIntegrity(content: String): boolean (static)

---

## 11. DTO LAYER (Data Transfer Objects)

### 11.1 DetectorConfigDTO
**Package:** com.devsync.dto  
**Type:** Data Transfer Object  
**Purpose:** Transfers detector configuration data

**Attributes:**
- detectorName: String
- enabled: boolean
- parameters: Map<String, Object>

**Operations:**
- DetectorConfigDTO()
- Getters and setters for all attributes

---

### 11.2 LongMethodThresholdDetails
**Package:** com.devsync.dto  
**Type:** Data Transfer Object  
**Purpose:** Detailed threshold information for long method detection

**Attributes:**
- statementCount: int
- cyclomaticComplexity: int
- cognitiveComplexity: int
- nestingDepth: int
- responsibilities: int
- maxStatements: int
- maxComplexity: int
- maxCognitive: int
- maxNesting: int
- maxResponsibilities: int

**Operations:**
- LongMethodThresholdDetails()
- Getters and setters for all attributes

---

## 12. MAIN APPLICATION CLASS

### 12.1 DevsyncApplication
**Package:** com.devsync  
**Type:** Spring Boot Application Class  
**Purpose:** Application entry point

**Attributes:**
- None

**Operations:**
- main(args: String[]): void (static)

**Annotations:**
- @SpringBootApplication

---

## RELATIONSHIPS SUMMARY

### Key Relationships:

1. **Controller → Service → Repository Pattern:**
   - Controllers depend on Services
   - Services depend on Repositories
   - Repositories interact with Database Entities

2. **Analysis Flow:**
   - CodeAnalysisController → CodeAnalysisEngine → Detectors
   - CodeAnalysisEngine → JavaFileCollector, LOCCounter
   - All Detectors analyze CompilationUnit objects

3. **Report Generation:**
   - CodeAnalysisController → ReportGenerator → GradingSystem
   - ReportGenerator → AIAssistantService (optional)

4. **Visual Diagrams:**
   - CodeAnalysisController → VisualReportGenerator
   - VisualReportGenerator → VisualDependencyAnalyzer → PlantUMLGenerator

5. **Configuration:**
   - All Detectors configured by AnalysisConfig
   - AnalysisConfig reads from UserSettings
   - UserSettings stored per user in database

6. **Authentication:**
   - AuthController → UserService → UserRepository
   - UserService uses BCryptPasswordEncoder

7. **AI Integration:**
   - AIAssistantService integrates with external APIs
   - Configured through UserSettings
   - Used by CodeAnalysisController and AIRefactorController

---

## DESIGN PATTERNS USED

1. **Repository Pattern:** All Repository interfaces
2. **Service Layer Pattern:** All Service classes
3. **Singleton Pattern:** Detector instances in CodeAnalysisEngine
4. **Factory Pattern:** Detector initialization
5. **Strategy Pattern:** Different AI providers in AIAssistantService
6. **Visitor Pattern:** AST traversal in Detectors
7. **Builder Pattern:** Report generation
8. **DTO Pattern:** Data transfer objects for API communication
9. **MVC Pattern:** Controller-Service-Repository architecture

---

## TOTAL CLASS COUNT

- **Entity Classes:** 6
- **Repository Interfaces:** 5
- **Controller Classes:** 10
- **Service Classes:** 6
- **Analyzer Classes:** 3
- **Detector Classes:** 12
- **Report Classes:** 2
- **Visual Classes:** 5
- **Configuration Classes:** 3
- **Utility Classes:** 3
- **DTO Classes:** 2
- **Main Application:** 1

**Total: 58 Classes/Interfaces**

---

*End of Class Diagram Documentation*
