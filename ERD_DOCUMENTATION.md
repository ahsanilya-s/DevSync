# DevSync System - Entity Relationship Diagram (ERD) Documentation

## Overview
This document provides a detailed description of the database schema for the DevSync code analysis system, including all entities, their attributes, relationships, and constraints.

---

## DATABASE INFORMATION

**Database Name:** devsyncdb  
**Database Type:** MySQL  
**Total Tables:** 5

---

## ENTITIES (TABLES)

### 1. users

**Purpose:** Stores user account information for authentication and profile management

**Columns:**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| username | VARCHAR(255) | NOT NULL | User's display name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | User's email address (used for login) |
| password | VARCHAR(255) | NOT NULL | Encrypted password (BCrypt) |
| created_at | DATETIME | NOT NULL | Account creation timestamp |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `email`

**Relationships:**
- One-to-One with `user_settings` (one user has one settings record)
- One-to-Many with `analysis_history` (one user has many analysis records)
- One-to-Many with `commit_analysis` (one user has many commit analyses)

---

### 2. user_settings

**Purpose:** Stores per-user configuration for code analysis detectors and AI settings

**Columns:**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique settings identifier |
| user_id | VARCHAR(255) | NOT NULL | Foreign key to users table |
| long_method_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable long method detector |
| max_method_length | INT | DEFAULT 50 | Maximum allowed method length |
| max_method_complexity | INT | DEFAULT 10 | Maximum cyclomatic complexity |
| long_parameter_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable parameter list detector |
| max_parameter_count | INT | DEFAULT 5 | Maximum method parameters |
| long_identifier_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable identifier length detector |
| max_identifier_length | INT | DEFAULT 30 | Maximum identifier length |
| min_identifier_length | INT | DEFAULT 3 | Minimum identifier length |
| magic_number_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable magic number detector |
| magic_number_threshold | INT | DEFAULT 3 | Threshold for magic numbers |
| missing_default_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable missing default detector |
| empty_catch_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable empty catch detector |
| complex_conditional_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable complex conditional detector |
| max_conditional_operators | INT | DEFAULT 4 | Maximum conditional operators |
| max_nesting_depth | INT | DEFAULT 3 | Maximum nesting depth |
| long_statement_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable long statement detector |
| max_statement_tokens | INT | DEFAULT 40 | Maximum statement tokens |
| max_statement_chars | INT | DEFAULT 250 | Maximum statement characters |
| max_method_chain_length | INT | DEFAULT 5 | Maximum method chain length |
| broken_modularization_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable modularization detector |
| max_responsibilities | INT | DEFAULT 3 | Maximum class responsibilities |
| min_cohesion_index | DOUBLE | DEFAULT 0.4 | Minimum cohesion index |
| max_coupling_count | INT | DEFAULT 6 | Maximum coupling count |
| deficient_encapsulation_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable encapsulation detector |
| unnecessary_abstraction_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable abstraction detector |
| max_abstraction_usage | INT | DEFAULT 1 | Maximum abstraction usage |
| memory_leak_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable memory leak detector |
| ai_provider | VARCHAR(50) | DEFAULT 'ollama' | AI provider (ollama/openai/anthropic) |
| ai_api_key | VARCHAR(255) | NULL | API key for AI services |
| ai_model | VARCHAR(100) | DEFAULT 'deepseek-coder:latest' | AI model name |
| ai_enabled | BOOLEAN | DEFAULT TRUE | Enable/disable AI analysis |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `user_id`

**Relationships:**
- Many-to-One with `users` (many settings can reference one user, but typically 1:1)

---

### 3. analysis_history

**Purpose:** Records all code analysis sessions with summary metrics and results

**Columns:**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique analysis record identifier |
| user_id | VARCHAR(255) | NOT NULL | Foreign key to users table |
| project_name | VARCHAR(255) | NOT NULL | Name of analyzed project |
| report_path | VARCHAR(500) | NOT NULL | File system path to report |
| analysis_date | DATETIME | NOT NULL | When analysis was performed |
| total_issues | INT | NOT NULL | Total number of issues found |
| critical_issues | INT | NOT NULL | Number of critical severity issues |
| warnings | INT | NOT NULL | Number of high severity issues |
| suggestions | INT | NOT NULL | Number of medium severity issues |
| project_path | VARCHAR(500) | NULL | Path to analyzed project |
| total_loc | INT | NULL | Total lines of code analyzed |
| grade | VARCHAR(10) | NULL | Quality grade (A+, A, B, C, D, F) |
| issue_density | DOUBLE | NULL | Issues per 1000 lines of code |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `user_id`
- INDEX on `analysis_date` (for sorting)

