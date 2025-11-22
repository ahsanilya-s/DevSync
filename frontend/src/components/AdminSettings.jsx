import React, { useState, useEffect } from 'react'
import { Save, RefreshCw, Settings, Filter, Shield, Server } from 'lucide-react'
import { Button } from './ui/button'
import { Input } from './ui/input'
import { toast } from 'sonner'
import api from '../api'

export function AdminSettings({ isDarkMode }) {
  const [settings, setSettings] = useState({})
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    loadSettings()
  }, [])

  const loadSettings = async () => {
    try {
      const response = await api.get('/admin/settings')
      setSettings(response.data)
    } catch (error) {
      console.error('Failed to load settings:', error)
      // Initialize default settings if none exist
      await initializeSettings()
    } finally {
      setLoading(false)
    }
  }

  const initializeSettings = async () => {
    try {
      await api.post('/admin/settings/init')
      await loadSettings()
      toast.success('Default settings initialized')
    } catch (error) {
      toast.error('Failed to initialize settings')
    }
  }

  const handleSettingChange = (category, index, field, value) => {
    setSettings(prev => ({
      ...prev,
      [category]: prev[category].map((setting, i) => 
        i === index ? { ...setting, [field]: value } : setting
      )
    }))
  }

  const saveSettings = async () => {
    setSaving(true)
    try {
      const allSettings = Object.values(settings).flat()
      await api.post('/admin/settings', allSettings)
      toast.success('Settings saved successfully')
    } catch (error) {
      toast.error('Failed to save settings: ' + (error.response?.data || error.message))
    } finally {
      setSaving(false)
    }
  }

  const getCategoryIcon = (category) => {
    switch (category) {
      case 'filters': return <Filter className="h-5 w-5" />
      case 'detection': return <Shield className="h-5 w-5" />
      case 'system': return <Server className="h-5 w-5" />
      default: return <Settings className="h-5 w-5" />
    }
  }

  const getCategoryTitle = (category) => {
    switch (category) {
      case 'filters': return 'App Filters & Limits'
      case 'detection': return 'Detection Rules'
      case 'system': return 'System Settings'
      default: return category.charAt(0).toUpperCase() + category.slice(1)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <RefreshCw className="h-6 w-6 animate-spin" />
        <span className="ml-2">Loading settings...</span>
      </div>
    )
  }

  return (
    <div className={`p-6 ${isDarkMode ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'}`}>
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <Settings className="h-6 w-6" />
          <h2 className="text-2xl font-bold">Admin Settings</h2>
        </div>
        <Button onClick={saveSettings} disabled={saving}>
          <Save className="mr-2 h-4 w-4" />
          {saving ? 'Saving...' : 'Save All Settings'}
        </Button>
      </div>

      <div className="space-y-8">
        {Object.entries(settings).map(([category, categorySettings]) => (
          <div key={category} className={`rounded-lg border p-6 ${
            isDarkMode ? 'border-gray-700 bg-gray-800' : 'border-gray-200 bg-gray-50'
          }`}>
            <div className="flex items-center gap-3 mb-4">
              {getCategoryIcon(category)}
              <h3 className="text-lg font-semibold">{getCategoryTitle(category)}</h3>
            </div>
            
            <div className="grid gap-4">
              {categorySettings.map((setting, index) => (
                <div key={setting.settingKey} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
                  <div>
                    <label className="block text-sm font-medium mb-1">
                      {setting.settingKey.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase())}
                    </label>
                    <p className="text-xs text-gray-500">{setting.description}</p>
                  </div>
                  
                  <div>
                    {setting.settingKey.includes('enabled') || setting.settingKey.includes('mode') ? (
                      <select
                        value={setting.settingValue}
                        onChange={(e) => handleSettingChange(category, index, 'settingValue', e.target.value)}
                        className={`w-full p-2 border rounded-md ${
                          isDarkMode 
                            ? 'bg-gray-700 border-gray-600 text-white' 
                            : 'bg-white border-gray-300 text-gray-900'
                        }`}
                      >
                        <option value="true">Enabled</option>
                        <option value="false">Disabled</option>
                      </select>
                    ) : (
                      <Input
                        type={setting.settingKey.includes('max') || setting.settingKey.includes('count') ? 'number' : 'text'}
                        value={setting.settingValue}
                        onChange={(e) => handleSettingChange(category, index, 'settingValue', e.target.value)}
                        className="w-full"
                      />
                    )}
                  </div>
                  
                  <div className="text-sm text-gray-500">
                    Current: <span className="font-mono">{setting.settingValue}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {Object.keys(settings).length === 0 && (
        <div className="text-center py-12">
          <Settings className="h-12 w-12 mx-auto mb-4 text-gray-400" />
          <h3 className="text-lg font-medium mb-2">No Settings Found</h3>
          <p className="text-gray-500 mb-4">Initialize default settings to get started.</p>
          <Button onClick={initializeSettings}>
            <RefreshCw className="mr-2 h-4 w-4" />
            Initialize Default Settings
          </Button>
        </div>
      )}
    </div>
  )
}