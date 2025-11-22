import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Code, Shield, Zap, Search, Bug, Target, Sun, Moon } from 'lucide-react'

const Features = () => {
  const navigate = useNavigate()
  const [isDarkMode, setIsDarkMode] = useState(false)

  const features = [
    {
      icon: <Code className="w-8 h-8" />,
      title: "AST-Based Analysis",
      description: "Deep Abstract Syntax Tree parsing for comprehensive Java code analysis with precise detection of code patterns and structures.",
      details: ["Parse Java source code into AST", "Analyze code structure and patterns", "Detect complex code relationships"]
    },
    {
      icon: <Bug className="w-8 h-8" />,
      title: "Code Smell Detection",
      description: "Identify anti-patterns, long methods, duplicate code blocks, and other maintainability issues in your Java projects.",
      details: ["Long method detection", "Duplicate code identification", "Complex class analysis", "Parameter list optimization"]
    },
    {
      icon: <Shield className="w-8 h-8" />,
      title: "Security Analysis",
      description: "Comprehensive security vulnerability scanning to detect potential exploits and security weaknesses in your code.",
      details: ["SQL injection detection", "XSS vulnerability scanning", "Input validation analysis", "Security best practices"]
    },
    {
      icon: <Zap className="w-8 h-8" />,
      title: "Performance Optimization",
      description: "Identify performance bottlenecks and get AI-powered suggestions for optimizing your Java application performance.",
      details: ["Memory leak detection", "Algorithm efficiency analysis", "Resource usage optimization", "Performance metrics"]
    },
    {
      icon: <Search className="w-8 h-8" />,
      title: "Magic Number Detection",
      description: "Find hardcoded numeric literals and suggest meaningful constants to improve code readability and maintainability.",
      details: ["Hardcoded value identification", "Constant extraction suggestions", "Configuration externalization", "Code clarity improvement"]
    },
    {
      icon: <Target className="w-8 h-8" />,
      title: "Quality Metrics",
      description: "Comprehensive code quality scoring with detailed metrics and maintainability index for your Java projects.",
      details: ["Cyclomatic complexity", "Code coverage analysis", "Maintainability index", "Technical debt assessment"]
    }
  ]

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-gradient-to-br from-gray-950 via-purple-950 to-blue-950 text-white' : 'bg-white text-gray-900'}`}>
      <div className="container mx-auto px-6 py-8">
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

        <div className="text-center mb-16">
          <h1 className="text-5xl font-bold mb-6">DevSync Features</h1>
          <p className={`text-xl max-w-3xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-900'
          }`}>
            Comprehensive Java code analysis tools designed to improve code quality, security, and maintainability
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div key={index} className={`backdrop-blur-sm border rounded-xl p-6 transition-all duration-300 ${
              isDarkMode ? 'bg-white/10 border-white/20 hover:bg-white/15' : 'bg-gray-50 border-gray-200 hover:bg-gray-100'
            }`}>
              <div className="text-blue-500 mb-4">{feature.icon}</div>
              <h3 className="text-xl font-semibold mb-3">{feature.title}</h3>
              <p className={`mb-4 ${
                isDarkMode ? 'text-gray-300' : 'text-gray-900'
              }`}>{feature.description}</p>
              <ul className="space-y-2">
                {feature.details.map((detail, idx) => (
                  <li key={idx} className={`text-sm flex items-center gap-2 ${
                    isDarkMode ? 'text-gray-400' : 'text-gray-800'
                  }`}>
                    <div className="w-1.5 h-1.5 bg-blue-500 rounded-full"></div>
                    {detail}
                  </li>
                ))}
              </ul>
            </div>
          ))}
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
                <h4 className="font-semibold mb-4">Features</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li>Code Smell Detection</li>
                  <li>Security Analysis</li>
                  <li>Performance Insights</li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Resources</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/documentation')} className="hover:text-blue-500">Documentation</button></li>
                  <li><button onClick={() => navigate('/support')} className="hover:text-blue-500">Support</button></li>
                  <li><button onClick={() => navigate('/about')} className="hover:text-blue-500">About</button></li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Contact</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/contact')} className="hover:text-blue-500">Get in Touch</button></li>
                  <li><button onClick={() => navigate('/pricing')} className="hover:text-blue-500">Pricing</button></li>
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

export default Features