import React, { useState, useEffect } from 'react'
import { X, Github, GitBranch, Clock, TrendingUp, TrendingDown } from 'lucide-react'
import { Button } from './ui/button'
import api from '../api'
import { toast } from 'sonner'

export function GitHubIntegration({ isOpen, onClose, isDarkMode }) {
  const [githubToken, setGithubToken] = useState('')
  const [isConnected, setIsConnected] = useState(false)
  const [repos, setRepos] = useState([])
  const [selectedRepo, setSelectedRepo] = useState(null)
  const [commits, setCommits] = useState([])
  const [analyzing, setAnalyzing] = useState(null)
  const [versionComparison, setVersionComparison] = useState([])
  const [showComparison, setShowComparison] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem('githubToken')
    if (token) {
      setGithubToken(token)
      setIsConnected(true)
      fetchRepos(token)
    }
  }, [isOpen])

  const handleConnect = async () => {
    if (!githubToken) {
      toast.error('Please enter GitHub token')
      return
    }
    
    try {
      const response = await api.post('/github/repos', { token: githubToken })
      setRepos(response.data)
      localStorage.setItem('githubToken', githubToken)
      setIsConnected(true)
      toast.success('Connected to GitHub!')
    } catch (error) {
      toast.error('Failed to connect: ' + error.message)
    }
  }

  const fetchRepos = async (token) => {
    try {
      const response = await api.post('/github/repos', { token })
      setRepos(response.data)
    } catch (error) {
      console.error('Failed to fetch repos:', error)
    }
  }

  const handleRepoSelect = async (repo) => {
    setSelectedRepo(repo)
    setShowComparison(false)
    try {
      const response = await api.post('/github/commits', {
        token: githubToken,
        owner: repo.owner.login,
        repo: repo.name
      })
      setCommits(response.data)
      
      // Fetch analysis history
      const userId = localStorage.getItem('userId')
      const historyResponse = await api.get('/github/commit-history', {
        params: { userId, owner: repo.owner.login, repo: repo.name }
      })
      setVersionComparison(historyResponse.data)
    } catch (error) {
      toast.error('Failed to fetch commits')
    }
  }

  const handleAnalyzeCommit = async (commit) => {
    setAnalyzing(commit.sha)
    toast.loading('Analyzing commit...', { id: 'analyze' })
    
    try {
      const userId = localStorage.getItem('userId')
      const response = await api.post('/github/analyze-commit', {
        token: githubToken,
        owner: selectedRepo.owner.login,
        repo: selectedRepo.name,
        sha: commit.sha,
        userId,
        commitMessage: commit.commit.message,
        commitDate: commit.commit.author.date
      })
      
      setVersionComparison(prev => [response.data, ...prev])
      setShowComparison(true)
      toast.success('Analysis complete! Check Quality Trends below.', { id: 'analyze' })
    } catch (error) {
      toast.error('Analysis failed: ' + error.message, { id: 'analyze' })
    } finally {
      setAnalyzing(null)
    }
  }

  const handleDisconnect = () => {
    localStorage.removeItem('githubToken')
    setGithubToken('')
    setIsConnected(false)
    setRepos([])
    setSelectedRepo(null)
    setCommits([])
    toast.info('Disconnected from GitHub')
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <div className={`rounded-xl w-full max-w-5xl h-[85vh] flex flex-col transition-all duration-500 ${
        isDarkMode ? 'bg-gray-900 border border-gray-700' : 'bg-white border border-gray-200'
      }`}>
        
        {/* Header */}
        <div className={`flex items-center justify-between p-6 border-b ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <div className="flex items-center gap-3">
            <Github className="h-6 w-6" />
            <h2 className={`text-xl font-semibold ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
              GitHub Integration
            </h2>
          </div>
          <button onClick={onClose} className={`p-2 rounded-lg ${
            isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-100'
          }`}>
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-hidden flex">
          
          {/* Left Panel - Connection & Repos */}
          <div className={`w-1/3 border-r p-4 overflow-y-auto ${
            isDarkMode ? 'border-gray-700' : 'border-gray-200'
          }`}>
            
            {!isConnected ? (
              <div className="space-y-4">
                <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                  Connect your GitHub account to track code quality across versions
                </p>
                <input
                  type="password"
                  placeholder="GitHub Personal Access Token"
                  value={githubToken}
                  onChange={(e) => setGithubToken(e.target.value)}
                  className={`w-full px-3 py-2 rounded-lg border ${
                    isDarkMode ? 'bg-gray-800 border-gray-700 text-gray-200' : 'bg-white border-gray-300'
                  }`}
                />
                <Button onClick={handleConnect} className="w-full">
                  Connect GitHub
                </Button>
                <a 
                  href="https://github.com/settings/tokens" 
                  target="_blank"
                  className="text-xs text-blue-500 hover:underline block"
                >
                  Get GitHub Token
                </a>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className={`text-sm ${isDarkMode ? 'text-green-400' : 'text-green-600'}`}>
                    ✓ Connected
                  </span>
                  <button onClick={handleDisconnect} className="text-xs text-red-500 hover:underline">
                    Disconnect
                  </button>
                </div>
                
                <div className="space-y-2">
                  <h3 className={`text-sm font-semibold ${isDarkMode ? 'text-gray-300' : 'text-gray-700'}`}>
                    Your Repositories
                  </h3>
                  {repos.map((repo) => (
                    <div
                      key={repo.id}
                      onClick={() => handleRepoSelect(repo)}
                      className={`p-3 rounded-lg cursor-pointer transition-all ${
                        selectedRepo?.id === repo.id
                          ? isDarkMode ? 'bg-blue-600/20 border border-blue-500/30' : 'bg-blue-50 border border-blue-200'
                          : isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-50'
                      }`}
                    >
                      <p className={`font-medium text-sm ${isDarkMode ? 'text-gray-200' : 'text-gray-900'}`}>
                        {repo.name}
                      </p>
                      <p className={`text-xs ${isDarkMode ? 'text-gray-500' : 'text-gray-500'}`}>
                        {repo.language || 'Unknown'}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Right Panel - Commits & Analysis */}
          <div className="flex-1 p-4 overflow-y-auto">
            {selectedRepo ? (
              <div className="space-y-4">
                <div>
                  <h3 className={`text-lg font-semibold mb-2 ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
                    {selectedRepo.name}
                  </h3>
                  <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                    Recent commits - Select to analyze
                  </p>
                </div>

                <div className="space-y-2">
                  {commits.map((commit) => (
                    <div
                      key={commit.sha}
                      className={`p-4 rounded-lg border ${
                        isDarkMode ? 'bg-gray-800/50 border-gray-700 hover:bg-gray-800' : 'bg-gray-50 border-gray-200 hover:bg-gray-100'
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <p className={`font-medium text-sm ${isDarkMode ? 'text-gray-200' : 'text-gray-900'}`}>
                            {commit.commit.message.split('\n')[0]}
                          </p>
                          <div className="flex items-center gap-4 mt-2 text-xs">
                            <span className={isDarkMode ? 'text-gray-500' : 'text-gray-500'}>
                              {commit.commit.author.name}
                            </span>
                            <span className={`flex items-center gap-1 ${isDarkMode ? 'text-gray-500' : 'text-gray-500'}`}>
                              <Clock className="h-3 w-3" />
                              {new Date(commit.commit.author.date).toLocaleDateString()}
                            </span>
                          </div>
                        </div>
                        <Button 
                          size="sm" 
                          variant="outline" 
                          className="ml-2"
                          onClick={() => handleAnalyzeCommit(commit)}
                          disabled={analyzing === commit.sha}
                        >
                          {analyzing === commit.sha ? 'Analyzing...' : 'Analyze'}
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>

                {versionComparison.length > 0 && (
                  <div className="mt-6">
                    <div className="flex items-center justify-between mb-3">
                      <h4 className={`text-lg font-semibold ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
                        📊 Quality Trends ({versionComparison.length} analyses)
                      </h4>
                    </div>
                    
                    <div className={`p-4 rounded-lg border ${isDarkMode ? 'bg-gray-800/50 border-gray-700' : 'bg-gray-50 border-gray-200'}`}>
                      {versionComparison.map((analysis, idx) => (
                          <div key={analysis.id} className={`flex items-center justify-between py-3 ${idx !== 0 ? 'border-t' : ''} ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
                            <div className="flex-1">
                              <p className={`text-sm font-medium ${isDarkMode ? 'text-gray-200' : 'text-gray-900'}`}>
                                {analysis.commitMessage.split('\n')[0]}
                              </p>
                              <p className={`text-xs ${isDarkMode ? 'text-gray-500' : 'text-gray-500'}`}>
                                {new Date(analysis.commitDate).toLocaleDateString()}
                              </p>
                            </div>
                            <div className="flex items-center gap-4 text-xs">
                              <span className="text-red-500">🔴 {analysis.criticalIssues}</span>
                              <span className="text-yellow-500">🟡 {analysis.warnings}</span>
                              <span className="text-orange-500">🟠 {analysis.suggestions}</span>
                              <span className={`font-semibold ${isDarkMode ? 'text-gray-300' : 'text-gray-700'}`}>
                                Total: {analysis.totalIssues}
                              </span>
                              {idx > 0 && (
                                <span className={`flex items-center gap-1 ${
                                  analysis.totalIssues < versionComparison[idx-1].totalIssues 
                                    ? 'text-green-500' 
                                    : analysis.totalIssues > versionComparison[idx-1].totalIssues
                                    ? 'text-red-500'
                                    : 'text-gray-500'
                                }`}>
                                  {analysis.totalIssues < versionComparison[idx-1].totalIssues ? (
                                    <><TrendingDown className="h-3 w-3" /> Improved</>
                                  ) : analysis.totalIssues > versionComparison[idx-1].totalIssues ? (
                                    <><TrendingUp className="h-3 w-3" /> Degraded</>
                                  ) : (
                                    'Same'
                                  )}
                                </span>
                              )}
                            </div>
                          </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex items-center justify-center h-full">
                <p className={`text-center ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                  Select a repository to view commits
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
