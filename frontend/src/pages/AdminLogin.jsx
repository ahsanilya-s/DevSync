import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Shield, Eye, EyeOff } from 'lucide-react'
import { toast } from 'sonner'
import { Toaster } from '../components/ui/sonner'
import api from '../api'
import './AdminLogin.css'

const AdminLogin = () => {
  const [credentials, setCredentials] = useState({ username: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (e) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      const response = await api.post('/admin/login', credentials)
      if (response.data.success) {
        localStorage.setItem('adminAuth', 'true')
        toast.success('Admin login successful')
        navigate('/admin/dashboard')
      }
    } catch (error) {
      toast.error('Invalid admin credentials')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="adminLoginPage">
      <div className="adminLoginCard">
        <div className="adminLoginHeader">
          <div className="adminIconWrapper">
            <Shield className="adminIcon" />
          </div>
          <h1 className="adminLoginTitle">Admin Panel</h1>
          <p className="adminLoginSubtitle">Enter admin credentials to continue</p>
        </div>
        <div className="adminLoginContent">
          <form onSubmit={handleLogin} className="adminLoginForm">
            <div>
              <input
                type="text"
                placeholder="Username"
                value={credentials.username}
                onChange={(e) => setCredentials({...credentials, username: e.target.value})}
                className="adminInputField"
                required
              />
            </div>
            <div className="passwordFieldWrapper">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={credentials.password}
                onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                className="adminInputField"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="passwordToggleBtn"
              >
                {showPassword ? <EyeOff className="passwordToggleIcon" /> : <Eye className="passwordToggleIcon" />}
              </button>
            </div>
            <button
              type="submit"
              disabled={isLoading}
              className="adminSubmitBtn"
            >
              {isLoading ? 'Logging in...' : 'Login'}
            </button>
          </form>
          <div className="adminCredentialsInfo">
            <p className="adminCredentialsText">
              Default: admin / aaaa
            </p>
          </div>
        </div>
      </div>
      <Toaster position="bottom-right" />
    </div>
  )
}

export default AdminLogin