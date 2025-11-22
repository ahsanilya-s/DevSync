import React, { useState, useEffect } from 'react'
import { X, AlertTriangle, CheckCircle, XCircle, TrendingUp, FileText, Code, Bug } from 'lucide-react'
import { Button } from './ui/button'
import './VisualReport.css'

export function VisualReport({ reportContent, isOpen, onClose, isDarkMode, projectName }) {
  const [reportData, setReportData] = useState(null)

  useEffect(() => {
    if (reportContent && isOpen) {
      parseReportContent(reportContent)
    }
  }, [reportContent, isOpen])

  const parseReportContent = (content) => {
    try {
      console.log('Raw report content:', content)
      const lines = content.split('\n')
      console.log('Report lines:', lines.slice(0, 10)) // First 10 lines for debugging
      const issues = []
      const fileStats = {}
      const typeStats = {}
      let totalFiles = 0
      let totalIssues = 0

      // Parse different sections of the comprehensive report
      let currentSection = ''
      let inFileBreakdown = false
    
    lines.forEach(line => {
      // Track sections
      if (line.startsWith('ISSUE TYPE BREAKDOWN')) {
        currentSection = 'types'
        return
      }
      if (line.startsWith('FILE-WISE BREAKDOWN')) {
        currentSection = 'files'
        inFileBreakdown = true
        return
      }
      if (line.startsWith('DETAILED ISSUES')) {
        currentSection = 'issues'
        inFileBreakdown = false
        return
      }
      
      // Parse issue type breakdown
      if (currentSection === 'types' && line.includes(':') && !line.startsWith('-') && line.trim() !== '') {
        const match = line.match(/^([\w\s]+?)\s*:\s*(\d+)$/)
        if (match) {
          const [, type, count] = match
          typeStats[type.trim()] = parseInt(count)
          console.log('Parsed type:', type.trim(), 'count:', count)
        }
      }
      
      // Parse file-wise breakdown
      if (currentSection === 'files' && line.startsWith('File: ')) {
        const fileMatch = line.match(/File: (.+?) \(Total: (\d+)\)/)
        if (fileMatch) {
          const [, fileName, total] = fileMatch
          // Extract just filename from path
          let cleanFileName = fileName
          if (fileName.includes('/')) cleanFileName = fileName.split('/').pop()
          if (fileName.includes('\\')) cleanFileName = fileName.split('\\').pop()
          
          if (!fileStats[cleanFileName]) {
            fileStats[cleanFileName] = { critical: 0, high: 0, medium: 0, low: 0, total: parseInt(total) }
          }
        }
      }
      
      // Parse severity counts within file breakdown
      if (currentSection === 'files' && line.trim().includes(':') && line.startsWith('  ')) {
        const severityMatch = line.match(/^\s+(\w+)\s*:\s*(\d+)$/)
        if (severityMatch) {
          const [, severity, count] = severityMatch
          // Find the last file in fileStats to assign this severity count
          const fileKeys = Object.keys(fileStats)
          const lastFile = fileKeys[fileKeys.length - 1]
          if (lastFile && fileStats[lastFile]) {
            const severityKey = severity.toLowerCase()
            if (fileStats[lastFile].hasOwnProperty(severityKey)) {
              fileStats[lastFile][severityKey] = parseInt(count)
            }
          }
        }
      }
      
      // Parse detailed issues
      if (line.startsWith('🚨 ')) {
        const cleanLine = line.substring(3).trim() // Remove '🚨 ' prefix (3 chars)
        
        // Match format: 🔴 [Type] file.java:123 - description
        const issueMatch = cleanLine.match(/^([🔴🟡🟠⚠️])\s+\[(\w+)\]\s+(.+?):(\d+)\s+-\s+(.+)/)
        
        if (issueMatch) {
          const [, severity, type, file, lineNum, description] = issueMatch
          // Extract just filename from path
          let fileName = file
          if (file.includes('/')) fileName = file.split('/').pop()
          if (file.includes('\\')) fileName = file.split('\\').pop()
          if (fileName === 'UnknownFile') fileName = 'Unknown'
          
          // Extract clean description (before | or [Score:] or [Risk:])
          let cleanDescription = description.split('|')[0].split('[Score:')[0].split('[Risk:')[0].trim()
          
          const issue = {
            severity: getSeverityLevel(severity),
            type,
            file: fileName,
            line: parseInt(lineNum),
            description: cleanDescription
          }
          issues.push(issue)
          totalIssues++
          console.log('Parsed issue:', issue.severity, issue.type, issue.file)
        } else {
          console.log('Failed to parse issue line:', cleanLine)
        }
      }
      
      // Parse metadata from DevSync report
      if (line.includes('Analyzed') && line.includes('files, found')) {
        const match = line.match(/Analyzed (\d+) files, found (\d+) issues/)
        if (match) {
          totalFiles = parseInt(match[1])
          if (totalIssues === 0) totalIssues = parseInt(match[2])
        }
      }
      
      // Handle "No issues found" case
      if (line.includes('🎉 No issues found')) {
        totalIssues = 0
      }
    })

    // Calculate statistics from parsed issues
    let severityStats = {
      critical: issues.filter(i => i.severity === 'critical').length,
      high: issues.filter(i => i.severity === 'high').length,
      medium: issues.filter(i => i.severity === 'medium').length,
      low: issues.filter(i => i.severity === 'low').length
    }
    
    // Update totalIssues if we parsed more issues than detected
    if (issues.length > totalIssues) {
      totalIssues = issues.length
    }
    
    // Always try to parse severity from SEVERITY BREAKDOWN section
    const severitySection = content.split('SEVERITY BREAKDOWN')[1]?.split('\n\n')[0]
    if (severitySection) {
      const criticalMatch = severitySection.match(/Critical\s*:\s*(\d+)/)
      const highMatch = severitySection.match(/High\s*:\s*(\d+)/)
      const mediumMatch = severitySection.match(/Medium\s*:\s*(\d+)/)
      const lowMatch = severitySection.match(/Low\s*:\s*(\d+)/)
      
      if (criticalMatch || highMatch || mediumMatch || lowMatch) {
        severityStats = {
          critical: criticalMatch ? parseInt(criticalMatch[1]) : 0,
          high: highMatch ? parseInt(highMatch[1]) : 0,
          medium: mediumMatch ? parseInt(mediumMatch[1]) : 0,
          low: lowMatch ? parseInt(lowMatch[1]) : 0
        }
        // Update totalIssues from severity breakdown
        totalIssues = Object.values(severityStats).reduce((a, b) => a + b, 0)
        console.log('Parsed severity from breakdown:', severityStats, 'total:', totalIssues)
      }
    }

    // Ensure typeStats is populated - prefer parsed data, fallback to issues
    if (Object.keys(typeStats).length === 0 && issues.length > 0) {
      issues.forEach(issue => {
        typeStats[issue.type] = (typeStats[issue.type] || 0) + 1
      })
    }
    
    // If fileStats is empty but we have issues, generate from issues
    if (Object.keys(fileStats).length === 0 && issues.length > 0) {
      issues.forEach(issue => {
        if (!fileStats[issue.file]) {
          fileStats[issue.file] = { critical: 0, high: 0, medium: 0, low: 0, total: 0 }
        }
        fileStats[issue.file][issue.severity]++
        fileStats[issue.file].total++
      })
    }

      console.log('Final parsed data:')
      console.log('- Issues:', issues.length)
      console.log('- File stats keys:', Object.keys(fileStats))
      console.log('- Type stats:', typeStats)
      console.log('- Severity stats:', severityStats)
      console.log('- Total files:', totalFiles, 'Total issues:', totalIssues)
      
      setReportData({
        issues,
        fileStats,
        severityStats,
        typeStats,
        totalFiles,
        totalIssues,
        qualityScore: calculateQualityScore(severityStats, totalFiles),
        rawContent: content // Keep raw content for fallback
      })
    } catch (error) {
      console.error('Error parsing report content:', error)
      // Fallback: show raw content if parsing fails
      setReportData({
        issues: [],
        fileStats: {},
        severityStats: { critical: 0, high: 0, medium: 0, low: 0 },
        typeStats: {},
        totalFiles: 0,
        totalIssues: 0,
        qualityScore: 0,
        rawContent: content,
        parseError: true
      })
    }
  }

  const getSeverityLevel = (emoji) => {
    switch (emoji) {
      case '🔴': return 'critical'
      case '🟡': return 'high'
      case '🟠': return 'medium'
      case '⚠️': return 'low'
      default: return 'low'
    }
  }

  const calculateQualityScore = (stats, totalFiles) => {
    const totalIssues = Object.values(stats).reduce((a, b) => a + b, 0)
    
    // Perfect score if no issues
    if (totalIssues === 0) return 100
    if (totalFiles === 0) return totalIssues === 0 ? 100 : 50
    
    // Weight issues by severity
    const issueWeight = (stats.critical * 10) + (stats.high * 6) + (stats.medium * 3) + (stats.low * 1)
    
    // Calculate score based on issues per file ratio
    const issuesPerFile = totalIssues / totalFiles
    const baseScore = Math.max(0, 100 - (issuesPerFile * 15)) // 15 points per issue per file
    
    // Apply severity penalty
    const severityPenalty = Math.min(50, issueWeight * 2) // Max 50 point penalty
    const finalScore = Math.max(0, baseScore - severityPenalty)
    
    return Math.round(finalScore)
  }

  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'critical': return 'bg-red-500'
      case 'high': return 'bg-yellow-500'
      case 'medium': return 'bg-orange-500'
      case 'low': return 'bg-blue-500'
      default: return 'bg-gray-500'
    }
  }

  const getQualityGrade = (score) => {
    if (score >= 90) return { grade: 'A+', color: 'text-green-600', bg: 'bg-green-100' }
    if (score >= 80) return { grade: 'A', color: 'text-green-600', bg: 'bg-green-100' }
    if (score >= 70) return { grade: 'B', color: 'text-blue-600', bg: 'bg-blue-100' }
    if (score >= 60) return { grade: 'C', color: 'text-yellow-600', bg: 'bg-yellow-100' }
    if (score >= 50) return { grade: 'D', color: 'text-orange-600', bg: 'bg-orange-100' }
    return { grade: 'F', color: 'text-red-600', bg: 'bg-red-100' }
  }

  if (!isOpen || !reportData) return null

  // Show raw content if parsing failed
  if (reportData.parseError) {
    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className={`w-full max-w-4xl max-h-[95vh] overflow-hidden rounded-lg shadow-xl ${
          isDarkMode ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'
        }`}>
          <div className={`flex items-center justify-between p-6 border-b ${
            isDarkMode ? 'border-gray-700' : 'border-gray-200'
          }`}>
            <h2 className="text-2xl font-bold">Analysis Report</h2>
            <Button variant="ghost" size="sm" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
          <div className="p-6 overflow-y-auto max-h-[calc(95vh-120px)]">
            <div className={`p-4 rounded border font-mono text-sm whitespace-pre-line ${
              isDarkMode ? 'bg-gray-800 border-gray-600 text-gray-300' : 'bg-gray-50 border-gray-300 text-gray-700'
            }`}>
              {reportData.rawContent}
            </div>
          </div>
        </div>
      </div>
    )
  }

  const qualityGrade = getQualityGrade(reportData.qualityScore)

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className={`w-full max-w-7xl max-h-[95vh] overflow-hidden rounded-lg shadow-xl ${
        isDarkMode ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'
      }`}>
        {/* Header */}
        <div className={`flex items-center justify-between p-6 border-b ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <div>
            <h2 className="text-2xl font-bold">Code Quality Report</h2>
            <p className="text-sm opacity-75">{projectName || 'Project Analysis'}</p>
          </div>
          <Button variant="ghost" size="sm" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="p-6 overflow-y-auto max-h-[calc(95vh-120px)]">
          {/* Quality Score Overview */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div className={`p-6 rounded-lg text-center ${qualityGrade.bg}`}>
              <div className={`text-4xl font-bold ${qualityGrade.color}`}>{reportData.qualityScore}</div>
              <div className={`text-lg font-semibold ${qualityGrade.color}`}>Grade: {qualityGrade.grade}</div>
              <div className="text-sm opacity-75">
                {reportData.totalIssues === 0 ? 'Excellent Code!' : 'Quality Score'}
              </div>
            </div>
            
            <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <div className="flex items-center gap-2 mb-2">
                <FileText className="h-5 w-5 text-blue-500" />
                <span className="font-semibold">Files Analyzed</span>
              </div>
              <div className="text-2xl font-bold">{reportData.totalFiles}</div>
            </div>
            
            <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <div className="flex items-center gap-2 mb-2">
                <Bug className="h-5 w-5 text-red-500" />
                <span className="font-semibold">Total Issues</span>
              </div>
              <div className="text-2xl font-bold">{reportData.totalIssues}</div>
            </div>
            
            <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <div className="flex items-center gap-2 mb-2">
                <TrendingUp className="h-5 w-5 text-green-500" />
                <span className="font-semibold">Clean Files</span>
              </div>
              <div className="text-2xl font-bold">{Math.max(0, reportData.totalFiles - Object.keys(reportData.fileStats).length)}</div>
            </div>
          </div>

          {/* Issue Severity Breakdown */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
            <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <h3 className="text-lg font-semibold mb-4">Issue Severity Distribution</h3>
              <div className="space-y-3">
                {Object.entries(reportData.severityStats).map(([severity, count]) => (
                  <div key={severity} className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className={`w-4 h-4 rounded ${getSeverityColor(severity)}`}></div>
                      <span className="capitalize font-medium">{severity}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className={`w-32 h-2 rounded-full ${isDarkMode ? 'bg-gray-700' : 'bg-gray-200'}`}>
                        <div 
                          className={`h-full rounded-full ${getSeverityColor(severity)}`}
                          style={{ width: `${reportData.totalIssues > 0 ? (count / reportData.totalIssues) * 100 : 0}%` }}
                        ></div>
                      </div>
                      <span className="font-bold w-8 text-right">{count}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <h3 className="text-lg font-semibold mb-4">Issue Types</h3>
              <div className="space-y-2">
                {Object.entries(reportData.typeStats).slice(0, 6).map(([type, count]) => (
                  <div key={type} className="flex justify-between items-center">
                    <span className="text-sm">{type}</span>
                    <span className="font-bold">{count}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* File-wise Issues */}
          <div className={`p-6 rounded-lg mb-8 ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
            <h3 className="text-lg font-semibold mb-4">Files with Issues</h3>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className={`border-b ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
                    <th className="text-left py-2">File Name</th>
                    <th className="text-center py-2">Critical</th>
                    <th className="text-center py-2">High</th>
                    <th className="text-center py-2">Medium</th>
                    <th className="text-center py-2">Low</th>
                    <th className="text-center py-2">Total</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.keys(reportData.fileStats).length > 0 ? (
                    Object.entries(reportData.fileStats)
                      .sort(([,a], [,b]) => b.total - a.total)
                      .slice(0, 10)
                      .map(([file, stats]) => (
                      <tr key={file} className={`border-b ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
                        <td className="py-2 font-mono text-sm">{file}</td>
                        <td className="text-center py-2">
                          <span className={`px-2 py-1 rounded text-xs ${stats.critical > 0 ? 'bg-red-100 text-red-800' : 'text-gray-400'}`}>
                            {stats.critical}
                          </span>
                        </td>
                        <td className="text-center py-2">
                          <span className={`px-2 py-1 rounded text-xs ${stats.high > 0 ? 'bg-yellow-100 text-yellow-800' : 'text-gray-400'}`}>
                            {stats.high}
                          </span>
                        </td>
                        <td className="text-center py-2">
                          <span className={`px-2 py-1 rounded text-xs ${stats.medium > 0 ? 'bg-orange-100 text-orange-800' : 'text-gray-400'}`}>
                            {stats.medium}
                          </span>
                        </td>
                        <td className="text-center py-2">
                          <span className={`px-2 py-1 rounded text-xs ${stats.low > 0 ? 'bg-blue-100 text-blue-800' : 'text-gray-400'}`}>
                            {stats.low}
                          </span>
                        </td>
                        <td className="text-center py-2 font-bold">{stats.total}</td>
                      </tr>
                    ))
                  ) : reportData.totalIssues > 0 ? (
                    <tr>
                      <td colSpan="6" className="text-center py-8 text-gray-500">
                        <FileText className="h-8 w-8 mx-auto mb-2 opacity-50" />
                        <p>File-level breakdown not available</p>
                        <p className="text-sm">Issues detected but file mapping incomplete</p>
                      </td>
                    </tr>
                  ) : (
                    <tr>
                      <td colSpan="6" className="text-center py-8 text-green-600">
                        <CheckCircle className="h-8 w-8 mx-auto mb-2" />
                        <p>No files with issues found!</p>
                        <p className="text-sm">All analyzed files are clean</p>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Detailed Issues */}
          <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
            <h3 className="text-lg font-semibold mb-4">Critical & High Priority Issues</h3>
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {reportData.issues
                .filter(issue => issue.severity === 'critical' || issue.severity === 'high')
                .slice(0, 20)
                .map((issue, index) => (
                <div key={index} className={`p-4 rounded border-l-4 ${
                  issue.severity === 'critical' ? 'border-red-500 bg-red-50' : 'border-yellow-500 bg-yellow-50'
                } ${isDarkMode ? 'bg-opacity-10' : ''}`}>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className={`px-2 py-1 rounded text-xs font-medium ${
                          issue.severity === 'critical' ? 'bg-red-100 text-red-800' : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {issue.severity.toUpperCase()}
                        </span>
                        <span className="text-sm font-medium">{issue.type}</span>
                      </div>
                      <p className="text-sm mb-1">{issue.description}</p>
                      <p className="text-xs opacity-75">
                        <Code className="inline h-3 w-3 mr-1" />
                        {issue.file}:{issue.line}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Recommendations */}
          <div className={`p-6 rounded-lg mt-8 ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
            <h3 className="text-lg font-semibold mb-4">Recommendations</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <h4 className="font-medium text-green-600">✅ Immediate Actions</h4>
                <ul className="text-sm space-y-1">
                  {reportData.severityStats.critical > 0 && (
                    <li>• Fix {reportData.severityStats.critical} critical security/functionality issues</li>
                  )}
                  {reportData.severityStats.high > 5 && (
                    <li>• Address high-priority code quality issues</li>
                  )}
                  <li>• Review files with highest issue counts</li>
                </ul>
              </div>
              <div className="space-y-2">
                <h4 className="font-medium text-blue-600">🔄 Long-term Improvements</h4>
                <ul className="text-sm space-y-1">
                  <li>• Implement code review processes</li>
                  <li>• Add automated testing coverage</li>
                  <li>• Establish coding standards</li>
                  <li>• Regular code quality monitoring</li>
                </ul>
              </div>
            </div>
          </div>
          
          {/* No Issues Found - Celebration */}
          {reportData.totalIssues === 0 && (
            <div className={`p-8 rounded-lg mt-8 text-center ${isDarkMode ? 'bg-green-900 bg-opacity-20' : 'bg-green-50'}`}>
              <div className="text-6xl mb-4">🎉</div>
              <h3 className="text-2xl font-bold text-green-600 mb-2">Excellent Code Quality!</h3>
              <p className="text-lg mb-4">No code smells or issues detected in your project.</p>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
                <div className="text-center">
                  <CheckCircle className="h-8 w-8 text-green-500 mx-auto mb-2" />
                  <p className="font-medium">Clean Architecture</p>
                </div>
                <div className="text-center">
                  <CheckCircle className="h-8 w-8 text-green-500 mx-auto mb-2" />
                  <p className="font-medium">Best Practices</p>
                </div>
                <div className="text-center">
                  <CheckCircle className="h-8 w-8 text-green-500 mx-auto mb-2" />
                  <p className="font-medium">Maintainable Code</p>
                </div>
              </div>
            </div>
          )}
          
          {/* Raw Report Fallback - Show if parsing failed but content exists */}
          {reportData.issues.length === 0 && reportData.totalIssues > 0 && (
            <div className={`p-6 rounded-lg mt-8 ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
              <h3 className="text-lg font-semibold mb-4">Raw Analysis Report</h3>
              <p className="text-sm mb-4 text-yellow-600">Visual parsing incomplete. Showing raw report content:</p>
              <div className={`p-4 rounded border font-mono text-sm whitespace-pre-line max-h-96 overflow-y-auto ${
                isDarkMode ? 'bg-gray-900 border-gray-600 text-gray-300' : 'bg-white border-gray-300 text-gray-700'
              }`}>
                {reportData.rawContent}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}