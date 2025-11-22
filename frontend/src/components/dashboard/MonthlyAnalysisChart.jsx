import React from 'react'
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts'

function monthLabel(m) {
  // m can be number 1-12 or string month name
  if (!m && m !== 0) return ''
  if (typeof m === 'number') return ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'][m-1] || `${m}`
  return m.toString()
}

export default function MonthlyAnalysisChart({ monthlyData = [] }) {
  if (!monthlyData || monthlyData.length === 0) return <div className="text-sm text-gray-500">No activity</div>

  const data = monthlyData.map(item => ({
    name: monthLabel(item.month || item.label || item.name),
    analyses: item.analyses ?? item.count ?? item.value ?? 0
  })).sort((a,b) => a.name.localeCompare(b.name))

  return (
    <div style={{ width: '100%', height: 260 }}>
      <ResponsiveContainer>
        <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="colorAnalyses" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#60a5fa" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#60a5fa" stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis dataKey="name" />
          <YAxis />
          <CartesianGrid strokeDasharray="3 3" />
          <Tooltip />
          <Area type="monotone" dataKey="analyses" stroke="#2563eb" fillOpacity={1} fill="url(#colorAnalyses)" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}

