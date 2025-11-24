import React, { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Sidebar } from '../components/Sidebar'
import { Header } from '../components/Header'
import { UploadArea } from '../components/UploadArea'
import { History } from '../components/History'
import { Settings } from '../components/Settings'
import { VisualReport } from '../components/VisualReport'
import { AdvancedVisualReport } from '../components/AdvancedVisualReport'
import { Toaster } from '../components/ui/sonner'
import { toast } from 'sonner'
import api from '../api'
import './Home.css'


export default function Home() {
  const navigate = useNavigate()
  const location = useLocation()
  const [showResults, setShowResults] = useState(false)
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [historyOpen, setHistoryOpen] = useState(false)
  const [_isAnalyzing, setIsAnalyzing] = useState(false)
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [analysisResults, setAnalysisResults] = useState(null)
  const [showReportModal, setShowReportModal] = useState(false)
  const [reportContent, setReportContent] = useState('')
  const [reportPath, setReportPath] = useState('')
  const [projectName, setProjectName] = useState('')
  const [visualReportData, setVisualReportData] = useState(null)
  const [showVisualReport, setShowVisualReport] = useState(false)

  // Handle returning from file viewer
  useEffect(() => {
    if (location.state?.openReport) {
      setShowReportModal(true)
      // Clear the state
      navigate('/home', { replace: true, state: {} })
    }
  }, [location.state])

  const handleNewAnalysis = () => {
    setShowResults(false)
    setAnalysisResults(null)
    setReportPath('')
    setReportContent('')
    setShowReportModal(false)
    setProjectName('')
    toast.info("Starting new analysis session")
  }

  const handleVisualReport = async (file) => {
    toast.loading("Generating visual architecture report...", { id: "visual-report" })

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await api.post('/upload/visual', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      setVisualReportData(response.data)
      setShowVisualReport(true)
      toast.success('Visual architecture report generated successfully!', { id: "visual-report" })
    } catch (error) {
      toast.error('Failed to generate visual report: ' + (error.response?.data?.message || error.message), { id: "visual-report" })
      console.error('Visual report error:', error)
    }
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
          
          // persist fetched report content so PDF generator and viewer can use it
          setReportContent(reportText)

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
      setProjectName(file.name.replace(/\.[^/.]+$/, "")) // Remove file extension
      
      // Extract project path from report path (remove filename)
      if (extractedReportPath) {
        const projectPath = extractedReportPath.substring(0, extractedReportPath.lastIndexOf('/'))
        console.log('Extracted project path:', projectPath)
      }
      
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
    <div className={`pageContainer ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
      {/* Animated Gradient Orbs */}
      {isDarkMode && (
        <>
          <div className="backgroundOrb topOrb" />
          <div className="backgroundOrb bottomOrb" />
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
      <div className="mainContent">
        <Header 
          onLogout={handleLogout} 
          onAdminPanel={handleAdminPanel}
          isDarkMode={isDarkMode}
          onToggleTheme={handleToggleTheme}
        />
        
        <main className="contentArea">
          {!showResults ? (
            <UploadArea onAnalyze={handleAnalyze} onVisualReport={handleVisualReport} isDarkMode={isDarkMode} />
          ) : (
            <div className="resultsContainer">
              <div className={`resultsCard ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
                <h2 className={`resultsTitle ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>Analysis Results</h2>
                <p className={`resultsDescription ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>Your project analysis has been completed.</p>
                
                <div className="statsGrid">
                  <div className="statCard criticalCard">
                    <h3 className="statTitle criticalTitle">Critical Issues</h3>
                    <p className="statValue criticalValue">{analysisResults?.criticalIssues || 0}</p>
                  </div>
                  
                  <div className="statCard warningCard">
                    <h3 className="statTitle warningTitle">Warnings</h3>
                    <p className="statValue warningValue">{analysisResults?.warnings || 0}</p>
                  </div>
                  
                  <div className="statCard suggestionCard">
                    <h3 className="statTitle suggestionTitle">AI Suggestions</h3>
                    <p className="statValue suggestionValue">{analysisResults?.suggestions || 0}</p>
                  </div>
                </div>
                
                {/* Analysis Summary */}
                {analysisResults?.summary && (
                  <div className="actionButtons">
                    <button onClick={handleShowReport} className="reportBtn">
                      View Detailed Report
                    </button>

                    <button onClick={handleNewAnalysis} className="newAnalysisBtn">
                      New Analysis
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Settings Modal */}
      <Settings 
        isOpen={settingsOpen} 
        onClose={() => setSettingsOpen(false)} 
        isDarkMode={isDarkMode} 
      />

      {/* History Modal */}
      <History
        isOpen={historyOpen}
        onClose={() => setHistoryOpen(false)}
        isDarkMode={isDarkMode}
      />

      {/* Visual Report Modal */}
      <VisualReport
        reportContent={reportContent}
        isOpen={showReportModal}
        onClose={() => setShowReportModal(false)}
        isDarkMode={isDarkMode}
        projectName={projectName}
        projectPath={reportPath ? reportPath.substring(0, reportPath.lastIndexOf('/')) : ''}
      />

      {/* Advanced Visual Report Modal */}
      <AdvancedVisualReport
        reportData={visualReportData}
        isOpen={showVisualReport}
        onClose={() => setShowVisualReport(false)}
        isDarkMode={isDarkMode}
      />

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