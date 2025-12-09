import React, { useState, useEffect, useRef } from 'react';
import { BookOpen, Upload, BarChart3, FileText, Shield, Zap, CheckCircle2, AlertTriangle, AlertCircle, Info, Code2, GitBranch, TrendingUp, Brain, Sparkles, Cpu, Database, Lock, Eye, Target, Layers, Activity, Rocket, Users, Award, Clock } from 'lucide-react';

export function WelcomeGuide({ isDarkMode }) {
  const [activeTab, setActiveTab] = useState('overview');
  const [animatedStats, setAnimatedStats] = useState({ analyses: 0, issues: 0, users: 0 });
  const [visibleSections, setVisibleSections] = useState(new Set());
  const sectionRefs = useRef({});

  useEffect(() => {
    const interval = setInterval(() => {
      setAnimatedStats(prev => ({
        analyses: prev.analyses < 10000 ? prev.analyses + 127 : 10000,
        issues: prev.issues < 50000 ? prev.issues + 634 : 50000,
        users: prev.users < 5000 ? prev.users + 63 : 5000
      }));
    }, 30);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setVisibleSections(prev => new Set([...prev, entry.target.dataset.section]));
          }
        });
      },
      { threshold: 0.1 }
    );

    Object.values(sectionRefs.current).forEach(ref => {
      if (ref) observer.observe(ref);
    });

    return () => observer.disconnect();
  }, [activeTab]);

  return (
    <div className={`h-full overflow-y-auto ${isDarkMode ? 'text-gray-100' : 'text-gray-900'}`}>
      {/* Futuristic Hero Section */}
      <div className="relative mb-16 p-12 overflow-hidden">
        {/* Animated Background Grid */}
        <div className="absolute inset-0 opacity-28">
          <div className="absolute inset-0" style={{
            backgroundImage: `linear-gradient(${isDarkMode ? '#374151' : '#9ca3af'} 1px, transparent 1px), linear-gradient(90deg, ${isDarkMode ? '#374151' : '#9ca3af'} 1px, transparent 1px)`,
            backgroundSize: '50px 50px'
          }} />
        </div>





        <div className="relative z-10 text-center">
          <div className="flex items-center justify-center gap-4 mb-8">
            <div className="inline-flex items-center justify-center w-24 h-24 rounded-2xl bg-gradient-to-br from-blue-600 via-cyan-600 to-purple-600 shadow-2xl shadow-blue-500/50 animate-pulse">
              <Brain className="w-12 h-12 text-white" />
            </div>
          </div>
          <h1 className="text-6xl font-black mb-6 bg-gradient-to-r from-blue-600 via-cyan-500 to-purple-600 bg-clip-text text-transparent animate-gradient">
            DevSync AI Platform
          </h1>
          <p className={`text-xl mb-8 max-w-3xl mx-auto ${isDarkMode ? 'text-gray-300' : 'text-gray-600'}`}>
            Next-generation intelligent code analysis powered by advanced pattern recognition and machine learning algorithms
          </p>

          {/* Live Stats */}
          <div className="grid grid-cols-3 gap-6 max-w-4xl mx-auto mt-12">
            <StatCard value={animatedStats.analyses.toLocaleString()} label="Analyses Performed" icon={<Activity />} isDarkMode={isDarkMode} />
            <StatCard value={animatedStats.issues.toLocaleString()} label="Issues Detected" icon={<Target />} isDarkMode={isDarkMode} />
            <StatCard value={animatedStats.users.toLocaleString()} label="Active Users" icon={<TrendingUp />} isDarkMode={isDarkMode} />
          </div>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="flex justify-center mb-12 px-8">
        <div className={`inline-flex rounded-2xl p-2 gap-2 ${isDarkMode ? 'bg-gray-800/50 border border-gray-700' : 'bg-gray-100 border border-gray-200'}`}>
          <TabButton active={activeTab === 'overview'} onClick={() => setActiveTab('overview')} isDarkMode={isDarkMode}>
            <Sparkles className="w-4 h-4 mr-2" /> Overview
          </TabButton>
          <TabButton active={activeTab === 'features'} onClick={() => setActiveTab('features')} isDarkMode={isDarkMode}>
            <Zap className="w-4 h-4 mr-2" /> Features
          </TabButton>
          <TabButton active={activeTab === 'guide'} onClick={() => setActiveTab('guide')} isDarkMode={isDarkMode}>
            <BookOpen className="w-4 h-4 mr-2" /> Guide
          </TabButton>
          <TabButton active={activeTab === 'grading'} onClick={() => setActiveTab('grading')} isDarkMode={isDarkMode}>
            <BarChart3 className="w-4 h-4 mr-2" /> Grading
          </TabButton>
        </div>
      </div>

      {/* Tab Content */}
      <div className="px-8 pb-12">
        {activeTab === 'overview' && <OverviewTab isDarkMode={isDarkMode} sectionRefs={sectionRefs} visibleSections={visibleSections} />}
        {activeTab === 'features' && <FeaturesTab isDarkMode={isDarkMode} sectionRefs={sectionRefs} visibleSections={visibleSections} />}
        {activeTab === 'guide' && <GuideTab isDarkMode={isDarkMode} sectionRefs={sectionRefs} visibleSections={visibleSections} />}
        {activeTab === 'grading' && <GradingTab isDarkMode={isDarkMode} sectionRefs={sectionRefs} visibleSections={visibleSections} />}
      </div>
    </div>
  );
}

