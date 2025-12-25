import { Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'

export default function ProtectedRoute({ children }) {
  const [isChecking, setIsChecking] = useState(true)
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  
  useEffect(() => {
    const token = localStorage.getItem('token')
    const userId = localStorage.getItem('userId')
    
    if (token && userId) {
      setIsAuthenticated(true)
    } else {
      setIsAuthenticated(false)
    }
    setIsChecking(false)
  }, [])
  
  if (isChecking) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  
  return children
}
