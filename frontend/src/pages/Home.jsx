import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Sidebar } from '../components/Sidebar'
import { Header } from '../components/Header'
import { UploadArea } from '../components/UploadArea'
import { History } from '../components/History'
import { Toaster } from '../components/ui/sonner'
import { toast } from 'sonner'
import api from '../api'

export default function Home() {
  const navigate = useNavigate()
  const [showResults, setShowResults] = useState(false)
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [historyOpen, setHistoryOpen] = useState(false)
  const [isAnalyzing, setIsAnalyzing] = useState(false)
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [analysisResults, setAnalysisResults] = useState(null)
  const [showReportModal, setShowReportModal] = useState(false)
  const [reportContent, setReportContent] = useState('')
  const [reportPath, setReportPath] = useState('')

  const handleNewAnalysis = () => {
    setShowResults(false)
    setAnalysisResults(null)
    setReportPath('')
    setReportContent('')
    setShowReportModal(false)
    toast.info("Starting new analysis session")
  }

  const handleAnalyze = async (file) => {
    setIsAnalyzing(true)
    toast.loading("Uploading and analyzing your project...", { id: "analysis" })

    try {
      // Get userId from localStorage or session
      const userId = localStorage.getItem('userId') || 'anonymous'
      
      // Create FormData for file upload
      const formData = new FormData()
      formData.append('file', file)
      formData.append('userId', userId)

      // Upload file and start analysis
      const response = await api.post('/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      // Parse the text response to extract issue count and report path
      const responseText = response.data
      const issueMatch = responseText.match(/🔍 Issues detected: (\d+)/)
      const reportPathMatch = responseText.match(/📋 Report path: (.+)$/m)
      const issueCount = issueMatch ? parseInt(issueMatch[1]) : 0
      const extractedReportPath = reportPathMatch ? reportPathMatch[1] : ''
      
      // Get actual report content to parse issue severities
      let criticalCount = 0, warningCount = 0, suggestionCount = 0
      
      if (extractedReportPath && issueCount > 0) {
        try {
          // Add a small delay to ensure report is fully written
          await new Promise(resolve => setTimeout(resolve, 500))
          
          const userId = localStorage.getItem('userId') || 'anonymous'
          const reportResponse = await api.get(`/upload/report?path=${encodeURIComponent(extractedReportPath)}&userId=${userId}`)
          const reportText = reportResponse.data
          
          // Count issues by severity based on emoji indicators
          const criticalMatches = reportText.match(/🚨 🔴/g)
          const warningMatches = reportText.match(/🚨 🟡/g) 
          const suggestionMatches = reportText.match(/🚨 🟠/g)
          
          // Also count parsing errors as warnings
          const errorMatches = reportText.match(/🚨 ⚠️/g)
          const additionalWarnings = errorMatches ? errorMatches.length : 0
          
          criticalCount = criticalMatches ? criticalMatches.length : 0
          warningCount = (warningMatches ? warningMatches.length : 0) + additionalWarnings
          suggestionCount = suggestionMatches ? suggestionMatches.length : 0
          
          // Debug logging
          console.log('Report parsing results:', {
            critical: criticalCount,
            warnings: warningCount,
            suggestions: suggestionCount,
            totalFromBackend: issueCount,
            reportPreview: reportText.substring(0, 500)
          })
        } catch (error) {
          console.warn('Could not parse report for severity counts:', error)
          // Fallback to percentage distribution if report parsing fails
          criticalCount = Math.floor(issueCount * 0.2)
          warningCount = Math.floor(issueCount * 0.5)
          suggestionCount = Math.ceil(issueCount * 0.3)
        }
      } else if (issueCount > 0) {
        // Fallback when no report path available
        criticalCount = Math.floor(issueCount * 0.2)
        warningCount = Math.floor(issueCount * 0.5)
        suggestionCount = Math.ceil(issueCount * 0.3)
      }
      
      // Create results object from response
      const results = {
        totalIssues: issueCount,
        criticalIssues: criticalCount,
        warnings: warningCount,
        suggestions: suggestionCount,
        summary: responseText
      }
      
      setReportPath(extractedReportPath)
      
      setAnalysisResults(results)
      setIsAnalyzing(false)
      setShowResults(true)
      toast.success(`Analysis complete! Found ${issueCount} issues in ${file.name}`, { id: "analysis" })
    } catch (error) {
      setIsAnalyzing(false)
      toast.error('Analysis failed: ' + (error.response?.data?.message || error.message), { id: "analysis" })
      console.error('Analysis error:', error)
    }
  }

  const handleToggleTheme = () => {
    setIsDarkMode(!isDarkMode)
  }

  const handleShowReport = async () => {
    if (!reportPath) {
      toast.error('No report available')
      return
    }
    
    try {
      const userId = localStorage.getItem('userId') || 'anonymous'
      toast.loading('Loading report...', { id: 'report' })
      const response = await api.get(`/upload/report?path=${encodeURIComponent(reportPath)}&userId=${userId}`)
      setReportContent(response.data)
      setShowReportModal(true)
      toast.success('Report loaded successfully', { id: 'report' })
    } catch (error) {
      toast.error('Failed to load report: ' + (error.response?.data || error.message), { id: 'report' })
    }
  }

  const handleLogout = () => {
    toast.success('Logged out successfully')
    navigate('/')
  }

  const handleAdminPanel = () => {
    navigate('/admin/login')
  }

  return (
    <div className={`min-h-screen transition-all duration-500 ${
      isDarkMode
        ? 'bg-gradient-to-br from-gray-950 via-gray-900 to-gray-950 text-white'
        : 'bg-gradient-to-br from-blue-50 via-white to-cyan-50 text-gray-900'
    }`}>
      {/* Animated Gradient Orbs */}
      {isDarkMode && (
        <>
          <div className="fixed top-0 left-1/2 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl -z-10" />
          <div className="fixed bottom-0 right-0 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl -z-10" />
        </>
      )}
      
      {/* Sidebar */}
      <Sidebar
        onNewAnalysis={handleNewAnalysis}
        onSettingsClick={() => setSettingsOpen(true)}
        onHistoryClick={() => setHistoryOpen(true)}
        isDarkMode={isDarkMode}
      />

      {/* Main Content */}
      <div className="ml-64">
        <Header 
          onLogout={handleLogout} 
          onAdminPanel={handleAdminPanel}
          isDarkMode={isDarkMode}
          onToggleTheme={handleToggleTheme}
        />
        
        <main className="min-h-[calc(100vh-73px)] pt-8">
          {!showResults ? (
            <UploadArea onAnalyze={handleAnalyze} isDarkMode={isDarkMode} />
          ) : (
            <div className="p-8">
              <div className={`rounded-xl p-8 backdrop-blur-sm transition-all duration-500 ${
                isDarkMode 
                  ? 'bg-gray-800/50 border border-gray-700'
                  : 'bg-white/50 border border-gray-200'
              }`}>
                <h2 className={`text-2xl font-semibold mb-4 ${
                  isDarkMode ? 'text-gray-100' : 'text-gray-900'
                }`}>Analysis Results</h2>
                <p className={`mb-6 ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>Your project analysis has been completed.</p>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="bg-red-500/10 border border-red-500/30 rounded-lg p-6">
                    <h3 className="text-red-400 font-semibold mb-2">Critical Issues</h3>
                    <p className="text-3xl font-bold text-red-400">{analysisResults?.criticalIssues || 0}</p>
                  </div>
                  
                  <div className="bg-yellow-500/10 border border-yellow-500/30 rounded-lg p-6">
                    <h3 className="text-yellow-400 font-semibold mb-2">Warnings</h3>
                    <p className="text-3xl font-bold text-yellow-400">{analysisResults?.warnings || 0}</p>
                  </div>
                  
                  <div className="bg-blue-500/10 border border-blue-500/30 rounded-lg p-6">
                    <h3 className="text-blue-400 font-semibold mb-2">AI Suggestions</h3>
                    <p className="text-3xl font-bold text-blue-400">{analysisResults?.suggestions || 0}</p>
                  </div>
                </div>
                
                {/* Analysis Summary */}
                {analysisResults?.summary && (
                  <div className="mt-8">
                    <h3 className={`text-xl font-semibold mb-4 ${
                      isDarkMode ? 'text-gray-100' : 'text-gray-900'
                    }`}>Analysis Summary</h3>
                    <div className={`p-4 rounded-lg border font-mono text-sm whitespace-pre-line ${
                      isDarkMode 
                        ? 'bg-gray-800/50 border-gray-700 text-gray-300'
                        : 'bg-gray-50 border-gray-200 text-gray-700'
                    }`}>
                      {analysisResults.summary}
                    </div>
                    
                    {/* ShowReport Button */}
                    <div className="mt-6 flex justify-center">
                      <button
                        onClick={handleShowReport}
                        className={`px-8 py-3 rounded-lg font-semibold transition-all duration-300 ${
                          isDarkMode
                            ? 'bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white shadow-lg shadow-blue-500/30 hover:shadow-blue-500/50'
                            : 'bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white shadow-lg shadow-blue-400/40 hover:shadow-blue-500/60'
                        }`}
                      >
                        ShowReport
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Settings Modal Placeholder */}
      {settingsOpen && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className={`rounded-xl p-6 w-full max-w-md transition-all duration-500 ${
            isDarkMode 
              ? 'bg-gray-900 border border-gray-700'
              : 'bg-white border border-gray-200'
          }`}>
            <h3 className={`text-xl font-semibold mb-4 ${
              isDarkMode ? 'text-gray-100' : 'text-gray-900'
            }`}>Settings</h3>
            <p className={`mb-6 ${
              isDarkMode ? 'text-gray-400' : 'text-gray-600'
            }`}>Settings panel coming soon...</p>
            <button
              onClick={() => setSettingsOpen(false)}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-lg transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      )}

      {/* History Modal */}
      <History
        isOpen={historyOpen}
        onClose={() => setHistoryOpen(false)}
        isDarkMode={isDarkMode}
      />

      {/* Report Modal */}
      {showReportModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className={`rounded-xl w-full max-w-4xl max-h-[90vh] overflow-hidden transition-all duration-500 ${
            isDarkMode 
              ? 'bg-gray-900 border border-gray-700'
              : 'bg-white border border-gray-200'
          }`}>
            <div className={`p-6 border-b ${
              isDarkMode ? 'border-gray-700' : 'border-gray-200'
            }`}>
              <div className="flex items-center justify-between">
                <h3 className={`text-2xl font-semibold ${
                  isDarkMode ? 'text-gray-100' : 'text-gray-900'
                }`}>Detailed Analysis Report</h3>
                <button
                  onClick={() => setShowReportModal(false)}
                  className={`p-2 rounded-lg transition-colors ${
                    isDarkMode 
                      ? 'hover:bg-gray-800 text-gray-400 hover:text-gray-200'
                      : 'hover:bg-gray-100 text-gray-600 hover:text-gray-800'
                  }`}
                >
                  ✕
                </button>
              </div>
            </div>
            <div className="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
              <div className={`p-4 rounded-lg border font-mono text-sm whitespace-pre-line ${
                isDarkMode 
                  ? 'bg-gray-800/50 border-gray-700 text-gray-300'
                  : 'bg-gray-50 border-gray-200 text-gray-700'
              }`}>
                {reportContent || 'Loading report...'}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Toast Notifications */}
      <Toaster
        position="bottom-right"
        toastOptions={{
          style: {
            background: "#1f2937",
            border: "1px solid #374151",
            color: "#f3f4f6",
          },
        }}
      />


    </div>
  )
}