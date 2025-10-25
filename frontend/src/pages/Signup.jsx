import React, {useState} from 'react'
import api from '../api'
import { useNavigate } from 'react-router-dom'

export default function Signup(){
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const nav = useNavigate()

    async function handleSignup(){
        try {
            const res = await api.post('/auth/signup', { name, email, password })
            alert(res.data)
            nav('/login')
        } catch(e) {
            alert('Error: ' + (e.response?.data || e.message))
        }
    }
    return (
        <div style={{maxWidth:500, margin:'40px auto'}}>
            <h2>Signup</h2>
            <input placeholder="Name" value={name} onChange={e=>setName(e.target.value)} /><br/>
            <input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} /><br/>
            <input type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)} /><br/>
            <button onClick={handleSignup}>Signup</button>
        </div>
    )
}
