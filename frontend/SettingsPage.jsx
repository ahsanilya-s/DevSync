import React, { useState, useEffect } from 'react';
import './SettingsPage.css';

const SettingsPage = ({ userId = 'default-user' }) => {
  const [settings, setSettings] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadSettings();
  }, [userId]);

  const loadSettings = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/settings/${userId}`);
      const data = await response.json();
      setSettings(data);
      setLoading(false);
    } catch (error) {
      setMessage('Error loading settings: ' + error.message);
      setLoading(false);
    }
  };

  const saveSettings = async () => {
    setSaving(true);
    try {
      const response = await fetch(`http://localhost:8080/api/settings/${userId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(settings)
      });
      if (response.ok) {
        setMessage('✅ Settings saved successfully!');
        setTimeout(() => setMessage(''), 3000);
      }
    } catch (error) {
      setMessage('❌ Error saving settings: ' + error.message);
    }
    setSaving(false);
  };

  const resetSettings = async () => {
    if (!confirm('Reset all settings to defaults?')) return;
    try {
      const response = await fetch(`http://localhost:8080/api/settings/${userId}/reset`, {
        method: 'POST'
      });
      const data = await response.json();
      setSettings(data);
      setMessage('✅ Settings reset to defaults!');
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage('❌ Error resetting settings: ' + error.message);
    }
  };

  const updateSetting = (key, value) => {
    setSettings({ ...settings, [key]: value });
  };

  if (loading) return <div className="loading">Loading settings...</div>;

  return (
    <div className="settings-page">
      <div className="settings-header">
        <h1>🔧 DevSync Analysis Settings</h1>
        <div className="settings-actions">
          <button onClick={saveSettings} disabled={saving} className="btn-save">
            {saving ? 'Saving...' : '💾 Save Settings'}
          </button>
          <button onClick={resetSettings} className="btn-reset">
            🔄 Reset to Defaults
          </button>
        </div>
      </div>

      {message && <div className="message">{message}</div>}

      <div className="settings-grid">
        {/* 1. Long Method Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🔴 Long Method Detector</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.longMethodEnabled} 
                onChange={e => updateSetting('longMethodEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Detects methods that are too long or complex</p>
          <div className="setting-item">
            <label>Max Method Length: <strong>{settings.maxMethodLength}</strong></label>
            <input type="range" min="10" max="200" value={settings.maxMethodLength}
              onChange={e => updateSetting('maxMethodLength', parseInt(e.target.value))} />
            <span className="range-label">10 ← → 200</span>
          </div>
          <div className="setting-item">
            <label>Max Complexity: <strong>{settings.maxMethodComplexity}</strong></label>
            <input type="range" min="1" max="50" value={settings.maxMethodComplexity}
              onChange={e => updateSetting('maxMethodComplexity', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 50</span>
          </div>
        </div>

        {/* 2. Long Parameter List Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟡 Long Parameter List</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.longParameterEnabled}
                onChange={e => updateSetting('longParameterEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Identifies methods with too many parameters</p>
          <div className="setting-item">
            <label>Max Parameters: <strong>{settings.maxParameterCount}</strong></label>
            <input type="range" min="1" max="15" value={settings.maxParameterCount}
              onChange={e => updateSetting('maxParameterCount', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 15</span>
          </div>
        </div>

        {/* 3. Long Identifier Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟠 Long Identifier</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.longIdentifierEnabled}
                onChange={e => updateSetting('longIdentifierEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Finds identifiers that are too long or too short</p>
          <div className="setting-item">
            <label>Max Length: <strong>{settings.maxIdentifierLength}</strong></label>
            <input type="range" min="10" max="100" value={settings.maxIdentifierLength}
              onChange={e => updateSetting('maxIdentifierLength', parseInt(e.target.value))} />
            <span className="range-label">10 ← → 100</span>
          </div>
          <div className="setting-item">
            <label>Min Length: <strong>{settings.minIdentifierLength}</strong></label>
            <input type="range" min="1" max="10" value={settings.minIdentifierLength}
              onChange={e => updateSetting('minIdentifierLength', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 10</span>
          </div>
        </div>

        {/* 4. Magic Number Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟡 Magic Number</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.magicNumberEnabled}
                onChange={e => updateSetting('magicNumberEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Detects hardcoded numeric literals</p>
          <div className="setting-item">
            <label>Threshold: <strong>{settings.magicNumberThreshold}</strong></label>
            <input type="range" min="0" max="20" value={settings.magicNumberThreshold}
              onChange={e => updateSetting('magicNumberThreshold', parseInt(e.target.value))} />
            <span className="range-label">0 ← → 20</span>
          </div>
        </div>

        {/* 5. Missing Default Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🔴 Missing Default</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.missingDefaultEnabled}
                onChange={e => updateSetting('missingDefaultEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Identifies switch statements without default cases</p>
        </div>

        {/* 6. Empty Catch Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🔴 Empty Catch</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.emptyCatchEnabled}
                onChange={e => updateSetting('emptyCatchEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Finds empty catch blocks</p>
        </div>

        {/* 7. Complex Conditional Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟡 Complex Conditional</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.complexConditionalEnabled}
                onChange={e => updateSetting('complexConditionalEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Detects overly complex conditional expressions</p>
          <div className="setting-item">
            <label>Max Operators: <strong>{settings.maxConditionalOperators}</strong></label>
            <input type="range" min="1" max="15" value={settings.maxConditionalOperators}
              onChange={e => updateSetting('maxConditionalOperators', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 15</span>
          </div>
          <div className="setting-item">
            <label>Max Nesting: <strong>{settings.maxNestingDepth}</strong></label>
            <input type="range" min="1" max="10" value={settings.maxNestingDepth}
              onChange={e => updateSetting('maxNestingDepth', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 10</span>
          </div>
        </div>

        {/* 8. Long Statement Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟠 Long Statement</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.longStatementEnabled}
                onChange={e => updateSetting('longStatementEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Identifies statements that are too long</p>
          <div className="setting-item">
            <label>Max Tokens: <strong>{settings.maxStatementTokens}</strong></label>
            <input type="range" min="10" max="150" value={settings.maxStatementTokens}
              onChange={e => updateSetting('maxStatementTokens', parseInt(e.target.value))} />
            <span className="range-label">10 ← → 150</span>
          </div>
          <div className="setting-item">
            <label>Max Chars: <strong>{settings.maxStatementChars}</strong></label>
            <input type="range" min="50" max="1000" value={settings.maxStatementChars}
              onChange={e => updateSetting('maxStatementChars', parseInt(e.target.value))} />
            <span className="range-label">50 ← → 1000</span>
          </div>
          <div className="setting-item">
            <label>Max Chain: <strong>{settings.maxMethodChainLength}</strong></label>
            <input type="range" min="2" max="20" value={settings.maxMethodChainLength}
              onChange={e => updateSetting('maxMethodChainLength', parseInt(e.target.value))} />
            <span className="range-label">2 ← → 20</span>
          </div>
        </div>

        {/* 9. Broken Modularization Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🔴 Broken Modularization</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.brokenModularizationEnabled}
                onChange={e => updateSetting('brokenModularizationEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Detects poor modularization</p>
          <div className="setting-item">
            <label>Max Responsibilities: <strong>{settings.maxResponsibilities}</strong></label>
            <input type="range" min="1" max="10" value={settings.maxResponsibilities}
              onChange={e => updateSetting('maxResponsibilities', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 10</span>
          </div>
          <div className="setting-item">
            <label>Min Cohesion: <strong>{settings.minCohesionIndex?.toFixed(1)}</strong></label>
            <input type="range" min="0" max="1" step="0.1" value={settings.minCohesionIndex}
              onChange={e => updateSetting('minCohesionIndex', parseFloat(e.target.value))} />
            <span className="range-label">0.0 ← → 1.0</span>
          </div>
          <div className="setting-item">
            <label>Max Coupling: <strong>{settings.maxCouplingCount}</strong></label>
            <input type="range" min="1" max="20" value={settings.maxCouplingCount}
              onChange={e => updateSetting('maxCouplingCount', parseInt(e.target.value))} />
            <span className="range-label">1 ← → 20</span>
          </div>
        </div>

        {/* 10. Deficient Encapsulation Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟡 Deficient Encapsulation</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.deficientEncapsulationEnabled}
                onChange={e => updateSetting('deficientEncapsulationEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Identifies public fields that break encapsulation</p>
        </div>

        {/* 11. Unnecessary Abstraction Detector */}
        <div className="detector-card">
          <div className="detector-header">
            <h3>🟠 Unnecessary Abstraction</h3>
            <label className="toggle">
              <input type="checkbox" checked={settings.unnecessaryAbstractionEnabled}
                onChange={e => updateSetting('unnecessaryAbstractionEnabled', e.target.checked)} />
              <span className="slider"></span>
            </label>
          </div>
          <p>Finds abstractions with only one implementation</p>
          <div className="setting-item">
            <label>Max Usage: <strong>{settings.maxAbstractionUsage}</strong></label>
            <input type="range" min="0" max="5" value={settings.maxAbstractionUsage}
              onChange={e => updateSetting('maxAbstractionUsage', parseInt(e.target.value))} />
            <span className="range-label">0 ← → 5</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;
