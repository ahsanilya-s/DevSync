import React, { useMemo, useState } from 'react'

export default function ReportsTable({ data = [], onViewReport = () => {} }) {
  const [filter, setFilter] = useState('')
  const [sortDesc, setSortDesc] = useState(true)

  const filtered = useMemo(() => {
    const q = filter.trim().toLowerCase()
    let arr = Array.isArray(data) ? data.slice() : []
    if (q) arr = arr.filter(r => (r.projectName || '').toLowerCase().includes(q) || (r.userName || '').toLowerCase().includes(q))
    arr.sort((a,b) => {
      const da = new Date(a.analysisDate || a.date || a.createdAt || 0).getTime()
      const db = new Date(b.analysisDate || b.date || b.createdAt || 0).getTime()
      return sortDesc ? db - da : da - db
    })
    return arr
  }, [data, filter, sortDesc])

  return (
    <div>
      <div className="flex items-center gap-2 mb-3">
        <input value={filter} onChange={e => setFilter(e.target.value)} placeholder="Filter by project or user" className="border rounded px-2 py-1" />
        <button onClick={() => setSortDesc(!sortDesc)} className="px-3 py-1 bg-gray-100 rounded">Sort by date {sortDesc ? '↓' : '↑'}</button>
      </div>

      <div className="overflow-auto">
        <table className="min-w-full table-auto">
          <thead>
            <tr className="text-left border-b">
              <th className="px-2 py-2">Project</th>
              <th className="px-2 py-2">User</th>
              <th className="px-2 py-2">Date</th>
              <th className="px-2 py-2">Total Issues</th>
              <th className="px-2 py-2">Critical</th>
              <th className="px-2 py-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((r) => (
              <tr key={r.id || r.reportPath || r.folderName} className="border-b">
                <td className="px-2 py-2">{r.projectName || r.folderName || '—'}</td>
                <td className="px-2 py-2">{r.userName || r.userId || '—'}</td>
                <td className="px-2 py-2">{new Date(r.analysisDate || r.date || r.lastModified || 0).toLocaleString()}</td>
                <td className="px-2 py-2">{r.totalIssues ?? r.issues ?? 0}</td>
                <td className="px-2 py-2">{r.criticalIssues ?? r.crit ?? 0}</td>
                <td className="px-2 py-2">
                  <button onClick={() => onViewReport(r)} className="px-2 py-1 bg-blue-500 text-white rounded">View</button>
                </td>
              </tr>
            ))}

            {filtered.length === 0 && (
              <tr>
                <td colSpan={6} className="px-2 py-4 text-center text-gray-500">No reports available</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