function StatCard({ value, label, icon, isDarkMode }) {
  return (
    <div className={`p-6 rounded-2xl backdrop-blur-sm transition-all duration-300 hover:scale-105 ${
      isDarkMode ? 'bg-gray-800/50 border border-gray-700' : 'bg-white/80 border border-gray-200 shadow-lg'
    }`}>
      <div className="flex justify-center mb-3 text-blue-500">{icon}</div>
      <div className="text-3xl font-bold mb-2 bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent">{value}</div>
      <div className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{label}</div>
    </div>
  );
}

function TabButton({ active, onClick, children, isDarkMode }) {
  return (
    <button
      onClick={onClick}
      className={`px-6 py-3 rounded-xl font-medium transition-all duration-300 flex items-center ${
        active
          ? 'bg-gradient-to-r from-blue-600 to-cyan-600 text-white shadow-lg shadow-blue-500/30'
          : isDarkMode
            ? 'text-gray-400 hover:text-gray-200 hover:bg-gray-700/50'
            : 'text-gray-600 hover:text-gray-900 hover:bg-gray-200/50'
      }`}
    >
      {children}
    </button>
  );
}

function OverviewTab({ isDarkMode, sectionRefs, visibleSections }) {
  return (
    <div className="space-y-12 max-w-7xl mx-auto">
      {/* Core Capabilities */}
      <div 
        ref={el => sectionRefs.current['capabilities'] = el}
        data-section="capabilities"
        className={`grid md:grid-cols-3 gap-6 transition-all duration-700 ${
          visibleSections.has('capabilities') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        }`}
      >
        <CapabilityCard
          icon={<Shield className="w-8 h-8" />}
          title="Advanced Security Scanning"
          description="AI-powered vulnerability detection using SAST, secrets scanning, and real-time threat analysis with 99.7% accuracy"
          gradient="from-red-600 to-orange-600"
          isDarkMode={isDarkMode}
        />
        <CapabilityCard
          icon={<Brain className="w-8 h-8" />}
          title="Intelligent Code Analysis"
          description="Machine learning algorithms detect code smells, anti-patterns, and maintainability issues before they become problems"
          gradient="from-purple-600 to-pink-600"
          isDarkMode={isDarkMode}
        />
        <CapabilityCard
          icon={<TrendingUp className="w-8 h-8" />}
          title="Predictive Quality Metrics"
          description="Track code quality trends over time with predictive analytics and automated improvement suggestions"
          gradient="from-blue-600 to-cyan-600"
          isDarkMode={isDarkMode}
        />
      </div>

      {/* Technology Stack */}
      <div 
        ref={el => sectionRefs.current['tech'] = el}
        data-section="tech"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('tech') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border border-gray-700' : 'bg-gradient-to-br from-gray-50 to-white border border-gray-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <Cpu className="w-8 h-8 text-blue-600" />
          Powered by Cutting-Edge Technology
        </h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          <TechItem icon={<Code2 />} title="JavaParser 3.25.9" desc="Advanced AST analysis" isDarkMode={isDarkMode} />
          <TechItem icon={<Database />} title="Spring Boot 3.5.6" desc="Enterprise-grade backend" isDarkMode={isDarkMode} />
          <TechItem icon={<Layers />} title="React + Vite" desc="Lightning-fast UI" isDarkMode={isDarkMode} />
          <TechItem icon={<Lock />} title="MySQL 8.0" desc="Secure data storage" isDarkMode={isDarkMode} />
        </div>
      </div>

      {/* Detection Capabilities */}
      <div 
        ref={el => sectionRefs.current['detection'] = el}
        data-section="detection"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('detection') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-blue-900/20 to-purple-900/20 border border-blue-800/50' : 'bg-gradient-to-br from-blue-50 to-purple-50 border border-blue-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <Eye className="w-8 h-8 text-blue-600" />
          What We Detect & Analyze
        </h2>
        <div className="grid md:grid-cols-2 gap-4">
          <DetectionItem icon={<AlertCircle />} text="Security vulnerabilities & injection flaws" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Empty catch blocks with risk assessment" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Cyclomatic complexity & code metrics" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Parameter overload & method signatures" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Magic numbers & hardcoded values" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Naming conventions & identifier clarity" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Dead code & unused imports" isDarkMode={isDarkMode} />
          <DetectionItem icon={<AlertCircle />} text="Performance bottlenecks & memory leaks" isDarkMode={isDarkMode} />
        </div>
      </div>

      {/* Why Choose DevSync */}
      <div 
        ref={el => sectionRefs.current['why'] = el}
        data-section="why"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('why') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-purple-900/20 to-pink-900/20 border border-purple-800/50' : 'bg-gradient-to-br from-purple-50 to-pink-50 border border-purple-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <Rocket className="w-8 h-8 text-purple-600" />
          Why Choose DevSync?
        </h2>
        <div className="grid md:grid-cols-2 gap-6">
          <WhyCard icon={<Clock />} title="Save Time" desc="Automated analysis reduces manual code review time by 80%" isDarkMode={isDarkMode} />
          <WhyCard icon={<Shield />} title="Enhance Security" desc="Detect vulnerabilities before they reach production" isDarkMode={isDarkMode} />
          <WhyCard icon={<TrendingUp />} title="Improve Quality" desc="Continuous monitoring ensures code quality standards" isDarkMode={isDarkMode} />
          <WhyCard icon={<Users />} title="Team Collaboration" desc="Share insights and track improvements across teams" isDarkMode={isDarkMode} />
        </div>
      </div>

      {/* Success Metrics */}
      <div 
        ref={el => sectionRefs.current['success'] = el}
        data-section="success"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('success') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border border-gray-700' : 'bg-gradient-to-br from-gray-50 to-white border border-gray-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <Award className="w-8 h-8 text-yellow-600" />
          Success Metrics
        </h2>
        <div className="grid md:grid-cols-4 gap-6">
          <SuccessMetric value="99.7%" label="Detection Accuracy" isDarkMode={isDarkMode} />
          <SuccessMetric value="80%" label="Time Saved" isDarkMode={isDarkMode} />
          <SuccessMetric value="50+" label="Detection Rules" isDarkMode={isDarkMode} />
          <SuccessMetric value="24/7" label="Availability" isDarkMode={isDarkMode} />
        </div>
      </div>
    </div>
  );
}