**Relationships:**
- Many-to-One with `users` (many analyses belong to one user)

---

### 4. commit_analysis

**Purpose:** Stores analysis results for specific Git repository commits

**Columns:**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique commit analysis identifier |
| user_id | VARCHAR(255) | NOT NULL | Foreign key to users table |
| repo_owner | VARCHAR(255) | NOT NULL | GitHub repository owner |
| repo_name | VARCHAR(255) | NOT NULL | GitHub repository name |
| commit_sha | VARCHAR(255) | NOT NULL | Git commit SHA hash |
| commit_message | TEXT | NULL | Commit message |
| commit_date | DATETIME | NULL | When commit was made |
| analysis_date | DATETIME | NOT NULL | When analysis was performed |
| total_issues | INT | NULL | Total number of issues found |
| critical_issues | INT | NULL | Number of critical severity issues |
| warnings | INT | NULL | Number of high severity issues |
| suggestions | INT | NULL | Number of medium severity issues |
| report_path | VARCHAR(1000) | NULL | File system path to report |

**Indexes:**
- PRIMARY KEY on `id`
- INDEX on `user_id`
- INDEX on `commit_sha`
- INDEX on `analysis_date`

**Relationships:**
- Many-to-One with `users` (many commit analyses belong to one user)

---

### 5. admin_settings

**Purpose:** System-wide administrative configuration stored as key-value pairs

**Columns:**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique settings identifier |
| setting_key | VARCHAR(255) | UNIQUE, NOT NULL | Configuration key name |
| setting_value | TEXT | NULL | Configuration value |
| description | VARCHAR(500) | NULL | Human-readable description |
| category | VARCHAR(100) | NULL | Settings category grouping |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `setting_key`

**Common Setting Keys:**
- `maintenance_mode` - System maintenance flag
- `max_file_size` - Maximum upload size in MB
- `allowed_file_types` - Comma-separated file extensions
- `ai_analysis_enabled` - Global AI toggle

**Relationships:**
- None (standalone configuration table)

---

## ENTITY RELATIONSHIP DIAGRAM (Textual Representation)

```
┌─────────────────┐
│     users       │
│─────────────────│
│ PK: id          │
│    username     │
│    email (UK)   │
│    password     │
│    created_at   │
└────────┬────────┘
         │
         │ 1:1
         │
         ├──────────────────────────────────┐
         │                                  │
         │ 1:N                              │ 1:N
         │                                  │
┌────────▼────────┐              ┌─────────▼──────────┐
│ user_settings   │              │ analysis_history   │
│─────────────────│              │────────────────────│
│ PK: id          │              │ PK: id             │
│ FK: user_id     │              │ FK: user_id        │
│    [30+ config  │              │    project_name    │
│     fields]     │              │    report_path     │
│                 │              │    analysis_date   │
└─────────────────┘              │    total_issues    │
                                 │    critical_issues │
                                 │    warnings        │
                                 │    suggestions     │
                                 │    total_loc       │
                                 │    grade           │
                                 │    issue_density   │
                                 └────────────────────┘
         │
         │ 1:N
         │
┌────────▼────────┐              ┌─────────────────┐
│ commit_analysis │              │ admin_settings  │
│─────────────────│              │─────────────────│
│ PK: id          │              │ PK: id          │
│ FK: user_id     │              │    maintenance  │
│    repo_owner   │              │    max_file_size│
│    repo_name    │              │    allowed_types│
│    commit_sha   │              │    ai_enabled   │
│    commit_msg   │              └─────────────────┘
│    commit_date  │              (No relationships)
│    analysis_date│
│    total_issues │
│    report_path  │
└─────────────────┘
```

---

## RELATIONSHIPS DETAIL

### 1. users ↔ user_settings (One-to-One)
- **Type:** One-to-One
- **Foreign Key:** `user_settings.user_id` references `users.id`
- **Cardinality:** Each user has exactly one settings record
- **Cascade:** ON DELETE CASCADE (deleting user deletes settings)

