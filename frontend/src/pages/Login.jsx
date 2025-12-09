import React, {useState} from 'react'
import api from '../api'
import { useNavigate } from 'react-router-dom'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { toast } from 'sonner'
import './Login.css'

export default function Login(){
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const nav = useNavigate()

    async function handleLogin(){
        try {
            const res = await api.post('/auth/login', { email, password })
            // Store user info and token in localStorage
            localStorage.setItem('userId', res.data.userId)
            localStorage.setItem('username', res.data.username)
            localStorage.setItem('token', res.data.token)
            toast.success('Login successful!')
            nav('/home')
        } catch(e) {
            toast.error('Invalid credentials')
        }
    }
    
    return (
        <div className="authContainer">
            {/* Animated Gradient Orbs */}
            <div className="animatedOrb topLeftOrb" />
            <div className="animatedOrb bottomRightOrb" />
            <div className="animatedOrb centerOrb" />

            {/* Left Half - Social Login & Branding */}
            <div className="leftSection">
                <div className="formContainer">
                    {/* Logo */}
                    <div className="brandSection">
                        <div className="flex items-center gap-3 mb-6">
                            <img 
                                src="/logo_for_blacktheme.png" 
                                alt="DevSync" 
                                className="brandLogo" 
                            />
                            <span className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent">
                                DevSync
                            </span>
                        </div>
                        <h1 className="brandTitle">Welcome Back</h1>
                        <p className="brandSubtitle">Sign in to continue to DevSync</p>
                    </div>

                    {/* Social Login Options */}
                    <div className="socialButtons">
                        <button 
                            className="socialBtn"
                            onClick={() => alert('Email login coming soon!')}
                        >
                            <svg className="socialIcon" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                            </svg>
                            Continue with Email
                        </button>
                        
                        <button 
                            className="socialBtn"
                            onClick={() => alert('Google login coming soon!')}
                        >
                            <svg className="socialIcon" viewBox="0 0 24 24">
                                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                            </svg>
                            Continue with Google
                        </button>
                        
                        <button 
                            className="socialBtn"
                            onClick={() => alert('Microsoft login coming soon!')}
                        >
                            <svg className="socialIcon" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M11.4 24H0V12.6h11.4V24zM24 24H12.6V12.6H24V24zM11.4 11.4H0V0h11.4v11.4zM24 11.4H12.6V0H24v11.4z"/>
                            </svg>
                            Continue with Microsoft
                        </button>
                    </div>
                </div>
            </div>

            {/* Right Half - Login Form */}
            <div className="rightSection">
                <div className="formContainer">
                    <div className="formSection">
                        <h2 className="formTitle">Sign In</h2>
                        <p className="formSubtitle">Enter your credentials to access your account</p>
                    </div>
                    
                    <div className="formFields">
                        <div className="fieldGroup">
                            <label className="fieldLabel">
                                Email Address
                            </label>
                            <input 
                                type="email"
                                placeholder="Enter your email" 
                                value={email} 
                                onChange={e=>setEmail(e.target.value)}
                                className="fieldInput"
                            />
                        </div>
                        
                        <div className="fieldGroup">
                            <label className="fieldLabel">
                                Password
                            </label>
                            <input 
                                type="password" 
                                placeholder="Enter your password" 
                                value={password} 
                                onChange={e=>setPassword(e.target.value)}
                                className="fieldInput"
                            />
                        </div>
                        
                        <button 
                            onClick={handleLogin}
                            className="submitBtn"
                        >
                            Sign In
                        </button>
                        
                        <div className="authFooter">
                            <p className="authFooterText">
                                Don't have an account?{' '}
                                <button 
                                    onClick={() => nav('/signup')}
                                    className="authLink"
                                >
                                    Sign up
                                </button>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}