import React from 'react'
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const COLORS = ['#ef4444', '#f59e0b', '#10b981']

export default function IssuePieChart({ issueDistribution = {} }) {
  const data = []
  if (!issueDistribution) return <div>No data</div>

  const critical = issueDistribution.critical ?? issueDistribution.criticalIssues ?? issueDistribution.crit ?? 0
  const warnings = issueDistribution.warnings ?? issueDistribution.warning ?? issueDistribution.warningsCount ?? 0
  const suggestions = issueDistribution.suggestions ?? issueDistribution.suggestionsCount ?? 0

  data.push({ name: 'Critical', value: critical })
  data.push({ name: 'Warnings', value: warnings })
  data.push({ name: 'Suggestions', value: suggestions })

  const nonZero = data.some(d => d.value > 0)
  if (!nonZero) return <div className="text-sm text-gray-500">No issues detected</div>

  return (
    <div style={{ width: '100%', height: 240 }}>
      <ResponsiveContainer>
        <PieChart>
          <Pie dataKey="value" data={data} cx="50%" cy="50%" outerRadius={80} label>
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}