### 2. users ↔ analysis_history (One-to-Many)
- **Type:** One-to-Many
- **Foreign Key:** `analysis_history.user_id` references `users.id`
- **Cardinality:** Each user can have multiple analysis records
- **Cascade:** ON DELETE CASCADE (deleting user deletes all analyses)

### 3. users ↔ commit_analysis (One-to-Many)
- **Type:** One-to-Many
- **Foreign Key:** `commit_analysis.user_id` references `users.id`
- **Cardinality:** Each user can have multiple commit analyses
- **Cascade:** ON DELETE CASCADE (deleting user deletes all commit analyses)

---

## DATA INTEGRITY CONSTRAINTS

### Primary Keys
- All tables have auto-incrementing BIGINT primary keys
- Ensures unique identification of each record

### Foreign Keys
- `user_settings.user_id` → `users.id`
- `analysis_history.user_id` → `users.id`
- `commit_analysis.user_id` → `users.id`

### Unique Constraints
- `users.email` must be unique (prevents duplicate accounts)

### Not Null Constraints
- Critical fields like `username`, `email`, `password` cannot be null
- Foreign keys are NOT NULL to maintain referential integrity
- Analysis metrics have NOT NULL constraints for data consistency

### Default Values
- Boolean flags default to TRUE for enabled detectors
- Numeric thresholds have sensible defaults (e.g., max_method_length = 50)
- AI provider defaults to 'ollama'
- Timestamps auto-populate on record creation

---

## INDEXING STRATEGY

### Primary Indexes
- All `id` columns are indexed as PRIMARY KEY

### Foreign Key Indexes
- `user_settings.user_id`
- `analysis_history.user_id`
- `commit_analysis.user_id`

### Query Optimization Indexes
- `users.email` (UNIQUE) - for login queries
- `analysis_history.analysis_date` - for chronological sorting
- `commit_analysis.commit_sha` - for commit lookups
- `commit_analysis.analysis_date` - for chronological sorting

---

## DATA TYPES RATIONALE

### BIGINT for IDs
- Supports up to 9,223,372,036,854,775,807 records
- Future-proof for large-scale deployments

### VARCHAR for Text Fields
- Variable length reduces storage overhead
- Appropriate sizes: 50-1000 based on expected content

### DATETIME for Timestamps
- Stores both date and time with precision
- Supports timezone-aware operations

### BOOLEAN for Flags
- Clear true/false semantics
- Efficient storage (1 byte)

### INT for Numeric Thresholds
- Sufficient range for configuration values
- 4-byte storage

### DOUBLE for Decimal Metrics
- Precise floating-point calculations
- Used for ratios and densities

---

## SAMPLE QUERIES

### Get User with Settings
```sql
SELECT u.*, s.*
FROM users u
LEFT JOIN user_settings s ON u.id = s.user_id
WHERE u.email = 'user@example.com';
```

### Get User's Analysis History
```sql
SELECT *
FROM analysis_history
WHERE user_id = '123'
ORDER BY analysis_date DESC
LIMIT 10;
```

### Get Recent Commit Analyses
```sql
SELECT *
FROM commit_analysis
WHERE user_id = '123'
  AND repo_name = 'my-project'
ORDER BY commit_date DESC;
```

### Calculate Average Grade by User
```sql
SELECT user_id, AVG(total_issues) as avg_issues, AVG(issue_density) as avg_density
FROM analysis_history
GROUP BY user_id;
```

---

## DATABASE NORMALIZATION

**Normalization Level:** 3rd Normal Form (3NF)

### 1NF (First Normal Form)
✓ All columns contain atomic values  
✓ No repeating groups  
✓ Each column has a unique name

### 2NF (Second Normal Form)
✓ Meets 1NF requirements  
✓ All non-key attributes fully depend on primary key  
✓ No partial dependencies

### 3NF (Third Normal Form)
✓ Meets 2NF requirements  
✓ No transitive dependencies  
✓ All attributes depend only on primary key

---

## STORAGE ESTIMATES

### Per Record Size (Approximate)

| Table | Avg Size | Notes |
|-------|----------|-------|
| users | 200 bytes | Small, fixed-size records |
| user_settings | 500 bytes | Many configuration fields |
| analysis_history | 300 bytes | Includes metrics and paths |
| commit_analysis | 400 bytes | Includes Git metadata |
| admin_settings | 150 bytes | Single record typically |

