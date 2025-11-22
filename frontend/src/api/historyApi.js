import axios from 'axios'

const historyApi = {
  fetchHistory: async () => {
    const res = await axios.get('/api/history')
    return res.data
  },

  fetchReportByFolder: async (folderName) => {
    const res = await axios.get(`/api/history/report/${encodeURIComponent(folderName)}`)
    return res.data
  },

  fetchUserHistory: async (userId) => {
    const res = await axios.get(`/api/upload/history?userId=${encodeURIComponent(userId)}`)
    return res.data
  },

  fetchReportByPath: async (reportPath, userId) => {
    const params = new URLSearchParams()
    if (reportPath) params.append('path', reportPath)
    if (userId) params.append('userId', userId)
    const query = params.toString()
    const res = await axios.get(`/api/upload/report?${query}`)
    return res.data
  }
}

export default historyApi

