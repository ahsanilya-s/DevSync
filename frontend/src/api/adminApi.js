import axios from 'axios'

const adminApi = {
  fetchDashboard: async () => {
    const res = await axios.get('/api/admin/dashboard')
    return res.data
  },

  fetchReports: async () => {
    const res = await axios.get('/api/admin/reports')
    return res.data
  },

  fetchProjects: async () => {
    const res = await axios.get('/api/admin/projects')
    return res.data
  },

  fetchUsers: async () => {
    const res = await axios.get('/api/admin/users')
    return res.data
  },

  fixReportCounts: async () => {
    const res = await axios.post('/api/admin/fix-counts')
    return res.data
  },

  fetchPlatformStats: async () => {
    const res = await axios.get('/api/admin/dashboard')
    return {
      analyses: res.data.aiAnalysisCount || 0,
      issues: res.data.totalIssues || 0,
      users: res.data.totalUsers || 0
    }
  }
}

export default adminApi

