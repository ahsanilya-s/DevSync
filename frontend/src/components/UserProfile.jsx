import React, { useState, useEffect } from 'react'
import { X, User, Mail, Eye, EyeOff, Award } from 'lucide-react'
import { Button } from './ui/button'
import api from '../api'

export function UserProfile({ isOpen, onClose, isDarkMode }) {
  const [profile, setProfile] = useState(null)
  const [showPassword, setShowPassword] = useState(false)
  const [analysisCount, setAnalysisCount] = useState(0)
  const [userBadge, setUserBadge] = useState('Basic User')

  useEffect(() => {
    if (isOpen) {
      fetchProfile()
      fetchAnalysisHistory()
    }
  }, [isOpen])

  const fetchProfile = async () => {
    try {
      const userId = localStorage.getItem('userId')
      const response = await api.get(`/auth/profile/${userId}`)
      setProfile(response.data)
    } catch (error) {
      console.error('Failed to fetch profile:', error)
    }
  }

  const fetchAnalysisHistory = async () => {
    try {
      const userId = localStorage.getItem('userId')
      const response = await api.get(`/upload/history?userId=${userId}`)
      const count = response.data.length
      setAnalysisCount(count)
      
      // Determine badge based on usage
      if (count >= 20) {
        setUserBadge('Advanced User')
      } else if (count >= 5) {
        setUserBadge('Regular User')
      } else {
        setUserBadge('Basic User')
      }
    } catch (error) {
      console.error('Failed to fetch history:', error)
    }
  }

  const getBadgeColor = () => {
    if (userBadge === 'Advanced User') return 'bg-purple-500'
    if (userBadge === 'Regular User') return 'bg-blue-500'
    return 'bg-gray-500'
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <div className={`rounded-xl w-full max-w-md transition-all duration-500 ${
        isDarkMode ? 'bg-gray-900 border border-gray-700' : 'bg-white border border-gray-200'
      }`}>
        
        {/* Header */}
        <div className={`flex items-center justify-between p-6 border-b ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <div className="flex items-center gap-3">
            <User className="h-6 w-6" />
            <h2 className={`text-xl font-semibold ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
              User Profile
            </h2>
          </div>
          <button onClick={onClose} className={`p-2 rounded-lg ${
            isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-100'
          }`}>
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          {profile ? (
            <>
              {/* Profile Picture Placeholder */}
              <div className="flex justify-center">
                <div className={`w-24 h-24 rounded-full flex items-center justify-center ${
                  isDarkMode ? 'bg-gray-800' : 'bg-gray-100'
                }`}>
                  <User className={`h-12 w-12 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`} />
                </div>
              </div>

              {/* Username with Badge */}
              <div className="text-center">
                <h3 className={`text-2xl font-bold ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
                  {profile.username}
                </h3>
                <div className="flex items-center justify-center gap-2 mt-2">
                  <Award className="h-4 w-4" />
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold text-white ${getBadgeColor()}`}>
                    {userBadge}
                  </span>
                </div>
                <p className={`text-sm mt-2 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                  {analysisCount} analyses completed
                </p>
              </div>

              {/* Email */}
              <div className="space-y-2">
                <label className={`text-sm font-medium flex items-center gap-2 ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-700'
                }`}>
                  <Mail className="h-4 w-4" />
                  Email
                </label>
                <div className={`px-4 py-3 rounded-lg border ${
                  isDarkMode ? 'bg-gray-800 border-gray-700 text-gray-200' : 'bg-gray-50 border-gray-200 text-gray-900'
                }`}>
                  {profile.email}
                </div>
              </div>

              {/* Password */}
              <div className="space-y-2">
                <label className={`text-sm font-medium flex items-center gap-2 ${
                  isDarkMode ? 'text-gray-300' : 'text-gray-700'
                }`}>
                  Password
                </label>
                <div className={`px-4 py-3 rounded-lg border flex items-center justify-between ${
                  isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-gray-50 border-gray-200'
                }`}>
                  <span className={isDarkMode ? 'text-gray-200' : 'text-gray-900'}>
                    {showPassword ? 'Password is encrypted and hidden for security' : '••••••••'}
                  </span>
                  <button
                    onClick={() => setShowPassword(!showPassword)}
                    className={`p-1 rounded ${isDarkMode ? 'hover:bg-gray-700' : 'hover:bg-gray-200'}`}
                  >
                    {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              {/* Stats */}
              <div className={`p-4 rounded-lg ${
                isDarkMode ? 'bg-gray-800/50' : 'bg-blue-50'
              }`}>
                <h4 className={`text-sm font-semibold mb-3 ${isDarkMode ? 'text-gray-200' : 'text-gray-900'}`}>
                  Usage Statistics
                </h4>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className={`text-xs ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                      Total Analyses
                    </p>
                    <p className={`text-2xl font-bold ${isDarkMode ? 'text-blue-400' : 'text-blue-600'}`}>
                      {analysisCount}
                    </p>
                  </div>
                  <div>
                    <p className={`text-xs ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                      User Level
                    </p>
                    <p className={`text-2xl font-bold ${isDarkMode ? 'text-purple-400' : 'text-purple-600'}`}>
                      {userBadge === 'Advanced User' ? '3' : userBadge === 'Regular User' ? '2' : '1'}
                    </p>
                  </div>
                </div>
              </div>
            </>
          ) : (
            <div className="flex items-center justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
