import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Mail, Phone, MapPin, Send, MessageCircle, Sun, Moon } from 'lucide-react'

const Contact = () => {
  const navigate = useNavigate()
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    message: ''
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    // Handle form submission here
    console.log('Form submitted:', formData)
    alert('Thank you for your message! We\'ll get back to you soon.')
    setFormData({ name: '', email: '', subject: '', message: '' })
  }

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  return (
    <div className={`min-h-screen ${isDarkMode ? 'bg-gradient-to-br from-gray-950 via-purple-950 to-blue-950 text-white' : 'bg-gradient-to-br from-blue-50 via-white to-purple-50 text-gray-900'}`}>
      <div className="container mx-auto px-6 py-8 max-w-7xl">
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
          <h1 className="text-5xl font-bold mb-6">Contact Us</h1>
          <p className={`text-xl max-w-3xl mx-auto ${
            isDarkMode ? 'text-gray-300' : 'text-gray-700'
          }`}>
            Have questions about DevSync? We'd love to hear from you. Send us a message and we'll respond as soon as possible.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 max-w-6xl mx-auto">
          <div>
            <h2 className="text-3xl font-bold mb-8">Get in Touch</h2>
            
            <div className="space-y-6 mb-8">
              <div className="flex items-start gap-4">
                <div className="bg-blue-500/20 p-3 rounded-lg">
                  <Mail className="w-6 h-6 text-blue-400" />
                </div>
                <div>
                  <h3 className="font-semibold mb-1">Email</h3>
                  <p className={isDarkMode ? 'text-gray-300' : 'text-gray-700'}>support@devsync.com</p>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-400' : 'text-gray-600'
                  }`}>We'll respond within 24 hours</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="bg-green-500/20 p-3 rounded-lg">
                  <Phone className="w-6 h-6 text-green-400" />
                </div>
                <div>
                  <h3 className="font-semibold mb-1">Phone</h3>
                  <p className={isDarkMode ? 'text-gray-300' : 'text-gray-700'}>+1 (555) 123-4567</p>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-400' : 'text-gray-600'
                  }`}>Mon-Fri 9AM-6PM EST</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="bg-purple-500/20 p-3 rounded-lg">
                  <MapPin className="w-6 h-6 text-purple-400" />
                </div>
                <div>
                  <h3 className="font-semibold mb-1">Office</h3>
                  <p className={isDarkMode ? 'text-gray-300' : 'text-gray-700'}>123 Tech Street</p>
                  <p className={isDarkMode ? 'text-gray-300' : 'text-gray-700'}>San Francisco, CA 94105</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="bg-yellow-500/20 p-3 rounded-lg">
                  <MessageCircle className="w-6 h-6 text-yellow-400" />
                </div>
                <div>
                  <h3 className="font-semibold mb-1">Live Chat</h3>
                  <p className={isDarkMode ? 'text-gray-300' : 'text-gray-700'}>Available on our dashboard</p>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-400' : 'text-gray-600'
                  }`}>For registered users</p>
                </div>
              </div>
            </div>

            <div className={`backdrop-blur-sm border rounded-xl p-6 ${
              isDarkMode ? 'bg-white/10 border-white/20' : 'bg-white border-gray-200 shadow-md'
            }`}>
              <h3 className="text-xl font-semibold mb-4">Frequently Asked Questions</h3>
              <div className="space-y-4">
                <div>
                  <h4 className="font-semibold text-blue-500 mb-1">How long does analysis take?</h4>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-600'
                  }`}>Most projects are analyzed within 2-5 minutes, depending on size and complexity.</p>
                </div>
                <div>
                  <h4 className="font-semibold text-blue-500 mb-1">What file formats are supported?</h4>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-600'
                  }`}>We support ZIP files containing Java source code (.java files).</p>
                </div>
                <div>
                  <h4 className="font-semibold text-blue-500 mb-1">Is my code secure?</h4>
                  <p className={`text-sm ${
                    isDarkMode ? 'text-gray-300' : 'text-gray-600'
                  }`}>Yes, all uploads are encrypted and automatically deleted after analysis.</p>
                </div>
              </div>
            </div>
          </div>

          <div>
            <div className={`backdrop-blur-sm border rounded-xl p-8 ${
              isDarkMode ? 'bg-white/10 border-white/20' : 'bg-white border-gray-200 shadow-lg'
            }`}>
              <h2 className="text-2xl font-bold mb-6">Send us a Message</h2>
              
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label htmlFor="name" className="block text-sm font-medium mb-2">Name</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:border-blue-400 transition-all ${
                      isDarkMode ? 'bg-white/10 border-white/20 focus:bg-white/15 text-white' : 'bg-gray-50 border-gray-300 focus:bg-white text-gray-900'
                    }`}
                    placeholder="Your full name"
                  />
                </div>

                <div>
                  <label htmlFor="email" className="block text-sm font-medium mb-2">Email</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:border-blue-400 transition-all ${
                      isDarkMode ? 'bg-white/10 border-white/20 focus:bg-white/15 text-white' : 'bg-gray-50 border-gray-300 focus:bg-white text-gray-900'
                    }`}
                    placeholder="your.email@example.com"
                  />
                </div>

                <div>
                  <label htmlFor="subject" className="block text-sm font-medium mb-2">Subject</label>
                  <select
                    id="subject"
                    name="subject"
                    value={formData.subject}
                    onChange={handleChange}
                    required
                    className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:border-blue-400 transition-all ${
                      isDarkMode ? 'bg-white/10 border-white/20 focus:bg-white/15 text-white' : 'bg-gray-50 border-gray-300 focus:bg-white text-gray-900'
                    }`}
                  >
                    <option value="">Select a subject</option>
                    <option value="general">General Inquiry</option>
                    <option value="support">Technical Support</option>
                    <option value="billing">Billing Question</option>
                    <option value="feature">Feature Request</option>
                    <option value="partnership">Partnership</option>
                  </select>
                </div>

                <div>
                  <label htmlFor="message" className="block text-sm font-medium mb-2">Message</label>
                  <textarea
                    id="message"
                    name="message"
                    value={formData.message}
                    onChange={handleChange}
                    required
                    rows={6}
                    className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:border-blue-400 transition-all resize-none ${
                      isDarkMode ? 'bg-white/10 border-white/20 focus:bg-white/15 text-white' : 'bg-gray-50 border-gray-300 focus:bg-white text-gray-900'
                    }`}
                    placeholder="Tell us how we can help you..."
                  />
                </div>

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white py-3 px-6 rounded-lg font-semibold transition-all flex items-center justify-center gap-2"
                >
                  <Send className="w-4 h-4" />
                  Send Message
                </button>
              </form>
            </div>

            <div className="mt-8 text-center">
              <p className={`text-sm ${
                isDarkMode ? 'text-gray-400' : 'text-gray-600'
              }`}>
                Need immediate help? Check out our{' '}
                <button 
                  onClick={() => navigate('/documentation')}
                  className="text-blue-400 hover:underline"
                >
                  documentation
                </button>
                {' '}or{' '}
                <button className="text-blue-400 hover:underline">
                  join our community
                </button>
              </p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <footer className={`mt-20 py-12 border-t ${
          isDarkMode ? 'border-gray-700 bg-gray-900/50' : 'border-gray-200 bg-white'
        }`}>
          <div className="container mx-auto px-6 max-w-7xl">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div>
                <h4 className="font-semibold mb-4">DevSync</h4>
                <p className={`text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>Advanced Java code analysis for better software quality.</p>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Contact Info</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li>support@devsync.com</li>
                  <li>+1 (555) 123-4567</li>
                  <li>San Francisco, CA</li>
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
                <h4 className="font-semibold mb-4">Support</h4>
                <ul className={`space-y-2 text-sm ${
                  isDarkMode ? 'text-gray-400' : 'text-gray-600'
                }`}>
                  <li><button onClick={() => navigate('/support')} className="hover:text-blue-500">Help Center</button></li>
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

export default Contact