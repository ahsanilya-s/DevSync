import React, { useState, useEffect } from 'react'
import { X, Save, Trash2, FileText, Calendar, AlertTriangle, Edit2 } from 'lucide-react'
import { Button } from './ui/button'
import { Input } from './ui/input'
import { toast } from 'sonner'
import api from '../api'
import './UserDetailModal.css'

export function UserDetailModal({ userId, isOpen, onClose, onUserUpdated }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState(false)
  const [editForm, setEditForm] = useState({ username: '', email: '' })

  useEffect(() => {
    if (isOpen && userId) {
      loadUserDetails()
    }
  }, [isOpen, userId])

  const loadUserDetails = async () => {
    try {
      setLoading(true)
      const response = await api.get(`/admin/users/${userId}`)
      setUser(response.data)
      setEditForm({
        username: response.data.username || '',
        email: response.data.email || ''
      })
    } catch (error) {
      toast.error('Failed to load user details')
    } finally {
      setLoading(false)
    }
  }

  const handleUpdateUser = async () => {
    try {
      await api.put(`/admin/users/${userId}`, editForm)
      toast.success('User updated successfully')
      setEditing(false)
      loadUserDetails()
      onUserUpdated()
    } catch (error) {
      toast.error('Failed to update user')
    }
  }

  const handleDeleteUser = async () => {
    if (!confirm('Are you sure you want to delete this user? This will also delete all their analyses.')) {
      return
    }
    
    try {
      await api.delete(`/admin/users/${userId}`)
      toast.success('User deleted successfully')
      onClose()
      onUserUpdated()
    } catch (error) {
      toast.error('Failed to delete user')
    }
  }

  const handleDeleteAnalysis = async (analysisId) => {
    if (!confirm('Are you sure you want to delete this analysis?')) {
      return
    }
    
    try {
      await api.delete(`/admin/users/${userId}/analyses/${analysisId}`)
      toast.success('Analysis deleted successfully')
      loadUserDetails()
    } catch (error) {
      toast.error('Failed to delete analysis')
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-2xl font-bold">User Details</h2>
          <Button variant="ghost" size="sm" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        {loading ? (
          <div className="p-8 text-center">Loading user details...</div>
        ) : user ? (
          <div className="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
            {/* User Info */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
              <div className="space-y-4">
                <h3 className="text-lg font-semibold">User Information</h3>
                
                {editing ? (
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm font-medium mb-1">Username</label>
                      <Input
                        value={editForm.username}
                        onChange={(e) => setEditForm(prev => ({ ...prev, username: e.target.value }))}
                        placeholder="Enter username"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Email</label>
                      <Input
                        value={editForm.email}
                        onChange={(e) => setEditForm(prev => ({ ...prev, email: e.target.value }))}
                        placeholder="Enter email"
                      />
                    </div>
                    <div className="flex gap-2">
                      <Button onClick={handleUpdateUser} size="sm">
                        <Save className="h-4 w-4 mr-1" />
                        Save
                      </Button>
                      <Button variant="outline" onClick={() => setEditing(false)} size="sm">
                        Cancel
                      </Button>
                    </div>
                  </div>
                ) : (
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-600">ID</label>
                      <p className="text-lg">{user.id}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-600">Username</label>
                      <p className="text-lg">{user.username || 'N/A'}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-600">Email</label>
                      <p className="text-lg">{user.email || 'N/A'}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-600">Created At</label>
                      <p className="text-lg">{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}</p>
                    </div>
                    <Button onClick={() => setEditing(true)} size="sm" variant="outline">
                      <Edit2 className="h-4 w-4 mr-1" />
                      Edit User
                    </Button>
                  </div>
                )}
              </div>

              <div className="space-y-4">
                <h3 className="text-lg font-semibold">Statistics</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-blue-50 p-4 rounded-lg">
                    <div className="flex items-center gap-2">
                      <FileText className="h-5 w-5 text-blue-600" />
                      <span className="text-sm font-medium">Total Analyses</span>
                    </div>
                    <p className="text-2xl font-bold text-blue-600">{user.totalAnalyses}</p>
                  </div>
                  <div className="bg-red-50 p-4 rounded-lg">
                    <div className="flex items-center gap-2">
                      <AlertTriangle className="h-5 w-5 text-red-600" />
                      <span className="text-sm font-medium">Total Issues</span>
                    </div>
                    <p className="text-2xl font-bold text-red-600">{user.totalIssuesFound}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Analysis History */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold">Analysis History</h3>
                <span className="text-sm text-gray-600">{user.analyses.length} analyses</span>
              </div>
              
              {user.analyses.length > 0 ? (
                <div className="overflow-x-auto">
                  <table className="w-full border border-gray-200 rounded-lg">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Project Name</th>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Date</th>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Issues</th>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Critical</th>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Warnings</th>
                        <th className="px-4 py-3 text-left text-sm font-medium text-gray-600">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {user.analyses.map((analysis) => (
                        <tr key={analysis.id} className="hover:bg-gray-50">
                          <td className="px-4 py-3 text-sm font-medium">{analysis.projectName}</td>
                          <td className="px-4 py-3 text-sm text-gray-600">
                            {new Date(analysis.analysisDate).toLocaleDateString()}
                          </td>
                          <td className="px-4 py-3 text-sm font-bold text-red-600">{analysis.totalIssues}</td>
                          <td className="px-4 py-3 text-sm text-red-500">{analysis.criticalIssues}</td>
                          <td className="px-4 py-3 text-sm text-yellow-600">{analysis.warnings}</td>
                          <td className="px-4 py-3 text-sm">
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleDeleteAnalysis(analysis.id)}
                              className="text-red-600 hover:text-red-700"
                            >
                              <Trash2 className="h-3 w-3" />
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <FileText className="h-12 w-12 mx-auto mb-2 text-gray-300" />
                  <p>No analyses found for this user</p>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className="p-8 text-center text-red-600">Failed to load user details</div>
        )}

        {/* Footer */}
        {user && (
          <div className="flex justify-between items-center p-6 border-t bg-gray-50">
            <Button
              variant="destructive"
              onClick={handleDeleteUser}
              className="bg-red-600 hover:bg-red-700"
            >
              <Trash2 className="h-4 w-4 mr-2" />
              Delete User
            </Button>
            <Button variant="outline" onClick={onClose}>
              Close
            </Button>
          </div>
        )}
      </div>
    </div>
  )
}