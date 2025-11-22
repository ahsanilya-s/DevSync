// Test script to verify report parsing logic
const fs = require('fs');

// Read the sample report
const reportContent = `=== DevSync Code Analysis Report ===
Generated: 2025-11-21T22:02:18.0015165

SUMMARY
-------
Analyzed 11 files, found 18 issues (4 critical, 5 high, 9 medium)

SEVERITY BREAKDOWN
------------------
High      : 5
Medium    : 9
Critical  : 4

ISSUE TYPE BREAKDOWN
--------------------
MissingDefault      : 1
LongParameterList   : 1
ComplexConditional  : 1
DeficientEncapsulation: 4
UnnecessaryAbstraction: 1
LongIdentifier      : 1
MagicNumber         : 9

FILE-WISE BREAKDOWN
-------------------
File: LongStatementExample.java (Total: 5)
  Medium    : 5
File: LongParameterListExample.java (Total: 1)
  Medium    : 1
File: LongIdentifierExample.java (Total: 2)
  High      : 1
  Critical  : 1

DETAILED ISSUES
---------------
🚨 🟠 [MagicNumber] ComplexConditionalExample.java:4 - Number '3' in comparison context [Risk: 0.55] - Controls program flow, Unclear meaning | Suggestions: Replace with named constant, Use descriptive constant name
🚨 🔴 [DeficientEncapsulation] DeficientEncapsulationExample.java:3 - Field 'name' (Risk: 1.80) - Public field exposure, Mutable state, Missing accessors | Suggestions: Make field private, Add getter/setter methods, Consider immutable design
🚨 🟡 [MagicNumber] LongMethodExample.java:7 - Number '200' in comparison context [Risk: 0.80] - Business logic constant, Controls program flow, Unclear meaning | Suggestions: Replace with named constant, Document business rule, Use descriptive constant name`;

function parseReportContent(content) {
  const lines = content.split('\n');
  const issues = [];
  const fileStats = {};
  const typeStats = {};
  let totalFiles = 0;
  let totalIssues = 0;

  let currentSection = '';
  
  lines.forEach(line => {
    // Track sections
    if (line.startsWith('SEVERITY BREAKDOWN')) {
      currentSection = 'severity';
      return;
    }
    if (line.startsWith('ISSUE TYPE BREAKDOWN')) {
      currentSection = 'types';
      return;
    }
    if (line.startsWith('FILE-WISE BREAKDOWN')) {
      currentSection = 'files';
      return;
    }
    if (line.startsWith('DETAILED ISSUES')) {
      currentSection = 'issues';
      return;
    }
    
    // Parse issue type breakdown
    if (currentSection === 'types' && line.includes(':') && !line.startsWith('-')) {
      const match = line.match(/^([\\w\\s]+?)\\s*:\\s*(\\d+)$/);
      if (match) {
        const [, type, count] = match;
        typeStats[type.trim()] = parseInt(count);
      }
    }
    
    // Parse file-wise breakdown
    if (currentSection === 'files' && line.startsWith('File: ')) {
      const fileMatch = line.match(/File: (.+?) \\(Total: (\\d+)\\)/);
      if (fileMatch) {
        const [, fileName, total] = fileMatch;
        let cleanFileName = fileName;
        if (fileName.includes('/')) cleanFileName = fileName.split('/').pop();
        if (fileName.includes('\\\\')) cleanFileName = fileName.split('\\\\').pop();
        
        if (!fileStats[cleanFileName]) {
          fileStats[cleanFileName] = { critical: 0, high: 0, medium: 0, low: 0, total: parseInt(total) };
        }
      }
    }
    
    // Parse severity counts within file breakdown
    if (currentSection === 'files' && line.trim().includes(':') && line.startsWith('  ')) {
      const severityMatch = line.match(/^\\s+(\\w+)\\s*:\\s*(\\d+)$/);
      if (severityMatch) {
        const [, severity, count] = severityMatch;
        const fileKeys = Object.keys(fileStats);
        const lastFile = fileKeys[fileKeys.length - 1];
        if (lastFile && fileStats[lastFile]) {
          const severityKey = severity.toLowerCase();
          if (fileStats[lastFile].hasOwnProperty(severityKey)) {
            fileStats[lastFile][severityKey] = parseInt(count);
          }
        }
      }
    }
    
    // Parse detailed issues
    if (line.startsWith('🚨 ')) {
      const cleanLine = line.substring(3).trim(); // Skip '🚨 ' (3 chars including space)
      
      // Match format: 🔴 [Type] file.java:123 - description
      const issueMatch = cleanLine.match(/^([🔴🟡🟠⚠️])\\s+\\[(\\w+)\\]\\s+(.+?):(\\d+)\\s+-\\s+(.+)/);
      
      if (issueMatch) {
        const [, severity, type, file, lineNum, description] = issueMatch;
        let fileName = file;
        if (file.includes('/')) fileName = file.split('/').pop();
        if (file.includes('\\\\')) fileName = file.split('\\\\').pop();
        if (fileName === 'UnknownFile') fileName = 'Unknown';
        
        let cleanDescription = description.split('|')[0].split('[Score:')[0].split('[Risk:')[0].trim();
        
        const issue = {
          severity: getSeverityLevel(severity),
          type,
          file: fileName,
          line: parseInt(lineNum),
          description: cleanDescription
        };
        issues.push(issue);
        totalIssues++;
      }
    }
    
    // Parse metadata
    if (line.includes('Analyzed') && line.includes('files, found')) {
      const match = line.match(/Analyzed (\\d+) files, found (\\d+) issues/);
      if (match) {
        totalFiles = parseInt(match[1]);
        if (totalIssues === 0) totalIssues = parseInt(match[2]);
      }
    }
  });

  // Calculate statistics
  let severityStats = {
    critical: issues.filter(i => i.severity === 'critical').length,
    high: issues.filter(i => i.severity === 'high').length,
    medium: issues.filter(i => i.severity === 'medium').length,
    low: issues.filter(i => i.severity === 'low').length
  };
  
  // Parse severity from the breakdown section
  const severitySection = content.split('SEVERITY BREAKDOWN')[1]?.split('\\n\\n')[0];
  if (severitySection) {
    const criticalMatch = severitySection.match(/Critical\\s*:\\s*(\\d+)/);
    const highMatch = severitySection.match(/High\\s*:\\s*(\\d+)/);
    const mediumMatch = severitySection.match(/Medium\\s*:\\s*(\\d+)/);
    const lowMatch = severitySection.match(/Low\\s*:\\s*(\\d+)/);
    
    if (criticalMatch || highMatch || mediumMatch || lowMatch) {
      severityStats = {
        critical: criticalMatch ? parseInt(criticalMatch[1]) : 0,
        high: highMatch ? parseInt(highMatch[1]) : 0,
        medium: mediumMatch ? parseInt(mediumMatch[1]) : 0,
        low: lowMatch ? parseInt(lowMatch[1]) : 0
      };
    }
  }

  return {
    issues,
    fileStats,
    severityStats,
    typeStats,
    totalFiles,
    totalIssues
  };
}

