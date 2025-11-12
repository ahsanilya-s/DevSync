import React from "react";
import { LogOut, Sun, Moon, Shield } from "lucide-react";
import { Button } from "./ui/button";

export function Header({ onLogout, onAdminPanel, isDarkMode, onToggleTheme }) {
  return (
    <header className={`sticky top-0 z-50 backdrop-blur-xl border-b-2 shadow-lg transition-all duration-500 ${
      isDarkMode 
        ? 'bg-gray-900/60 border-blue-500/50'
        : 'bg-white/80 border-gray-200' 
    }`}>
      <div className="flex items-center justify-between px-8 py-4">
        {/* Empty left space for cleaner look */}
        <div></div>

        {/* Right Side */}
        <div className="flex items-center gap-6">
          <div className={`flex items-center gap-4 ${
            isDarkMode ? 'text-gray-300' : 'text-gray-700'
          }`}>
            <span>Welcome, User 👋</span>
            <span className={isDarkMode ? 'text-gray-500' : 'text-gray-400'}>|</span>
            <div className="flex items-center gap-2">
              <span>Backend:</span>
              <span className="flex items-center gap-1 text-green-400">
                Online
                <span className="inline-block w-2 h-2 bg-green-400 rounded-full animate-pulse"></span>
              </span>
            </div>
          </div>
          
          <Button
            onClick={onToggleTheme}
            variant="outline"
            size="sm"
            className={`transition-all duration-500 ${
              isDarkMode
                ? 'border-purple-500/50 text-purple-300 hover:bg-purple-500/10 hover:text-purple-200'
                : 'border-gray-300 text-gray-700 hover:bg-gray-100'
            }`}
          >
            {isDarkMode ? <Sun className="h-4 w-4 mr-2" /> : <Moon className="h-4 w-4 mr-2" />}
            Toggle Theme
          </Button>
          
          {onAdminPanel && (
            <Button
              onClick={onAdminPanel}
              variant="outline"
              size="sm"
              className={`transition-all duration-500 ${
                isDarkMode
                  ? 'border-blue-500/50 text-blue-300 hover:bg-blue-500/10 hover:text-blue-200'
                  : 'border-blue-300 text-blue-700 hover:bg-blue-100'
              }`}
            >
              <Shield className="h-4 w-4 mr-2" />
              Admin Panel
            </Button>
          )}
          
          <Button
            variant="ghost"
            className={`transition-colors duration-300 ${
              isDarkMode 
                ? 'text-gray-300 hover:text-red-400 hover:bg-red-400/10'
                : 'text-gray-700 hover:text-red-400 hover:bg-red-400/10'
            }`}
            onClick={onLogout}
          >
            <LogOut className="h-4 w-4 mr-2" />
            Logout
          </Button>
        </div>
      </div>
    </header>
  );
}