import React, { useState } from 'react'
import { X, FileText, Code, Network, TrendingUp, AlertTriangle, CheckCircle, BarChart3, PieChart, Activity } from 'lucide-react'
import { Button } from './ui/button'

export function AdvancedVisualReport({ reportData, isOpen, onClose, isDarkMode }) {
  const [activeTab, setActiveTab] = useState('overview')

  if (!isOpen || !reportData) return null

  const { projectName, analysisResults, diagramBase64 } = reportData
  const classes = analysisResults?.classes || {}
  const dependencies = analysisResults?.dependencies || []
  
  const totalClasses = Object.keys(classes).length
  const totalDependencies = dependencies.length
  const totalLOC = Object.values(classes).reduce((sum, cls) => sum + (cls.linesOfCode || 0), 0)
  const avgComplexity = totalClasses > 0 ? Math.round(Object.values(classes).reduce((sum, cls) => sum + (cls.complexity || 0), 0) / totalClasses) : 0
  
  const highComplexityClasses = Object.values(classes).filter(cls => cls.complexity > 10).length
  const interfaces = Object.values(classes).filter(cls => cls.interface).length
  const abstractClasses = Object.values(classes).filter(cls => cls.abstract).length

  const tabs = [
    { id: 'overview', label: 'Overview', icon: BarChart3 },
    { id: 'architecture', label: 'Architecture', icon: Network },
    { id: 'classes', label: 'Classes', icon: Code },
    { id: 'metrics', label: 'Metrics', icon: Activity },
    { id: 'insights', label: 'Insights', icon: TrendingUp }
  ]

  const getComplexityColor = (complexity) => {
    if (complexity > 15) return 'bg-red-500'
    if (complexity > 10) return 'bg-orange-500'
    if (complexity > 5) return 'bg-yellow-500'
    return 'bg-green-500'
  }

  const getQualityScore = () => {
    if (totalClasses === 0) return 100
    const complexityPenalty = (highComplexityClasses / totalClasses) * 30
    const sizePenalty = Math.min(20, (totalLOC / 10000) * 10)
    const dependencyPenalty = Math.min(15, (totalDependencies / totalClasses) * 5)
    return Math.max(0, Math.round(100 - complexityPenalty - sizePenalty - dependencyPenalty))
  }

  const qualityScore = getQualityScore()

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
            <h2 className="text-2xl font-bold">Visual Architecture Report</h2>
            <p className="text-sm opacity-75">{projectName}</p>
          </div>
          <Button variant="ghost" size="sm" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        {/* Tabs */}
        <div className={`flex border-b ${isDarkMode ? 'border-gray-700' : 'border-gray-200'}`}>
          {tabs.map(tab => {
            const Icon = tab.icon
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-6 py-3 font-medium transition-colors ${
                  activeTab === tab.id
                    ? isDarkMode ? 'bg-gray-800 text-blue-400 border-b-2 border-blue-400' : 'bg-gray-50 text-blue-600 border-b-2 border-blue-600'
                    : isDarkMode ? 'text-gray-400 hover:text-gray-200' : 'text-gray-600 hover:text-gray-800'
                }`}
              >
                <Icon className="h-4 w-4" />
                {tab.label}
              </button>
            )
          })}
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto max-h-[calc(95vh-180px)]">
          
          {/* Overview Tab */}
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Key Metrics Cards */}
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-full ${qualityScore >= 80 ? 'bg-green-100' : qualityScore >= 60 ? 'bg-yellow-100' : 'bg-red-100'}`}>
                      <TrendingUp className={`h-5 w-5 ${qualityScore >= 80 ? 'text-green-600' : qualityScore >= 60 ? 'text-yellow-600' : 'text-red-600'}`} />
                    </div>
                    <div>
                      <p className="text-sm opacity-75">Quality Score</p>
                      <p className="text-2xl font-bold">{qualityScore}%</p>
                    </div>
                  </div>
                </div>

                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-full bg-blue-100">
                      <Code className="h-5 w-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm opacity-75">Total Classes</p>
                      <p className="text-2xl font-bold">{totalClasses}</p>
                    </div>
                  </div>
                </div>

                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-full bg-purple-100">
                      <FileText className="h-5 w-5 text-purple-600" />
                    </div>
                    <div>
                      <p className="text-sm opacity-75">Lines of Code</p>
                      <p className="text-2xl font-bold">{totalLOC.toLocaleString()}</p>
                    </div>
                  </div>
                </div>

                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-full bg-orange-100">
                      <Network className="h-5 w-5 text-orange-600" />
                    </div>
                    <div>
                      <p className="text-sm opacity-75">Dependencies</p>
                      <p className="text-2xl font-bold">{totalDependencies}</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Summary Stats */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <h3 className="text-lg font-semibold mb-4">Project Health</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span>Average Complexity</span>
                      <span className="font-bold">{avgComplexity}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span>High Complexity Classes</span>
                      <span className={`font-bold ${highComplexityClasses > 0 ? 'text-red-500' : 'text-green-500'}`}>
                        {highComplexityClasses}
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span>Interfaces</span>
                      <span className="font-bold">{interfaces}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span>Abstract Classes</span>
                      <span className="font-bold">{abstractClasses}</span>
                    </div>
                  </div>
                </div>

                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <h3 className="text-lg font-semibold mb-4">Recommendations</h3>
                  <div className="space-y-2">
                    {highComplexityClasses > 0 && (
                      <div className="flex items-center gap-2 text-orange-600">
                        <AlertTriangle className="h-4 w-4" />
                        <span className="text-sm">Refactor {highComplexityClasses} complex classes</span>
                      </div>
                    )}
                    {totalDependencies > totalClasses * 2 && (
                      <div className="flex items-center gap-2 text-yellow-600">
                        <AlertTriangle className="h-4 w-4" />
                        <span className="text-sm">High coupling detected</span>
                      </div>
                    )}
                    {qualityScore >= 80 && (
                      <div className="flex items-center gap-2 text-green-600">
                        <CheckCircle className="h-4 w-4" />
                        <span className="text-sm">Excellent code quality</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Architecture Tab */}
          {activeTab === 'architecture' && (
            <div className="space-y-6">
              <div className="text-center">
                <h3 className="text-lg font-semibold mb-4">System Architecture Diagram</h3>
                {diagramBase64 ? (
                  <img 
                    src={`data:image/png;base64,${diagramBase64}`}
                    alt="Architecture Diagram"
                    className="max-w-full h-auto mx-auto rounded-lg shadow-lg"
                  />
                ) : (
                  <div className={`p-8 rounded-lg border-2 border-dashed ${isDarkMode ? 'border-gray-600' : 'border-gray-300'}`}>
                    <Network className="h-12 w-12 mx-auto mb-2 opacity-50" />
                    <p>Architecture diagram not available</p>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Classes Tab */}
          {activeTab === 'classes' && (
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">Class Analysis</h3>
              <div className="grid gap-4">
                {Object.entries(classes).slice(0, 20).map(([fullName, classInfo]) => (
                  <div key={fullName} className={`p-4 rounded-lg border ${isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-gray-50 border-gray-200'}`}>
                    <div className="flex justify-between items-start">
                      <div>
                        <h4 className="font-semibold">{classInfo.className}</h4>
                        <p className="text-sm opacity-75">{classInfo.packageName}</p>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-1 rounded text-xs text-white ${getComplexityColor(classInfo.complexity)}`}>
                          Complexity: {classInfo.complexity}
                        </span>
                        {classInfo.interface && <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs">Interface</span>}
                        {classInfo.abstract && <span className="px-2 py-1 bg-purple-100 text-purple-800 rounded text-xs">Abstract</span>}
                      </div>
                    </div>
                    <div className="mt-2 flex gap-4 text-sm opacity-75">
                      <span>LOC: {classInfo.linesOfCode}</span>
                      <span>Dependencies: {dependencies.filter(d => d.fromClass === fullName).length}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Metrics Tab */}
          {activeTab === 'metrics' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <h3 className="text-lg font-semibold mb-4">Complexity Distribution</h3>
                  <div className="space-y-3">
                    {[
                      { label: 'Low (1-5)', count: Object.values(classes).filter(c => c.complexity <= 5).length, color: 'bg-green-500' },
                      { label: 'Medium (6-10)', count: Object.values(classes).filter(c => c.complexity > 5 && c.complexity <= 10).length, color: 'bg-yellow-500' },
                      { label: 'High (11-15)', count: Object.values(classes).filter(c => c.complexity > 10 && c.complexity <= 15).length, color: 'bg-orange-500' },
                      { label: 'Critical (>15)', count: Object.values(classes).filter(c => c.complexity > 15).length, color: 'bg-red-500' }
                    ].map(item => (
                      <div key={item.label} className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <div className={`w-4 h-4 rounded ${item.color}`}></div>
                          <span>{item.label}</span>
                        </div>
                        <span className="font-bold">{item.count}</span>
                      </div>
                    ))}
                  </div>
                </div>

                <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                  <h3 className="text-lg font-semibold mb-4">Size Metrics</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span>Largest Class</span>
                      <span className="font-bold">{Math.max(...Object.values(classes).map(c => c.linesOfCode || 0))} LOC</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Average Class Size</span>
                      <span className="font-bold">{Math.round(totalLOC / totalClasses)} LOC</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Total Packages</span>
                      <span className="font-bold">{new Set(Object.values(classes).map(c => c.packageName)).size}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Insights Tab */}
          {activeTab === 'insights' && (
            <div className="space-y-6">
              <div className={`p-6 rounded-lg ${isDarkMode ? 'bg-gray-800' : 'bg-gray-50'}`}>
                <h3 className="text-lg font-semibold mb-4">Key Insights</h3>
                <div className="space-y-4">
                  <div className="flex items-start gap-3">
                    <div className={`p-2 rounded-full ${qualityScore >= 80 ? 'bg-green-100' : 'bg-orange-100'}`}>
                      {qualityScore >= 80 ? <CheckCircle className="h-5 w-5 text-green-600" /> : <AlertTriangle className="h-5 w-5 text-orange-600" />}
                    </div>
                    <div>
                      <h4 className="font-semibold">Overall Code Quality</h4>
                      <p className="text-sm opacity-75">
                        {qualityScore >= 80 ? 'Excellent code quality with good structure and maintainability.' :
                         qualityScore >= 60 ? 'Good code quality with some areas for improvement.' :
                         'Code quality needs attention. Consider refactoring complex components.'}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start gap-3">
                    <div className="p-2 rounded-full bg-blue-100">
                      <Network className="h-5 w-5 text-blue-600" />
                    </div>
                    <div>
                      <h4 className="font-semibold">Architecture Complexity</h4>
                      <p className="text-sm opacity-75">
                        {totalDependencies > totalClasses * 2 ? 
                          'High coupling detected. Consider reducing dependencies between classes.' :
                          'Well-structured architecture with reasonable coupling levels.'}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start gap-3">
                    <div className="p-2 rounded-full bg-purple-100">
                      <Code className="h-5 w-5 text-purple-600" />
                    </div>
                    <div>
                      <h4 className="font-semibold">Maintainability</h4>
                      <p className="text-sm opacity-75">
                        {highComplexityClasses === 0 ? 
                          'All classes have manageable complexity levels.' :
                          `${highComplexityClasses} classes have high complexity and may need refactoring.`}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}