### Projected Storage (10,000 users)

| Table | Records | Total Size |
|-------|---------|------------|
| users | 10,000 | ~2 MB |
| user_settings | 10,000 | ~5 MB |
| analysis_history | 100,000 | ~30 MB |
| commit_analysis | 50,000 | ~20 MB |
| admin_settings | 1 | <1 KB |

**Total Database Size:** ~60 MB (excluding indexes)

---

## BACKUP AND MAINTENANCE

### Recommended Backup Strategy
- **Full Backup:** Daily at off-peak hours
- **Incremental Backup:** Every 6 hours
- **Retention:** 30 days for full backups, 7 days for incremental

### Maintenance Tasks
- **Index Optimization:** Weekly ANALYZE TABLE
- **Cleanup Old Records:** Archive analyses older than 1 year
- **Vacuum/Optimize:** Monthly table optimization

---

*End of ERD Documentation*


---

## COMPLETE SYSTEM ERD (Enhanced)

### System Architecture Overview

The DevSync system uses a **hybrid data storage approach**:
1. **Relational Database (MySQL)** - Structured transactional data
2. **File System** - Large artifacts (reports, uploaded projects, diagrams)
3. **In-Memory** - Temporary processing data

---

## EXTENDED DATA STORAGE COMPONENTS

### A. DATABASE ENTITIES (Persistent Storage)

#### Core Tables (Already Documented Above):
1. **users** - User accounts
2. **user_settings** - User configurations  
3. **analysis_history** - Analysis records
4. **commit_analysis** - Git commit analyses
5. **admin_settings** - System configuration

---

### B. FILE SYSTEM STORAGE (Non-Relational)

#### File Storage Structure:

```
uploads/
├── {timestamp_folder}/
│   ├── {project_name}/
│   │   ├── src/
│   │   │   └── [Java source files]
│   │   ├── lib/
│   │   │   └── [JAR dependencies]
│   │   └── resources/
│   │       └── [Configuration files]
│   ├── {project_name}_comprehensive.txt
│   └── diagrams/
│       ├── class_diagram.png
│       ├── class_diagram.puml
│       ├── sequence_diagram.png
│       └── dependency_graph.png
```

#### File System Entities:

**1. Uploaded Projects**
- **Location:** `uploads/{timestamp}/{project_name}/`
- **Content:** Extracted ZIP/JAR files
- **Linked To:** `analysis_history.project_path`
- **Lifecycle:** Retained until manual cleanup
- **Size:** Variable (typically 1-100 MB per project)

**2. Analysis Reports**
- **Location:** `uploads/{timestamp}/{project_name}_comprehensive.txt`
- **Content:** Detailed text report with all issues
- **Linked To:** `analysis_history.report_path`
- **Format:** Plain text with emoji markers
- **Size:** 10-500 KB per report

**3. Visual Diagrams**
- **Location:** `uploads/{timestamp}/diagrams/`
- **Types:**
  - Class diagrams (PNG, PlantUML)
  - Sequence diagrams (PNG)
  - Dependency graphs (PNG)
- **Linked To:** Derived from `analysis_history.project_path`
- **Size:** 50-500 KB per diagram

---

### C. IN-MEMORY DATA STRUCTURES (Transient)

#### Runtime Processing Entities:

**1. CompilationUnit Objects**
- **Type:** JavaParser AST nodes
- **Lifecycle:** Created during parsing, discarded after analysis
- **Purpose:** Represent parsed Java source code
- **Memory:** ~1-5 MB per file during processing

**2. Analysis Results Map**
- **Type:** HashMap<String, Object>
- **Lifecycle:** Created per analysis, cleared after report generation
- **Contents:**
  - `issues`: List<String>
  - `severityCounts`: Map<String, Integer>
  - `detectorCounts`: Map<String, Integer>
  - `totalLOC`: Integer
  - `totalClasses`: Integer
  - `totalMethods`: Integer
- **Memory:** ~5-20 MB per analysis

**3. Detector Instances**
- **Type:** Singleton objects
- **Lifecycle:** Initialized once, reused across analyses
- **Count:** 12 detector instances
- **Memory:** ~2-5 MB total

