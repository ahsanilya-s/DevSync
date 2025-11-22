import React, { useState, useEffect } from 'react'
import { X, Save, TestTube, Eye, EyeOff } from 'lucide-react'
import { Button } from './ui/button'
import { Input } from './ui/input'
import { toast } from 'sonner'
import api from '../api'
import './Settings.css'

export function Settings({ isOpen, onClose, isDarkMode }) {
  const [settings, setSettings] = useState({
    maxMethodLength: 50,
    maxParameterCount: 5,
    maxIdentifierLength: 30,
    magicNumberThreshold: 3,
    missingDefaultEnabled: true,
    emptyCatchEnabled: true,
    longMethodEnabled: true,
    longParameterEnabled: true,
    magicNumberEnabled: true,
    longIdentifierEnabled: true,
    aiProvider: 'ollama',
    aiApiKey: '',
    aiModel: 'deepseek-coder:latest',
    aiEnabled: true
  })
  
  const [showApiKey, setShowApiKey] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isTesting, setIsTesting] = useState(false)

  useEffect(() => {
    if (isOpen) {
      loadSettings()
    }
  }, [isOpen])

  const loadSettings = async () => {
    try {
      const userId = localStorage.getItem('userId') || 'anonymous'
      const response = await api.get(`/settings/${userId}`)
      setSettings(response.data)
    } catch (error) {
      console.error('Failed to load settings:', error)
    }
  }

  const handleSave = async () => {
    setIsLoading(true)
    try {
      const userId = localStorage.getItem('userId') || 'anonymous'
      await api.post(`/settings/${userId}`, settings)
      toast.success('Settings saved successfully!')
      onClose()
    } catch (error) {
      toast.error('Failed to save settings: ' + (error.response?.data || error.message))
    } finally {
      setIsLoading(false)
    }
  }

  const handleTestAI = async () => {
    setIsTesting(true)
    try {
      const userId = localStorage.getItem('userId') || 'anonymous'
      const response = await api.post(`/settings/${userId}/test-ai`, settings)
      toast.success(response.data)
    } catch (error) {
      toast.error('AI test failed: ' + (error.response?.data || error.message))
    } finally {
      setIsTesting(false)
    }
  }

  const handleInputChange = (field, value) => {
    setSettings(prev => ({ ...prev, [field]: value }))
  }

  const aiProviders = [
    { value: 'ollama', label: 'Ollama (Local)', models: ['deepseek-coder:latest', 'codellama:latest', 'llama2:latest'] },
    { value: 'openai', label: 'OpenAI', models: ['gpt-4', 'gpt-3.5-turbo', 'gpt-4-turbo'] },
    { value: 'anthropic', label: 'Anthropic Claude', models: ['claude-3-opus-20240229', 'claude-3-sonnet-20240229', 'claude-3-haiku-20240307'] },
    { value: 'none', label: 'Disabled', models: [] }
  ]

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className={`w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-lg shadow-xl ${
        isDarkMode ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'
      }`}>
        {/* Header */}
        <div className={`flex items-center justify-between p-6 border-b ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <h2 className="text-2xl font-bold">Settings</h2>
          <Button variant="ghost" size="sm" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="p-6 space-y-8">
          {/* Code Smell Detection Parameters */}
          <section>
            <h3 className="text-lg font-semibold mb-4">Code Smell Detection Parameters</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Max Method Length (lines)</label>
                <Input
                  type="number"
                  value={settings.maxMethodLength}
                  onChange={(e) => handleInputChange('maxMethodLength', parseInt(e.target.value))}
                  min="10"
                  max="200"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Parameter Count</label>
                <Input
                  type="number"
                  value={settings.maxParameterCount}
                  onChange={(e) => handleInputChange('maxParameterCount', parseInt(e.target.value))}
                  min="1"
                  max="20"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Identifier Length</label>
                <Input
                  type="number"
                  value={settings.maxIdentifierLength}
                  onChange={(e) => handleInputChange('maxIdentifierLength', parseInt(e.target.value))}
                  min="10"
                  max="100"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Magic Number Threshold</label>
                <Input
                  type="number"
                  value={settings.magicNumberThreshold}
                  onChange={(e) => handleInputChange('magicNumberThreshold', parseInt(e.target.value))}
                  min="1"
                  max="10"
                />
              </div>
            </div>
          </section>

          {/* Detector Toggles */}
          <section>
            <h3 className="text-lg font-semibold mb-4">Code Smell Detectors</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {[
                { key: 'missingDefaultEnabled', label: 'Missing Default Case' },
                { key: 'emptyCatchEnabled', label: 'Empty Catch Blocks' },
                { key: 'longMethodEnabled', label: 'Long Methods' },
                { key: 'longParameterEnabled', label: 'Long Parameter Lists' },
                { key: 'magicNumberEnabled', label: 'Magic Numbers' },
                { key: 'longIdentifierEnabled', label: 'Long Identifiers' }
              ].map(({ key, label }) => (
                <div key={key} className="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id={key}
                    checked={settings[key]}
                    onChange={(e) => handleInputChange(key, e.target.checked)}
                    className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                  />
                  <label htmlFor={key} className="text-sm font-medium">{label}</label>
                </div>
              ))}
            </div>
          </section>

          {/* AI Assistant Settings */}
          <section>
            <h3 className="text-lg font-semibold mb-4">AI Assistant Configuration</h3>
            
            <div className="space-y-4">
              <div className="flex items-center space-x-3">
                <input
                  type="checkbox"
                  id="aiEnabled"
                  checked={settings.aiEnabled}
                  onChange={(e) => handleInputChange('aiEnabled', e.target.checked)}
                  className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                />
                <label htmlFor="aiEnabled" className="text-sm font-medium">Enable AI Analysis</label>
              </div>

              {settings.aiEnabled && (
                <>
                  <div>
                    <label className="block text-sm font-medium mb-2">AI Provider</label>
                    <select
                      value={settings.aiProvider}
                      onChange={(e) => {
                        handleInputChange('aiProvider', e.target.value)
                        const provider = aiProviders.find(p => p.value === e.target.value)
                        if (provider && provider.models.length > 0) {
                          handleInputChange('aiModel', provider.models[0])
                        }
                      }}
                      className={`w-full p-2 border rounded-md ${
                        isDarkMode 
                          ? 'bg-gray-800 border-gray-600 text-white' 
                          : 'bg-white border-gray-300 text-gray-900'
                      }`}
                    >
                      {aiProviders.map(provider => (
                        <option key={provider.value} value={provider.value}>
                          {provider.label}
                        </option>
                      ))}
                    </select>
                  </div>

                  {settings.aiProvider !== 'none' && settings.aiProvider !== 'ollama' && (
                    <div>
                      <label className="block text-sm font-medium mb-2">API Key</label>
                      <div className="relative">
                        <Input
                          type={showApiKey ? 'text' : 'password'}
                          value={settings.aiApiKey}
                          onChange={(e) => handleInputChange('aiApiKey', e.target.value)}
                          placeholder="Enter your API key"
                          className="pr-10"
                        />
                        <button
                          type="button"
                          onClick={() => setShowApiKey(!showApiKey)}
                          className="absolute right-2 top-1/2 transform -translate-y-1/2"
                        >
                          {showApiKey ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                        </button>
                      </div>
                    </div>
                  )}

                  {settings.aiProvider !== 'none' && (
                    <div>
                      <label className="block text-sm font-medium mb-2">Model</label>
                      <select
                        value={settings.aiModel}
                        onChange={(e) => handleInputChange('aiModel', e.target.value)}
                        className={`w-full p-2 border rounded-md ${
                          isDarkMode 
                            ? 'bg-gray-800 border-gray-600 text-white' 
                            : 'bg-white border-gray-300 text-gray-900'
                        }`}
                      >
                        {aiProviders
                          .find(p => p.value === settings.aiProvider)
                          ?.models.map(model => (
                            <option key={model} value={model}>{model}</option>
                          ))}
                      </select>
                    </div>
                  )}

                  {settings.aiProvider !== 'none' && (
                    <Button
                      onClick={handleTestAI}
                      disabled={isTesting}
                      variant="outline"
                      className="w-full"
                    >
                      <TestTube className="mr-2 h-4 w-4" />
                      {isTesting ? 'Testing...' : 'Test AI Connection'}
                    </Button>
                  )}
                </>
              )}
            </div>
          </section>
        </div>

        {/* Footer */}
        <div className={`flex justify-end space-x-3 p-6 border-t ${
          isDarkMode ? 'border-gray-700' : 'border-gray-200'
        }`}>
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleSave} disabled={isLoading}>
            <Save className="mr-2 h-4 w-4" />
            {isLoading ? 'Saving...' : 'Save Settings'}
          </Button>
        </div>
      </div>
    </div>
  )
}