import React, { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Sidebar } from '../components/Sidebar'
import { Header } from '../components/Header'
import { UploadArea } from '../components/UploadArea'
import { History } from '../components/History'
import { Settings } from '../components/Settings'
import { VisualReport } from '../components/VisualReport'
import { EnhancedVisualReport } from '../components/EnhancedVisualReport'
import { AdvancedVisualReport } from '../components/AdvancedVisualReport'
import { AnalysisResults } from '../components/AnalysisResults'
import { GitHubIntegration } from '../components/GitHubIntegration'
import { UserProfile } from '../components/UserProfile'
import { WelcomeGuide } from '../components/WelcomeGuide'
import { useState as useReactState } from 'react'
import { Toaster } from '../components/ui/sonner'
import { toast } from 'sonner'
import api from '../api'
import './Home.css'


export default function Home() {
  const navigate = useNavigate()
  const location = useLocation()
  
  // Security check: Verify authentication on component mount
  useEffect(() => {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    if (!token || !userId) {
      navigate('/login', { replace: true })
    }
  }, [])
  
  const [showResults, setShowResults] = useState(false)
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [historyOpen, setHistoryOpen] = useState(false)
  const [githubOpen, setGithubOpen] = useState(false)
  const [profileOpen, setProfileOpen] = useState(false)
  const [_isAnalyzing, setIsAnalyzing] = useState(false)
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [analysisResults, setAnalysisResults] = useState(null)
  const [showReportModal, setShowReportModal] = useState(false)
  const [reportContent, setReportContent] = useState('')
  const [reportPath, setReportPath] = useState('')
  const [projectName, setProjectName] = useState('')
  const [visualReportData, setVisualReportData] = useState(null)
  const [showVisualReport, setShowVisualReport] = useState(false)
  const [activeSection, setActiveSection] = useState('guide')
  const [useEnhancedReport, setUseEnhancedReport] = useState(true)

  // Handle returning from file viewer
  useEffect(() => {
    if (location.state?.openReport && location.state?.reportData) {
      const { reportContent: storedContent, projectName: storedName, projectPath: storedPath } = location.state.reportData
      
      // Restore the report data
      setReportContent(storedContent)
      setProjectName(storedName)
      
      // Reconstruct reportPath from projectPath
      // projectPath is like "uploads/ProjectName", reportPath should be "uploads/ProjectName/report.txt"
      if (storedPath) {
        const reconstructedReportPath = storedPath + '/' + storedPath.split('/').pop() + '_comprehensive.txt'
        setReportPath(reconstructedReportPath)
      }
      
      setShowReportModal(true)
      
      // Clear sessionStorage
      sessionStorage.removeItem('returnToReport')
      
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
    setActiveSection('upload')
    toast.info("Starting new analysis session")
  }

  const handleStartAnalysis = () => {
    setActiveSection('upload')
    setHistoryOpen(false)
    setSettingsOpen(false)
    setGithubOpen(false)
    setProfileOpen(false)
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
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
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
        onStartAnalysis={handleStartAnalysis}
        onWelcomeGuideClick={() => setActiveSection('guide')}
        onSettingsClick={() => { setSettingsOpen(true); setActiveSection('settings'); }}
        onHistoryClick={() => { setHistoryOpen(true); setActiveSection('history'); }}
        onGitHubClick={() => { setGithubOpen(true); setActiveSection('github'); }}
        onUserAccountClick={() => { setProfileOpen(true); setActiveSection('profile'); }}
        isDarkMode={isDarkMode}
        activeSection={activeSection}
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
          {activeSection === 'guide' ? (
            <WelcomeGuide isDarkMode={isDarkMode} />
          ) : !showResults ? (
            <UploadArea onAnalyze={handleAnalyze} onVisualReport={handleVisualReport} isDarkMode={isDarkMode} />
          ) : (
            <div className="resultsContainer">
              <AnalysisResults
                results={analysisResults}
                onShowReport={handleShowReport}
                onNewAnalysis={handleNewAnalysis}
                isDarkMode={isDarkMode}
              />
            </div>
          )}
        </main>
      </div>

      {/* Settings Modal */}
      <Settings 
        isOpen={settingsOpen} 
        onClose={() => { setSettingsOpen(false); setActiveSection('upload'); }} 
        isDarkMode={isDarkMode} 
      />

      {/* History Modal */}
      <History
        isOpen={historyOpen}
        onClose={() => { setHistoryOpen(false); setActiveSection('upload'); }}
        isDarkMode={isDarkMode}
      />

      {/* GitHub Integration Modal */}
      <GitHubIntegration
        isOpen={githubOpen}
        onClose={() => { setGithubOpen(false); setActiveSection('upload'); }}
        isDarkMode={isDarkMode}
      />

      {/* User Profile Modal */}
      <UserProfile
        isOpen={profileOpen}
        onClose={() => { setProfileOpen(false); setActiveSection('upload'); }}
        isDarkMode={isDarkMode}
      />

      {/* Visual Report Modal - Use Enhanced or Fallback */}
      {useEnhancedReport ? (
        <EnhancedVisualReport
          reportContent={reportContent}
          isOpen={showReportModal}
          onClose={() => setShowReportModal(false)}
          isDarkMode={isDarkMode}
          projectName={projectName}
          projectPath={reportPath ? reportPath.substring(0, reportPath.lastIndexOf('/')) : ''}
        />
      ) : (
        <VisualReport
          reportContent={reportContent}
          isOpen={showReportModal}
          onClose={() => setShowReportModal(false)}
          isDarkMode={isDarkMode}
          projectName={projectName}
          projectPath={reportPath ? reportPath.substring(0, reportPath.lastIndexOf('/')) : ''}
        />
      )}

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