import React, {useState} from 'react'
import api from '../api'
import { useNavigate } from 'react-router-dom'

export default function Login(){
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const nav = useNavigate()

    async function handleLogin(){
        try {
            const res = await api.post('/auth/login', { email, password })
            // backend should return success message or token. Here we assume simple success text
            alert(res.data)
            nav('/')
        } catch(e) {
            alert('Login failed: ' + (e.response?.data || e.message))
        }
    }
    return (
        <div style={{maxWidth:500, margin:'40px auto'}}>
            <h2>Login</h2>
            <input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} /><br/>
            <input type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)} /><br/>
            <button onClick={handleLogin}>Login</button>
        </div>
    )
}
