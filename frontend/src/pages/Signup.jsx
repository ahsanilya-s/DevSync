import React, {useState} from 'react'
import api from '../api'
import { useNavigate } from 'react-router-dom'
import Particles from '../components/Particles'

export default function Signup(){
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const nav = useNavigate()

    async function handleSignup(){
        try {
            const res = await api.post('/auth/signup', { username, email, password })
            alert('Sign up successful!')
            nav('/login')
        } catch(e) {
            alert('Error: ' + (e.response?.data || e.message))
        }
    }
    
    return (
        <div style={{ width: '100vw', height: '100vh', position: 'relative', overflow: 'hidden' }}>
            <Particles
                particleColors={['#ffffff', '#ffffff']}
                particleCount={150}
                particleSpread={10}
                speed={0.1}
                particleBaseSize={80}
                moveParticlesOnHover={true}
                alphaParticles={false}
                disableRotation={false}
            />
            
            <div style={{ 
                position: 'absolute', 
                top: '50%', 
                left: '50%', 
                transform: 'translate(-50%, -50%)',
                zIndex: 2,
                textAlign: 'center',
                color: 'white',
                width: '300px',
                maxWidth: '90vw'
            }}>
                <h2 style={{ fontSize: '32px', marginBottom: '30px' }}>Signup</h2>
                
                <input 
                    placeholder="Username" 
                    value={username} 
                    onChange={e=>setUsername(e.target.value)}
                    style={{
                        width: '100%',
                        padding: '12px',
                        fontSize: '16px',
                        backgroundColor: 'rgba(255,255,255,0.1)',
                        color: 'white',
                        border: '2px solid rgba(255,255,255,0.3)',
                        borderRadius: '5px',
                        marginBottom: '15px',
                        boxSizing: 'border-box'
                    }}
                />
                
                <input 
                    placeholder="Email" 
                    value={email} 
                    onChange={e=>setEmail(e.target.value)}
                    style={{
                        width: '100%',
                        padding: '12px',
                        fontSize: '16px',
                        backgroundColor: 'rgba(255,255,255,0.1)',
                        color: 'white',
                        border: '2px solid rgba(255,255,255,0.3)',
                        borderRadius: '5px',
                        marginBottom: '15px',
                        boxSizing: 'border-box'
                    }}
                />
                
                <input 
                    type="password" 
                    placeholder="Password" 
                    value={password} 
                    onChange={e=>setPassword(e.target.value)}
                    style={{
                        width: '100%',
                        padding: '12px',
                        fontSize: '16px',
                        backgroundColor: 'rgba(255,255,255,0.1)',
                        color: 'white',
                        border: '2px solid rgba(255,255,255,0.3)',
                        borderRadius: '5px',
                        marginBottom: '20px',
                        boxSizing: 'border-box'
                    }}
                />
                
                <button 
                    onClick={handleSignup}
                    style={{
                        width: '100%',
                        padding: '12px 24px',
                        fontSize: '16px',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        boxSizing: 'border-box'
                    }}
                >
                    Signup
                </button>
            </div>
        </div>
    )
}