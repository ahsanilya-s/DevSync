import React from "react";
import { User, History, Settings, Plus, Github, Upload } from "lucide-react";
import { Button } from "./ui/button";

export function Sidebar({ onNewAnalysis, onSettingsClick, onHistoryClick, onGitHubClick, onUserAccountClick, onStartAnalysis, isDarkMode, activeSection }) {
  return (
    <div className={`fixed left-0 top-0 h-screen w-64 border-r flex flex-col p-4 transition-all duration-500 ${
      isDarkMode 
        ? 'bg-gray-900 border-gray-800'
        : 'bg-white border-gray-200' 
    }`}>
      {/* Logo */}
      <div className="mb-8 px-2 ml-2 flex items-center gap-3">
        <img 
          src={isDarkMode ? "/logo_for_blacktheme.png" : "/logo_for_whitetheme.png"} 
          alt="DevSync" 
          className="h-8 w-auto transition-opacity duration-500" 
        />
        <span className={`text-xl font-bold bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent`}>
          DevSync
        </span>
      </div>

      {/* New Analysis Button */}
      <Button
        onClick={onNewAnalysis}
        className="w-full mb-6 bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white shadow-lg shadow-blue-500/30 transition-all duration-300 hover:shadow-blue-500/50"
      >
        <Plus className="mr-2 h-4 w-4" />
        New Analysis
      </Button>

      {/* Navigation Items */}
      <nav className="flex-1 space-y-2">
        <button
          onClick={onStartAnalysis}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 group ${
            activeSection === 'upload'
              ? isDarkMode 
                ? 'bg-gray-800 text-blue-400'
                : 'bg-gray-100 text-blue-600'
              : isDarkMode 
                ? 'text-gray-300 hover:bg-gray-800 hover:text-blue-400'
                : 'text-gray-700 hover:bg-gray-100 hover:text-blue-600'
          }`}
        >
          <Upload className="h-5 w-5 group-hover:drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
          <span>Start Analysis</span>
        </button>

        <button
          onClick={onUserAccountClick}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 group ${
            isDarkMode 
              ? 'text-gray-300 hover:bg-gray-800 hover:text-blue-400'
              : 'text-gray-700 hover:bg-gray-100 hover:text-blue-600'
          }`}
        >
          <User className="h-5 w-5 group-hover:drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
          <span>User Account</span>
        </button>

        <button
          onClick={onHistoryClick}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 group ${
            isDarkMode 
              ? 'text-gray-300 hover:bg-gray-800 hover:text-blue-400'
              : 'text-gray-700 hover:bg-gray-100 hover:text-blue-600'
          }`}
        >
          <History className="h-5 w-5 group-hover:drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
          <span>History</span>
        </button>

        <button
          onClick={onGitHubClick}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 group ${
            isDarkMode 
              ? 'text-gray-300 hover:bg-gray-800 hover:text-blue-400'
              : 'text-gray-700 hover:bg-gray-100 hover:text-blue-600'
          }`}
        >
          <Github className="h-5 w-5 group-hover:drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
          <span>GitHub</span>
        </button>

        <button
          onClick={onSettingsClick}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 group ${
            isDarkMode 
              ? 'text-gray-300 hover:bg-gray-800 hover:text-blue-400'
              : 'text-gray-700 hover:bg-gray-100 hover:text-blue-600'
          }`}
        >
          <Settings className="h-5 w-5 group-hover:drop-shadow-[0_0_8px_rgba(59,130,246,0.8)]" />
          <span>Settings</span>
        </button>
      </nav>

      {/* Footer */}
      <div className={`pt-4 border-t text-sm px-2 transition-all duration-500 ${
        isDarkMode 
          ? 'border-gray-800 text-gray-500'
          : 'border-gray-200 text-gray-400'
      }`}>
        <p>© 2025 DevSync</p>
      </div>
    </div>
  );
}