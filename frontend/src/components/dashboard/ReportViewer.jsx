import React from 'react'

export default function ReportViewer({ open = false, onClose = () => {}, report = {} }) {
  if (!open) return null

  const { loading, content, meta } = report || {}

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-white w-11/12 md:w-3/4 lg:w-2/3 max-h-[80vh] overflow-auto rounded shadow-lg p-4">
        <div className="flex items-center justify-between mb-4">
          <div>
            <div className="text-lg font-semibold">Report Viewer</div>
            <div className="text-sm text-gray-500">{meta?.projectName || meta?.folderName || ''}</div>
          </div>
          <div>
            <button onClick={onClose} className="px-3 py-1 bg-gray-100 rounded">Close</button>
          </div>
        </div>

        {loading && <div>Loading report...</div>}
        {!loading && (
          <pre className="whitespace-pre-wrap text-sm leading-relaxed bg-gray-50 p-3 rounded">{content || 'No content'}</pre>
        )}
      </div>
    </div>
  )
}