**4. HTTP Session Data**
- **Type:** Spring Security session
- **Contents:**
  - User authentication token
  - User ID
  - Session timestamp
- **Lifecycle:** 30 minutes timeout
- **Storage:** In-memory session store

---

## COMPLETE ENTITY RELATIONSHIP DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         MYSQL DATABASE (devsyncdb)                      │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────┐
                    │     users       │
                    │─────────────────│
                    │ PK: id          │
                    │    username     │
                    │    email (UK)   │
                    │    password     │
                    │    created_at   │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                │ 1:1        │ 1:N        │ 1:N
                │            │            │
       ┌────────▼────────┐  │   ┌────────▼────────┐
       │ user_settings   │  │   │commit_analysis  │
       │─────────────────│  │   │─────────────────│
       │ PK: id          │  │   │ PK: id          │
       │ FK: user_id     │  │   │ FK: user_id     │
       │ [Detector Cfg]  │  │   │    repo_owner   │
       │ [AI Settings]   │  │   │    repo_name    │
       └─────────────────┘  │   │    commit_sha   │
                            │   │    report_path ─┼──┐
                            │   └─────────────────┘  │
                            │                        │
                            │ 1:N                    │
                   ┌────────▼────────┐               │
                   │analysis_history │               │
                   │─────────────────│               │
                   │ PK: id          │               │
                   │ FK: user_id     │               │
                   │    project_name │               │
                   │    report_path ─┼───────────────┼──┐
                   │    project_path─┼───────────┐   │  │
                   │    analysis_date│           │   │  │
                   │    total_issues │           │   │  │
                   │    grade        │           │   │  │
                   │    issue_density│           │   │  │
                   └─────────────────┘           │   │  │
                                                 │   │  │
       ┌─────────────────┐                      │   │  │
       │ admin_settings  │                      │   │  │
       │─────────────────│                      │   │  │
       │ PK: id          │                      │   │  │
       │    setting_key  │                      │   │  │
       │    setting_value│                      │   │  │
       │    description  │                      │   │  │
       │    category     │                      │   │  │
       └─────────────────┘                      │   │  │
       (No FK relationships)                    │   │  │
                                                │   │  │
┌───────────────────────────────────────────────┼───┼──┼──────────────────┐
│                    FILE SYSTEM STORAGE        │   │  │                  │
└───────────────────────────────────────────────┼───┼──┼──────────────────┘
                                                │   │  │
                    ┌───────────────────────────┘   │  │
                    │                               │  │
                    ▼                               │  │
       ┌────────────────────────┐                  │  │
       │  Uploaded Projects     │                  │  │
       │────────────────────────│                  │  │
       │ Path: uploads/{ts}/    │                  │  │
       │       {project}/       │                  │  │
       │ Contains:              │                  │  │
       │  - Java source files   │                  │  │
       │  - JAR dependencies    │                  │  │
       │  - Resources           │                  │  │
       └────────────────────────┘                  │  │
                                                   │  │
                    ┌──────────────────────────────┘  │
                    │                                 │
                    ▼                                 │
       ┌────────────────────────┐                    │
       │  Analysis Reports      │◄───────────────────┘
       │────────────────────────│
       │ Path: uploads/{ts}/    │
       │   {project}_report.txt │
       │ Contains:              │
       │  - Issue list          │
       │  - Metrics             │
       │  - Grade report        │
       │  - AI analysis         │
       └────────────────────────┘
                    │
                    │ Generated from
                    │
                    ▼
       ┌────────────────────────┐
       │  Visual Diagrams       │
       │────────────────────────│
       │ Path: uploads/{ts}/    │
       │       diagrams/        │
       │ Contains:              │
       │  - class_diagram.png   │
       │  - class_diagram.puml  │
       │  - sequence_diagram.png│
       │  - dependency_graph.png│
       └────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                    IN-MEMORY PROCESSING DATA                            │
