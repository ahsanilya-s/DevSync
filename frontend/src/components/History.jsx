import React, { useState, useEffect } from 'react';
import { X, FileText, Folder, Clock, Eye } from 'lucide-react';
import { VisualReport } from './VisualReport';
import api from '../api';

export function History({ isOpen, onClose, isDarkMode }) {
  const [history, setHistory] = useState([]);
  const [selectedReport, setSelectedReport] = useState(null);
  const [reportContent, setReportContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [showVisualReport, setShowVisualReport] = useState(false);
  const [selectedProjectPath, setSelectedProjectPath] = useState('');
  const [selectedProjectName, setSelectedProjectName] = useState('');

  useEffect(() => {
    if (isOpen) {
      fetchHistory();
    }
  }, [isOpen]);

  const fetchHistory = async () => {
    try {
      const userId = localStorage.getItem('userId') || 'anonymous';
      const response = await api.get(`/upload/history?userId=${userId}`);
      setHistory(response.data);
    } catch (error) {
      console.error('Failed to fetch history:', error);
    }
  };

  const handleReportClick = async (reportPath, projectPath, projectName) => {
    setLoading(true);
    try {
      const userId = localStorage.getItem('userId') || 'anonymous';
      const response = await api.get(`/upload/report?path=${encodeURIComponent(reportPath)}&userId=${userId}`);
      setReportContent(response.data);
      setSelectedReport(reportPath);
      setSelectedProjectPath(projectPath || reportPath.substring(0, reportPath.lastIndexOf('/')));
      setSelectedProjectName(projectName);
    } catch (error) {
      console.error('Failed to fetch report:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleShowVisualReport = () => {
    setShowVisualReport(true);
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <div className={`rounded-xl w-full max-w-4xl h-[80vh] flex transition-all duration-500 ${
        isDarkMode 
          ? 'bg-gray-900 border border-gray-700'
          : 'bg-white border border-gray-200'
      }`}>
        
        {/* History List */}
        <div className={`w-1/3 border-r p-4 ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <div className="flex items-center justify-between mb-4">
            <h3 className={`text-lg font-semibold ${
              isDarkMode ? 'text-gray-100' : 'text-gray-900'
            }`}>Analysis History</h3>
            <button
              onClick={onClose}
              className={`p-1 rounded-lg transition-colors ${
                isDarkMode 
                  ? 'hover:bg-gray-800 text-gray-400 hover:text-gray-200'
                  : 'hover:bg-gray-100 text-gray-600 hover:text-gray-800'
              }`}
            >
              <X className="h-5 w-5" />
            </button>
          </div>
          
          <div className="space-y-2 overflow-y-auto max-h-[calc(80vh-100px)]">
            {history.length === 0 ? (
              <p className={`text-center py-8 ${
                isDarkMode ? 'text-gray-400' : 'text-gray-600'
              }`}>
                No analysis history found
              </p>
            ) : (
              history.map((item, index) => (
                <div
                  key={index}
                  onClick={() => handleReportClick(item.reportPath, item.projectPath, item.projectName)}
                  className={`p-3 rounded-lg cursor-pointer transition-all duration-200 ${
                    selectedReport === item.reportPath
                      ? isDarkMode 
                        ? 'bg-blue-600/20 border border-blue-500/30'
                        : 'bg-blue-50 border border-blue-200'
                      : isDarkMode
                        ? 'hover:bg-gray-800 border border-transparent'
                        : 'hover:bg-gray-50 border border-transparent'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <Folder className={`h-5 w-5 mt-0.5 ${
                      isDarkMode ? 'text-blue-400' : 'text-blue-600'
                    }`} />
                    <div className="flex-1 min-w-0">
                      <p className={`font-medium truncate ${
                        isDarkMode ? 'text-gray-200' : 'text-gray-900'
                      }`}>
                        {item.projectName}
                      </p>
                      <div className="flex items-center gap-4 mt-1 text-xs">
                        <span className={`${
                          isDarkMode ? 'text-red-400' : 'text-red-600'
                        }`}>🔴 {item.criticalIssues}</span>
                        <span className={`${
                          isDarkMode ? 'text-yellow-400' : 'text-yellow-600'
                        }`}>🟡 {item.warnings}</span>
                        <span className={`${
                          isDarkMode ? 'text-orange-400' : 'text-orange-600'
                        }`}>🟠 {item.suggestions}</span>
                      </div>
                      <div className="flex items-center gap-1 mt-1">
                        <Clock className={`h-3 w-3 ${
                          isDarkMode ? 'text-gray-500' : 'text-gray-400'
                        }`} />
                        <span className={`text-xs ${
                          isDarkMode ? 'text-gray-500' : 'text-gray-400'
                        }`}>
                          {new Date(item.analysisDate).toLocaleString()}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Report Content */}
        <div className="flex-1 p-4">
          {loading ? (
            <div className="flex items-center justify-center h-full">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            </div>
          ) : selectedReport ? (
            <div>
              <div className="flex items-center justify-between mb-4">
                <h4 className={`text-lg font-semibold ${
                  isDarkMode ? 'text-gray-100' : 'text-gray-900'
                }`}>
                  {selectedProjectName}
                </h4>
                <button
                  onClick={handleShowVisualReport}
                  className={`px-4 py-2 rounded-lg flex items-center gap-2 transition-colors ${
                    isDarkMode
                      ? 'bg-blue-600 hover:bg-blue-700 text-white'
                      : 'bg-blue-500 hover:bg-blue-600 text-white'
                  }`}
                >
                  <Eye className="h-4 w-4" />
                  Visual Report
                </button>
              </div>
              <div className={`p-4 rounded-lg border font-mono text-sm whitespace-pre-wrap overflow-y-auto max-h-[calc(80vh-120px)] ${
                isDarkMode 
                  ? 'bg-gray-800/50 border-gray-700 text-gray-300'
                  : 'bg-gray-50 border-gray-200 text-gray-700'
              }`}>
                {reportContent}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center h-full">
              <p className={`text-center ${
                isDarkMode ? 'text-gray-400' : 'text-gray-600'
              }`}>
                Select a report from the history to view its content
              </p>
            </div>
          )}
        </div>
      </div>

      <VisualReport
        reportContent={reportContent}
        isOpen={showVisualReport}
        onClose={() => setShowVisualReport(false)}
        isDarkMode={isDarkMode}
        projectName={selectedProjectName}
        projectPath={selectedProjectPath}
      />
    </div>
  );
}