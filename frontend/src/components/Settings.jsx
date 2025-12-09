import React, { useState, useEffect } from 'react'
import { X, Save, TestTube, Eye, EyeOff } from 'lucide-react'
import { Button } from './ui/button'
import { Input } from './ui/input'
import { toast } from 'sonner'
import api from '../api'
import './Settings.css'
import { ChevronDown, ChevronUp, RotateCcw } from 'lucide-react'

export function Settings({ isOpen, onClose, isDarkMode }) {
  const [settings, setSettings] = useState({
    // Long Method
    longMethodEnabled: true,
    maxMethodLength: 50,
    maxMethodComplexity: 10,
    // Long Parameter
    longParameterEnabled: true,
    maxParameterCount: 5,
    // Long Identifier
    longIdentifierEnabled: true,
    maxIdentifierLength: 30,
    minIdentifierLength: 3,
    // Magic Number
    magicNumberEnabled: true,
    magicNumberThreshold: 3,
    // Missing Default
    missingDefaultEnabled: true,
    // Empty Catch
    emptyCatchEnabled: true,
    // Complex Conditional
    complexConditionalEnabled: true,
    maxConditionalOperators: 4,
    maxNestingDepth: 3,
    // Long Statement
    longStatementEnabled: true,
    maxStatementTokens: 40,
    maxStatementChars: 250,
    maxMethodChainLength: 5,
    // Broken Modularization
    brokenModularizationEnabled: true,
    maxResponsibilities: 3,
    minCohesionIndex: 0.4,
    maxCouplingCount: 6,
    // Deficient Encapsulation
    deficientEncapsulationEnabled: true,
    // Unnecessary Abstraction
    unnecessaryAbstractionEnabled: true,
    maxAbstractionUsage: 1,
    // AI Settings
    aiProvider: 'ollama',
    aiApiKey: '',
    aiModel: 'deepseek-coder:latest',
    aiEnabled: true
  })
  
  const [showApiKey, setShowApiKey] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isTesting, setIsTesting] = useState(false)
  const [expandedSections, setExpandedSections] = useState({
    longMethod: true,
    longParameter: false,
    longIdentifier: false,
    magicNumber: false,
    complexConditional: false,
    longStatement: false,
    brokenModularization: false,
    unnecessaryAbstraction: false,
    simple: false,
    ai: false
  })

  const toggleSection = (section) => {
    setExpandedSections(prev => ({ ...prev, [section]: !prev[section] }))
  }

  const resetToDefaults = async () => {
    if (!window.confirm('Reset all settings to default values?')) return
    try {
      const userId = localStorage.getItem('userId') || 'anonymous'
      const response = await api.post(`/settings/${userId}/reset`)
      setSettings(response.data)
      toast.success('Settings reset to defaults!')
    } catch (error) {
      toast.error('Failed to reset settings')
    }
  }

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

        <div className="p-6 space-y-4">
          {/* Quick Actions */}
          <div className="flex gap-2 mb-4">
            <Button onClick={resetToDefaults} variant="outline" size="sm" className="flex-1">
              <RotateCcw className="mr-2 h-4 w-4" />
              Reset to Defaults
            </Button>
          </div>
          {/* Long Method */}
          <div className={`detector-card ${isDarkMode ? 'dark' : ''}`}>
            <div className="detector-header" onClick={() => toggleSection('longMethod')}>
              <div className="flex items-center gap-3">
                <span className="text-2xl">🔴</span>
                <div>
                  <h3 className="text-lg font-semibold">Long Method Detector</h3>
                  <p className="text-xs opacity-70">Detects methods that are too long or complex</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <input type="checkbox" id="longMethodEnabled" checked={settings.longMethodEnabled}
                  onChange={(e) => { e.stopPropagation(); handleInputChange('longMethodEnabled', e.target.checked); }}
                  className="toggle-switch" />
                {expandedSections.longMethod ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
              </div>
            </div>
            {expandedSections.longMethod && (
              <div className="detector-content">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="input-group">
                    <label>Max Method Length <span className="value-badge">{settings.maxMethodLength}</span></label>
                    <input type="range" value={settings.maxMethodLength}
                      onChange={(e) => handleInputChange('maxMethodLength', parseInt(e.target.value))}
                      min="10" max="200" className="range-slider" />
                    <div className="range-labels"><span>10</span><span>200</span></div>
                  </div>
                  <div className="input-group">
                    <label>Max Complexity <span className="value-badge">{settings.maxMethodComplexity}</span></label>
                    <input type="range" value={settings.maxMethodComplexity}
                      onChange={(e) => handleInputChange('maxMethodComplexity', parseInt(e.target.value))}
                      min="1" max="50" className="range-slider" />
                    <div className="range-labels"><span>1</span><span>50</span></div>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Long Parameter */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟡 Long Parameter List</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="longParameterEnabled" checked={settings.longParameterEnabled}
                onChange={(e) => handleInputChange('longParameterEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="longParameterEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div>
              <label className="block text-sm font-medium mb-2">Max Parameters</label>
              <Input type="number" value={settings.maxParameterCount}
                onChange={(e) => handleInputChange('maxParameterCount', parseInt(e.target.value))}
                min="1" max="15" />
            </div>
          </section>

          {/* Long Identifier */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟠 Long Identifier</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="longIdentifierEnabled" checked={settings.longIdentifierEnabled}
                onChange={(e) => handleInputChange('longIdentifierEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="longIdentifierEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Max Length</label>
                <Input type="number" value={settings.maxIdentifierLength}
                  onChange={(e) => handleInputChange('maxIdentifierLength', parseInt(e.target.value))}
                  min="10" max="100" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Min Length</label>
                <Input type="number" value={settings.minIdentifierLength}
                  onChange={(e) => handleInputChange('minIdentifierLength', parseInt(e.target.value))}
                  min="1" max="10" />
              </div>
            </div>
          </section>

          {/* Magic Number */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟡 Magic Number</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="magicNumberEnabled" checked={settings.magicNumberEnabled}
                onChange={(e) => handleInputChange('magicNumberEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="magicNumberEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div>
              <label className="block text-sm font-medium mb-2">Threshold</label>
              <Input type="number" value={settings.magicNumberThreshold}
                onChange={(e) => handleInputChange('magicNumberThreshold', parseInt(e.target.value))}
                min="0" max="20" />
            </div>
          </section>

          {/* Complex Conditional */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟡 Complex Conditional</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="complexConditionalEnabled" checked={settings.complexConditionalEnabled}
                onChange={(e) => handleInputChange('complexConditionalEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="complexConditionalEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Max Operators</label>
                <Input type="number" value={settings.maxConditionalOperators}
                  onChange={(e) => handleInputChange('maxConditionalOperators', parseInt(e.target.value))}
                  min="1" max="15" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Nesting</label>
                <Input type="number" value={settings.maxNestingDepth}
                  onChange={(e) => handleInputChange('maxNestingDepth', parseInt(e.target.value))}
                  min="1" max="10" />
              </div>
            </div>
          </section>

          {/* Long Statement */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟠 Long Statement</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="longStatementEnabled" checked={settings.longStatementEnabled}
                onChange={(e) => handleInputChange('longStatementEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="longStatementEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Max Tokens</label>
                <Input type="number" value={settings.maxStatementTokens}
                  onChange={(e) => handleInputChange('maxStatementTokens', parseInt(e.target.value))}
                  min="10" max="150" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Chars</label>
                <Input type="number" value={settings.maxStatementChars}
                  onChange={(e) => handleInputChange('maxStatementChars', parseInt(e.target.value))}
                  min="50" max="1000" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Chain</label>
                <Input type="number" value={settings.maxMethodChainLength}
                  onChange={(e) => handleInputChange('maxMethodChainLength', parseInt(e.target.value))}
                  min="2" max="20" />
              </div>
            </div>
          </section>

          {/* Broken Modularization */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🔴 Broken Modularization</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="brokenModularizationEnabled" checked={settings.brokenModularizationEnabled}
                onChange={(e) => handleInputChange('brokenModularizationEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="brokenModularizationEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium mb-2">Max Responsibilities</label>
                <Input type="number" value={settings.maxResponsibilities}
                  onChange={(e) => handleInputChange('maxResponsibilities', parseInt(e.target.value))}
                  min="1" max="10" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Min Cohesion</label>
                <Input type="number" step="0.1" value={settings.minCohesionIndex}
                  onChange={(e) => handleInputChange('minCohesionIndex', parseFloat(e.target.value))}
                  min="0" max="1" />
              </div>
              <div>
                <label className="block text-sm font-medium mb-2">Max Coupling</label>
                <Input type="number" value={settings.maxCouplingCount}
                  onChange={(e) => handleInputChange('maxCouplingCount', parseInt(e.target.value))}
                  min="1" max="20" />
              </div>
            </div>
          </section>

          {/* Unnecessary Abstraction */}
          <section>
            <h3 className="text-lg font-semibold mb-4">🟠 Unnecessary Abstraction</h3>
            <div className="flex items-center space-x-3 mb-4">
              <input type="checkbox" id="unnecessaryAbstractionEnabled" checked={settings.unnecessaryAbstractionEnabled}
                onChange={(e) => handleInputChange('unnecessaryAbstractionEnabled', e.target.checked)}
                className="w-4 h-4 text-blue-600 rounded" />
              <label htmlFor="unnecessaryAbstractionEnabled" className="text-sm font-medium">Enable</label>
            </div>
            <div>
              <label className="block text-sm font-medium mb-2">Max Usage</label>
              <Input type="number" value={settings.maxAbstractionUsage}
                onChange={(e) => handleInputChange('maxAbstractionUsage', parseInt(e.target.value))}
                min="0" max="5" />
            </div>
          </section>

          {/* Simple Detectors */}
          <section>
            <h3 className="text-lg font-semibold mb-4">Simple Detectors (Toggle Only)</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center space-x-3">
                <input type="checkbox" id="missingDefaultEnabled" checked={settings.missingDefaultEnabled}
                  onChange={(e) => handleInputChange('missingDefaultEnabled', e.target.checked)}
                  className="w-4 h-4 text-blue-600 rounded" />
                <label htmlFor="missingDefaultEnabled" className="text-sm font-medium">🔴 Missing Default Case</label>
              </div>
              <div className="flex items-center space-x-3">
                <input type="checkbox" id="emptyCatchEnabled" checked={settings.emptyCatchEnabled}
                  onChange={(e) => handleInputChange('emptyCatchEnabled', e.target.checked)}
                  className="w-4 h-4 text-blue-600 rounded" />
                <label htmlFor="emptyCatchEnabled" className="text-sm font-medium">🔴 Empty Catch Blocks</label>
              </div>
              <div className="flex items-center space-x-3">
                <input type="checkbox" id="deficientEncapsulationEnabled" checked={settings.deficientEncapsulationEnabled}
                  onChange={(e) => handleInputChange('deficientEncapsulationEnabled', e.target.checked)}
                  className="w-4 h-4 text-blue-600 rounded" />
                <label htmlFor="deficientEncapsulationEnabled" className="text-sm font-medium">🟡 Deficient Encapsulation</label>
              </div>
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