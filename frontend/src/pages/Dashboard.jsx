import React, { useEffect, useState } from 'react'
import adminApi from '../api/adminApi'
import historyApi from '../api/historyApi'
import OverviewCards from '../components/dashboard/OverviewCards'
import IssuePieChart from '../components/dashboard/IssuePieChart'
import MonthlyAnalysisChart from '../components/dashboard/MonthlyAnalysisChart'
import ReportsTable from '../components/dashboard/ReportsTable'
import ReportViewer from '../components/dashboard/ReportViewer'

export default function Dashboard() {
  const [summary, setSummary] = useState(null)
  const [reportsData, setReportsData] = useState([])
  const [issueDistribution, setIssueDistribution] = useState(null)
  const [monthlyData, setMonthlyData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedReport, setSelectedReport] = useState(null)
  const [fixing, setFixing] = useState(false)

  useEffect(() => {
    let mounted = true
    setLoading(true)
    Promise.all([adminApi.fetchDashboard(), adminApi.fetchReports()])
      .then(([dash, reports]) => {
        if (!mounted) return
        setSummary(dash)
        // reports may contain { allReports, issueDistribution, monthlyAnalysis }
        if (reports) {
          setReportsData(reports.allReports || reports)
          setIssueDistribution(reports.issueDistribution || null)
          setMonthlyData(reports.monthlyAnalysis || [])
        }
      })
      .catch((err) => {
        console.error('Failed to load dashboard data', err)
        setError('Failed to load dashboard data')
      })
      .finally(() => mounted && setLoading(false))

    return () => { mounted = false }
  }, [])

  const handleFixCounts = async () => {
    setFixing(true)
    try {
      const result = await adminApi.fixReportCounts()
      alert('Success: ' + result)
      window.location.reload()
    } catch (err) {
      alert('Failed to fix counts: ' + err.message)
    } finally {
      setFixing(false)
    }
  }

  const onViewReport = async (row) => {
    // row may include folderName or reportPath
    setSelectedReport({ loading: true })
    try {
      let content = null
      if (row.folderName) content = await historyApi.fetchReportByFolder(row.folderName)
      else if (row.reportPath) content = await historyApi.fetchReportByPath(row.reportPath, row.userId)
      else if (row.reportId) content = await historyApi.fetchReportByPath(row.reportPath, row.userId)
      setSelectedReport({ loading: false, content, meta: row })
    } catch (err) {
      console.error('Failed to load report content', err)
      setSelectedReport({ loading: false, content: 'Unable to load report', meta: row })
    }
  }

  const onDeleteReport = async (report) => {
    try {
      await historyApi.deleteReport(report.id, report.userId)
      setReportsData(prev => prev.filter(r => r.id !== report.id))
      alert('Report deleted successfully')
    } catch (err) {
      alert('Failed to delete report: ' + err.message)
    }
  }

  return (
    <div className="container mx-auto px-6 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-semibold">Dashboard</h1>
        <button 
          onClick={handleFixCounts} 
          disabled={fixing}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {fixing ? 'Fixing...' : 'Fix Report Counts'}
        </button>
      </div>

      {loading && <div>Loading dashboard...</div>}
      {error && <div className="text-red-500">{error}</div>}

      {!loading && !error && (
        <>
          <OverviewCards
            totalUsers={summary?.totalUsers}
            totalIssues={summary?.totalIssues}
            aiAnalysisCount={summary?.aiAnalysisCount}
            criticalIssues={summary?.criticalIssues}
            warningIssues={summary?.warningIssues}
            suggestionIssues={summary?.suggestionIssues}
            cleanFiles={summary?.cleanFiles}
          />

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
            <div className="col-span-2 p-4 bg-white rounded-lg shadow">
              <h2 className="text-xl font-medium mb-4">Monthly Analyses</h2>
              <MonthlyAnalysisChart monthlyData={monthlyData} />
            </div>

            <div className="p-4 bg-white rounded-lg shadow">
              <h2 className="text-xl font-medium mb-4">Issue Distribution</h2>
              <IssuePieChart issueDistribution={issueDistribution} />
            </div>
          </div>

          <div className="mt-6 p-4 bg-white rounded-lg shadow">
            <h2 className="text-xl font-medium mb-4">Reports</h2>
            <ReportsTable data={reportsData} onViewReport={onViewReport} onDeleteReport={onDeleteReport} />
          </div>

          {selectedReport && (
            <ReportViewer
              open={!!selectedReport}
              onClose={() => setSelectedReport(null)}
              report={selectedReport}
            />
          )}
        </>
      )}
    </div>
  )
}

