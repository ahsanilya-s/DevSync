import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Users, FolderOpen, AlertTriangle, Brain, Search, Settings, BarChart3, Home, LogOut } from 'lucide-react'
import { toast } from 'sonner'
import { Toaster } from '../components/ui/sonner'
import api from '../api'

const AdminPanel = () => {
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('dashboard')
  const [dashboardData, setDashboardData] = useState({})
  const [users, setUsers] = useState([])
  const [projects, setProjects] = useState([])
  const [reports, setReports] = useState({})
  const [searchTerm, setSearchTerm] = useState('')

  useEffect(() => {
    if (!localStorage.getItem('adminAuth')) {
      navigate('/admin/login')
      return
    }
    loadDashboardData()
    loadUsers()
    loadProjects()
    loadReports()
  }, [])

  const loadDashboardData = async () => {
    try {
      const response = await api.get('/admin/dashboard')
      setDashboardData(response.data)
    } catch (error) {
      console.error('Error loading dashboard data:', error)
    }
  }

  const loadUsers = async () => {
    try {
      const response = await api.get('/admin/users')
      console.log('Users response:', response.data)
      setUsers(response.data || [])
    } catch (error) {
      console.error('Error loading users:', error)
      toast.error('Failed to load users')
      setUsers([])
    }
  }

  const loadProjects = async () => {
    try {
      const response = await api.get('/admin/projects')
      setProjects(response.data)
    } catch (error) {
      console.error('Error loading projects:', error)
    }
  }

  const loadReports = async () => {
    try {
      const response = await api.get('/admin/reports')
      console.log('Reports response:', response.data)
      setReports(response.data || {})
    } catch (error) {
      console.error('Error loading reports:', error)
      toast.error('Failed to load reports')
      setReports({})
    }
  }

  const StatCard = ({ title, value, icon: Icon, color }) => (
    <Card className="hover:shadow-lg transition-shadow">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-600">{title}</p>
            <p className={`text-3xl font-bold ${color}`}>{value || 0}</p>
          </div>
          <Icon className={`h-8 w-8 ${color}`} />
        </div>
      </CardContent>
    </Card>
  )

  const renderDashboard = () => (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard 
          title="Total Users" 
          value={dashboardData.totalUsers} 
          icon={Users} 
          color="text-blue-600" 
        />
        <StatCard 
          title="Total Issues" 
          value={dashboardData.totalIssues} 
          icon={AlertTriangle} 
          color="text-red-600" 
        />
        <StatCard 
          title="AI Analysis Count" 
          value={dashboardData.aiAnalysisCount} 
          icon={Brain} 
          color="text-purple-600" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {projects.slice(0, 5).map((project, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium">{project.projectName}</p>
                    <p className="text-sm text-gray-600">{project.userEmail}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium">{project.totalIssues} issues</p>
                    <p className="text-xs text-gray-500">
                      {new Date(project.analysisDate).toLocaleDateString()}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Issue Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {reports.issueDistribution && Object.entries(reports.issueDistribution).map(([type, count]) => (
                <div key={type} className="flex items-center justify-between">
                  <span className="capitalize font-medium">{type}</span>
                  <div className="flex items-center gap-2">
                    <div className={`w-20 h-2 rounded-full ${
                      type === 'critical' ? 'bg-red-200' : 
                      type === 'warnings' ? 'bg-yellow-200' : 'bg-blue-200'
                    }`}>
                      <div className={`h-full rounded-full ${
                        type === 'critical' ? 'bg-red-500' : 
                        type === 'warnings' ? 'bg-yellow-500' : 'bg-blue-500'
                      }`} style={{ width: `${Math.min(count / 100 * 100, 100)}%` }}></div>
                    </div>
                    <span className="font-bold">{count}</span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )

  const renderUsers = () => {
    if (!users || users.length === 0) {
      return (
        <div className="space-y-6">
          <h2 className="text-2xl font-bold">Users Management</h2>
          <Card>
            <CardContent className="p-8 text-center">
              <p className="text-gray-500">No users found or loading...</p>
            </CardContent>
          </Card>
        </div>
      )
    }

    return (
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold">Users Management</h2>
            <p className="text-gray-600">Total Users: {users.length}</p>
          </div>
          <div className="flex gap-2">
            <Input 
              placeholder="Search users..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-64"
            />
            <Button variant="outline">
              <Search className="h-4 w-4" />
            </Button>
          </div>
        </div>

        <Card>
          <CardContent className="p-0">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Username</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Projects</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {users.filter(user => 
                    (user.username || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
                    (user.email || '').toLowerCase().includes(searchTerm.toLowerCase())
                  ).map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{user.id}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{user.username || 'N/A'}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">{user.email || 'N/A'}</div>
                        <div className="text-xs text-gray-500">Verified User</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{user.projectCount || 0}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        <Button variant="outline" size="sm">View Details</Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  const renderProjects = () => (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold">Projects Overview</h2>
          <p className="text-gray-600">Total Projects: {projects.length}</p>
        </div>
        <Input 
          placeholder="Search projects..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-64"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {projects.filter(project => 
          project.projectName.toLowerCase().includes(searchTerm.toLowerCase())
        ).map((project) => (
          <Card key={project.id} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <CardTitle className="text-lg">{project.projectName}</CardTitle>
              <div className="space-y-1">
                <p className="text-sm text-blue-600 font-medium">{project.userEmail}</p>
                <p className="text-xs text-gray-500">User ID: {project.userId}</p>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span>Total Issues:</span>
                  <span className="font-bold text-red-600">{project.totalIssues}</span>
                </div>
                <div className="flex justify-between">
                  <span>Critical:</span>
                  <span className="font-bold text-red-500">{project.criticalIssues}</span>
                </div>
                <div className="flex justify-between">
                  <span>Analysis Date:</span>
                  <span className="text-sm">{new Date(project.analysisDate).toLocaleDateString()}</span>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )

  const renderReports = () => (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Analytics & Reports</h2>
      
      <div className="grid grid-cols-1 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>All Analysis Reports</CardTitle>
            <p className="text-sm text-gray-600">Complete history of all analyses performed</p>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Project</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">User</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Issues</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Critical</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Warnings</th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Suggestions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {reports.allReports && reports.allReports.length > 0 ? reports.allReports.map((report) => (
                    <tr key={report.id} className="hover:bg-gray-50">
                      <td className="px-4 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{report.projectName || 'N/A'}</td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-blue-600">{report.userName || 'Unknown'}</td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-gray-900">
                        {report.analysisDate ? new Date(report.analysisDate).toLocaleDateString() : 'N/A'}
                      </td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm font-bold text-red-600">{report.totalIssues || 0}</td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-red-500">{report.criticalIssues || 0}</td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-yellow-600">{report.warnings || 0}</td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-blue-600">{report.suggestions || 0}</td>
                    </tr>
                  )) : (
                    <tr>
                      <td colSpan="7" className="px-4 py-8 text-center text-gray-500">
                        No reports found in database
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle>Issue Types Distribution</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {reports.issueDistribution && Object.entries(reports.issueDistribution).map(([type, count]) => (
                  <div key={type} className="space-y-2">
                    <div className="flex justify-between">
                      <span className="capitalize font-medium">{type}</span>
                      <span className="font-bold">{count}</span>
                    </div>
                    <div className={`w-full h-3 rounded-full ${
                      type === 'critical' ? 'bg-red-100' : 
                      type === 'warnings' ? 'bg-yellow-100' : 'bg-blue-100'
                    }`}>
                      <div className={`h-full rounded-full ${
                        type === 'critical' ? 'bg-red-500' : 
                        type === 'warnings' ? 'bg-yellow-500' : 'bg-blue-500'
                      }`} style={{ width: `${Math.min(count / 100 * 100, 100)}%` }}></div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Monthly Analysis Trend</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {reports.monthlyAnalysis && reports.monthlyAnalysis.map((month) => (
                  <div key={month.month} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded">
                    <span>Month {month.month}</span>
                    <span className="font-bold text-blue-600">{month.analyses} analyses</span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )

  const handleLogout = () => {
    localStorage.removeItem('adminAuth')
    toast.success('Logged out successfully')
    navigate('/admin/login')
  }

  const sidebarItems = [
    { id: 'dashboard', label: 'Dashboard', icon: Home },
    { id: 'users', label: 'Users', icon: Users },
    { id: 'projects', label: 'Projects', icon: FolderOpen },
    { id: 'reports', label: 'Reports', icon: BarChart3 },
    { id: 'settings', label: 'Settings', icon: Settings },
  ]

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <div className="w-64 bg-white shadow-lg">
        <div className="p-6 border-b">
          <img src="/logo_for_whitetheme.png" alt="DevSync" className="h-8 w-auto" />
          <p className="text-sm text-gray-600 mt-2">Admin Panel</p>
          <Button
            onClick={handleLogout}
            variant="outline"
            size="sm"
            className="mt-3 w-full text-red-600 border-red-200 hover:bg-red-50"
          >
            <LogOut className="h-4 w-4 mr-2" />
            Logout
          </Button>
        </div>
        
        <nav className="mt-6">
          {sidebarItems.map((item) => {
            const Icon = item.icon
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`w-full flex items-center px-6 py-3 text-left hover:bg-blue-50 transition-colors ${
                  activeTab === item.id ? 'bg-blue-50 text-blue-600 border-r-2 border-blue-600' : 'text-gray-700'
                }`}
              >
                <Icon className="h-5 w-5 mr-3" />
                {item.label}
              </button>
            )
          })}
        </nav>
      </div>

      {/* Main Content */}
      <div className="flex-1 p-8">
        <div className="max-w-7xl mx-auto">
          {activeTab === 'dashboard' && renderDashboard()}
          {activeTab === 'users' && renderUsers()}
          {activeTab === 'projects' && renderProjects()}
          {activeTab === 'reports' && renderReports()}
          {activeTab === 'settings' && (
            <div className="text-center py-12">
              <Settings className="h-16 w-16 mx-auto text-gray-400 mb-4" />
              <h3 className="text-xl font-medium text-gray-900 mb-2">Settings</h3>
              <p className="text-gray-600">Settings panel coming soon...</p>
            </div>
          )}
        </div>
      </div>
      <Toaster position="bottom-right" />
    </div>
  )
}

export default AdminPanel