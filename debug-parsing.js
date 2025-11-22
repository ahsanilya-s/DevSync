// Debug script to see what's happening with parsing
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

DETAILED ISSUES
---------------
🚨 🟠 [MagicNumber] ComplexConditionalExample.java:4 - Number '3' in comparison context [Risk: 0.55] - Controls program flow, Unclear meaning | Suggestions: Replace with named constant, Use descriptive constant name
🚨 🔴 [DeficientEncapsulation] DeficientEncapsulationExample.java:3 - Field 'name' (Risk: 1.80) - Public field exposure, Mutable state, Missing accessors | Suggestions: Make field private, Add getter/setter methods, Consider immutable design`;

const lines = reportContent.split('\n');
let currentSection = '';

console.log('=== Debug Report Parsing ===');
console.log('Total lines:', lines.length);

lines.forEach((line, index) => {
  // Track sections
  if (line.startsWith('SEVERITY BREAKDOWN')) {
    currentSection = 'severity';
    console.log(`Line ${index}: Section changed to 'severity'`);
    return;
  }
  if (line.startsWith('ISSUE TYPE BREAKDOWN')) {
    currentSection = 'types';
    console.log(`Line ${index}: Section changed to 'types'`);
    return;
  }
  if (line.startsWith('FILE-WISE BREAKDOWN')) {
    currentSection = 'files';
    console.log(`Line ${index}: Section changed to 'files'`);
    return;
  }
  if (line.startsWith('DETAILED ISSUES')) {
    currentSection = 'issues';
    console.log(`Line ${index}: Section changed to 'issues'`);
    return;
  }
  
  // Debug type parsing
  if (currentSection === 'types' && line.includes(':') && !line.startsWith('-')) {
    console.log(`Line ${index} (types): "${line}"`);
    const match = line.match(/^([\\w\\s]+?)\\s*:\\s*(\\d+)$/);
    if (match) {
      console.log(`  Matched: type="${match[1].trim()}", count=${match[2]}`);
    } else {
      console.log(`  No match for regex`);
    }
  }
  
  // Debug issue parsing
  if (line.startsWith('🚨 ')) {
    console.log(`Line ${index} (issue): "${line}"`);
    const cleanLine = line.substring(3).trim();
    console.log(`  Clean line: "${cleanLine}"`);
    const issueMatch = cleanLine.match(/^([🔴🟡🟠⚠️])\\s+\\[(\\w+)\\]\\s+(.+?):(\\d+)\\s+-\\s+(.+)/);
    if (issueMatch) {
      console.log(`  Matched issue: severity="${issueMatch[1]}", type="${issueMatch[2]}", file="${issueMatch[3]}", line=${issueMatch[4]}`);
    } else {
      console.log(`  No match for issue regex`);
    }
  }
});

// Test severity section parsing
console.log('\\n=== Testing Severity Section Parsing ===');
const severitySection = reportContent.split('SEVERITY BREAKDOWN')[1]?.split('\\n\\n')[0];
console.log('Severity section:', severitySection);

if (severitySection) {
  const criticalMatch = severitySection.match(/Critical\\s*:\\s*(\\d+)/);
  const highMatch = severitySection.match(/High\\s*:\\s*(\\d+)/);
  const mediumMatch = severitySection.match(/Medium\\s*:\\s*(\\d+)/);
  
  console.log('Critical match:', criticalMatch);
  console.log('High match:', highMatch);
  console.log('Medium match:', mediumMatch);
}