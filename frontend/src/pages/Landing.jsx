import React from 'react'
import { useNavigate } from 'react-router-dom'

export default function Landing(){
    const nav = useNavigate()

    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100vh',
            gap: '20px'
        }}>
            <h1>Welcome to DevSync</h1>
            <div style={{display: 'flex', gap: '20px'}}>
                <button 
                    onClick={() => nav('/signup')}
                    style={{
                        padding: '12px 24px',
                        fontSize: '16px',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    Sign Up
                </button>
                <button 
                    onClick={() => nav('/login')}
                    style={{
                        padding: '12px 24px',
                        fontSize: '16px',
                        backgroundColor: '#28a745',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    Login
                </button>
            </div>
        </div>
    )
}