function getSeverityLevel(emoji) {
  switch (emoji) {
    case '🔴': return 'critical';
    case '🟡': return 'high';
    case '🟠': return 'medium';
    case '⚠️': return 'low';
    default: return 'low';
  }
}

// Run the test
console.log('=== Testing Report Parsing ===');
const result = parseReportContent(reportContent);

console.log('\\nParsed Results:');
console.log('Total Files:', result.totalFiles);
console.log('Total Issues:', result.totalIssues);
console.log('Severity Stats:', result.severityStats);
console.log('Type Stats:', result.typeStats);
console.log('File Stats:', result.fileStats);
console.log('Parsed Issues Count:', result.issues.length);

// Validation
let valid = true;
const expectedSeverity = { critical: 4, high: 5, medium: 9, low: 0 };
const expectedTypes = { 
  MissingDefault: 1, 
  LongParameterList: 1, 
  ComplexConditional: 1, 
  DeficientEncapsulation: 4, 
  UnnecessaryAbstraction: 1, 
  LongIdentifier: 1, 
  MagicNumber: 9 
};

console.log('\\n=== Validation ===');

// Check severity parsing
if (JSON.stringify(result.severityStats) === JSON.stringify(expectedSeverity)) {
  console.log('✅ Severity parsing: PASSED');
} else {
  console.log('❌ Severity parsing: FAILED');
  console.log('Expected:', expectedSeverity);
  console.log('Got:', result.severityStats);
  valid = false;
}

// Check type parsing
if (JSON.stringify(result.typeStats) === JSON.stringify(expectedTypes)) {
  console.log('✅ Type parsing: PASSED');
} else {
  console.log('❌ Type parsing: FAILED');
  console.log('Expected:', expectedTypes);
  console.log('Got:', result.typeStats);
  valid = false;
}

// Check file parsing
if (Object.keys(result.fileStats).length >= 3) {
  console.log('✅ File parsing: PASSED');
} else {
  console.log('❌ File parsing: FAILED');
  console.log('Expected at least 3 files, got:', Object.keys(result.fileStats).length);
  valid = false;
}

// Check issue parsing
if (result.issues.length >= 3) {
  console.log('✅ Issue parsing: PASSED');
} else {
  console.log('❌ Issue parsing: FAILED');
  console.log('Expected at least 3 issues, got:', result.issues.length);
  valid = false;
}

if (valid) {
  console.log('\\n🎉 All tests PASSED! Report parsing is working correctly.');
} else {
  console.log('\\n💥 Some tests FAILED. Check the parsing logic.');
}