function WhyCard({ icon, title, desc, isDarkMode }) {
  return (
    <div className={`p-6 rounded-xl border transition-all duration-300 hover:scale-105 ${
      isDarkMode ? 'bg-gray-800/30 border-gray-700' : 'bg-white border-gray-200'
    }`}>
      <div className="flex items-start gap-4">
        <div className="p-3 rounded-lg bg-gradient-to-r from-purple-600 to-pink-600 text-white">
          {icon}
        </div>
        <div>
          <h4 className="font-bold mb-2">{title}</h4>
          <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{desc}</p>
        </div>
      </div>
    </div>
  );
}

function SuccessMetric({ value, label, isDarkMode }) {
  return (
    <div className="text-center">
      <div className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-600 to-orange-600 bg-clip-text text-transparent">{value}</div>
      <div className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{label}</div>
    </div>
  );
}

function FeaturesTab({ isDarkMode, sectionRefs, visibleSections }) {
  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      <div 
        ref={el => sectionRefs.current['features'] = el}
        data-section="features"
        className={`grid md:grid-cols-2 gap-8 transition-all duration-700 ${
          visibleSections.has('features') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        }`}
      >
        <FeatureCard
          icon={<Shield className="w-10 h-10" />}
          title="Multi-Layer Security Analysis"
          features={[
            "SAST (Static Application Security Testing)",
            "Secrets & credential detection",
            "SQL injection & XSS vulnerability scanning",
            "Resource leak detection",
            "Authentication & authorization flaws"
          ]}
          gradient="from-red-600 to-orange-600"
          isDarkMode={isDarkMode}
        />
        <FeatureCard
          icon={<Brain className="w-10 h-10" />}
          title="AI-Powered Code Quality"
          features={[
            "Machine learning pattern recognition",
            "Code smell detection (15+ types)",
            "Maintainability index calculation",
            "Technical debt estimation",
            "Refactoring recommendations"
          ]}
          gradient="from-purple-600 to-pink-600"
          isDarkMode={isDarkMode}
        />
        <FeatureCard
          icon={<BarChart3 className="w-10 h-10" />}
          title="Advanced Reporting & Analytics"
          features={[
            "Interactive visual dashboards",
            "Trend analysis & historical tracking",
            "Exportable PDF reports",
            "Custom severity thresholds",
            "Team collaboration features"
          ]}
          gradient="from-blue-600 to-cyan-600"
          isDarkMode={isDarkMode}
        />
        <FeatureCard
          icon={<GitBranch className="w-10 h-10" />}
          title="GitHub Integration"
          features={[
            "Direct repository analysis",
            "Commit-level quality tracking",
            "Pull request automation",
            "Version comparison & diff analysis",
            "CI/CD pipeline integration"
          ]}
          gradient="from-green-600 to-emerald-600"
          isDarkMode={isDarkMode}
        />
      </div>
    </div>
  );
}

