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

  const projectPath = searchParams.get('project')
  const fileName = searchParams.get('file')
  const returnTo = searchParams.get('returnTo')
  const userId = localStorage.getItem('userId')

  const handleBack = () => {
    // Navigate back to home/dashboard and trigger report modal to reopen
    navigate('/home', { state: { openReport: true, projectPath, reportContent: null } })
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
      
      // Fetch file content
      const contentRes = await api.get('/fileview/content', {
        params: { projectPath, fileName, userId }
      })
      setFileContent(contentRes.data.content)

      // Fetch highlights
      const highlightsRes = await api.get('/fileview/highlights', {
        params: { projectPath, userId }
      })
      setHighlights(highlightsRes.data[fileName] || {})

      // Fetch issues
      const issuesRes = await api.get('/fileview/issues', {
        params: { projectPath, fileName, userId }
      })
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
      {/* Header */}
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

      {/* Smell Type Tabs */}
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

      {/* Code Display */}
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

        {/* Issues List */}
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
                          <p className={`text-sm ${
                            isDarkMode ? 'text-blue-400' : 'text-blue-600'
                          }`}>
                            💡 {issue.suggestion}
                          </p>
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
