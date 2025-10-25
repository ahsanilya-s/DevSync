import React from 'react'
import { Link } from 'react-router-dom'

export default function Navbar(){
    return (
        <nav style={{padding:10, background:'#1e293b', color:'#fff'}}>
            <div style={{maxWidth:1000, margin:'auto', display:'flex', justifyContent:'space-between'}}>
                <div><Link to="/" style={{color:'#fff', textDecoration:'none'}}>DevSync</Link></div>
                <div>
                    <Link to="/signup" style={{color:'#fff', marginRight:12}}>Signup</Link>
                    <Link to="/login" style={{color:'#fff'}}>Login</Link>
                </div>
            </div>
        </nav>
    )
}
