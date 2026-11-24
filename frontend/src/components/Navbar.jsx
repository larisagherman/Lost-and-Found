
import './Navbar.css'
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { logout } from '../services/authService'

function Navbar({ user, setUser, authLoading }) {
  const navigate = useNavigate()

  const [loggingOut, setLoggingOut] = useState(false)
  const [error, setError] = useState('')

  async function handleLogout() {
    setLoggingOut(true)
    setError('')

    try {
      await logout()
      setUser(null)
      navigate('/login')
    } catch (error) {
      console.error('Logout failed:', error)
      setError('Logout failed. Please try again.')
    } finally {
      setLoggingOut(false)
    }
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <span className="brand-icon">✦</span>
        <span>lost&found.</span>
      </div>

      <div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/items">Browse Items</Link>
        {user && (
          <Link to="/my-listings">My Listings</Link>
        )}
      </div>

      <div className="navbar-auth">
        {authLoading ? (
          <span>Loading...</span>
        ) : user ? (
          <>
            <span className="user-name">
              {user.displayName}
            </span>

            <button
              type="button"
              className="login-button"
              onClick={handleLogout}
              disabled={loggingOut}
            >
              {loggingOut ? 'Logging out...' : 'Logout'}
            </button>
          </>
        ) : (
          <Link to="/login" className="login-button">
            Log in
          </Link>
        )}

        {error && <p role="alert">{error}</p>}
      </div>
    </nav>
  )
}

export default Navbar
