
import './Navbar.css'
import { Link } from 'react-router-dom'

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <span className="brand-icon">✦</span>
        <span>lost&found.</span>
      </div>

      <div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/items">Browse Items</Link>
      </div>

      <button className="login-button">
        Log in
      </button>
    </nav>
  )
}

export default Navbar
