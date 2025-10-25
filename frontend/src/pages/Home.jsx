import React, { useState } from 'react'
import api from '../api'

export default function Home(){
    const [file, setFile] = useState(null)
    const [report, setReport] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleUpload(){
        if (!file) return alert('Please choose a zip file')
        setLoading(true)
        const fd = new FormData()
        fd.append('file', file)
        try {
            const res = await api.post('/upload', fd, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
            // backend returns summary string
            setReport(res.data)
        } catch(err){
            console.error(err)
            setReport('Error: ' + (err.response?.data || err.message))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{maxWidth:1000, margin:'40px auto', padding:20}}>
            <h2>DevSync - Upload Java Project ZIP</h2>
            <input type="file" accept=".zip" onChange={e => setFile(e.target.files[0])} />
            <button onClick={handleUpload} disabled={loading} style={{marginLeft:10}}>
                {loading ? 'Analyzing...' : 'Analyze'}
            </button>

            <div style={{marginTop:20, whiteSpace:'pre-wrap', background:'#f9fafb', padding:12, borderRadius:6}}>
                {report || 'No report yet.'}
            </div>
        </div>
    )
}
