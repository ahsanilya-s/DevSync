import React, { useState } from 'react'
import { Routes, Route, useNavigate } from 'react-router-dom'
import { Toaster } from './components/ui/sonner'
import { toast } from 'sonner'
import api from './api'

// Import existing pages
import Home from './pages/Home'

// Import new UI components
import { Button } from './components/ui/button'
import { Input } from './components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './components/ui/card'
import { Sun, Moon } from 'lucide-react'

// Landing Page Component with Backend Integration
function LandingPage({ onLogin, onSignup }) {
  const [isDarkMode, setIsDarkMode] = useState(true)
  const [showLearnMore, setShowLearnMore] = useState(false)

  const handleToggleTheme = () => {
    setIsDarkMode(!isDarkMode)
  }

  const handleLearnMore = () => {
    setShowLearnMore(true)
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
              <div className="flex items-center gap-2">
                <div className={`transition-colors duration-500 ${
                  isDarkMode ? 'text-blue-400' : 'text-blue-600'
                }`}>
                  <span className="text-2xl">{'{'}</span>
                  <span className="mx-1">D</span>
                  <span className="text-2xl">{'}'}</span>
                </div>
                <span className={`transition-colors duration-500 ${
                  isDarkMode ? 'text-white' : 'text-gray-900'
                }`}>
                  evSync
                </span>
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
                  {isDarkMode ? <Sun className="h-4 w-4 mr-2" /> : <Moon className="h-4 w-4 mr-2" />}
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
              </div>
            </div>
          </div>
        </header>

        {/* Hero Section */}
        <section className="py-20 px-6">
          <div className="container mx-auto text-center">
            <h1 className={`text-6xl font-semibold mb-8 transition-colors duration-500 ${
              isDarkMode ? 'text-white' : 'text-gray-900'
            }`}>
              Sync Your Development
              <br />
              <span className={`bg-gradient-to-r bg-clip-text text-transparent transition-all duration-500 ${
                isDarkMode
                  ? 'from-blue-400 to-purple-400'
                  : 'from-blue-600 to-purple-600'
              }`}>
                Workflow
              </span>
            </h1>
            
            <p className={`text-xl mb-16 max-w-3xl mx-auto transition-colors duration-500 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-600'
            }`}>
              DevSync helps development teams collaborate seamlessly with intelligent code analysis, 
              real-time synchronization, and powerful project management tools.
            </p>

            <div className="flex gap-4 justify-center">
              <Button
                onClick={onLogin}
                size="lg"
                className={`transition-all duration-500 hover:scale-105 ${
                  isDarkMode
                    ? 'bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white shadow-2xl shadow-blue-500/50'
                    : 'bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white shadow-lg'
                }`}
              >
                Get Started
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

        {/* Learn More Section */}
        {showLearnMore && (
          <>
            {/* Features Section */}
            <section className="py-20 px-6">
              <div className="container mx-auto">
                <h2 className={`text-4xl font-semibold text-center mb-16 ${
                  isDarkMode ? 'text-white' : 'text-gray-900'
                }`}>Features</h2>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                  {[
                    { title: 'Code Analysis', desc: 'Advanced static analysis to detect code smells and quality issues', icon: '🔍' },
                    { title: 'Real-time Sync', desc: 'Seamless synchronization across your development team', icon: '🔄' },
                    { title: 'Smart Reports', desc: 'Detailed reports with actionable insights and recommendations', icon: '📊' },
                    { title: 'Team Collaboration', desc: 'Built-in tools for effective team communication and workflow', icon: '👥' },
                    { title: 'Security Scanning', desc: 'Identify security vulnerabilities before they reach production', icon: '🛡️' },
                    { title: 'Performance Metrics', desc: 'Track and optimize your codebase performance over time', icon: '⚡' }
                  ].map((feature, index) => (
                    <Card key={index} className={`p-6 text-center transition-all duration-500 hover:scale-105 ${
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
            <section className={`py-20 px-6 ${
              isDarkMode ? 'bg-gray-900/50' : 'bg-blue-50/50'
            }`}>
              <div className="container mx-auto">
                <h2 className={`text-4xl font-semibold text-center mb-16 ${
                  isDarkMode ? 'text-white' : 'text-gray-900'
                }`}>How to Use DevSync</h2>
                
                <div className="max-w-4xl mx-auto space-y-12">
                  {[
                    { step: '1', title: 'Sign Up & Login', desc: 'Create your account and access the dashboard' },
                    { step: '2', title: 'Upload Project', desc: 'Upload your Java project as a ZIP file for analysis' },
                    { step: '3', title: 'Get Analysis', desc: 'Receive detailed reports on code quality and issues' },
                    { step: '4', title: 'Collaborate', desc: 'Share results with your team and track improvements' }
                  ].map((item, index) => (
                    <div key={index} className="flex items-center gap-8">
                      <div className={`w-16 h-16 rounded-full flex items-center justify-center text-2xl font-bold ${
                        isDarkMode 
                          ? 'bg-blue-600 text-white'
                          : 'bg-blue-500 text-white'
                      }`}>
                        {item.step}
                      </div>
                      <div>
                        <h3 className={`text-2xl font-semibold mb-2 ${
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
            <footer className={`py-12 px-6 border-t ${
              isDarkMode 
                ? 'bg-gray-900 border-gray-800'
                : 'bg-white border-gray-200'
            }`}>
              <div className="container mx-auto">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
                  <div>
                    <div className="flex items-center gap-2 mb-4">
                      <div className={`${
                        isDarkMode ? 'text-blue-400' : 'text-blue-600'
                      }`}>
                        <span className="text-2xl">{'{'}</span>
                        <span className="mx-1">D</span>
                        <span className="text-2xl">{'}'}</span>
                      </div>
                      <span className={`font-semibold ${
                        isDarkMode ? 'text-white' : 'text-gray-900'
                      }`}>DevSync</span>
                    </div>
                    <p className={`${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>Sync your development workflow with intelligent code analysis.</p>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-white' : 'text-gray-900'
                    }`}>Product</h4>
                    <ul className={`space-y-2 ${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>
                      <li>Features</li>
                      <li>Pricing</li>
                      <li>Documentation</li>
                    </ul>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-white' : 'text-gray-900'
                    }`}>Company</h4>
                    <ul className={`space-y-2 ${
                      isDarkMode ? 'text-gray-400' : 'text-gray-600'
                    }`}>
                      <li>About</li>
                      <li>Contact</li>
                      <li>Support</li>
                    </ul>
                  </div>
                  
                  <div>
                    <h4 className={`font-semibold mb-4 ${
                      isDarkMode ? 'text-white' : 'text-gray-900'
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
                
                <div className={`mt-8 pt-8 border-t text-center ${
                  isDarkMode 
                    ? 'border-gray-800 text-gray-500'
                    : 'border-gray-200 text-gray-500'
                }`}>
                  <p>© 2025 DevSync. All rights reserved.</p>
                </div>
              </div>
            </footer>
          </>
        )}
      </div>
    </div>
  )
}

// Login Modal Component
function LoginModal({ isOpen, onClose, onSuccess }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleLogin = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    
    try {
      const res = await api.post('/auth/login', { email, password })
      toast.success('Login successful!')
      onSuccess()
      onClose()
    } catch (error) {
      toast.error('Invalid credentials')
    } finally {
      setIsLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <Card className="w-full max-w-md bg-gray-900 border-gray-700">
        <CardHeader>
          <CardTitle className="text-white">Login to DevSync</CardTitle>
          <CardDescription className="text-gray-400">
            Enter your credentials to access your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <Input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="bg-gray-800 border-gray-600 text-white"
              />
            </div>
            <div>
              <Input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="bg-gray-800 border-gray-600 text-white"
              />
            </div>
            <div className="flex gap-2">
              <Button
                type="submit"
                disabled={isLoading}
                className="flex-1 bg-blue-600 hover:bg-blue-700"
              >
                {isLoading ? 'Logging in...' : 'Login'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                className="border-gray-600 text-gray-300 hover:bg-gray-800"
              >
                Cancel
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

// Signup Modal Component
function SignupModal({ isOpen, onClose, onSuccess }) {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSignup = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    
    try {
      const res = await api.post('/auth/signup', { username, email, password })
      toast.success('Account created successfully!')
      onSuccess()
      onClose()
    } catch (error) {
      toast.error('Error creating account: ' + (error.response?.data || error.message))
    } finally {
      setIsLoading(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <Card className="w-full max-w-md bg-gray-900 border-gray-700">
        <CardHeader>
          <CardTitle className="text-white">Sign Up for DevSync</CardTitle>
          <CardDescription className="text-gray-400">
            Create your account to get started
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSignup} className="space-y-4">
            <div>
              <Input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="bg-gray-800 border-gray-600 text-white"
              />
            </div>
            <div>
              <Input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="bg-gray-800 border-gray-600 text-white"
              />
            </div>
            <div>
              <Input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="bg-gray-800 border-gray-600 text-white"
              />
            </div>
            <div className="flex gap-2">
              <Button
                type="submit"
                disabled={isLoading}
                className="flex-1 bg-blue-600 hover:bg-blue-700"
              >
                {isLoading ? 'Creating Account...' : 'Sign Up'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={onClose}
                className="border-gray-600 text-gray-300 hover:bg-gray-800"
              >
                Cancel
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [showSignupModal, setShowSignupModal] = useState(false)
  const navigate = useNavigate()

  const handleLogin = () => {
    setShowLoginModal(true)
  }

  const handleSignup = () => {
    setShowSignupModal(true)
  }

  const handleLoginSuccess = () => {
    setIsLoggedIn(true)
    toast.success('Welcome to DevSync!')
  }

  const handleSignupSuccess = () => {
    setShowSignupModal(false)
    setShowLoginModal(true)
    toast.success('Account created! Please login.')
  }

  if (!isLoggedIn) {
    return (
      <>
        <LandingPage onLogin={handleLogin} onSignup={handleSignup} />
        <LoginModal 
          isOpen={showLoginModal} 
          onClose={() => setShowLoginModal(false)}
          onSuccess={handleLoginSuccess}
        />
        <SignupModal 
          isOpen={showSignupModal} 
          onClose={() => setShowSignupModal(false)}
          onSuccess={handleSignupSuccess}
        />
        <Toaster
          position="bottom-right"
          toastOptions={{
            style: {
              background: "#1f2937",
              border: "1px solid #374151",
              color: "#f3f4f6",
            },
          }}
        />
      </>
    )
  }

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/home" element={<Home />} />
    </Routes>
  )
}