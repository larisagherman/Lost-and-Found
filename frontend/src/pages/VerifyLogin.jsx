
import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { verifyLoginCode } from '../services/authService'
import './Auth.css'

function VerifyLogin({ setUser }) {
    const [token, setToken] = useState('')
    const [message, setMessage] = useState('')
    const [loading, setLoading] = useState(false)

    const navigate = useNavigate()
    const location = useLocation()
    const returnTo = location.state?.from || '/'

    async function handleSubmit(event) {
        event.preventDefault()

        setLoading(true)
        setMessage('')

        try {
            const user = await verifyLoginCode(token)
            setUser(user)
            navigate(returnTo, { replace: true })
        } catch (error) {
            setMessage('Invalid or expired code. Please try again.')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="auth-page">
            <div className="auth-card">

                <div className="auth-header">
                    <div className="auth-icon">
                        ✦
                    </div>

                    <h1>Verify Login</h1>

                    <p>
                        Enter the sign-in code received
                        by email to access your account.
                    </p>
                </div>

                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                >
                    <label htmlFor="token">
                        Sign-in Code
                    </label>

                    <input
                        id="token"
                        type="text"
                        value={token}
                        onChange={(event) => setToken(event.target.value)}
                        placeholder="Enter your code"
                        autoComplete="off"
                        required
                    />

                    <button
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Verifying...' : 'Verify Code'}
                    </button>
                </form>

                {message && (
                    <p className="auth-message" role="alert">
                        {message}
                    </p>
                )}

                <div className="auth-footer">
                    Your sign-in code expires after 10 minutes.
                </div>

            </div>
        </main>
    )
}

export default VerifyLogin
