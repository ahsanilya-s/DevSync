import React, {useState} from 'react'
import api from '../api'
import { useNavigate } from 'react-router-dom'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { toast } from 'sonner'

export default function Signup(){
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const nav = useNavigate()

    async function handleSignup(){
        try {
            const res = await api.post('/auth/signup', { username, email, password })
            toast.success('Account created successfully!')
            nav('/login')
        } catch(e) {
            toast.error('Error: ' + (e.response?.data || e.message))
        }
    }
    
    return (
        <div className="min-h-screen flex bg-gradient-to-br from-gray-950 via-purple-950 to-blue-950">
            {/* Animated Gradient Orbs */}
            <div className="fixed top-0 left-0 w-[500px] h-[500px] bg-blue-600/20 rounded-full blur-3xl animate-pulse pointer-events-none" />
            <div className="fixed bottom-0 right-0 w-[500px] h-[500px] bg-purple-600/20 rounded-full blur-3xl animate-pulse pointer-events-none" 
                 style={{ animationDelay: '1s' }} />
            <div className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px] bg-violet-600/10 rounded-full blur-3xl animate-pulse pointer-events-none"
                 style={{ animationDelay: '2s' }} />

            {/* Left Half - Social Signup & Branding */}
            <div className="flex-1 flex flex-col justify-center items-center p-8 text-white relative z-10">
                <div className="max-w-md w-full space-y-8">
                    {/* Logo */}
                    <div className="text-center mb-8">
                        <img 
                            src="/logo_for_blacktheme.png" 
                            alt="DevSync" 
                            className="h-16 w-auto mx-auto mb-4" 
                        />
                        <h1 className="text-3xl font-bold text-white">Join DevSync</h1>
                        <p className="text-gray-300 mt-2">Create your account and start analyzing code</p>
                    </div>

                    {/* Social Signup Options */}
                    <div className="space-y-4">
                        <Button 
                            variant="outline" 
                            className="w-full bg-white/10 border-white/20 text-white hover:bg-white/20 backdrop-blur-sm transition-all duration-300"
                            onClick={() => alert('Email signup coming soon!')}
                        >
                            <svg className="w-5 h-5 mr-3" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                            </svg>
                            Continue with Email
                        </Button>
                        
                        <Button 
                            variant="outline" 
                            className="w-full bg-white/10 border-white/20 text-white hover:bg-white/20 backdrop-blur-sm transition-all duration-300"
                            onClick={() => alert('Google signup coming soon!')}
                        >
                            <svg className="w-5 h-5 mr-3" viewBox="0 0 24 24">
                                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                            </svg>
                            Continue with Google
                        </Button>
                        
                        <Button 
                            variant="outline" 
                            className="w-full bg-white/10 border-white/20 text-white hover:bg-white/20 backdrop-blur-sm transition-all duration-300"
                            onClick={() => alert('Microsoft signup coming soon!')}
                        >
                            <svg className="w-5 h-5 mr-3" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M11.4 24H0V12.6h11.4V24zM24 24H12.6V12.6H24V24zM11.4 11.4H0V0h11.4v11.4zM24 11.4H12.6V0H24v11.4z"/>
                            </svg>
                            Continue with Microsoft
                        </Button>
                    </div>
                </div>
            </div>

            {/* Right Half - Signup Form */}
            <div className="flex-1 bg-gray-900/80 backdrop-blur-xl border-l border-purple-500/30 flex flex-col justify-center items-center p-8 relative z-10">
                <div className="max-w-md w-full space-y-8">
                    <div className="text-center">
                        <h2 className="text-3xl font-bold text-white mb-2">Create Account</h2>
                        <p className="text-gray-300">Fill in your details to get started</p>
                    </div>
                    
                    <div className="space-y-6">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                Username
                            </label>
                            <Input 
                                type="text"
                                placeholder="Enter your username" 
                                value={username} 
                                onChange={e=>setUsername(e.target.value)}
                                className="w-full bg-gray-800/50 border-gray-600 text-white placeholder:text-gray-400 focus:border-blue-500"
                            />
                        </div>
                        
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                Email Address
                            </label>
                            <Input 
                                type="email"
                                placeholder="Enter your email" 
                                value={email} 
                                onChange={e=>setEmail(e.target.value)}
                                className="w-full bg-gray-800/50 border-gray-600 text-white placeholder:text-gray-400 focus:border-blue-500"
                            />
                        </div>
                        
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                Password
                            </label>
                            <Input 
                                type="password" 
                                placeholder="Create a password" 
                                value={password} 
                                onChange={e=>setPassword(e.target.value)}
                                className="w-full bg-gray-800/50 border-gray-600 text-white placeholder:text-gray-400 focus:border-blue-500"
                            />
                        </div>
                        
                        <Button 
                            onClick={handleSignup}
                            className="w-full bg-gradient-to-r from-blue-600 to-blue-500 hover:from-blue-700 hover:to-blue-600 text-white py-3 shadow-lg shadow-blue-500/30 transition-all duration-500"
                        >
                            Create Account
                        </Button>
                        
                        <div className="text-center">
                            <p className="text-gray-400">
                                Already have an account?{' '}
                                <button 
                                    onClick={() => nav('/login')}
                                    className="text-blue-400 hover:text-blue-300 font-medium transition-colors duration-300"
                                >
                                    Sign in
                                </button>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}