import React, { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { ArrowLeft, AlertCircle, Loader2 } from 'lucide-react'
import { Button } from '../components/ui/button'
import api from '../api'

export default function FileViewer() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [fileContent, setFileContent] = useState('')
  const [highlights, setHighlights] = useState({})
  const [issues, setIssues] = useState([])
  const [activeSmell, setActiveSmell] = useState('all')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isDarkMode, setIsDarkMode] = useState(true)
  const [expandedIssue, setExpandedIssue] = useState(null)
  const [aiRefactoring, setAiRefactoring] = useState({})
  const [loadingAi, setLoadingAi] = useState({})
  const [showWhyReason, setShowWhyReason] = useState({})

  const projectPath = searchParams.get('project')
  const fileName = searchParams.get('file')
  const userId = localStorage.getItem('userId')

  const handleBack = () => {
    const storedData = sessionStorage.getItem('returnToReport')
    if (storedData) {
      navigate('/home', { state: { openReport: true, reportData: JSON.parse(storedData) } })
    } else {
      navigate('/home')
    }
  }

  useEffect(() => {
    if (!projectPath || !fileName || !userId) {
      setError('Missing required parameters')
      setLoading(false)
      return
    }
    fetchData()
  }, [projectPath, fileName, userId])

  const fetchData = async () => {
    try {
      setLoading(true)
      
      const contentRes = await api.get('/fileview/content', {
        params: { projectPath, fileName, userId }
      })
      setFileContent(contentRes.data.content)

      const highlightsRes = await api.get('/fileview/highlights', {
        params: { projectPath, userId }
      })
      setHighlights(highlightsRes.data[fileName] || {})

      const issuesRes = await api.get('/fileview/issues', {
        params: { projectPath, fileName, userId }
      })
      console.log('Issues loaded:', issuesRes.data)
      setIssues(issuesRes.data)

      setLoading(false)
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load file')
      setLoading(false)
    }
  }

  const getHighlightedLines = () => {
    if (activeSmell === 'all') {
      return Object.values(highlights).flat()
    }
    return highlights[activeSmell] || []
  }

  const getLineProps = (lineNumber) => {
    const highlightedLines = getHighlightedLines()
    if (!highlightedLines.includes(lineNumber)) return {}

    const lineIssues = issues.filter(issue => issue.line === lineNumber)
    const severity = lineIssues[0]?.severity || 'Low'
    
    const colors = {
      Critical: isDarkMode ? 'rgba(239, 68, 68, 0.2)' : 'rgba(239, 68, 68, 0.15)',
      High: isDarkMode ? 'rgba(234, 179, 8, 0.2)' : 'rgba(234, 179, 8, 0.15)',
      Medium: isDarkMode ? 'rgba(249, 115, 22, 0.2)' : 'rgba(249, 115, 22, 0.15)',
      Low: isDarkMode ? 'rgba(156, 163, 175, 0.2)' : 'rgba(156, 163, 175, 0.15)'
    }

    return {
      style: {
        backgroundColor: colors[severity],
        display: 'block',
        width: '100%'
      }
    }
  }

  const getSeverityIcon = (severity) => {
    const icons = {
      Critical: '🔴',
      High: '🟡',
      Medium: '🟠',
      Low: '⚪'
    }
    return icons[severity] || '⚪'
  }

  const extractCodeChunk = (lineNumber, smellType) => {
    const lines = fileContent.split('\n')
    let startLine = lineNumber - 1
    let endLine = lineNumber - 1

    if (smellType === 'LongMethod') {
      for (let i = startLine; i >= 0; i--) {
        if (lines[i].match(/\b(public|private|protected)\s+(static\s+)?\w+\s+\w+\s*\(/)) {
          startLine = i
          break
        }
      }
      let braceCount = 0
      for (let i = startLine; i < lines.length; i++) {
        braceCount += (lines[i].match(/\{/g) || []).length
        braceCount -= (lines[i].match(/\}/g) || []).length
        if (braceCount === 0 && i > startLine) {
          endLine = i
          break
        }
      }
    } else {
      startLine = Math.max(0, lineNumber - 6)
      endLine = Math.min(lines.length - 1, lineNumber + 4)
    }

    return {
      code: lines.slice(startLine, endLine + 1).join('\n'),
      startLine: startLine + 1,
      endLine: endLine + 1
    }
  }

  const handleAiRefactor = async (issue, idx) => {
    if (expandedIssue === idx && aiRefactoring[idx]) {
      setExpandedIssue(null)
      return
    }

    setExpandedIssue(idx)
    
    if (aiRefactoring[idx]) return

    setLoadingAi({ ...loadingAi, [idx]: true })

    try {
      const { code, startLine, endLine } = extractCodeChunk(issue.line, issue.type)
      
      const response = await api.post('/ai/refactor', {
        smellType: issue.type,
        fileName: fileName,
        startLine,
        endLine,
        code,
        message: issue.message
      })

      setAiRefactoring({ ...aiRefactoring, [idx]: response.data })
    } catch (err) {
      setAiRefactoring({ 
        ...aiRefactoring, 
        [idx]: { 
          error: 'Failed to get AI refactoring: ' + (err.response?.data?.error || err.message) 
        } 
      })
    } finally {
      setLoadingAi({ ...loadingAi, [idx]: false })
    }
  }

  if (loading) {
    return (
      <div className={`min-h-screen flex items-center justify-center ${
        isDarkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-900'
      }`}>
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  if (error) {
    return (
      <div className={`min-h-screen flex items-center justify-center ${
        isDarkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-900'
      }`}>
        <div className="text-center">
          <AlertCircle className="h-12 w-12 mx-auto mb-4 text-red-500" />
          <h2 className="text-xl font-semibold mb-2">Error Loading File</h2>
          <p className="text-gray-500 mb-4">{error}</p>
          <Button onClick={() => navigate(-1)}>Go Back</Button>
        </div>
      </div>
    )
  }

  const smellTypes = Object.keys(highlights)

  return (
    <div className={`min-h-screen ${
      isDarkMode ? 'bg-gray-900 text-white' : 'bg-gray-50 text-gray-900'
    }`}>
      <div className={`sticky top-0 z-10 border-b ${
        isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-white border-gray-200'
      }`}>
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button
                variant="ghost"
                size="sm"
                onClick={handleBack}
                className={isDarkMode ? 'text-gray-300 hover:text-white' : ''}
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Report
              </Button>
              <div>
                <h1 className="text-xl font-semibold">{fileName}</h1>
                <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                  {issues.length} issue{issues.length !== 1 ? 's' : ''} found
                </p>
              </div>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setIsDarkMode(!isDarkMode)}
            >
              {isDarkMode ? '☀️ Light' : '🌙 Dark'}
            </Button>
          </div>
        </div>
      </div>

      <div className={`border-b ${
        isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-white border-gray-200'
      }`}>
        <div className="container mx-auto px-6 py-3">
          <div className="flex gap-2 overflow-x-auto">
            <button
              onClick={() => setActiveSmell('all')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap ${
                activeSmell === 'all'
                  ? isDarkMode
                    ? 'bg-blue-600 text-white'
                    : 'bg-blue-500 text-white'
                  : isDarkMode
                    ? 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              All Issues ({Object.values(highlights).flat().length})
            </button>
            {smellTypes.map(smell => (
              <button
                key={smell}
                onClick={() => setActiveSmell(smell)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap ${
                  activeSmell === smell
                    ? isDarkMode
                      ? 'bg-blue-600 text-white'
                      : 'bg-blue-500 text-white'
                    : isDarkMode
                      ? 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {smell} ({highlights[smell]?.length || 0})
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="container mx-auto px-6 py-6">
        <div className={`rounded-lg overflow-hidden border ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <SyntaxHighlighter
            language="java"
            style={isDarkMode ? vscDarkPlus : vs}
            showLineNumbers={true}
            wrapLines={true}
            lineProps={lineNumber => getLineProps(lineNumber)}
            customStyle={{
              margin: 0,
              padding: '1rem',
              fontSize: '14px',
              backgroundColor: isDarkMode ? '#1e1e1e' : '#ffffff'
            }}
          >
            {fileContent}
          </SyntaxHighlighter>
        </div>

        {issues.length > 0 && (
          <div className={`mt-6 rounded-lg border p-6 ${
            isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-white border-gray-200'
          }`}>
            <h2 className="text-lg font-semibold mb-4">Issues in this file</h2>
            <div className="space-y-3">
              {issues
                .filter(issue => activeSmell === 'all' || issue.type === activeSmell)
                .map((issue, idx) => (
                  <div
                    key={idx}
                    className={`p-4 rounded-lg border ${
                      isDarkMode ? 'bg-gray-700 border-gray-600' : 'bg-gray-50 border-gray-200'
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      <span className="text-2xl">{getSeverityIcon(issue.severity)}</span>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <span className={`px-2 py-1 rounded text-xs font-medium ${
                            isDarkMode ? 'bg-gray-600 text-gray-200' : 'bg-gray-200 text-gray-700'
                          }`}>
                            {issue.type}
                          </span>
                          <span className={`text-sm ${
                            isDarkMode ? 'text-gray-400' : 'text-gray-600'
                          }`}>
                            Line {issue.line}
                          </span>
                        </div>
                        <p className={`text-sm mb-2 ${
                          isDarkMode ? 'text-gray-300' : 'text-gray-700'
                        }`}>
                          {issue.message}
                        </p>
                        {issue.suggestion && (
                          <p className={`text-sm mb-3 ${
                            isDarkMode ? 'text-blue-400' : 'text-blue-600'
                          }`}>
                            💡 {issue.suggestion}
                          </p>
                        )}
                        
                        <div className="flex gap-2">
                          <button
                            onClick={() => setShowWhyReason({ ...showWhyReason, [idx]: !showWhyReason[idx] })}
                            className={`px-3 py-1.5 rounded text-xs font-medium transition-colors ${
                              isDarkMode
                                ? 'bg-blue-600 hover:bg-blue-700 text-white'
                                : 'bg-blue-500 hover:bg-blue-600 text-white'
                            }`}
                          >
                            {showWhyReason[idx] ? '❌ Close' : '❓ Why?'}
                          </button>
                          
                          <button
                            onClick={() => handleAiRefactor(issue, idx)}
                            disabled={loadingAi[idx]}
                            className={`px-3 py-1.5 rounded text-xs font-medium transition-colors ${
                              isDarkMode
                                ? 'bg-purple-600 hover:bg-purple-700 text-white'
                                : 'bg-purple-500 hover:bg-purple-600 text-white'
                            } disabled:opacity-50 disabled:cursor-not-allowed`}
                          >
                            {loadingAi[idx] ? '🔄 Loading...' : '🤖 AI Refactored Code'}
                          </button>
                        </div>

                        {showWhyReason[idx] && (
                          <div className={`mt-4 p-4 rounded-lg border ${
                            isDarkMode ? 'bg-gray-800 border-gray-600' : 'bg-blue-50 border-blue-200'
                          }`}>
                            <h4 className="font-semibold mb-3 text-blue-500 flex items-center gap-2">
                              <span>❓</span>
                              <span>Why is this a code smell?</span>
                            </h4>
                            
                            {issue.type === 'LongMethod' && issue.thresholdDetails ? (
                              <div className="space-y-3">
                                <div className={`grid grid-cols-1 md:grid-cols-2 gap-3`}>
                                  <div className={`p-3 rounded-lg ${
                                    issue.thresholdDetails.exceedsStatementCount
                                      ? isDarkMode ? 'bg-red-900/30 border border-red-700' : 'bg-red-50 border border-red-300'
                                      : isDarkMode ? 'bg-green-900/30 border border-green-700' : 'bg-green-50 border border-green-300'
                                  }`}>
                                    <div className="flex items-center justify-between mb-1">
                                      <span className="text-xs font-semibold uppercase tracking-wide">
                                        {issue.thresholdDetails.exceedsStatementCount ? '❌' : '✅'} Statement Count
                                      </span>
                                    </div>
                                    <div className="text-sm">
                                      <span className="font-bold text-lg">{issue.thresholdDetails.statementCount}</span>
                                      <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}> / {issue.thresholdDetails.baseThreshold}</span>
                                    </div>
                                    <div className={`text-xs mt-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                                      Base: {issue.thresholdDetails.baseThreshold}, Critical: {issue.thresholdDetails.criticalThreshold}
                                    </div>
                                  </div>

                                  <div className={`p-3 rounded-lg ${
                                    issue.thresholdDetails.exceedsCyclomaticComplexity
                                      ? isDarkMode ? 'bg-red-900/30 border border-red-700' : 'bg-red-50 border border-red-300'
                                      : isDarkMode ? 'bg-green-900/30 border border-green-700' : 'bg-green-50 border border-green-300'
                                  }`}>
                                    <div className="flex items-center justify-between mb-1">
                                      <span className="text-xs font-semibold uppercase tracking-wide">
                                        {issue.thresholdDetails.exceedsCyclomaticComplexity ? '❌' : '✅'} Cyclomatic Complexity
                                      </span>
                                    </div>
                                    <div className="text-sm">
                                      <span className="font-bold text-lg">{issue.thresholdDetails.cyclomaticComplexity}</span>
                                      <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}> / {issue.thresholdDetails.maxCyclomaticComplexity}</span>
                                    </div>
                                    <div className={`text-xs mt-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                                      Decision points (if/for/while)
                                    </div>
                                  </div>

                                  <div className={`p-3 rounded-lg ${
                                    issue.thresholdDetails.exceedsCognitiveComplexity
                                      ? isDarkMode ? 'bg-red-900/30 border border-red-700' : 'bg-red-50 border border-red-300'
                                      : isDarkMode ? 'bg-green-900/30 border border-green-700' : 'bg-green-50 border border-green-300'
                                  }`}>
                                    <div className="flex items-center justify-between mb-1">
                                      <span className="text-xs font-semibold uppercase tracking-wide">
                                        {issue.thresholdDetails.exceedsCognitiveComplexity ? '❌' : '✅'} Cognitive Complexity
                                      </span>
                                    </div>
                                    <div className="text-sm">
                                      <span className="font-bold text-lg">{issue.thresholdDetails.cognitiveComplexity}</span>
                                      <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}> / {issue.thresholdDetails.maxCognitiveComplexity}</span>
                                    </div>
                                    <div className={`text-xs mt-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                                      How hard to understand
                                    </div>
                                  </div>

                                  <div className={`p-3 rounded-lg ${
                                    issue.thresholdDetails.exceedsNestingDepth
                                      ? isDarkMode ? 'bg-red-900/30 border border-red-700' : 'bg-red-50 border border-red-300'
                                      : isDarkMode ? 'bg-green-900/30 border border-green-700' : 'bg-green-50 border border-green-300'
                                  }`}>
                                    <div className="flex items-center justify-between mb-1">
                                      <span className="text-xs font-semibold uppercase tracking-wide">
                                        {issue.thresholdDetails.exceedsNestingDepth ? '❌' : '✅'} Nesting Depth
                                      </span>
                                    </div>
                                    <div className="text-sm">
                                      <span className="font-bold text-lg">{issue.thresholdDetails.nestingDepth}</span>
                                      <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}> / {issue.thresholdDetails.maxNestingDepth}</span>
                                    </div>
                                    <div className={`text-xs mt-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                                      Levels of nested blocks
                                    </div>
                                  </div>

                                  <div className={`p-3 rounded-lg md:col-span-2 ${
                                    issue.thresholdDetails.exceedsResponsibilityCount
                                      ? isDarkMode ? 'bg-red-900/30 border border-red-700' : 'bg-red-50 border border-red-300'
                                      : isDarkMode ? 'bg-green-900/30 border border-green-700' : 'bg-green-50 border border-green-300'
                                  }`}>
                                    <div className="flex items-center justify-between mb-1">
                                      <span className="text-xs font-semibold uppercase tracking-wide">
                                        {issue.thresholdDetails.exceedsResponsibilityCount ? '❌' : '✅'} Responsibility Count
                                      </span>
                                    </div>
                                    <div className="text-sm">
                                      <span className="font-bold text-lg">{issue.thresholdDetails.responsibilityCount}</span>
                                      <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}> / {issue.thresholdDetails.maxResponsibilityCount}</span>
                                    </div>
                                    <div className={`text-xs mt-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                                      Single Responsibility Principle
                                    </div>
                                  </div>
                                </div>
                                
                                <div className={`p-3 rounded-lg text-center ${
                                  isDarkMode ? 'bg-blue-900/30 border border-blue-700' : 'bg-blue-100 border border-blue-300'
                                }`}>
                                  <p className={`text-sm font-medium ${isDarkMode ? 'text-blue-300' : 'text-blue-700'}`}>
                                    💡 {issue.thresholdDetails.summary}
                                  </p>
                                </div>
                              </div>
                            ) : (
                              <p className={`text-sm leading-relaxed ${
                                isDarkMode ? 'text-gray-300' : 'text-gray-700'
                              }`}>
                                {issue.detailedReason || 'Detailed explanation not available. Please re-analyze the project to generate detailed reasons.'}
                              </p>
                            )}
                          </div>
                        )}

                        {expandedIssue === idx && aiRefactoring[idx] && (
                          <div className={`mt-4 p-4 rounded-lg border max-w-full overflow-hidden ${
                            isDarkMode ? 'bg-gray-800 border-gray-600' : 'bg-white border-gray-300'
                          }`}>
                            {aiRefactoring[idx].error ? (
                              <p className="text-red-500 text-sm">{aiRefactoring[idx].error}</p>
                            ) : (
                              <>
                                <h4 className="font-semibold mb-2 text-green-500">✨ Refactored Code</h4>
                                <div className="rounded mb-3 overflow-hidden max-w-full">
                                  <SyntaxHighlighter
                                    language="java"
                                    style={isDarkMode ? vscDarkPlus : vs}
                                    showLineNumbers={true}
                                    customStyle={{
                                      margin: 0,
                                      fontSize: '12px',
                                      maxWidth: '100%'
                                    }}
                                  >
                                    {aiRefactoring[idx].refactoredCode}
                                  </SyntaxHighlighter>
                                </div>
                                
                                <h4 className="font-semibold mb-1 text-blue-500">📝 Explanation</h4>
                                <p className={`text-sm mb-3 break-words ${
                                  isDarkMode ? 'text-gray-300' : 'text-gray-700'
                                }`}>
                                  {aiRefactoring[idx].explanation}
                                </p>
                                
                                <h4 className="font-semibold mb-1 text-yellow-500">🎯 How Smell is Removed</h4>
                                <p className={`text-sm break-words ${
                                  isDarkMode ? 'text-gray-300' : 'text-gray-700'
                                }`}>
                                  {aiRefactoring[idx].howRemoved}
                                </p>
                              </>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