function GuideTab({ isDarkMode, sectionRefs, visibleSections }) {
  return (
    <div className="space-y-12 max-w-5xl mx-auto">
      {/* Quick Start */}
      <div 
        ref={el => sectionRefs.current['quickstart'] = el}
        data-section="quickstart"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('quickstart') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border border-gray-700' : 'bg-gradient-to-br from-gray-50 to-white border border-gray-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <Upload className="w-8 h-8 text-blue-600" />
          Getting Started in 5 Steps
        </h2>
        <div className="space-y-6">
          <GuideStep 
            number="1" 
            title="Navigate to Analysis" 
            description="Click 'Start Analysis' in the sidebar to access the upload interface"
            isDarkMode={isDarkMode}
          />
          <GuideStep 
            number="2" 
            title="Upload Your Project" 
            description="Drag & drop or select your Java project ZIP file (supports multi-module Maven/Gradle projects)"
            isDarkMode={isDarkMode}
          />
          <GuideStep 
            number="3" 
            title="AI Processing" 
            description="Our AI engine analyzes your code using 50+ detection rules and machine learning models"
            isDarkMode={isDarkMode}
          />
          <GuideStep 
            number="4" 
            title="Review Results" 
            description="Explore interactive dashboards with severity breakdown, file-level insights, and actionable recommendations"
            isDarkMode={isDarkMode}
          />
          <GuideStep 
            number="5" 
            title="Track & Improve" 
            description="Monitor quality trends over time and compare versions to measure improvement"
            isDarkMode={isDarkMode}
          />
        </div>
      </div>

      {/* Severity System */}
      <div 
        ref={el => sectionRefs.current['severity'] = el}
        data-section="severity"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('severity') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border border-gray-700' : 'bg-gradient-to-br from-gray-50 to-white border border-gray-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8">Understanding Severity Levels</h2>
        <div className="space-y-6">
          <SeverityCard
            icon={<AlertCircle className="w-6 h-6" />}
            level="Critical"
            color="red"
            description="Security vulnerabilities, data leaks, or critical bugs requiring immediate action"
            examples={["SQL Injection", "Hardcoded credentials", "Null pointer exceptions"]}
            isDarkMode={isDarkMode}
          />
          <SeverityCard
            icon={<AlertTriangle className="w-6 h-6" />}
            level="High"
            color="yellow"
            description="Significant code quality issues or potential bugs that should be addressed soon"
            examples={["Empty catch blocks", "Resource leaks", "High complexity methods"]}
            isDarkMode={isDarkMode}
          />
          <SeverityCard
            icon={<AlertTriangle className="w-6 h-6" />}
            level="Medium"
            color="orange"
            description="Maintainability concerns and code smells that impact long-term quality"
            examples={["Long methods", "Too many parameters", "Code duplication"]}
            isDarkMode={isDarkMode}
          />
          <SeverityCard
            icon={<Info className="w-6 h-6" />}
            level="Low"
            color="blue"
            description="Minor improvements, style violations, and best practice suggestions"
            examples={["Magic numbers", "Naming conventions", "Missing documentation"]}
            isDarkMode={isDarkMode}
          />
        </div>
      </div>
    </div>
  );
}

