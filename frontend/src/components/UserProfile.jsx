import React, { useState, useEffect } from 'react'
import { X, User, Mail, Eye, EyeOff, Award, LogOut, Calendar, Shield, TrendingUp } from 'lucide-react'
import { Button } from './ui/button'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import api from '../api'

export function UserProfile({ isOpen, onClose, isDarkMode }) {
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [showPassword, setShowPassword] = useState(false)
  const [analysisCount, setAnalysisCount] = useState(0)
  const [userBadge, setUserBadge] = useState('Basic User')
  const [memberSince, setMemberSince] = useState('')

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
      
      // Set member since date (mock for now)
      const date = new Date()
      date.setMonth(date.getMonth() - 3)
      setMemberSince(date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' }))
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
    if (userBadge === 'Advanced User') return 'from-purple-600 to-pink-600'
    if (userBadge === 'Regular User') return 'from-blue-600 to-cyan-600'
    return 'from-gray-600 to-gray-500'
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    toast.success('Logged out successfully')
    onClose()
    navigate('/')
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
      <div className={`rounded-2xl w-full max-w-lg shadow-2xl transition-all duration-500 my-8 ${
        isDarkMode ? 'bg-gray-900 border border-gray-700' : 'bg-white border border-gray-200'
      }`}>
        
        {/* Header with Gradient */}
        <div className={`relative p-8 border-b overflow-hidden ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <div className="absolute inset-0 bg-gradient-to-r from-blue-600/10 to-purple-600/10"></div>
          <div className="relative flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-xl bg-gradient-to-r from-blue-600 to-cyan-600">
                <User className="h-6 w-6 text-white" />
              </div>
              <h2 className={`text-2xl font-bold ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
                User Profile
              </h2>
            </div>
            <button onClick={onClose} className={`p-2 rounded-lg transition-all ${
              isDarkMode ? 'hover:bg-gray-800' : 'hover:bg-gray-100'
            }`}>
              <X className="h-5 w-5" />
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="p-6 space-y-4 max-h-[calc(100vh-12rem)] overflow-y-auto">
          {profile ? (
            <>
              {/* Profile Picture with Gradient Border */}
              <div className="flex justify-center">
                <div className="relative">
                  <div className="absolute inset-0 bg-gradient-to-r from-blue-600 to-purple-600 rounded-full blur-lg opacity-50"></div>
                  <div className={`relative w-28 h-28 rounded-full flex items-center justify-center border-4 ${
                    isDarkMode ? 'bg-gray-800 border-gray-700' : 'bg-gray-100 border-white'
                  }`}>
                    <User className={`h-14 w-14 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`} />
                  </div>
                </div>
              </div>

              {/* Username with Badge */}
              <div className="text-center">
                <h3 className={`text-3xl font-bold mb-2 ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
                  {profile.username}
                </h3>
                <div className="flex items-center justify-center gap-2 mb-3">
                  <Award className="h-5 w-5 text-yellow-500" />
                  <span className={`px-4 py-1.5 rounded-full text-sm font-semibold text-white bg-gradient-to-r ${getBadgeColor()} shadow-lg`}>
                    {userBadge}
                  </span>
                </div>
                <div className="flex items-center justify-center gap-4 text-sm">
                  <div className="flex items-center gap-1">
                    <TrendingUp className={`h-4 w-4 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`} />
                    <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}>
                      {analysisCount} analyses
                    </span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Calendar className={`h-4 w-4 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`} />
                    <span className={isDarkMode ? 'text-gray-400' : 'text-gray-600'}>
                      Since {memberSince}
                    </span>
                  </div>
                </div>
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
              <div className={`p-6 rounded-xl border ${
                isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border-gray-700' : 'bg-gradient-to-br from-blue-50 to-purple-50 border-gray-200'
              }`}>
                <h4 className={`text-sm font-semibold mb-4 flex items-center gap-2 ${isDarkMode ? 'text-gray-200' : 'text-gray-900'}`}>
                  <Shield className="h-4 w-4" />
                  Usage Statistics
                </h4>
                <div className="grid grid-cols-2 gap-4">
                  <div className={`p-4 rounded-lg ${
                    isDarkMode ? 'bg-gray-800/50' : 'bg-white/80'
                  }`}>
                    <p className={`text-xs mb-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                      Total Analyses
                    </p>
                    <p className={`text-3xl font-bold ${isDarkMode ? 'text-blue-400' : 'text-blue-600'}`}>
                      {analysisCount}
                    </p>
                  </div>
                  <div className={`p-4 rounded-lg ${
                    isDarkMode ? 'bg-gray-800/50' : 'bg-white/80'
                  }`}>
                    <p className={`text-xs mb-1 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>
                      User Level
                    </p>
                    <p className={`text-3xl font-bold ${isDarkMode ? 'text-purple-400' : 'text-purple-600'}`}>
                      {userBadge === 'Advanced User' ? '3' : userBadge === 'Regular User' ? '2' : '1'}
                    </p>
                  </div>
                </div>
              </div>

              {/* Logout Button */}
              <Button
                onClick={handleLogout}
                className="w-full bg-gradient-to-r from-red-600 to-red-500 hover:from-red-700 hover:to-red-600 text-white shadow-lg transition-all duration-300 hover:shadow-red-500/30"
              >
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </Button>
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
