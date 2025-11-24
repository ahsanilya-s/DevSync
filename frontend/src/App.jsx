import React, { useState, useEffect } from 'react'
import { Routes, Route, useNavigate } from 'react-router-dom'
import { Toaster } from './components/ui/sonner'
import { Card } from './components/ui/card'
import { Sun, Moon } from 'lucide-react'

// Import existing pages
import Home from './pages/Home'
import Login from './pages/Login'
import Signup from './pages/Signup'
import AdminPanel from './pages/AdminPanel'
import AdminLogin from './pages/AdminLogin'
import Dashboard from './pages/Dashboard'

// Import new pages
import Features from './pages/Features'
import Pricing from './pages/Pricing'
import Documentation from './pages/Documentation'
import About from './pages/About'
import Contact from './pages/Contact'
import Support from './pages/Support'
import FileViewer from './pages/FileViewer'

// Import new UI components
import { Button } from './components/ui/button'

// Landing Page Component with Backend Integration
function LandingPage({ onLogin, onSignup, isDarkMode, setIsDarkMode }) {
  const navigate = useNavigate()

  useEffect(() => {
    const observerOptions = {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px'
    }

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-in-view')
        }
      })
    }, observerOptions)

    const animateElements = document.querySelectorAll('.animate-on-scroll')
    animateElements.forEach(el => observer.observe(el))

    return () => observer.disconnect()
  }, [])

  const handleToggleTheme = () => {
    setIsDarkMode(!isDarkMode)
  }

  const handleLearnMore = () => {
    const targetSection = document.getElementById('code-smell-section')
    if (targetSection) {
      targetSection.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      })
    }
  }

  return (
    <div className={`min-h-screen transition-all duration-500 ${
      isDarkMode
        ? 'bg-gradient-to-br from-gray-950 via-purple-950 to-blue-950 text-white'
        : 'bg-white text-gray-900'
    }`}>
      {/* Animated Gradient Orbs */}
      {isDarkMode && (
        <>
          <div className="fixed top-0 left-0 w-[500px] h-[500px] bg-blue-600/20 rounded-full blur-3xl animate-pulse pointer-events-none" />
          <div className="fixed bottom-0 right-0 w-[500px] h-[500px] bg-purple-600/20 rounded-full blur-3xl animate-pulse pointer-events-none" 
               style={{ animationDelay: '1s' }} />
          <div className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px] bg-violet-600/10 rounded-full blur-3xl animate-pulse pointer-events-none"
               style={{ animationDelay: '2s' }} />
        </>
      )}

      <div className="relative z-10">
        {/* Header */}
        <header className={`sticky top-0 z-50 backdrop-blur-xl border-b transition-all duration-500 ${
          isDarkMode 
            ? 'bg-gray-900/80 border-purple-500/30'
            : 'bg-white/80 border-gray-200' 
        }`}>
          <div className="container mx-auto px-6 py-4">
            <div className="flex items-center justify-between">
              {/* Logo */}
              <div className="flex items-center ml-4">
                <img 
                  src={isDarkMode ? "/logo_for_blacktheme.png" : "/logo_for_whitetheme.png"} 
                  alt="DevSync" 
                  className="h-10 w-auto transition-opacity duration-500" 
                />
              </div>

              {/* Right Side Buttons */}
              <div className="flex items-center gap-3">
                <Button
                  onClick={handleToggleTheme}
                  variant="outline"
                  size="sm"
                  className={`transition-all duration-500 ${
                    isDarkMode
                      ? 'border-purple-500/50 text-purple-300 hover:bg-purple-500/10 hover:text-purple-200'
                      : 'border-gray-300 text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  {isDarkMode ? <Moon className="h-4 w-4 mr-2" /> : <Sun className="h-4 w-4 mr-2" />}
                  Toggle Color Mode
                </Button>
                
                <Button
                  onClick={onSignup}
                  className={`transition-all duration-500 ${
                    isDarkMode
                      ? 'bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-blue-600 text-white shadow-lg shadow-blue-500/30'
                      : 'bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white'
                  }`}
                >
                  Sign Up
                </Button>
                
                <Button
                  onClick={onLogin}
                  className={`transition-all duration-500 ${
                    isDarkMode
                      ? 'bg-gradient-to-r from-green-600 to-green-500 hover:from-green-700 hover:to-green-600 text-white shadow-lg shadow-green-500/30'
                      : 'bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white'
                  }`}
                >
                  Login
                </Button>
                
                <Button
                  onClick={() => window.location.href = '/admin/login'}
                  variant="outline"
                  className={`transition-all duration-500 ${
                    isDarkMode
                      ? 'border-orange-500/50 text-orange-300 hover:bg-orange-500/10 hover:text-orange-200'
                      : 'border-orange-300 text-orange-700 hover:bg-orange-100'
                  }`}
                >
                  Admin Panel
                </Button>
              </div>
            </div>
          </div>
        </header>

        {/* Hero Section */}
        <section className="py-24 px-6 overflow-hidden">
          <div className="container mx-auto max-w-6xl text-center">
            <div className="animate-fade-in-up">
              <h1 className={`text-6xl font-semibold mb-8 transition-colors duration-500 ${
                isDarkMode ? 'text-white' : 'text-gray-900'
              }`}>
                Eliminate Java
                <br />
                <span className={`bg-gradient-to-r bg-clip-text text-transparent transition-all duration-500 ${
                  isDarkMode
                    ? 'from-blue-400 to-purple-400'
                    : 'from-blue-600 to-purple-600'
                }`}>
                  Code Smells
                </span>
              </h1>
            </div>
            
            <div className="animate-fade-in-up" style={{ animationDelay: '0.2s' }}>
              <p className={`text-xl mb-16 max-w-3xl mx-auto transition-colors duration-500 ${
                isDarkMode ? 'text-gray-300' : 'text-gray-600'
              }`}>
                Advanced AST-based analysis to detect code smells in Java projects with AI-powered 
                recommendations for enhanced code quality and maintainability.
              </p>
            </div>

            <div className="animate-fade-in-up flex gap-4 justify-center" style={{ animationDelay: '0.4s' }}>
              <Button
                onClick={onLogin}
                size="lg"
                className={`transition-all duration-500 hover:scale-105 ${
                  isDarkMode
                    ? 'bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white shadow-2xl shadow-blue-500/50'
                    : 'bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white shadow-lg'
                }`}
              >
                Analyze Your Code
              </Button>
              
              <Button
                onClick={handleLearnMore}
                variant="outline"
                size="lg"
                className={`transition-all duration-500 hover:scale-105 ${
                  isDarkMode
                    ? 'border-purple-500/50 text-purple-300 hover:bg-purple-500/10 hover:text-purple-200 hover:border-purple-500/60'
                    : 'border-gray-300 text-gray-700 hover:bg-gray-100'
                }`}
              >
                Learn More
              </Button>
            </div>
          </div>
        </section>

        {/* Code Smell Detection Section */}
        <section id="code-smell-section" className={`py-24 px-6 ${
          isDarkMode ? 'bg-gray-900/30' : 'bg-gray-50/50'
        }`}>
          <div className="container mx-auto max-w-6xl">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
              <div className="animate-on-scroll animate-slide-in-left">
                <h2 className={`text-4xl font-bold mb-6 ${
                  isDarkMode ? 'text-white' : 'text-gray-900'
                }`}>Detect Code Smells with AST Analysis</h2>
                <p className={`text-lg mb-8 ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-600'
                }`}>
                  Our advanced Abstract Syntax Tree (AST) parser analyzes your Java codebase to identify 
                  code smells, anti-patterns, and quality issues that impact maintainability.
                </p>
                <div className="space-y-4">
                  {[
                    { icon: '🎯', title: 'Long Methods', desc: 'Identify overly complex methods that need refactoring' },
                    { icon: '🔄', title: 'Duplicate Code', desc: 'Find repeated code blocks across your project' },
                    { icon: '⚡', title: 'Performance Issues', desc: 'Detect inefficient algorithms and memory leaks' }
                  ].map((item, index) => (
                    <div key={index} className={`flex items-start gap-4 p-4 rounded-lg transition-all duration-300 hover:scale-105 ${
                      isDarkMode ? 'bg-gray-800/30 hover:bg-gray-800/50' : 'bg-white/50 hover:bg-white/80'
                    }`}>
                      <span className="text-2xl">{item.icon}</span>
                      <div>
                        <h4 className={`font-semibold mb-1 ${
                          isDarkMode ? 'text-white' : 'text-gray-900'
                        }`}>{item.title}</h4>
                        <p className={`text-sm ${
                          isDarkMode ? 'text-gray-400' : 'text-gray-600'
                        }`}>{item.desc}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              <div className="animate-on-scroll animate-slide-in-right">
                <div className={`p-6 rounded-xl border transition-all duration-500 ${
                  isDarkMode 
                    ? 'bg-gray-800/50 border-gray-700 shadow-2xl shadow-purple-500/10'
                    : 'bg-white border-gray-200 shadow-2xl shadow-blue-500/10'
                }`}>
                  <div className="flex items-center gap-2 mb-4">
                    <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                    <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                    <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                    <span className={`ml-2 text-sm ${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>CodeAnalyzer.java</span>
                  </div>
                  <div className={`font-mono text-sm space-y-2 ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-700'
                  }`}>
                    <div className="text-red-400">// Code Smell Detected: Long Method</div>
                    <div>public void processData() {'{'}</div>
                    <div className="pl-4 text-yellow-400">⚠️ Method too long (150 lines)</div>
                    <div className="pl-4 text-blue-400">💡 AI Suggestion: Extract methods</div>
                    <div>{'}'}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* AI Enhancement Section */}
        <section className="py-24 px-6">
          <div className="container mx-auto max-w-6xl text-center">
            <div className="animate-on-scroll animate-fade-in-up mb-16">
              <h2 className={`text-4xl font-bold mb-6 ${
                isDarkMode ? 'text-white' : 'text-gray-900'
              }`}>AI-Powered Code Enhancement</h2>
              <p className={`text-lg max-w-3xl mx-auto ${
                isDarkMode ? 'text-gray-300' : 'text-gray-600'
              }`}>
                Get intelligent recommendations to improve your code quality, performance, and maintainability 
                with our advanced AI analysis engine.
              </p>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              {[
                { 
                  icon: '🤖', 
                  title: 'Smart Refactoring', 
                  desc: 'AI suggests optimal refactoring patterns for cleaner code',
                  color: 'blue'
                },
                { 
                  icon: '📈', 
                  title: 'Performance Optimization', 
                  desc: 'Identify bottlenecks and get performance improvement tips',
                  color: 'green'
                },
                { 
                  icon: '🛡️', 
                  title: 'Security Analysis', 
                  desc: 'Detect security vulnerabilities and get fix recommendations',
                  color: 'purple'
                }
              ].map((item, index) => (
                <div key={index} className={`animate-on-scroll animate-fade-in-up p-6 rounded-xl transition-all duration-500 hover:scale-105 ${
                  isDarkMode 
                    ? 'bg-gray-800/50 border border-gray-700 hover:bg-gray-800/70'
                    : 'bg-white border border-gray-200 shadow-lg hover:shadow-xl'
                }`} style={{ animationDelay: `${index * 0.2}s` }}>
                  <div className={`w-16 h-16 mx-auto mb-4 rounded-full flex items-center justify-center text-2xl ${
                    item.color === 'blue' ? (isDarkMode ? 'bg-blue-600/20 text-blue-400' : 'bg-blue-100 text-blue-600') :
                    item.color === 'green' ? (isDarkMode ? 'bg-green-600/20 text-green-400' : 'bg-green-100 text-green-600') :
                    (isDarkMode ? 'bg-purple-600/20 text-purple-400' : 'bg-purple-100 text-purple-600')
                  }`}>
                    {item.icon}
                  </div>
                  <h3 className={`text-xl font-semibold mb-3 ${
                    isDarkMode ? 'text-white' : 'text-gray-900'
                  }`}>{item.title}</h3>
                  <p className={`${
                    isDarkMode ? 'text-gray-400' : 'text-gray-600'
                  }`}>{item.desc}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Features Section */}
            <section className={`py-24 px-6 ${
              isDarkMode ? 'bg-gray-900/20' : 'bg-blue-50/30'
            }`}>
              <div className="container mx-auto max-w-6xl">
                <div className="animate-on-scroll text-center mb-16">
                  <h2 className={`text-4xl font-bold mb-6 ${
                    isDarkMode ? 'text-white' : 'text-gray-900'
                  }`}>Comprehensive Analysis Features</h2>
                  <p className={`text-lg max-w-3xl mx-auto ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-600'
                  }`}>
                    Powerful tools designed specifically for Java developers to maintain code quality
                  </p>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                  {[
                    { title: 'AST Analysis', desc: 'Deep Abstract Syntax Tree parsing for comprehensive Java code analysis', icon: '🌳' },
                    { title: 'Code Smell Detection', desc: 'Identify anti-patterns, long methods, and duplicate code blocks', icon: '👃' },
                    { title: 'AI Recommendations', desc: 'Get intelligent suggestions for code improvements and refactoring', icon: '🤖' },
                    { title: 'Quality Metrics', desc: 'Comprehensive code quality scoring and maintainability index', icon: '📏' },
                    { title: 'Vulnerability Scanning', desc: 'Detect security issues and potential exploits in your Java code', icon: '🔒' },
                    { title: 'Performance Analysis', desc: 'Identify performance bottlenecks and optimization opportunities', icon: '⚡' }
                  ].map((feature, index) => (
                    <Card key={index} className={`animate-fade-in-up p-6 text-center transition-all duration-500 hover:scale-105 ${
                      isDarkMode 
                        ? 'bg-gray-800/50 border-gray-700 hover:bg-gray-800/70'
                        : 'bg-white/90 border-blue-100 shadow-lg hover:shadow-xl'
                    }`}>
                      <div className="text-4xl mb-4">{feature.icon}</div>
                      <h3 className={`text-xl font-semibold mb-3 ${
                        isDarkMode ? 'text-white' : 'text-gray-900'
                      }`}>{feature.title}</h3>
                      <p className={`${
                        isDarkMode ? 'text-gray-400' : 'text-gray-600'
                      }`}>{feature.desc}</p>
                    </Card>
                  ))}
                </div>
              </div>
            </section>

            {/* How to Use Section */}
            <section className={`py-24 px-6 ${
              isDarkMode ? 'bg-gray-900/50' : 'bg-blue-50/50'
            }`}>
              <div className="container mx-auto max-w-6xl">
                <div className="animate-on-scroll text-center mb-16">
                  <h2 className={`text-4xl font-bold mb-6 ${
                    isDarkMode ? 'text-white' : 'text-gray-900'
                  }`}>How to Use DevSync</h2>
                  <p className={`text-lg max-w-3xl mx-auto ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-600'
                  }`}>
                    Get started with Java code analysis in just a few simple steps
                  </p>
                </div>
                
                <div className="max-w-4xl mx-auto space-y-16">
                  {[
                    { step: '1', title: 'Sign Up & Login', desc: 'Create your account and access the dashboard' },
                    { step: '2', title: 'Upload Project', desc: 'Upload your Java project as a ZIP file for analysis' },
                    { step: '3', title: 'Get Analysis', desc: 'Receive detailed reports on code quality and issues' },
                    { step: '4', title: 'Collaborate', desc: 'Share results with your team and track improvements' }
                  ].map((item, index) => (
                    <div key={index} className={`animate-on-scroll flex items-center gap-12 ${
                      index % 2 === 1 ? 'flex-row-reverse' : ''
                    }`}>
                      <div className={`w-20 h-20 rounded-full flex items-center justify-center text-2xl font-bold shadow-lg ${
                        isDarkMode 
                          ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-blue-500/30'
                          : 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-blue-500/30'
                      }`}>
                        {item.step}
                      </div>
                      <div className="flex-1">
                        <h3 className={`text-2xl font-semibold mb-3 ${
                          isDarkMode ? 'text-white' : 'text-gray-900'
                        }`}>{item.title}</h3>
                        <p className={`text-lg ${
                          isDarkMode ? 'text-gray-400' : 'text-gray-600'
                        }`}>{item.desc}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </section>

            {/* Footer */}
            <footer className={`py-16 px-6 border-t ${
              isDarkMode 
                ? 'bg-gray-900 border-gray-800'
                : 'bg-white border-gray-200'
            }`}>
              <div className="container mx-auto max-w-6xl px-8">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-12">
                  <div>
                    <div className="flex items-center mb-4">
                      <img 
                        src={isDarkMode ? "/logo_for_blacktheme.png" : "/logo_for_whitetheme.png"} 
                        alt="DevSync" 
                        className="h-8 w-auto transition-opacity duration-500" 
                      />
                    </div>
                    <p className={`${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>Sync your development workflow with intelligent code analysis.</p>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-gray-100' : 'text-gray-900'
                    }`}>Product</h4>
                    <ul className={`space-y-2 ${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>
                      <li><button onClick={() => navigate('/features')} className="hover:text-blue-400 transition-colors cursor-pointer">Features</button></li>
                      <li><button onClick={() => navigate('/pricing')} className="hover:text-blue-400 transition-colors cursor-pointer">Pricing</button></li>
                      <li><button onClick={() => navigate('/documentation')} className="hover:text-blue-400 transition-colors cursor-pointer">Documentation</button></li>
                    </ul>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-gray-100' : 'text-gray-900'
                    }`}>Company</h4>
                    <ul className={`space-y-2 ${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>
                      <li><button onClick={() => navigate('/about')} className="hover:text-blue-400 transition-colors cursor-pointer">About</button></li>
                      <li><button onClick={() => navigate('/contact')} className="hover:text-blue-400 transition-colors cursor-pointer">Contact</button></li>
                      <li><button onClick={() => navigate('/support')} className="hover:text-blue-400 transition-colors cursor-pointer">Support</button></li>
                    </ul>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-gray-100' : 'text-gray-900'
                    }`}>Connect</h4>
                    <div className="flex gap-4">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                        isDarkMode ? 'bg-gray-800 text-gray-400' : 'bg-gray-100 text-gray-600'
                      }`}>📧</div>
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                        isDarkMode ? 'bg-gray-800 text-gray-400' : 'bg-gray-100 text-gray-600'
                      }`}>🐙</div>
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                        isDarkMode ? 'bg-gray-800 text-gray-400' : 'bg-gray-100 text-gray-600'
                      }`}>💼</div>
                    </div>
                  </div>
                </div>
                
                <div className={`mt-12 pt-8 border-t text-center ${
                  isDarkMode 
                    ? 'border-gray-700 text-gray-500'
                    : 'border-gray-200 text-gray-500'
                }`}>
                  <p className="text-lg">© 2025 DevSync. All rights reserved.</p>
                </div>
              </div>
            </footer>
      </div>
    </div>
  )
}



export default function App() {
  const [isDarkMode, setIsDarkMode] = useState(false)
  const navigate = useNavigate()

  const handleLogin = () => {
    navigate('/login')
  }

  const handleSignup = () => {
    navigate('/signup')
  }

  return (
    <>
      <Routes>
        <Route path="/" element={
          <LandingPage 
            onLogin={handleLogin} 
            onSignup={handleSignup}
            isDarkMode={isDarkMode}
            setIsDarkMode={setIsDarkMode}
          />
        } />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/home" element={<Home />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin" element={<AdminPanel />} />
        <Route path="/admin/dashboard" element={<AdminPanel />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/features" element={<Features />} />
        <Route path="/pricing" element={<Pricing />} />
        <Route path="/documentation" element={<Documentation />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/support" element={<Support />} />
        <Route path="/fileviewer" element={<FileViewer />} />
      </Routes>
      <Toaster
        position="bottom-right"
        toastOptions={{
          style: {
            background: isDarkMode ? "#1f2937" : "#ffffff",
            border: isDarkMode ? "1px solid #374151" : "1px solid #e5e7eb",
            color: isDarkMode ? "#f3f4f6" : "#1f2937",
          },
        }}
      />
    </>
  )
}