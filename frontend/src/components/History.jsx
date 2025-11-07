import React, { useState, useEffect } from 'react';
import { X, FileText, Folder, Clock } from 'lucide-react';
import api from '../api';

export function History({ isOpen, onClose, isDarkMode }) {
  const [history, setHistory] = useState([]);
  const [selectedReport, setSelectedReport] = useState(null);
  const [reportContent, setReportContent] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen) {
      fetchHistory();
    }
  }, [isOpen]);

  const fetchHistory = async () => {
    try {
      console.log('Fetching history...');
      const response = await api.get('/history');
      console.log('History response:', response.data);
      setHistory(response.data);
    } catch (error) {
      console.error('Failed to fetch history:', error);
      console.error('Error details:', error.response?.data);
    }
  };

  const handleReportClick = async (folderName) => {
    setLoading(true);
    try {
      const response = await api.get(`/history/report/${folderName}`);
      setReportContent(response.data);
      setSelectedReport(folderName);
    } catch (error) {
      console.error('Failed to fetch report:', error);
    } finally {
      setLoading(false);
    }
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
                  onClick={() => handleReportClick(item.folderName)}
                  className={`p-3 rounded-lg cursor-pointer transition-all duration-200 ${
                    selectedReport === item.folderName
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
                        {item.folderName}
                      </p>
                      {item.reportFile && (
                        <div className="flex items-center gap-1 mt-1">
                          <FileText className={`h-3 w-3 ${
                            isDarkMode ? 'text-gray-400' : 'text-gray-500'
                          }`} />
                          <span className={`text-xs ${
                            isDarkMode ? 'text-gray-400' : 'text-gray-500'
                          }`}>
                            {item.reportFile}
                          </span>
                        </div>
                      )}
                      <div className="flex items-center gap-1 mt-1">
                        <Clock className={`h-3 w-3 ${
                          isDarkMode ? 'text-gray-500' : 'text-gray-400'
                        }`} />
                        <span className={`text-xs ${
                          isDarkMode ? 'text-gray-500' : 'text-gray-400'
                        }`}>
                          {formatDate(item.lastModified)}
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
              <h4 className={`text-lg font-semibold mb-4 ${
                isDarkMode ? 'text-gray-100' : 'text-gray-900'
              }`}>
                Report: {selectedReport}
              </h4>
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
    </div>
  );
}