import React from 'react'
import { AlertTriangle, CheckCircle, Info, FileText, TrendingUp, Download } from 'lucide-react'
import { Button } from './ui/button'

export function AnalysisResults({ results, onShowReport, onNewAnalysis, onVisualReport, isDarkMode }) {
  const totalIssues = results?.totalIssues || 0
  const criticalIssues = results?.criticalIssues || 0
  const warnings = results?.warnings || 0
  const suggestions = results?.suggestions || 0

  const getHealthStatus = () => {
    if (totalIssues === 0) return { text: 'Excellent', color: 'green', icon: CheckCircle }
    if (criticalIssues > 5) return { text: 'Critical', color: 'red', icon: AlertTriangle }
    if (criticalIssues > 0 || warnings > 10) return { text: 'Needs Attention', color: 'orange', icon: AlertTriangle }
    return { text: 'Good', color: 'blue', icon: Info }
  }

  const health = getHealthStatus()
  const HealthIcon = health.icon

  return (
    <div className="max-w-5xl mx-auto">
      {/* Health Status Banner */}
      <div className={`mb-6 p-6 rounded-xl border-2 ${
        health.color === 'green' ? 'border-green-500 bg-green-50' :
        health.color === 'red' ? 'border-red-500 bg-red-50' :
        health.color === 'orange' ? 'border-orange-500 bg-orange-50' :
        'border-blue-500 bg-blue-50'
      } ${isDarkMode ? 'bg-opacity-10' : ''}`}>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className={`p-3 rounded-full ${
              health.color === 'green' ? 'bg-green-100' :
              health.color === 'red' ? 'bg-red-100' :
              health.color === 'orange' ? 'bg-orange-100' :
              'bg-blue-100'
            }`}>
              <HealthIcon className={`h-8 w-8 ${
                health.color === 'green' ? 'text-green-600' :
                health.color === 'red' ? 'text-red-600' :
                health.color === 'orange' ? 'text-orange-600' :
                'text-blue-600'
              }`} />
            </div>
            <div>
              <h3 className={`text-2xl font-bold ${
                health.color === 'green' ? 'text-green-700' :
                health.color === 'red' ? 'text-red-700' :
                health.color === 'orange' ? 'text-orange-700' :
                'text-blue-700'
              }`}>
                Code Health: {health.text}
              </h3>
              <p className={`text-sm ${
                isDarkMode ? 'text-gray-400' : 'text-gray-600'
              }`}>
                {totalIssues === 0 
                  ? 'No issues detected in your codebase!' 
                  : `Found ${totalIssues} issue${totalIssues > 1 ? 's' : ''} that need attention`}
              </p>
            </div>
          </div>
          <div className="text-right">
            <div className="text-4xl font-bold">{totalIssues}</div>
            <div className="text-sm opacity-75">Total Issues</div>
          </div>
        </div>
      </div>

      {/* Issue Breakdown Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        {/* Critical Issues */}
        <div className={`p-6 rounded-xl border-2 transition-all duration-300 hover:scale-105 ${
          criticalIssues > 0 
            ? 'border-red-500 bg-red-50' 
            : isDarkMode ? 'border-gray-700 bg-gray-800' : 'border-gray-200 bg-gray-50'
        } ${isDarkMode && criticalIssues > 0 ? 'bg-opacity-10' : ''}`}>
          <div className="flex items-center justify-between mb-4">
            <div className={`p-3 rounded-full ${
              criticalIssues > 0 ? 'bg-red-100' : isDarkMode ? 'bg-gray-700' : 'bg-gray-200'
            }`}>
              <AlertTriangle className={`h-6 w-6 ${
                criticalIssues > 0 ? 'text-red-600' : 'text-gray-400'
              }`} />
            </div>
            <div className={`text-4xl font-bold ${
              criticalIssues > 0 ? 'text-red-600' : 'text-gray-400'
            }`}>
              {criticalIssues}
            </div>
          </div>
          <h4 className={`text-lg font-semibold mb-2 ${
            isDarkMode ? 'text-gray-200' : 'text-gray-900'
          }`}>Critical Issues</h4>
          <p className={`text-sm ${
            isDarkMode ? 'text-gray-400' : 'text-gray-600'
          }`}>
            {criticalIssues > 0 
              ? 'Require immediate attention' 
              : 'No critical issues found'}
          </p>
        </div>

        {/* Warnings */}
        <div className={`p-6 rounded-xl border-2 transition-all duration-300 hover:scale-105 ${
          warnings > 0 
            ? 'border-yellow-500 bg-yellow-50' 
            : isDarkMode ? 'border-gray-700 bg-gray-800' : 'border-gray-200 bg-gray-50'
        } ${isDarkMode && warnings > 0 ? 'bg-opacity-10' : ''}`}>
          <div className="flex items-center justify-between mb-4">
            <div className={`p-3 rounded-full ${
              warnings > 0 ? 'bg-yellow-100' : isDarkMode ? 'bg-gray-700' : 'bg-gray-200'
            }`}>
              <Info className={`h-6 w-6 ${
                warnings > 0 ? 'text-yellow-600' : 'text-gray-400'
              }`} />
            </div>
            <div className={`text-4xl font-bold ${
              warnings > 0 ? 'text-yellow-600' : 'text-gray-400'
            }`}>
              {warnings}
            </div>
          </div>
          <h4 className={`text-lg font-semibold mb-2 ${
            isDarkMode ? 'text-gray-200' : 'text-gray-900'
          }`}>Warnings</h4>
          <p className={`text-sm ${
            isDarkMode ? 'text-gray-400' : 'text-gray-600'
          }`}>
            {warnings > 0 
              ? 'Should be addressed soon' 
              : 'No warnings detected'}
          </p>
        </div>

        {/* Suggestions */}
        <div className={`p-6 rounded-xl border-2 transition-all duration-300 hover:scale-105 ${
          suggestions > 0 
            ? 'border-blue-500 bg-blue-50' 
            : isDarkMode ? 'border-gray-700 bg-gray-800' : 'border-gray-200 bg-gray-50'
        } ${isDarkMode && suggestions > 0 ? 'bg-opacity-10' : ''}`}>
          <div className="flex items-center justify-between mb-4">
            <div className={`p-3 rounded-full ${
              suggestions > 0 ? 'bg-blue-100' : isDarkMode ? 'bg-gray-700' : 'bg-gray-200'
            }`}>
              <TrendingUp className={`h-6 w-6 ${
                suggestions > 0 ? 'text-blue-600' : 'text-gray-400'
              }`} />
            </div>
            <div className={`text-4xl font-bold ${
              suggestions > 0 ? 'text-blue-600' : 'text-gray-400'
            }`}>
              {suggestions}
            </div>
          </div>
          <h4 className={`text-lg font-semibold mb-2 ${
            isDarkMode ? 'text-gray-200' : 'text-gray-900'
          }`}>Suggestions</h4>
          <p className={`text-sm ${
            isDarkMode ? 'text-gray-400' : 'text-gray-600'
          }`}>
            {suggestions > 0 
              ? 'Improvements recommended' 
              : 'No suggestions available'}
          </p>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-4 justify-center flex-wrap">
        <Button
          onClick={onShowReport}
          size="lg"
          className={`px-8 py-6 text-lg font-semibold transition-all duration-300 hover:scale-105 ${
            isDarkMode
              ? 'bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white shadow-lg shadow-blue-500/30'
              : 'bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white shadow-lg'
          }`}
        >
          <FileText className="mr-2 h-5 w-5" />
          View Detailed Report
        </Button>

        {onVisualReport && (
          <Button
            onClick={onVisualReport}
            size="lg"
            className={`px-8 py-6 text-lg font-semibold transition-all duration-300 hover:scale-105 ${
              isDarkMode
                ? 'bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white shadow-lg shadow-purple-500/30'
                : 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white shadow-lg shadow-purple-400/40'
            }`}
          >
            <TrendingUp className="mr-2 h-5 w-5" />
            Visual Architecture Report
          </Button>
        )}

        <Button
          onClick={onNewAnalysis}
          variant="outline"
          size="lg"
          className={`px-8 py-6 text-lg font-semibold transition-all duration-300 hover:scale-105 ${
            isDarkMode
              ? 'border-gray-500 text-gray-400 hover:bg-gray-500/10'
              : 'border-gray-500 text-gray-600 hover:bg-gray-50'
          }`}
        >
          New Analysis
        </Button>
      </div>

      {/* Quick Tips */}
      {totalIssues > 0 && (
        <div className={`mt-8 p-6 rounded-xl border ${
          isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-gray-50 border-gray-200'
        }`}>
          <h4 className={`text-lg font-semibold mb-3 ${
            isDarkMode ? 'text-gray-200' : 'text-gray-900'
          }`}>Quick Recommendations</h4>
          <ul className={`space-y-2 text-sm ${
            isDarkMode ? 'text-gray-400' : 'text-gray-600'
          }`}>
            {criticalIssues > 0 && (
              <li className="flex items-start gap-2">
                <span className="text-red-500">•</span>
                <span>Address critical issues first - they may impact functionality or security</span>
              </li>
            )}
            {warnings > 5 && (
              <li className="flex items-start gap-2">
                <span className="text-yellow-500">•</span>
                <span>Review warnings to improve code maintainability</span>
              </li>
            )}
            {suggestions > 0 && (
              <li className="flex items-start gap-2">
                <span className="text-blue-500">•</span>
                <span>Consider implementing suggestions for better code quality</span>
              </li>
            )}
            <li className="flex items-start gap-2">
              <span className="text-green-500">•</span>
              <span>View the detailed report for specific file locations and fix recommendations</span>
            </li>
          </ul>
        </div>
      )}
    </div>
  )
}
