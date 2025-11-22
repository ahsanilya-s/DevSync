import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, HelpCircle, Book, MessageCircle, Mail, Search, Sun, Moon } from 'lucide-react'

const Support = () => {
  const navigate = useNavigate()
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('all')

  const faqs = [
    {
      category: 'getting-started',
      question: 'How do I upload my Java project for analysis?',
      answer: 'Simply zip your Java project folder and upload it through our dashboard. Make sure to include all .java source files and maintain your package structure.'
    },
    {
      category: 'analysis',
      question: 'What types of code issues does DevSync detect?',
      answer: 'DevSync detects code smells (long methods, duplicate code), security vulnerabilities (SQL injection, XSS), performance issues, and maintainability problems using advanced AST analysis.'
    },
    {
      category: 'billing',
      question: 'Can I cancel my subscription at any time?',
      answer: 'Yes, you can cancel your subscription at any time from your account settings. You\'ll continue to have access until the end of your current billing period.'
    },
    {
      category: 'technical',
      question: 'What is the maximum file size for uploads?',
      answer: 'The maximum upload size is 100MB. If your project is larger, consider excluding build artifacts, dependencies, and non-source files.'
    },
    {
      category: 'analysis',
      question: 'How accurate are the security vulnerability detections?',
      answer: 'Our security analysis uses industry-standard patterns and has a very low false-positive rate. However, we recommend manual review of critical findings.'
    },
    {
      category: 'getting-started',
      question: 'Do I need to install anything to use DevSync?',
      answer: 'No installation required! DevSync is a web-based platform. Just create an account and start uploading your projects through your browser.'
    },
    {
      category: 'technical',
      question: 'Which Java versions are supported?',
      answer: 'DevSync supports Java 8 through Java 21, including all LTS versions. We continuously update our parser to support the latest Java features.'
    },
    {
      category: 'billing',
      question: 'Is there a free trial available?',
      answer: 'Yes! We offer a 14-day free trial for all paid plans. No credit card required to start your trial.'
    }
  ]

  const categories = [
    { id: 'all', name: 'All Topics', count: faqs.length },
    { id: 'getting-started', name: 'Getting Started', count: faqs.filter(f => f.category === 'getting-started').length },
    { id: 'analysis', name: 'Analysis Features', count: faqs.filter(f => f.category === 'analysis').length },
    { id: 'technical', name: 'Technical', count: faqs.filter(f => f.category === 'technical').length },
    { id: 'billing', name: 'Billing', count: faqs.filter(f => f.category === 'billing').length }
  ]

  const filteredFaqs = faqs.filter(faq => {
    const matchesCategory = selectedCategory === 'all' || faq.category === selectedCategory
    const matchesSearch = searchQuery === '' || 
      faq.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
      faq.answer.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesCategory && matchesSearch
  })

  const supportOptions = [
    {
      icon: <Book className="w-8 h-8" />,
      title: 'Documentation',
      description: 'Comprehensive guides and API documentation',
      action: () => navigate('/documentation'),
      buttonText: 'View Docs'
    },
    {
      icon: <MessageCircle className="w-8 h-8" />,
      title: 'Community Forum',
      description: 'Connect with other developers and get help',
      action: () => alert('Community forum coming soon!'),
      buttonText: 'Join Forum'
    },
    {
      icon: <Mail className="w-8 h-8" />,
      title: 'Contact Support',
      description: 'Get direct help from our support team',
      action: () => navigate('/contact'),
      buttonText: 'Contact Us'
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
          <h1 className="text-5xl font-bold mb-6 flex items-center justify-center gap-3">
            <HelpCircle className="w-12 h-12 text-blue-500" />
            Support Center
          </h1>
          <p className={`text-xl max-w-3xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-900'
          }`}>
            Find answers to common questions or get help from our support team
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {supportOptions.map((option, index) => (
            <div key={index} className="bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl p-6 text-center hover:bg-white/15 transition-all">
              <div className="text-blue-400 mb-4 flex justify-center">{option.icon}</div>
              <h3 className="text-xl font-semibold mb-3">{option.title}</h3>
              <p className={`mb-6 ${
                isDarkMode ? 'text-gray-300' : 'text-gray-800'
              }`}>{option.description}</p>
              <button
                onClick={option.action}
                className="bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white px-6 py-2 rounded-lg font-semibold transition-all"
              >
                {option.buttonText}
              </button>
            </div>
          ))}
        </div>

        <div className="bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl p-8">
          <h2 className="text-3xl font-bold mb-8 text-center">Frequently Asked Questions</h2>
          
          <div className="mb-8">
            <div className="relative mb-6">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Search FAQs..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:outline-none focus:border-blue-400 focus:bg-white/15 transition-all"
              />
            </div>

            <div className="flex flex-wrap gap-2">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() => setSelectedCategory(category.id)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                    selectedCategory === category.id
                      ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30'
                      : 'bg-white/10 text-gray-300 hover:bg-white/20'
                  }`}
                >
                  {category.name} ({category.count})
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-4">
            {filteredFaqs.length > 0 ? (
              filteredFaqs.map((faq, index) => (
                <details key={index} className="bg-white/5 border border-white/10 rounded-lg">
                  <summary className="p-4 cursor-pointer hover:bg-white/10 transition-all font-semibold">
                    {faq.question}
                  </summary>
                  <div className="px-4 pb-4 text-gray-300">
                    {faq.answer}
                  </div>
                </details>
              ))
            ) : (
              <div className="text-center py-12">
                <p className="text-gray-400 mb-4">No FAQs found matching your search.</p>
                <button
                  onClick={() => {
                    setSearchQuery('')
                    setSelectedCategory('all')
                  }}
                  className="text-blue-400 hover:underline"
                >
                  Clear filters
                </button>
              </div>
            )}
          </div>
        </div>

        <div className="mt-16 text-center bg-gradient-to-r from-blue-500/20 to-purple-500/20 border border-blue-500/30 rounded-xl p-8">
          <h3 className="text-2xl font-bold mb-4">Still need help?</h3>
          <p className="text-gray-300 mb-6">
            Can't find what you're looking for? Our support team is here to help.
          </p>
          <div className="flex gap-4 justify-center">
            <button
              onClick={() => navigate('/contact')}
              className="bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white px-6 py-3 rounded-lg font-semibold transition-all"
            >
              Contact Support
            </button>
            <button
              onClick={() => navigate('/documentation')}
              className="bg-white/20 hover:bg-white/30 text-white px-6 py-3 rounded-lg font-semibold border border-white/30 transition-all"
            >
              View Documentation
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
                <h4 className="font-semibold mb-4">Support</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li>FAQ</li>
                  <li>Help Center</li>
                  <li>Community Forum</li>
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

export default Support