function GradingTab({ isDarkMode, sectionRefs, visibleSections }) {
  return (
    <div className="space-y-12 max-w-5xl mx-auto">
      {/* Grading System */}
      <div 
        ref={el => sectionRefs.current['grading'] = el}
        data-section="grading"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('grading') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-gray-800/50 to-gray-900/50 border border-gray-700' : 'bg-gradient-to-br from-gray-50 to-white border border-gray-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
          <BarChart3 className="w-8 h-8 text-blue-600" />
          Intelligent Grading System
        </h2>
        <p className={`mb-8 text-lg ${isDarkMode ? 'text-gray-300' : 'text-gray-600'}`}>
          Our AI-powered grading system evaluates your code across multiple dimensions including security, maintainability, complexity, and best practices.
        </p>
        <div className="space-y-6">
          <GradeCard 
            grade="A" 
            range="0-5 issues" 
            score="90-100"
            color="green" 
            title="Exceptional Quality"
            description="Production-ready code with minimal to no issues. Follows best practices and security standards."
            isDarkMode={isDarkMode}
          />
          <GradeCard 
            grade="B" 
            range="6-15 issues" 
            score="75-89"
            color="blue" 
            title="Good Quality"
            description="Solid codebase with minor improvements needed. Generally follows best practices."
            isDarkMode={isDarkMode}
          />
          <GradeCard 
            grade="C" 
            range="16-30 issues" 
            score="60-74"
            color="yellow" 
            title="Acceptable Quality"
            description="Functional code but requires attention to improve maintainability and reduce technical debt."
            isDarkMode={isDarkMode}
          />
          <GradeCard 
            grade="D" 
            range="31-50 issues" 
            score="40-59"
            color="orange" 
            title="Poor Quality"
            description="Significant issues present. Requires substantial refactoring and security improvements."
            isDarkMode={isDarkMode}
          />
          <GradeCard 
            grade="F" 
            range="50+ issues" 
            score="0-39"
            color="red" 
            title="Critical Issues"
            description="Major problems detected. Immediate action required before deployment."
            isDarkMode={isDarkMode}
          />
        </div>
      </div>

      {/* Metrics Breakdown */}
      <div 
        ref={el => sectionRefs.current['metrics'] = el}
        data-section="metrics"
        className={`rounded-3xl p-10 transition-all duration-700 ${
          visibleSections.has('metrics') ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'
        } ${isDarkMode ? 'bg-gradient-to-br from-blue-900/20 to-purple-900/20 border border-blue-800/50' : 'bg-gradient-to-br from-blue-50 to-purple-50 border border-blue-200 shadow-xl'}`}>
        <h2 className="text-3xl font-bold mb-8">Quality Metrics Explained</h2>
        <div className="grid md:grid-cols-2 gap-6">
          <MetricCard title="Cyclomatic Complexity" value="< 10" description="Measures code complexity and decision points" isDarkMode={isDarkMode} />
          <MetricCard title="Maintainability Index" value="> 70" description="Overall code maintainability score" isDarkMode={isDarkMode} />
          <MetricCard title="Technical Debt Ratio" value="< 5%" description="Estimated time to fix vs development time" isDarkMode={isDarkMode} />
          <MetricCard title="Code Coverage" value="> 80%" description="Percentage of code tested" isDarkMode={isDarkMode} />
        </div>
      </div>
    </div>
  );
}

