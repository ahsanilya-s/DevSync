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
            alert(res.data)
            nav('/home')
        } catch(e) {
            alert('Invalid credentials')
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
