import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { X, AlertTriangle, CheckCircle, TrendingUp, FileText, Code, Bug, Download, Filter, Search, ChevronDown, ChevronUp } from 'lucide-react'
import { Button } from './ui/button'

export function EnhancedVisualReport({ reportContent, isOpen, onClose, isDarkMode, projectName, projectPath }) {
  const [reportData, setReportData] = useState(null)
  const [selectedSeverity, setSelectedSeverity] = useState('all')
  const [selectedType, setSelectedType] = useState('all')
  const [searchTerm, setSearchTerm] = useState('')
  const [expandedSections, setExpandedSections] = useState({
    overview: true,
    severity: true,
    files: true,
    issues: true
  })
  const navigate = useNavigate()

  useEffect(() => {
    if (reportContent && isOpen) {
      parseReportContent(reportContent)
    }
  }, [reportContent, isOpen])

  const parseReportContent = (content) => {
    try {
      console.log('RAW REPORT CONTENT (first 500 chars):', content.substring(0, 500))
      const lines = content.split('\n')
      const issues = []
      const fileStats = {}
      const typeStats = {}
      let totalFiles = 0
      let totalIssues = 0
      let currentSection = ''
      let parsedIssueCount = 0

      lines.forEach(line => {
        if (line.startsWith('ISSUE TYPE BREAKDOWN')) {
          currentSection = 'types'
          return
        }
        if (line.startsWith('FILE-WISE BREAKDOWN')) {
          currentSection = 'files'
          return
        }
        if (line.startsWith('DETAILED ISSUES')) {
          currentSection = 'issues'
          return
        }

        if (currentSection === 'types' && line.includes(':') && !line.startsWith('-') && line.trim() !== '') {
          const match = line.match(/^([\w\s]+?)\s*:\s*(\d+)$/)
          if (match) {
            const [, type, count] = match
            typeStats[type.trim()] = parseInt(count)
          }
        }

        if (currentSection === 'files' && line.startsWith('File: ')) {
          const fileMatch = line.match(/File: (.+?) \(Total: (\d+)\)/)
          if (fileMatch) {
            const [, fileName, total] = fileMatch
            let cleanFileName = fileName
            if (fileName.includes('/')) cleanFileName = fileName.split('/').pop()
            if (fileName.includes('\\')) cleanFileName = fileName.split('\\').pop()
            
            if (!fileStats[cleanFileName]) {
              fileStats[cleanFileName] = { critical: 0, high: 0, medium: 0, low: 0, total: parseInt(total) }
            }
          }
        }

        if (currentSection === 'files' && line.trim().includes(':') && line.startsWith('  ')) {
          const severityMatch = line.match(/^\s+(\w+)\s*:\s*(\d+)$/)
          if (severityMatch) {
            const [, severity, count] = severityMatch
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

        if (line.startsWith('🚨 ')) {
          parsedIssueCount++
          const cleanLine = line.substring(2).trim()
          console.log(`Parsing issue ${parsedIssueCount}:`, cleanLine.substring(0, 100))
          
          // Extract severity emoji first
          let severityEmoji = 'medium'
          let contentAfterEmoji = cleanLine
          
          if (cleanLine.startsWith('🔴')) {
            severityEmoji = 'critical'
            contentAfterEmoji = cleanLine.substring(1).trim()
          } else if (cleanLine.startsWith('🟡')) {
            severityEmoji = 'high'
            contentAfterEmoji = cleanLine.substring(1).trim()
          } else if (cleanLine.startsWith('🟠')) {
            severityEmoji = 'medium'
            contentAfterEmoji = cleanLine.substring(1).trim()
          } else if (cleanLine.startsWith('⚠️')) {
            severityEmoji = 'low'
            contentAfterEmoji = cleanLine.substring(2).trim()
          }
          
          // Now parse the rest: [Type] file:line - description
          const issueMatch = contentAfterEmoji.match(/\[(\w+)\]\s+(.+?):(\d+)\s+-\s+(.+)/)
          
          if (issueMatch) {
            const [, type, file, lineNum, description] = issueMatch
            let fileName = file
            if (file.includes('/')) fileName = file.split('/').pop()
            if (file.includes('\\')) fileName = file.split('\\').pop()
            if (fileName === 'UnknownFile') fileName = 'Unknown'
            
            let cleanDescription = description.split('|')[0].split('[Score:')[0].split('[Risk:')[0].trim()
            
            const issue = {
              severity: severityEmoji,
              type,
              file: fileName,
              line: parseInt(lineNum),
              description: cleanDescription
            }
            console.log('Parsed issue:', issue)
            issues.push(issue)
            totalIssues++
          } else {
            console.warn('Failed to parse issue line:', cleanLine)
          }
        }

        if (line.includes('Analyzed') && line.includes('files, found')) {
          let match = line.match(/Analyzed (\d+) files, found (\d+) issues/)
          if (match) {
            totalFiles = parseInt(match[1])
            if (totalIssues === 0) totalIssues = parseInt(match[2])
          }
        }

        if (line.includes('🎉 No issues found')) {
          totalIssues = 0
        }
      })

      let severityStats = {
        critical: issues.filter(i => i.severity === 'critical').length,
        high: issues.filter(i => i.severity === 'high').length,
        medium: issues.filter(i => i.severity === 'medium').length,
        low: issues.filter(i => i.severity === 'low').length
      }

      if (issues.length > totalIssues) {
        totalIssues = issues.length
      }

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
          totalIssues = Object.values(severityStats).reduce((a, b) => a + b, 0)
        }
      }

      if (Object.keys(typeStats).length === 0 && issues.length > 0) {
        issues.forEach(issue => {
          typeStats[issue.type] = (typeStats[issue.type] || 0) + 1
        })
      }

      if (Object.keys(fileStats).length === 0 && issues.length > 0) {
        issues.forEach(issue => {
          if (!fileStats[issue.file]) {
            fileStats[issue.file] = { critical: 0, high: 0, medium: 0, low: 0, total: 0 }
          }
          fileStats[issue.file][issue.severity]++
          fileStats[issue.file].total++
        })
      }

      if (totalFiles === 0 && Object.keys(fileStats).length > 0) {
        totalFiles = Object.keys(fileStats).length
      }

      console.log('PARSED REPORT DATA:', {
        issuesCount: issues.length,
        totalIssues,
        totalFiles,
        severityStats,
        sampleIssue: issues[0]
      })

      setReportData({
        issues,
        fileStats,
        severityStats,
        typeStats,
        totalFiles,
        totalIssues,
        qualityScore: calculateQualityScore(severityStats, totalFiles),
        rawContent: content
      })
    } catch (error) {
      console.error('Error parsing report content:', error)
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
    if (totalIssues === 0) return 100
    if (totalFiles === 0) return totalIssues === 0 ? 100 : 50
    
    const issueWeight = (stats.critical * 10) + (stats.high * 6) + (stats.medium * 3) + (stats.low * 1)
    const issuesPerFile = totalIssues / totalFiles
    const baseScore = Math.max(0, 100 - (issuesPerFile * 15))
    const severityPenalty = Math.min(50, issueWeight * 2)
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

  const handleFileClick = (fileName) => {
    if (!projectPath) {
      alert('Project path is missing. Please re-analyze the project.')
      return
    }
    
    sessionStorage.setItem('returnToReport', JSON.stringify({
      reportContent,
      projectName,
      projectPath
    }))
    
    onClose()
    const url = `/fileviewer?project=${encodeURIComponent(projectPath)}&file=${encodeURIComponent(fileName)}`
    navigate(url)
  }

  const toggleSection = (section) => {
    setExpandedSections(prev => ({ ...prev, [section]: !prev[section] }))
  }

  const filteredIssues = reportData?.issues ? reportData.issues.filter(issue => {
    const matchesSeverity = selectedSeverity === 'all' || issue.severity === selectedSeverity
    const matchesType = selectedType === 'all' || issue.type === selectedType
    const matchesSearch = searchTerm === '' || 
      issue.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      issue.file.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesSeverity && matchesType && matchesSearch
  }) : []

  if (!isOpen || !reportData) return null

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
          <div className="flex gap-2">
            <Button variant="outline" size="sm">
              <Download className="h-4 w-4 mr-2" />
              Export
            </Button>
            <Button variant="ghost" size="sm" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </div>

        <div className="p-6 overflow-y-auto max-h-[calc(95vh-120px)]">
          {/* Quality Score Overview - Collapsible */}
          <div className={`mb-6 rounded-lg border ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
            <button
              onClick={() => toggleSection('overview')}
              className={`w-full flex items-center justify-between p-4 ${
                isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-50'
              }`}
            >
              <h3 className="text-lg font-semibold">Overview</h3>
              {expandedSections.overview ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
            </button>
            
            {expandedSections.overview && (
              <div className="p-4 pt-0">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                  <div className={`p-6 rounded-lg text-center ${qualityGrade.bg}`}>
                    <div className={`text-4xl font-bold ${qualityGrade.color}`}>{reportData.qualityScore}</div>
                    <div className={`text-lg font-semibold ${qualityGrade.color}`}>Grade: {qualityGrade.grade}</div>
                    <div className="text-sm opacity-75">Quality Score</div>
                  </div>
                  
                  <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <FileText className="h-5 w-5 text-blue-500" />
                      <span className="font-semibold">Files</span>
                    </div>
                    <div className="text-2xl font-bold">{reportData.totalFiles}</div>
                  </div>
                  
                  <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <Bug className="h-5 w-5 text-red-500" />
                      <span className="font-semibold">Issues</span>
                    </div>
                    <div className="text-2xl font-bold">{reportData.totalIssues}</div>
                  </div>
                  
                  <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                    <div className="flex items-center gap-2 mb-2">
                      <TrendingUp className="h-5 w-5 text-green-500" />
                      <span className="font-semibold">Clean</span>
                    </div>
                    <div className="text-2xl font-bold">{Math.max(0, reportData.totalFiles - Object.keys(reportData.fileStats).length)}</div>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Severity Distribution - Collapsible */}
          <div className={`mb-6 rounded-lg border ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
            <button
              onClick={() => toggleSection('severity')}
              className={`w-full flex items-center justify-between p-4 ${
                isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-50'
              }`}
            >
              <h3 className="text-lg font-semibold">Severity Distribution</h3>
              {expandedSections.severity ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
            </button>
            
            {expandedSections.severity && (
              <div className="p-4 pt-0">
                <div className="space-y-3">
                  {Object.entries(reportData.severityStats).map(([severity, count]) => (
                    <div key={severity} className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className={`w-4 h-4 rounded ${getSeverityColor(severity)}`}></div>
                        <span className="capitalize font-medium">{severity}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className={`w-48 h-2 rounded-full ${isDarkMode ? 'bg-gray-700' : 'bg-gray-200'}`}>
                          <div 
                            className={`h-full rounded-full ${getSeverityColor(severity)}`}
                            style={{ width: `${reportData.totalIssues > 0 ? (count / reportData.totalIssues) * 100 : 0}%` }}
                          ></div>
                        </div>
                        <span className="font-bold w-12 text-right">{count}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Files with Issues - Collapsible */}
          <div className={`mb-6 rounded-lg border ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
            <button
              onClick={() => toggleSection('files')}
              className={`w-full flex items-center justify-between p-4 ${
                isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-50'
              }`}
            >
              <h3 className="text-lg font-semibold">Files with Issues ({Object.keys(reportData.fileStats).length})</h3>
              {expandedSections.files ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
            </button>
            
            {expandedSections.files && (
              <div className="p-4 pt-0 overflow-x-auto">
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
                    {Object.entries(reportData.fileStats)
                      .sort(([,a], [,b]) => b.total - a.total)
                      .map(([file, stats]) => (
                      <tr key={file} className={`border-b ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
                        <td className="py-2 font-mono text-sm">
                          <button
                            onClick={() => handleFileClick(file)}
                            className="text-blue-500 hover:text-blue-600 hover:underline cursor-pointer text-left"
                          >
                            {file}
                          </button>
                        </td>
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
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Detailed Issues - Collapsible with Filters */}
          <div className={`mb-6 rounded-lg border ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
            <button
              onClick={() => toggleSection('issues')}
              className={`w-full flex items-center justify-between p-4 ${
                isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-50'
              }`}
            >
              <h3 className="text-lg font-semibold">Detailed Issues ({filteredIssues.length})</h3>
              {expandedSections.issues ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
            </button>
            
            {expandedSections.issues && (
              <div className="p-4 pt-0">
                {/* Debug Info */}
                {reportData.issues.length === 0 && reportData.totalIssues > 0 && (
                  <div className={`mb-4 p-3 rounded border ${isDarkMode ? 'bg-yellow-900/20 border-yellow-700' : 'bg-yellow-50 border-yellow-200'}`}>
                    <p className="text-sm text-yellow-600">⚠️ Issues detected but detailed parsing incomplete. Check console for details.</p>
                  </div>
                )}
                
                {/* Filters */}
                <div className="flex gap-4 mb-4 flex-wrap">
                  <div className="flex items-center gap-2">
                    <Filter className="h-4 w-4" />
                    <select
                      value={selectedSeverity}
                      onChange={(e) => setSelectedSeverity(e.target.value)}
                      className={`px-3 py-1 rounded border ${
                        isDarkMode ? 'bg-gray-800 border-gray-600' : 'bg-white border-gray-300'
                      }`}
                    >
                      <option value="all">All Severities</option>
                      <option value="critical">Critical</option>
                      <option value="high">High</option>
                      <option value="medium">Medium</option>
                      <option value="low">Low</option>
                    </select>
                  </div>
                  
                  <div className="flex items-center gap-2">
                    <select
                      value={selectedType}
                      onChange={(e) => setSelectedType(e.target.value)}
                      className={`px-3 py-1 rounded border ${
                        isDarkMode ? 'bg-gray-800 border-gray-600' : 'bg-white border-gray-300'
                      }`}
                    >
                      <option value="all">All Types</option>
                      {Object.keys(reportData.typeStats).map(type => (
                        <option key={type} value={type}>{type}</option>
                      ))}
                    </select>
                  </div>
                  
                  <div className="flex-1 flex items-center gap-2">
                    <Search className="h-4 w-4" />
                    <input
                      type="text"
                      placeholder="Search issues..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      className={`flex-1 px-3 py-1 rounded border ${
                        isDarkMode ? 'bg-gray-800 border-gray-600' : 'bg-white border-gray-300'
                      }`}
                    />
                  </div>
                </div>

                {/* Issues List */}
                <div className="space-y-3 max-h-96 overflow-y-auto">
                  {reportData.totalIssues === 0 ? (
                    <div className="text-center py-8 text-green-600">
                      <CheckCircle className="h-8 w-8 mx-auto mb-2" />
                      <p>No issues found in your code!</p>
                    </div>
                  ) : filteredIssues.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <CheckCircle className="h-8 w-8 mx-auto mb-2 opacity-50" />
                      <p>No issues match your filters</p>
                      <p className="text-xs mt-2">Try adjusting your search or filter criteria</p>
                    </div>
                  ) : (
                    filteredIssues.map((issue, index) => (
                      <div key={index} className={`p-4 rounded border-l-4 ${
                        issue.severity === 'critical' ? 'border-red-500 bg-red-50' :
                        issue.severity === 'high' ? 'border-yellow-500 bg-yellow-50' :
                        issue.severity === 'medium' ? 'border-orange-500 bg-orange-50' :
                        'border-blue-500 bg-blue-50'
                      } ${isDarkMode ? 'bg-opacity-10' : ''}`}>
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <span className={`px-2 py-1 rounded text-xs font-medium ${
                                issue.severity === 'critical' ? 'bg-red-100 text-red-800' :
                                issue.severity === 'high' ? 'bg-yellow-100 text-yellow-800' :
                                issue.severity === 'medium' ? 'bg-orange-100 text-orange-800' :
                                'bg-blue-100 text-blue-800'
                              }`}>
                                {issue.severity.toUpperCase()}
                              </span>
                              <span className="text-sm font-medium">{issue.type}</span>
                            </div>
                            <p className="text-sm mb-1">{issue.description}</p>
                            <button
                              onClick={() => handleFileClick(issue.file)}
                              className="text-xs text-blue-500 hover:underline flex items-center gap-1"
                            >
                              <Code className="inline h-3 w-3" />
                              {issue.file}:{issue.line}
                            </button>
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
