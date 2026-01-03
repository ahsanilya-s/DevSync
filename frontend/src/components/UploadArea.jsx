import React, { useState } from "react";
import { Upload, FileArchive } from "lucide-react";
import { Button } from "./ui/button";
import { Card } from "./ui/card";

export function UploadArea({ onAnalyze, onVisualReport, isDarkMode }) {
  const [isDragging, setIsDragging] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files[0];
    if (file && file.name.endsWith(".zip")) {
      setSelectedFile(file);
    }
  };

  const handleFileSelect = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleAnalyze = () => {
    if (selectedFile) {
      onAnalyze(selectedFile);
    }
  };

  const handleVisualReport = () => {
    if (selectedFile) {
      onVisualReport(selectedFile);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-200px)]">
      <Card className={`w-full max-w-2xl p-12 mb-8 backdrop-blur-sm transition-all duration-500 ${
        isDarkMode 
          ? 'bg-gray-800/50 border-gray-700'
          : 'bg-white/90 border-blue-100 shadow-xl shadow-blue-100/50'
      }`}>
        <div className="text-center space-y-6">
          <div className="flex justify-center">
            <div className={`p-6 rounded-full bg-gradient-to-br border transition-all duration-500 ${
              isDarkMode 
                ? 'from-blue-500/20 to-cyan-500/20 border-blue-500/30'
                : 'from-blue-50 to-cyan-50 border-blue-200 shadow-lg shadow-blue-200/30'
            }`}>
              <FileArchive className={`h-16 w-16 transition-colors duration-500 ${
                isDarkMode ? 'text-blue-400' : 'text-blue-600'
              }`} />
            </div>
          </div>

          <div>
            <h2 className={`mb-2 text-2xl font-bold transition-colors duration-500 ${
              isDarkMode ? 'text-gray-100' : 'text-gray-900'
            }`}>Upload Your Project</h2>
            <p className={`transition-colors duration-500 ${
              isDarkMode ? 'text-gray-400' : 'text-gray-700'
            }`}>
              Analyze your Java codebase for quality issues and code smells
            </p>
          </div>

          <div
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            className={`relative border-2 border-dashed rounded-xl p-12 transition-all duration-300 ${
              isDragging
                ? "border-blue-500 bg-blue-500/10"
                : isDarkMode
                  ? "border-gray-600 hover:border-blue-500/50 bg-gray-900/30"
                  : "border-blue-200 hover:border-blue-300 bg-blue-50/30"
            }`}
          >
            <input
              type="file"
              accept=".zip"
              onChange={handleFileSelect}
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            />
            <div className="flex flex-col items-center gap-4">
              <Upload className={`h-12 w-12 transition-colors duration-500 ${
                isDarkMode ? 'text-gray-500' : 'text-gray-400'
              }`} />
              <div className="text-center">
                <p className={`mb-1 font-medium transition-colors duration-500 ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-700'
                }`}>
                  {selectedFile ? (
                    <span className={`font-semibold ${
                      isDarkMode ? 'text-blue-400' : 'text-blue-600'
                    }`}>{selectedFile.name}</span>
                  ) : (
                    "Drop your project ZIP file here or click to browse"
                  )}
                </p>
                <p className={`text-sm transition-colors duration-500 ${
                  isDarkMode ? 'text-gray-500' : 'text-gray-600'
                }`}>
                  Supports Java projects only (.zip)
                </p>
              </div>
            </div>
          </div>

          <div className="space-y-4">
            <Button
              onClick={handleAnalyze}
              disabled={!selectedFile}
              className={`w-full py-6 bg-gradient-to-r text-white shadow-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed ${
                isDarkMode
                  ? 'from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 shadow-blue-500/30 hover:shadow-blue-500/50'
                  : 'from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 shadow-blue-400/40 hover:shadow-blue-500/60'
              }`}
            >
              <span className="text-lg font-semibold">Analyze Project</span>
            </Button>
            
            <Button
              onClick={handleVisualReport}
              disabled={!selectedFile}
              className={`w-full py-4 bg-gradient-to-r text-white shadow-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed ${
                isDarkMode
                  ? 'from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 shadow-purple-500/30 hover:shadow-purple-500/50'
                  : 'from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 shadow-purple-400/40 hover:shadow-purple-500/60'
              }`}
            >
              <span className="text-base font-semibold">Generate Visual Architecture Report</span>
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}