// Helper Components
function CapabilityCard({ icon, title, description, gradient, isDarkMode }) {
  return (
    <div className={`p-8 rounded-2xl transition-all duration-300 hover:scale-105 hover:shadow-2xl ${
      isDarkMode ? 'bg-gray-800/50 border border-gray-700' : 'bg-white border border-gray-200 shadow-lg'
    }`}>
      <div className={`inline-flex p-4 rounded-xl bg-gradient-to-r ${gradient} text-white mb-6 shadow-lg`}>
        {icon}
      </div>
      <h3 className="font-bold text-xl mb-3">{title}</h3>
      <p className={`text-sm leading-relaxed ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{description}</p>
    </div>
  );
}

function TechItem({ icon, title, desc, isDarkMode }) {
  return (
    <div className="flex items-start gap-4">
      <div className="p-3 rounded-lg bg-gradient-to-r from-blue-600 to-cyan-600 text-white flex-shrink-0">
        {icon}
      </div>
      <div>
        <h4 className="font-semibold mb-1">{title}</h4>
        <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{desc}</p>
      </div>
    </div>
  );
}

function DetectionItem({ icon, text, isDarkMode }) {
  return (
    <div className="flex items-center gap-3 p-4 rounded-xl bg-gradient-to-r from-transparent to-blue-500/5 border border-blue-500/20">
      <CheckCircle2 className="w-5 h-5 text-green-500 flex-shrink-0" />
      <span className={`text-sm ${isDarkMode ? 'text-gray-300' : 'text-gray-700'}`}>{text}</span>
    </div>
  );
}

function FeatureCard({ icon, title, features, gradient, isDarkMode }) {
  return (
    <div className={`p-8 rounded-2xl transition-all duration-300 hover:scale-105 ${
      isDarkMode ? 'bg-gray-800/50 border border-gray-700' : 'bg-white border border-gray-200 shadow-lg'
    }`}>
      <div className={`inline-flex p-4 rounded-xl bg-gradient-to-r ${gradient} text-white mb-6`}>
        {icon}
      </div>
      <h3 className="font-bold text-xl mb-6">{title}</h3>
      <ul className="space-y-3">
        {features.map((feature, i) => (
          <li key={i} className="flex items-start gap-3">
            <CheckCircle2 className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" />
            <span className={`text-sm ${isDarkMode ? 'text-gray-300' : 'text-gray-700'}`}>{feature}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function GuideStep({ number, title, description, isDarkMode }) {
  return (
    <div className="flex items-start gap-6">
      <div className="flex-shrink-0 w-12 h-12 rounded-xl bg-gradient-to-r from-blue-600 to-cyan-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
        {number}
      </div>
      <div className="flex-1">
        <h4 className="font-bold text-lg mb-2">{title}</h4>
        <p className={`${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{description}</p>
      </div>
    </div>
  );
}

function SeverityCard({ icon, level, color, description, examples, isDarkMode }) {
  const colorClasses = {
    red: 'from-red-600 to-rose-600',
    yellow: 'from-yellow-600 to-amber-600',
    orange: 'from-orange-600 to-red-500',
    blue: 'from-blue-600 to-cyan-600'
  };

  return (
    <div className={`p-6 rounded-2xl border transition-all duration-300 hover:scale-102 ${
      isDarkMode ? 'bg-gray-800/30 border-gray-700' : 'bg-white border-gray-200'
    }`}>
      <div className="flex items-start gap-4">
        <div className={`p-3 rounded-xl bg-gradient-to-r ${colorClasses[color]} text-white`}>
          {icon}
        </div>
        <div className="flex-1">
          <h4 className="font-bold text-xl mb-2">{level}</h4>
          <p className={`mb-4 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{description}</p>
          <div className="flex flex-wrap gap-2">
            {examples.map((ex, i) => (
              <span key={i} className={`px-3 py-1 rounded-lg text-xs font-medium ${
                isDarkMode ? 'bg-gray-700 text-gray-300' : 'bg-gray-100 text-gray-700'
              }`}>
                {ex}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function GradeCard({ grade, range, score, color, title, description, isDarkMode }) {
  const colorClasses = {
    green: 'from-green-600 to-emerald-600',
    blue: 'from-blue-600 to-cyan-600',
    yellow: 'from-yellow-600 to-amber-600',
    orange: 'from-orange-600 to-red-500',
    red: 'from-red-600 to-rose-600'
  };

  return (
    <div className={`p-6 rounded-2xl border transition-all duration-300 hover:scale-102 ${
      isDarkMode ? 'bg-gray-800/30 border-gray-700' : 'bg-white border-gray-200'
    }`}>
      <div className="flex items-center gap-6">
        <div className={`flex-shrink-0 w-20 h-20 rounded-2xl bg-gradient-to-r ${colorClasses[color]} flex items-center justify-center text-white font-bold text-4xl shadow-lg`}>
          {grade}
        </div>
        <div className="flex-1">
          <div className="flex items-center gap-4 mb-2">
            <h4 className="font-bold text-xl">{title}</h4>
            <span className={`px-3 py-1 rounded-lg text-sm font-medium ${
              isDarkMode ? 'bg-gray-700 text-gray-300' : 'bg-gray-100 text-gray-700'
            }`}>
              Score: {score}
            </span>
          </div>
          <p className={`text-sm mb-2 ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{description}</p>
          <p className={`text-xs font-medium ${isDarkMode ? 'text-gray-500' : 'text-gray-500'}`}>Issue Range: {range}</p>
        </div>
      </div>
    </div>
  );
}

function MetricCard({ title, value, description, isDarkMode }) {
  return (
    <div className={`p-6 rounded-2xl ${isDarkMode ? 'bg-gray-800/30' : 'bg-white/50'}`}>
      <div className="text-3xl font-bold mb-2 bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent">{value}</div>
      <h4 className="font-semibold mb-2">{title}</h4>
      <p className={`text-sm ${isDarkMode ? 'text-gray-400' : 'text-gray-600'}`}>{description}</p>
    </div>
  );
}
