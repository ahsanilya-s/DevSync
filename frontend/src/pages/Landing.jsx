import React from 'react';
import { useNavigate } from 'react-router-dom';
import Particles from '../components/Particles';
import Carousel from '../components/Carousel';

export default function Landing() {
    const nav = useNavigate();

    return (
        <div style={{ width: '100%', maxWidth: '100vw', overflowX: 'hidden' }}>
            {/* Navigation Header */}
            <div style={{
                position: 'fixed',
                top: '20px',
                left: '50%',
                transform: 'translateX(-50%)',
                zIndex: 10,
                backgroundColor: 'rgba(0,0,0,0.3)',
                backdropFilter: 'blur(10px)',
                borderRadius: '50px',
                padding: '10px 30px',
                border: '1px solid rgba(255,255,255,0.2)',
                minWidth: '500px'
            }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div style={{ color: 'white', fontSize: '18px', fontWeight: 'bold' }}>DevSync</div>
                    <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
                        <a href="#features" style={{ color: 'white', textDecoration: 'none', padding: '8px 16px' }}>Features</a>
                        <button
                            onClick={() => nav('/login')}
                            style={{
                                padding: '8px 16px',
                                fontSize: '14px',
                                backgroundColor: 'transparent',
                                color: 'white',
                                border: '1px solid rgba(255,255,255,0.3)',
                                borderRadius: '20px',
                                cursor: 'pointer'
                            }}
                        >
                            Login
                        </button>
                        <button
                            onClick={() => nav('/signup')}
                            style={{
                                padding: '8px 16px',
                                fontSize: '14px',
                                backgroundColor: '#007bff',
                                color: 'white',
                                border: 'none',
                                borderRadius: '20px',
                                cursor: 'pointer'
                            }}
                        >
                            Sign Up
                        </button>
                    </div>
                </div>
            </div>

            {/* Hero Section */}
            <div style={{ width: '100%', height: '100vh', position: 'relative' }}>
                <Particles
                    particleColors={['#ffffff', '#ffffff']}
                    particleCount={200}
                    particleSpread={10}
                    speed={0.1}
                    particleBaseSize={100}
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
                    color: 'white'
                }}>
                    <h1 style={{ fontSize: '48px', marginBottom: '10px' }}>Welcome to DevSync</h1>
                    <p style={{ fontSize: '18px', marginBottom: '30px', color: '#d0d0d0' }}>
                        Analyze your Java code, detect bugs, and improve quality with AI-powered insights.
                    </p>
                    <div style={{ display: 'flex', gap: '20px', justifyContent: 'center' }}>
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
            </div>

            {/* Features Section */}
            <div id="features" style={{ 
                width: '100%', 
                height: '100vh', 
                position: 'relative',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                color: 'white',
                overflowX: 'hidden'
            }}>
                <div style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', opacity: 0.3 }}>
                    <Particles
                        particleColors={['#ffffff', '#ffffff']}
                        particleCount={100}
                        particleSpread={8}
                        speed={0.05}
                        particleBaseSize={60}
                        moveParticlesOnHover={false}
                        alphaParticles={false}
                        disableRotation={false}
                    />
                </div>
                <div style={{ 
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    height: '100%',
                    zIndex: 2,
                    position: 'relative'
                }}>
                    <h2 style={{ fontSize: '36px', marginBottom: '60px', textAlign: 'center' }}>
                        Explore DevSync Features
                    </h2>
                    <div style={{ width: '300px', height: '300px' }}>
                        <Carousel
                            baseWidth={300}
                            autoplay={true}
                            autoplayDelay={3000}
                            pauseOnHover={true}
                            loop={true}
                            round={true}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}