└─────────────────────────────────────────────────────────────────────────┘

       ┌────────────────────────┐
       │  CompilationUnit (AST) │
       │────────────────────────│
       │ Lifecycle: Transient   │
       │ Created: During parse  │
       │ Destroyed: After scan  │
       │ Contains:              │
       │  - Class declarations  │
       │  - Method declarations │
       │  - Field declarations  │
       │  - Statements          │
       └────────────────────────┘
                    │
                    │ Analyzed by
                    │
                    ▼
       ┌────────────────────────┐
       │  Detector Instances    │
       │────────────────────────│
       │ Lifecycle: Singleton   │
       │ Count: 12 detectors    │
       │ Types:                 │
       │  - LongMethodDetector  │
       │  - MagicNumberDetector │
       │  - [10 more...]        │
       └────────────────────────┘
                    │
                    │ Produces
                    │
                    ▼
       ┌────────────────────────┐
       │  Analysis Results Map  │
       │────────────────────────│
       │ Lifecycle: Per-analysis│
       │ Contains:              │
       │  - issues: List        │
       │  - severityCounts: Map │
       │  - detectorCounts: Map │
       │  - metrics: Map        │
       └────────────────────────┘
                    │
                    │ Persisted to
                    │
                    ▼
       ┌────────────────────────┐
       │  Database + Files      │
       │  (See above)           │
       └────────────────────────┘

       ┌────────────────────────┐
       │  HTTP Session Data     │
       │────────────────────────│
       │ Lifecycle: 30 min      │
       │ Contains:              │
       │  - User ID             │
       │  - Auth token          │
       │  - Session timestamp   │
       └────────────────────────┘
```

---

## DATA FLOW DIAGRAM

### Complete Analysis Workflow:

```
┌──────────────┐
│   User       │
│  (Browser)   │
└──────┬───────┘
       │
       │ 1. Upload ZIP file
       │
       ▼
┌──────────────────────┐
│ CodeAnalysisController│
└──────┬───────────────┘
       │
       │ 2. Extract to File System
       │
       ▼
┌──────────────────────┐         ┌─────────────────┐
│  File System         │         │  users table    │
│  uploads/{ts}/       │         │  (Verify user)  │
└──────┬───────────────┘         └─────────────────┘
       │                                  │
       │ 3. Read Java files               │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ JavaFileCollector    │                 │
└──────┬───────────────┘                 │
       │                                  │
       │ 4. Parse to AST                  │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ CompilationUnit      │                 │
│ (In-Memory AST)      │                 │
└──────┬───────────────┘                 │
       │                                  │
       │ 5. Analyze with detectors        │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ CodeAnalysisEngine   │◄────────────────┤
│ + 12 Detectors       │  6. Get user    │
└──────┬───────────────┘     settings    │
       │                                  │
       │ 7. Collect issues                │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ Analysis Results Map │                 │
│ (In-Memory)          │                 │
└──────┬───────────────┘                 │
       │                                  │
       │ 8. Generate report               │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ ReportGenerator      │                 │
└──────┬───────────────┘                 │
       │                                  │
       ├─ 9a. Save report to file        │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ File System          │                 │
│ report.txt           │                 │
└──────────────────────┘                 │
       │                                  │
       ├─ 9b. Generate diagrams          │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ PlantUMLGenerator    │                 │
