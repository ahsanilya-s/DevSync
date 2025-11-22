import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Book, Code, Settings, Upload, FileText, Zap, Sun, Moon } from 'lucide-react'

const Documentation = () => {
  const navigate = useNavigate()
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [activeSection, setActiveSection] = useState('getting-started')

  const sections = [
    { id: 'getting-started', title: 'Getting Started', icon: <Zap className="w-4 h-4" /> },
    { id: 'uploading', title: 'Uploading Projects', icon: <Upload className="w-4 h-4" /> },
    { id: 'analysis', title: 'Analysis Features', icon: <Code className="w-4 h-4" /> },
    { id: 'reports', title: 'Understanding Reports', icon: <FileText className="w-4 h-4" /> },
    { id: 'configuration', title: 'Configuration', icon: <Settings className="w-4 h-4" /> }
  ]

  const content = {
    'getting-started': {
      title: 'Getting Started with DevSync',
      content: (
        <div className="space-y-6">
          <p className={isDarkMode ? 'text-gray-300' : 'text-gray-900'}>Welcome to DevSync! This guide will help you get started with analyzing your Java projects.</p>
          
          <div className={`border rounded-lg p-6 ${
            isDarkMode ? 'bg-white/5 border-white/10' : 'bg-gray-50 border-gray-200'
          }`}>
            <h3 className="text-xl font-semibold mb-4">Quick Start Steps</h3>
            <ol className={`space-y-3 list-decimal list-inside ${
              isDarkMode ? 'text-gray-300' : 'text-gray-900'
            }`}>
              <li>Create your DevSync account</li>
              <li>Log in to your dashboard</li>
              <li>Upload your Java project as a ZIP file</li>
              <li>Wait for the analysis to complete</li>
              <li>Review your code quality report</li>
            </ol>
          </div>

          <div className="bg-blue-500/10 border border-blue-500/20 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4 text-blue-500">System Requirements</h3>
            <ul className={`space-y-2 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-900'
            }`}>
              <li>• Java projects (Java 8 or higher)</li>
              <li>• ZIP file format for uploads</li>
              <li>• Maximum file size: 100MB</li>
              <li>• Modern web browser with JavaScript enabled</li>
            </ul>
          </div>
        </div>
      )
    },
    'uploading': {
      title: 'Uploading Your Java Projects',
      content: (
        <div className="space-y-6">
          <p className={isDarkMode ? 'text-gray-300' : 'text-gray-900'}>Learn how to properly prepare and upload your Java projects for analysis.</p>
          
          <div className={`border rounded-lg p-6 ${
            isDarkMode ? 'bg-white/5 border-white/10' : 'bg-gray-50 border-gray-200'
          }`}>
            <h3 className="text-xl font-semibold mb-4">Preparing Your Project</h3>
            <div className={`space-y-4 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-900'
            }`}>
              <p>1. <strong>Clean your project:</strong> Remove build artifacts, compiled classes, and temporary files</p>
              <p>2. <strong>Include source files:</strong> Ensure all .java files are included in your ZIP</p>
              <p>3. <strong>Maintain structure:</strong> Keep your package structure intact</p>
              <p>4. <strong>Check file size:</strong> Ensure your ZIP file is under 100MB</p>
            </div>
          </div>

          <div className="bg-green-500/10 border border-green-500/20 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4 text-green-500">Supported File Types</h3>
            <ul className={`grid grid-cols-2 gap-2 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-900'
            }`}>
              <li>• .java (source files)</li>
              <li>• .properties (configuration)</li>
              <li>• .xml (Maven/Gradle configs)</li>
              <li>• .md (documentation)</li>
            </ul>
          </div>
        </div>
      )
    },
    'analysis': {
      title: 'Analysis Features Overview',
      content: (
        <div className="space-y-6">
          <p className="text-gray-300">DevSync provides comprehensive analysis of your Java code using advanced AST parsing.</p>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white/5 border border-white/10 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-3 text-red-400">Code Smells</h3>
              <ul className="space-y-2 text-gray-300 text-sm">
                <li>• Long methods detection</li>
                <li>• Large parameter lists</li>
                <li>• Duplicate code blocks</li>
                <li>• Complex conditionals</li>
              </ul>
            </div>
            
            <div className="bg-white/5 border border-white/10 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-3 text-yellow-400">Security Issues</h3>
              <ul className="space-y-2 text-gray-300 text-sm">
                <li>• SQL injection vulnerabilities</li>
                <li>• Input validation issues</li>
                <li>• Hardcoded credentials</li>
                <li>• Insecure configurations</li>
              </ul>
            </div>
            
            <div className="bg-white/5 border border-white/10 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-3 text-blue-400">Performance</h3>
              <ul className="space-y-2 text-gray-300 text-sm">
                <li>• Memory leak detection</li>
                <li>• Inefficient algorithms</li>
                <li>• Resource management</li>
                <li>• Optimization suggestions</li>
              </ul>
            </div>
            
            <div className="bg-white/5 border border-white/10 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-3 text-purple-400">Quality Metrics</h3>
              <ul className="space-y-2 text-gray-300 text-sm">
                <li>• Cyclomatic complexity</li>
                <li>• Maintainability index</li>
                <li>• Code coverage analysis</li>
                <li>• Technical debt assessment</li>
              </ul>
            </div>
          </div>
        </div>
      )
    },
    'reports': {
      title: 'Understanding Your Reports',
      content: (
        <div className="space-y-6">
          <p className="text-gray-300">Learn how to interpret and act on your DevSync analysis reports.</p>
          
          <div className="bg-white/5 border border-white/10 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4">Severity Levels</h3>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-4 h-4 bg-red-500 rounded-full"></div>
                <span className="font-semibold text-red-400">Critical:</span>
                <span className="text-gray-300">Immediate attention required - security vulnerabilities, major bugs</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-4 h-4 bg-yellow-500 rounded-full"></div>
                <span className="font-semibold text-yellow-400">High:</span>
                <span className="text-gray-300">Should be addressed soon - performance issues, code smells</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-4 h-4 bg-orange-500 rounded-full"></div>
                <span className="font-semibold text-orange-400">Medium:</span>
                <span className="text-gray-300">Consider improving - maintainability concerns</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-4 h-4 bg-gray-500 rounded-full"></div>
                <span className="font-semibold text-gray-400">Low:</span>
                <span className="text-gray-300">Minor improvements - style and convention issues</span>
              </div>
            </div>
          </div>

          <div className="bg-blue-500/10 border border-blue-500/20 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4 text-blue-400">Report Sections</h3>
            <ul className="space-y-2 text-gray-300">
              <li>• <strong>Executive Summary:</strong> High-level overview of code quality</li>
              <li>• <strong>Detailed Findings:</strong> Specific issues with file locations</li>
              <li>• <strong>Recommendations:</strong> AI-powered suggestions for improvements</li>
              <li>• <strong>Metrics Dashboard:</strong> Visual representation of code quality metrics</li>
            </ul>
          </div>
        </div>
      )
    },
    'configuration': {
      title: 'Configuration Options',
      content: (
        <div className="space-y-6">
          <p className="text-gray-300">Customize DevSync analysis to match your project requirements and coding standards.</p>
          
          <div className="bg-white/5 border border-white/10 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4">Analysis Thresholds</h3>
            <div className="space-y-4 text-gray-300">
              <p><strong>Method Length:</strong> Configure maximum method length (default: 50 lines)</p>
              <p><strong>Parameter Count:</strong> Set maximum parameters per method (default: 7)</p>
              <p><strong>Cyclomatic Complexity:</strong> Define complexity thresholds (default: 10)</p>
              <p><strong>Class Size:</strong> Maximum lines per class (default: 500)</p>
            </div>
          </div>

          <div className="bg-purple-500/10 border border-purple-500/20 rounded-lg p-6">
            <h3 className="text-xl font-semibold mb-4 text-purple-400">Custom Rules</h3>
            <p className="text-gray-300 mb-4">Create custom analysis rules for your specific coding standards:</p>
            <ul className="space-y-2 text-gray-300">
              <li>• Naming conventions</li>
              <li>• Architecture patterns</li>
              <li>• Security requirements</li>
              <li>• Performance benchmarks</li>
            </ul>
          </div>
        </div>
      )
    }
  }

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-gradient-to-br from-gray-950 via-purple-950 to-blue-950 text-white' : 'bg-white text-gray-900'}`}>
      <div className="container mx-auto px-8 py-8 max-w-7xl">
        <div className="flex justify-between items-center mb-8">
          <button
            onClick={() => navigate('/')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg backdrop-blur-sm border transition-all ${
              isDarkMode ? 'bg-white/10 border-white/20 hover:bg-white/20' : 'bg-gray-100 border-gray-300 hover:bg-gray-200'
            }`}
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Home
          </button>
          <button
            onClick={() => setIsDarkMode(!isDarkMode)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg backdrop-blur-sm border transition-all ${
              isDarkMode ? 'bg-white/10 border-white/20 hover:bg-white/20' : 'bg-gray-100 border-gray-300 hover:bg-gray-200'
            }`}
          >
            {isDarkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
            {isDarkMode ? 'Light' : 'Dark'} Mode
          </button>
        </div>

        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold mb-6 flex items-center justify-center gap-3">
            <Book className="w-12 h-12 text-blue-500" />
            Documentation
          </h1>
          <p className={`text-xl max-w-3xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-900'
          }`}>
            Complete guide to using DevSync for Java code analysis
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          <div className="lg:col-span-1">
            <div className={`backdrop-blur-sm border rounded-xl p-6 sticky top-8 ${
              isDarkMode ? 'bg-white/10 border-white/20' : 'bg-gray-50 border-gray-200'
            }`}>
              <h3 className="text-lg font-semibold mb-4">Table of Contents</h3>
              <nav className="space-y-2">
                {sections.map((section) => (
                  <button
                    key={section.id}
                    onClick={() => setActiveSection(section.id)}
                    className={`w-full text-left px-3 py-2 rounded-lg flex items-center gap-2 transition-all ${
                      activeSection === section.id 
                        ? 'bg-blue-500/20 text-blue-500 border border-blue-500/30' 
                        : isDarkMode 
                          ? 'hover:bg-white/10 text-gray-300'
                          : 'hover:bg-gray-100 text-gray-600'
                    }`}
                  >
                    {section.icon}
                    {section.title}
                  </button>
                ))}
              </nav>
            </div>
          </div>

          <div className="lg:col-span-3">
            <div className={`backdrop-blur-sm border rounded-xl p-8 ${
              isDarkMode ? 'bg-white/10 border-white/20' : 'bg-gray-50 border-gray-200'
            }`}>
              <h2 className="text-3xl font-bold mb-6">{content[activeSection].title}</h2>
              {content[activeSection].content}
            </div>
          </div>
        </div>

        {/* Footer */}
        <footer className={`mt-20 py-12 border-t ${
          isDarkMode ? 'border-gray-700 bg-gray-900/50' : 'border-gray-200 bg-gray-50'
        }`}>
          <div className="container mx-auto px-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div>
                <h4 className="font-semibold mb-4">DevSync</h4>
                <p className={`text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>Advanced Java code analysis for better software quality.</p>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Guides</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li>Getting Started</li>
                  <li>API Reference</li>
                  <li>Best Practices</li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Resources</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/features')} className="hover:text-blue-500">Features</button></li>
                  <li><button onClick={() => navigate('/pricing')} className="hover:text-blue-500">Pricing</button></li>
                  <li><button onClick={() => navigate('/support')} className="hover:text-blue-500">Support</button></li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Contact</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/contact')} className="hover:text-blue-500">Get in Touch</button></li>
                  <li><button onClick={() => navigate('/about')} className="hover:text-blue-500">About Us</button></li>
                </ul>
              </div>
            </div>
            <div className={`mt-8 pt-8 border-t text-center text-sm ${
              isDarkMode ? 'border-gray-700 text-gray-500' : 'border-gray-200 text-gray-500'
            }`}>
              © 2025 DevSync. All rights reserved.
            </div>
          </div>
        </footer>
      </div>
    </div>
  )
}

export default Documentation