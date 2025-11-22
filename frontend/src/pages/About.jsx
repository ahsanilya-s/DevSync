import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Users, Target, Lightbulb, Award, Sun, Moon } from 'lucide-react'

const About = () => {
  const navigate = useNavigate()
  const [isDarkMode, setIsDarkMode] = useState(false)

  const team = [
    {
      name: "Alex Johnson",
      role: "Lead Developer",
      description: "Full-stack developer with 8+ years experience in Java and AST parsing technologies.",
      avatar: "👨‍💻"
    },
    {
      name: "Sarah Chen",
      role: "Security Analyst",
      description: "Cybersecurity expert specializing in static code analysis and vulnerability detection.",
      avatar: "👩‍🔬"
    },
    {
      name: "Mike Rodriguez",
      role: "AI Engineer",
      description: "Machine learning specialist focused on code quality assessment and recommendation systems.",
      avatar: "🤖"
    }
  ]

  const values = [
    {
      icon: <Target className="w-8 h-8" />,
      title: "Quality First",
      description: "We believe that high-quality code is the foundation of successful software projects."
    },
    {
      icon: <Users className="w-8 h-8" />,
      title: "Developer-Centric",
      description: "Built by developers, for developers. We understand the challenges you face daily."
    },
    {
      icon: <Lightbulb className="w-8 h-8" />,
      title: "Innovation",
      description: "Leveraging cutting-edge AI and AST technology to provide intelligent code insights."
    },
    {
      icon: <Award className="w-8 h-8" />,
      title: "Excellence",
      description: "Committed to delivering the most accurate and actionable code analysis available."
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
          <h1 className="text-5xl font-bold mb-6">About DevSync</h1>
          <p className={`text-xl max-w-4xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-900'
          }`}>
            DevSync is a comprehensive Java code analysis platform that helps developers write better, 
            more secure, and maintainable code through advanced AST parsing and AI-powered insights.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 mb-20">
          <div>
            <h2 className="text-3xl font-bold mb-6">Our Mission</h2>
            <p className={`mb-6 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-900'
            }`}>
              We're on a mission to revolutionize how developers approach code quality. By combining 
              advanced Abstract Syntax Tree analysis with artificial intelligence, we provide 
              developers with the tools they need to write exceptional Java code.
            </p>
            <p className={isDarkMode ? 'text-gray-300' : 'text-gray-900'}>
              Our platform goes beyond simple linting to provide deep insights into code structure, 
              security vulnerabilities, performance bottlenecks, and maintainability issues. We believe 
              that every developer deserves access to enterprise-grade code analysis tools.
            </p>
          </div>
          
          <div>
            <h2 className="text-3xl font-bold mb-6">Why DevSync?</h2>
            <div className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="w-2 h-2 bg-blue-400 rounded-full mt-2"></div>
                <div>
                  <h3 className="font-semibold mb-1">Advanced AST Analysis</h3>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-800'
                  }`}>Deep parsing of Java source code for comprehensive analysis</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="w-2 h-2 bg-purple-400 rounded-full mt-2"></div>
                <div>
                  <h3 className="font-semibold mb-1">AI-Powered Insights</h3>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-800'
                  }`}>Machine learning algorithms provide intelligent recommendations</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="w-2 h-2 bg-green-400 rounded-full mt-2"></div>
                <div>
                  <h3 className="font-semibold mb-1">Security Focus</h3>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-800'
                  }`}>Comprehensive vulnerability detection and prevention</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="w-2 h-2 bg-yellow-400 rounded-full mt-2"></div>
                <div>
                  <h3 className="font-semibold mb-1">Developer Experience</h3>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-800'
                  }`}>Intuitive interface designed for modern development workflows</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="mb-20">
          <h2 className="text-3xl font-bold text-center mb-12">Our Values</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, index) => (
              <div key={index} className={`text-center backdrop-blur-sm border rounded-xl p-6 transition-all ${
                isDarkMode ? 'bg-white/10 border-white/20 hover:bg-white/15' : 'bg-gray-50 border-gray-200 hover:bg-gray-100'
              }`}>
                <div className="text-blue-500 mb-4 flex justify-center">{value.icon}</div>
                <h3 className="text-xl font-semibold mb-3">{value.title}</h3>
                <p className={`text-sm ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-800'
                }`}>{value.description}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="mb-20">
          <h2 className="text-3xl font-bold text-center mb-12">Meet Our Team</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {team.map((member, index) => (
              <div key={index} className={`text-center backdrop-blur-sm border rounded-xl p-8 transition-all ${
                isDarkMode ? 'bg-white/10 border-white/20 hover:bg-white/15' : 'bg-gray-50 border-gray-200 hover:bg-gray-100'
              }`}>
                <div className="text-6xl mb-4">{member.avatar}</div>
                <h3 className="text-xl font-semibold mb-2">{member.name}</h3>
                <p className="text-blue-500 mb-4">{member.role}</p>
                <p className={`text-sm ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-800'
                }`}>{member.description}</p>
              </div>
            ))}
          </div>
        </div>

        <div className={`text-center rounded-xl p-12 ${
          isDarkMode 
            ? 'bg-gradient-to-r from-blue-500/20 to-purple-500/20 border border-blue-500/30'
            : 'bg-gradient-to-r from-blue-50 to-purple-50 border border-blue-200'
        }`}>
          <h2 className="text-3xl font-bold mb-6">Join the DevSync Community</h2>
          <p className={`mb-8 max-w-2xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-900'
          }`}>
            Ready to improve your Java code quality? Join thousands of developers who trust DevSync 
            for their code analysis needs.
          </p>
          <div className="flex gap-4 justify-center">
            <button 
              onClick={() => navigate('/signup')}
              className="bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white px-8 py-3 rounded-lg font-semibold transition-all"
            >
              Get Started Free
            </button>
            <button 
              onClick={() => navigate('/contact')}
              className="bg-white/20 hover:bg-white/30 text-white px-8 py-3 rounded-lg font-semibold border border-white/30 transition-all"
            >
              Contact Us
            </button>
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
                <h4 className="font-semibold mb-4">Company</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li>Our Mission</li>
                  <li>Our Team</li>
                  <li>Our Values</li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Resources</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/features')} className="hover:text-blue-500">Features</button></li>
                  <li><button onClick={() => navigate('/pricing')} className="hover:text-blue-500">Pricing</button></li>
                  <li><button onClick={() => navigate('/documentation')} className="hover:text-blue-500">Documentation</button></li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Contact</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/contact')} className="hover:text-blue-500">Get in Touch</button></li>
                  <li><button onClick={() => navigate('/support')} className="hover:text-blue-500">Support</button></li>
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

export default About