└──────┬───────────────┘                 │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ File System          │                 │
│ diagrams/*.png       │                 │
└──────────────────────┘                 │
       │                                  │
       ├─ 9c. AI analysis (optional)     │
       │                                  │
       ▼                                  │
┌──────────────────────┐                 │
│ AIAssistantService   │                 │
│ (External API call)  │                 │
└──────┬───────────────┘                 │
       │                                  │
       │ 10. Save to database             │
       │                                  │
       ▼                                  ▼
┌──────────────────────────────────────────┐
│ analysis_history table                   │
│ (project_name, report_path, metrics)    │
└──────────────────────────────────────────┘
       │
       │ 11. Return summary
       │
       ▼
┌──────────────┐
│   User       │
│  (Browser)   │
└──────────────┘
```

---

## EXTERNAL SYSTEM INTEGRATIONS

### 1. AI Service Providers (External APIs)

```
┌─────────────────────┐
│ AIAssistantService  │
└──────┬──────────────┘
       │
       ├─────────────────────────────────┐
       │                                 │
       ▼                                 ▼
┌──────────────┐              ┌──────────────┐
│   Ollama     │              │   OpenAI     │
│ (localhost)  │              │  (Cloud API) │
└──────────────┘              └──────────────┘
       │                                 │
       │                                 │
       ▼                                 ▼
┌──────────────┐              ┌──────────────┐
│  Anthropic   │              │  Custom LLM  │
│ (Cloud API)  │              │  (Optional)  │
└──────────────┘              └──────────────┘

Relationship: External service calls
Data Flow: Report content → AI analysis text
Storage: AI responses appended to report files
```

### 2. GitHub Integration (External API)

```
┌─────────────────────┐
│ GitHubController    │
└──────┬──────────────┘
       │
       │ REST API calls
       │
       ▼
┌──────────────────────┐
│  GitHub API          │
│  (api.github.com)    │
└──────┬───────────────┘
       │
       │ Returns: Repository data, commits
       │
       ▼
┌──────────────────────┐
│ commit_analysis      │
│ (Database table)     │
└──────────────────────┘

Relationship: External API integration
Data Flow: Repo URL → Commit data → Analysis → Database
Storage: Commit metadata in commit_analysis table
```

---

## DATA RETENTION AND LIFECYCLE

### Database Records:

| Entity | Retention | Cleanup Strategy |
|--------|-----------|------------------|
| users | Permanent | Manual deletion only |
| user_settings | Permanent | Deleted with user (CASCADE) |
| analysis_history | 1 year | Archive old records quarterly |
| commit_analysis | 6 months | Archive old records monthly |
| admin_settings | Permanent | Manual updates only |

### File System:

| Entity | Retention | Cleanup Strategy |
|--------|-----------|------------------|
| Uploaded projects | 30 days | Automated cleanup job |
| Analysis reports | 1 year | Sync with database cleanup |
| Visual diagrams | 1 year | Sync with database cleanup |
| Temporary files | 24 hours | Daily cleanup job |

### In-Memory:

| Entity | Retention | Cleanup Strategy |
|--------|-----------|------------------|
| CompilationUnit | Analysis duration | Garbage collected after use |
| Analysis results | Analysis duration | Cleared after persistence |
| Detector instances | Application lifetime | Singleton pattern |
| HTTP sessions | 30 minutes | Session timeout |

---

## SECURITY AND ACCESS CONTROL

### Database Security:

```
┌─────────────────────┐
│  User Authentication│
│  (BCrypt password)  │
└──────┬──────────────┘
       │
       │ Validates
       │
       ▼
┌─────────────────────┐
│  Spring Security    │
│  (Session mgmt)     │
└──────┬──────────────┘
       │
       │ Authorizes
       │
       ▼
┌─────────────────────┐
│  Database Access    │
│  (JPA Repository)   │
└─────────────────────┘
```

### File System Security:

- **Access Control:** Report paths validated against user ownership
- **Path Traversal Prevention:** Canonical path checking in ZipExtractor
- **Upload Validation:** File type and size restrictions
- **Isolation:** Each user's files in separate directories

---

## SCALABILITY CONSIDERATIONS

### Database Scaling:

**Current:** Single MySQL instance  
**Future Options:**
- Read replicas for query scaling
- Partitioning analysis_history by date
- Sharding by user_id for horizontal scaling

### File System Scaling:

**Current:** Local file system  
**Future Options:**
- Object storage (S3, MinIO)
- CDN for diagram delivery
- Distributed file system (NFS, GlusterFS)

### In-Memory Scaling:

**Current:** Single application instance  
**Future Options:**
- Horizontal scaling with load balancer
- Distributed caching (Redis)
- Message queue for async processing (RabbitMQ)

---

## BACKUP STRATEGY

### Database Backup:

```
┌─────────────────────┐
│  MySQL Database     │
└──────┬──────────────┘
       │
       │ Daily full backup
       │ Hourly incremental
       │
       ▼
┌─────────────────────┐
│  Backup Storage     │
│  (30-day retention) │
└─────────────────────┘
```

### File System Backup:

```
┌─────────────────────┐
│  uploads/ directory │
└──────┬──────────────┘
       │
       │ Weekly full backup
       │ Daily incremental
       │
       ▼
┌─────────────────────┐
│  Backup Storage     │
│  (90-day retention) │
└─────────────────────┘
```

---

## MONITORING AND METRICS

### Database Metrics:

- Connection pool usage
- Query execution time
- Table sizes and growth rate
- Index efficiency

### File System Metrics:

- Disk space usage
- Upload success/failure rate
- Average file sizes
- Cleanup job effectiveness

### Application Metrics:

- Analysis duration per project
- Detector execution time
- Memory usage during analysis
- API response times

---

*End of Enhanced ERD Documentation*
