import React from 'react'

export const getDocumentationContent = (isDarkMode) => ({
  'getting-started': {
    title: 'Getting Started with DevSync',
    content: (
      <div className="space-y-6">
        <p className={isDarkMode ? 'text-gray-300' : 'text-gray-900'}>Welcome to DevSync! This guide will help you get started with analyzing your Java projects.</p>
        
        <div className="bg-white/5 border border-white/10 rounded-lg p-6">
          <h3 className="text-xl font-semibold mb-4">Quick Start Steps</h3>
          <ol className={`space-y-3 list-decimal list-inside ${isDarkMode ? 'text-gray-300' : 'text-gray-900'}`}>
            <li>Create your DevSync account</li>
            <li>Log in to your dashboard</li>
            <li>Upload your Java project as a ZIP file</li>
            <li>Wait for the analysis to complete</li>
            <li>Review your code quality report</li>
          </ol>
        </div>

        <div className="bg-blue-500/10 border border-blue-500/20 rounded-lg p-6">
          <h3 className="text-xl font-semibold mb-4 text-blue-400">System Requirements</h3>
          <ul className={`space-y-2 ${isDarkMode ? 'text-gray-300' : 'text-gray-900'}`}>
            <li>• Java projects (Java 8 or higher)</li>
            <li>• ZIP file format for uploads</li>
            <li>• Maximum file size: 100MB</li>
            <li>• Modern web browser with JavaScript enabled</li>
          </ul>
        </div>
      </div>
    )
  },
  'uploading': {
    title: 'Uploading Your Java Projects',
    content: (
      <div className="space-y-6">
        <p className={isDarkMode ? 'text-gray-300' : 'text-gray-900'}>Learn how to properly prepare and upload your Java projects for analysis.</p>
        
        <div className="bg-white/5 border border-white/10 rounded-lg p-6">
          <h3 className="text-xl font-semibold mb-4">Preparing Your Project</h3>
          <div className={`space-y-4 ${isDarkMode ? 'text-gray-300' : 'text-gray-900'}`}>
            <p>1. <strong>Clean your project:</strong> Remove build artifacts, compiled classes, and temporary files</p>
            <p>2. <strong>Include source files:</strong> Ensure all .java files are included in your ZIP</p>
            <p>3. <strong>Maintain structure:</strong> Keep your package structure intact</p>
            <p>4. <strong>Check file size:</strong> Ensure your ZIP file is under 100MB</p>
          </div>
        </div>

        <div className="bg-green-500/10 border border-green-500/20 rounded-lg p-6">
          <h3 className="text-xl font-semibold mb-4 text-green-400">Supported File Types</h3>
          <ul className={`grid grid-cols-2 gap-2 ${isDarkMode ? 'text-gray-300' : 'text-gray-900'}`}>
            <li>• .java (source files)</li>
            <li>• .properties (configuration)</li>
            <li>• .xml (Maven/Gradle configs)</li>
            <li>• .md (documentation)</li>
          </ul>
        </div>
      </div>
    )
  }
})