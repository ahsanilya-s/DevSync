import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sun, Moon } from 'lucide-react';
import Particles from '../components/Particles';
import Carousel from '../components/Carousel';
import './Landing.css';

export default function Landing() {
    const nav = useNavigate();
    const [isDarkMode, setIsDarkMode] = useState(true);

    const handleToggleTheme = () => {
        setIsDarkMode(!isDarkMode);
    };

    return (
        <div className={`mainWrapper ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
            {/* Navigation Header */}
            <div className={`topNavBar ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
                <div className="navContent">
                    <img src="/logo.png" alt="DevSync" className="brandLogo" />
                    <div className="navActions">
                        <a href="#features" className={`featuresLink ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>Features</a>
                        <button
                            onClick={handleToggleTheme}
                            className={`themeToggleBtn ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}
                        >
                            {isDarkMode ? <Sun size={16} /> : <Moon size={16} />}
                        </button>
                        <button
                            onClick={() => nav('/login')}
                            className={`loginBtn ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}
                        >
                            Login
                        </button>
                        <button
                            onClick={() => nav('/signup')}
                            className="signupBtn"
                        >
                            Sign Up
                        </button>
                    </div>
                </div>
            </div>

            {/* Hero Section */}
            <div className="heroContainer">
                <Particles
                    particleColors={isDarkMode ? ['#ffffff', '#ffffff'] : ['#000000', '#000000']}
                    particleCount={200}
                    particleSpread={10}
                    speed={0.1}
                    particleBaseSize={100}
                    moveParticlesOnHover={true}
                    alphaParticles={false}
                    disableRotation={false}
                />
                
                <div className={`heroContent ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
                    <h1 className="mainTitle">Welcome to DevSync</h1>
                    <p className={`heroDescription ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
                        Analyze your Java code, detect bugs, and improve quality with AI-powered insights.
                    </p>
                    <div className="heroButtons">
                        <button
                            onClick={() => nav('/signup')}
                            className="primaryBtn"
                        >
                            Sign Up
                        </button>
                        <button
                            onClick={() => nav('/login')}
                            className="secondaryBtn"
                        >
                            Login
                        </button>
                    </div>
                </div>
            </div>

            {/* Features Section */}
            <div id="features" className={`featuresSection ${isDarkMode ? 'darkTheme' : 'lightTheme'}`}>
                <div className="particleBackground">
                    <Particles
                        particleColors={isDarkMode ? ['#ffffff', '#ffffff'] : ['#000000', '#000000']}
                        particleCount={100}
                        particleSpread={8}
                        speed={0.05}
                        particleBaseSize={60}
                        moveParticlesOnHover={false}
                        alphaParticles={false}
                        disableRotation={false}
                    />
                </div>
                <div className="featuresContent">
                    <h2 className="featuresTitle">
                        Explore DevSync Features
                    </h2>
                    <div className="carouselWrapper">
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